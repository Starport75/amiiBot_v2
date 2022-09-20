package amiiBot_v2;

import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandUpdater;

public class CommandPing {
	SlashCommand thisCommand;

	// Spot to set the name and description of the command
	String commandName = "ping";
	String commandDescription = "Gives the bot a ping!!";

	public CommandPing(DiscordApi api, AmiiboHuntAccess amiiboData) {
		List<SlashCommand> globalCommands = api.getGlobalSlashCommands().join();

		int i = 0;
		boolean isInitalized = false;

		while (i < globalCommands.size() & !isInitalized) {
			if (globalCommands.get(i).getName().equals(commandName)) {
				isInitalized = true;
				thisCommand = globalCommands.get(i);
			}
			i++;
		}

		if (!isInitalized) {
			thisCommand = SlashCommand.with(commandName, commandDescription).createGlobal(api).join();
		}

		thisCommand = new SlashCommandUpdater(thisCommand.getId()).setDescription(commandDescription)
				// Spot to add any unique stuff to the command
				
				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getCommandName().equals(commandName)) {
				//Spot for command response
				slashCommandInteraction.createImmediateResponder().setContent("Pong!").respond();
			}
		});

	}
}
