package amiiBot_v2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommandUpdateUsername {
	SlashCommand thisCommand;

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
								Arrays.asList(SlashCommandOptionChoice.create("All", ""),
										SlashCommandOptionChoice.create("Japan", "release_jp"),
										SlashCommandOptionChoice.create("North America", "release_na"),
										SlashCommandOptionChoice.create("Europe", "release_eu"),
										SlashCommandOptionChoice.create("Australia", "release_au")))))
				// Spot to add any unique stuff to the command

				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getCommandName().equals(commandName)) {
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

				for (int i = 0; i < amiiboArray.length(); i++) {

				}

				slashCommandInteraction.createImmediateResponder().setContent("Pong!").setFlags(MessageFlag.EPHEMERAL)
						.respond();
			}
		});

	}
}
