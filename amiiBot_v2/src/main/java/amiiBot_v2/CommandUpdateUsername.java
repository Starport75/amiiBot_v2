package amiiBot_v2;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.javacord.api.AccountUpdater;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.ServerUpdater;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommandUpdateUsername {
	SlashCommand thisCommand;
	String output = "If you see this, that's bad";

	// Spot to set the name and description of the command
	String commandName = "update_username";
	String commandDescription = "Updates your username to match your collection!";

	public CommandUpdateUsername(DiscordApi api, AmiiboHuntAccess access) {
		Set<SlashCommand> globalCommands = api.getGlobalSlashCommands().join();

		boolean isInitalized = false;

		for (SlashCommand currCommand : globalCommands) {
			if (currCommand.getName().equals(commandName)) {
				isInitalized = true;
				thisCommand = currCommand;
			}
		}

		if (!isInitalized) {
			thisCommand = SlashCommand.with(commandName, commandDescription).createGlobal(api).join();
		}

		thisCommand = new SlashCommandUpdater(thisCommand.getId()).setDescription(commandDescription)
				.setDefaultEnabledForPermissions(PermissionType.MANAGE_ROLES)
				.setSlashCommandOptions(Arrays.asList(
						SlashCommandOption.create(SlashCommandOptionType.STRING, "username",
								"The username you want with your amiibo tally!", true),
						SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "region",
								"Restrict your data to amiibo avalible in a certain region", true,
								Arrays.asList(SlashCommandOptionChoice.create("All", "all"),
										SlashCommandOptionChoice.create("Japan", "release_jp"),
										SlashCommandOptionChoice.create("North America", "release_na"),
										SlashCommandOptionChoice.create("Europe", "release_eu"),
										SlashCommandOptionChoice.create("Australia", "release_au")))))
				// Spot to add any unique stuff to the command

				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getCommandName().equals(commandName)) {
				slashCommandInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
					JSONObject userAmiiboJSON = null;
					try {
						userAmiiboJSON = access.getUserList(event.getInteraction().getUser().getIdAsString());
					} catch (IOException e) {
						System.out.println("Error accessing AmiiboHunt");
					}

					JSONArray amiiboArray = userAmiiboJSON.getJSONArray("amiibo");
					int cardTotal = 0;
					int cardOwned = 0;
					int otherTotal = 0;
					int otherOwned = 0;

					String username = slashCommandInteraction.getArguments().get(0).getStringValue().get();
					String region = slashCommandInteraction.getArguments().get(1).getStringValue().get();

					// System.out.println(amiiboArray.getJSONObject(10).toString());

					for (int i = 0; i < amiiboArray.length(); i++) {
						if (region.equals("all") || !amiiboArray.getJSONObject(i).get(region).equals(null)) {
							if (amiiboArray.getJSONObject(i).getJSONObject("type").getString("type").equals("Card")) {
								if (amiiboArray.getJSONObject(i).getJSONArray("owned_data").length() > 0) {
									cardOwned++;
								}
								cardTotal++;
							} else {
								if (amiiboArray.getJSONObject(i).getJSONArray("owned_data").length() > 0) {
									otherOwned++;
								}
								otherTotal++;
							}
						}
					}

					String regionEmoji = "";
					switch (region) {
					case "all":
						regionEmoji = "🌎";
						break;
					case "release_jp":
						regionEmoji = "🇯🇵";
						break;
					case "release_na":
						regionEmoji = "🇺🇸";
						break;
					case "release_eu":
						regionEmoji = "🇪🇺";
						break;
					case "release_au":
						regionEmoji = "🇦🇺";
						break;

					}
					output = username + " [" + otherOwned + "/" + otherTotal + " " + cardOwned + "/" + cardTotal + "] "
							+ regionEmoji;
					EmbedBuilder embed = new EmbedBuilder()
							.addField("Warning!", "This will set your username to:\n" + output
									+ "\nPlease press the button below to confirm you wish to make this change.")
							.setColor(Color.red);

					interactionOriginalResponseUpdater.addEmbed(embed)
							.addComponents(ActionRow.of(Button.success("setUsername", "Set Username!"),
									Button.danger("cancel", "Cancel")))
							// .setFlags(MessageFlag.EPHEMERAL)
							.update().exceptionally(ExceptionLogger.get());
				});
			}
		});
		api.addMessageComponentCreateListener(event -> {
			MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
			String customId = messageComponentInteraction.getCustomId();

			switch (customId) {
			case "setUsername":

				new ServerUpdater(event.getInteraction().getServer().get()).setNickname(event.getInteraction().getUser(),
						output).update().exceptionally(ExceptionLogger.get());
				messageComponentInteraction.createImmediateResponder().setContent("Username set!")
						.setFlags(MessageFlag.EPHEMERAL).respond();
				break;
			case "cancel":
				messageComponentInteraction.getMessage().delete().exceptionally(ExceptionLogger.get());
				messageComponentInteraction.createImmediateResponder().setContent("Action canceled!")
						.setFlags(MessageFlag.EPHEMERAL).respond();
				break;
			}
		});
	}
}
