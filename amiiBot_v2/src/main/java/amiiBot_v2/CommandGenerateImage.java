package amiiBot_v2;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.JSONObject;

public class CommandGenerateImage {
	SlashCommand thisCommand;

	// Spot to set the name and description of the command
	String commandName = "generate_image";
	String commandDescription = "Generates an image of your collection!";

	public CommandGenerateImage(DiscordApi api, AmiiboHuntAccess amiiboData) {
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
				// Spot to add any unique stuff to the command
				.setSlashCommandOptions(Arrays.asList(
						SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "figures",
								"Generates an image of the figures in your collection!", Arrays.asList()),
						SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "cards",
								"Generates an image of the cards in your collection!",
								Arrays.asList(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "set",
										"Determines the set of cards to put in your image", true, Arrays.asList(
												SlashCommandOptionChoice.create("Animal Crossing Series 1", "1"),
												SlashCommandOptionChoice.create("Animal Crossing Series 2", "2"),
												SlashCommandOptionChoice.create("Animal Crossing Series 3", "3"),
												SlashCommandOptionChoice.create("Animal Crossing Series 4", "4"),
												SlashCommandOptionChoice.create("Animal Crossing Series 5", "5"))),
										SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "subset",
												"Additional options for which cards to put into your image", false,
												Arrays.asList(SlashCommandOptionChoice.create("Wishlist", "w"),
														SlashCommandOptionChoice.create("Unobtained", "u"),
														SlashCommandOptionChoice.create("Duplicates", "d")))))))
				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getFullCommandName().equals(commandName + " figures")) {
				slashCommandInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
					EmbedBuilder embed = new EmbedBuilder();
					boolean quiet = false;
					JSONObject data = amiiboData.getFigureImage(event.getInteraction().getUser().getIdAsString());
					System.out.println(data.toString());
					if (data.keySet().contains("error")) {
						quiet = true;
						embed.setColor(Color.red);
						switch (data.get("error").toString()) {

						case "user profile is not public":
							embed.addField("Error:",
									"Your AmiiboHunt account is set to private! Click [here](https://www.amiibohunt.com/settings) to visit your account settings. From there, select 'Privacy Settings', and toggle 'Public Profile' on!");
							break;

						case "discord ID not found":
						case "invalid discord ID":
							embed.addField("Error:",
									"You don't have an AmiiboHunt account linked to your Discord account! Click [here](https://www.amiibohunt.com/oauth/discord/redirect) to link and/or create your account!");
							break;
						}
					} else {
						embed.setImage(data.getString("val"))
								.setTitle(event.getInteraction().getUser().getName() + "'s Collection:");
					}
					interactionOriginalResponseUpdater.addEmbed(embed).update().exceptionally(ExceptionLogger.get());
				});
			}
			if (slashCommandInteraction.getFullCommandName().equals(commandName + " cards")) {
				// Spot for command response

				slashCommandInteraction.createImmediateResponder().setContent("Cards!").respond();
			}
		});

	}
}
