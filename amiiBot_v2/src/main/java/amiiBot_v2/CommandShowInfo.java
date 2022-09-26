package amiiBot_v2;

import java.util.List;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.json.JSONObject;

public class CommandShowInfo {
	SlashCommand thisCommand;

	// Spot to set the name and description of the command
	String commandName = "show_info";
	String commandDescription = "Returns information on the given amiibo!";

	public CommandShowInfo(DiscordApi api, AmiiboHuntAccess amiiboData) {
		List<SlashCommand> globalCommands = api.getGlobalSlashCommands().join();

		int i = 0;
		boolean isInitalized = false;

		while (i < globalCommands.size() & !isInitalized) {
			System.out.println(globalCommands.get(i).getName());
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

				// Spot for command response
				JSONObject amiibo = amiiboData.getBaseList().get("");
				EmbedBuilder embed = new EmbedBuilder().setTitle("Title").setImage(currAmiibo.getImage(egg, userDiscordID))
						.addField("Release Dates:",
								"🇯🇵: " + currAmiibo.getReleaseJP() + "\n🇺🇸: " + currAmiibo.getReleaseNA()
										+ "\n🇪🇺: " + currAmiibo.getReleaseEU() + "\n🇦🇺: "
										+ currAmiibo.getReleaseAU())
						.addField("**Retailers with Stock**", retailOutput).setColor(currAmiibo.getColor())
						.addField("\u200b", "**Average Current Listed Prices** *(est.)*")
						.addInlineField("Average Price NiB",
								currAmiibo.getFormattedNewPriceListedNA() + "\n"
										+ currAmiibo.getFormattedNewPriceListedUK())
						.addInlineField("Average Price OoB", currAmiibo.getFormattedUsedPriceListedNA() + "\n"
								+ currAmiibo.getFormattedUsedPriceListedUK());
				slashCommandInteraction.createImmediateResponder().addEmbed(embed).respond();
			}
		});

	}
}
