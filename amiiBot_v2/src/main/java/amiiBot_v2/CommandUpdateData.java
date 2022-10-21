package amiiBot_v2;

import java.io.IOException;
import java.util.Set;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandUpdater;

public class CommandUpdateData {
	SlashCommand thisCommand;

	// Spot to set the name and description of the command
	String commandName = "update_data";
	String commandDescription = "Updates the bot to the most recent list of amiibo on amiiboHunt";

	public CommandUpdateData(DiscordApi api, AmiiboHuntAccess amiiboData) {

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

				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getCommandName().equals(commandName)) {
				try {
					amiiboData.updateBaseList();
				} catch (IOException e) {
					System.out.println("Failed to update AmiiboAccess base list");
					e.printStackTrace();
				}
				slashCommandInteraction.createImmediateResponder().setContent("Data updated successfully!")
						.setFlags(MessageFlag.EPHEMERAL).respond();
			}
		});

	}
}
