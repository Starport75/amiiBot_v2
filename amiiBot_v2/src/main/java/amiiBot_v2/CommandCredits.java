package amiiBot_v2;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandUpdater;

public class CommandCredits {
	SlashCommand thisCommand;

	// Spot to set the name and description of the command
	String commandName = "credits";
	String commandDescription = "Take a look at everyone who helped make amiiBot!";

	public CommandCredits(DiscordApi api) {
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

		thisCommand = new SlashCommandUpdater(thisCommand.getId()).setDescription(commandDescription).setDefaultEnabledForPermissions(PermissionType.MANAGE_ROLES)
				// Spot to add any unique stuff to the command
				
				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getCommandName().equals(commandName)) {
				EmbedBuilder embed = new EmbedBuilder().addField("**Head amiiBot Programmer: **", getName(api, "205877471067766784"))
						.addField("**amiiboHunt Developer and amiiBot Programming Support: **", getName(api, "240899417010470912"))
						.addField("**Data Collectors: **", 
								getName(api, "571177163789697025") + " \n" +
								getName(api, "272452471056760832") + " \n" +
								getName(api, "268428850239766529") + " \n" +
								getName(api, "170514612792328192") + " \n" +
								getName(api, "277092431814590464") + " \n" +
								getName(api, "854531142388547584") + " \n" +
								getName(api, "221300998617038848") + " \n" +
								getName(api, "363119455544410113") + " \n" )
								
						.addField("\u200b", "and a huge shoutout to " + getName(api, "83396497059614720") + " for allowing amiiBot to exist!")
						;
				
				slashCommandInteraction.createImmediateResponder().addEmbed(embed).respond();
			}
		});

	}
	public String getName(DiscordApi api, String discordID) {
		try {
			return api.getUserById(discordID).get().getName();
		} catch (InterruptedException | ExecutionException e) {
			return "Deleted User";
		}
	}
}
