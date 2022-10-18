package amiiBot_v2;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.JSONObject;

public class CommandShowInfo {
	SlashCommand thisCommand;

	// Spot to set the name and description of the command
	String commandName = "show_info";
	String commandDescription = "Returns information on the given amiibo!";

	public CommandShowInfo(DiscordApi api, AmiiboHuntAccess amiiboData, AmiiboAssistant AAssist) {
		List<SlashCommand> globalCommands = api.getGlobalSlashCommands().join();

		int i = 0;
		boolean isInitalized = false;

		while (i < globalCommands.size() & !isInitalized) {
			// List commands
			// System.out.println(globalCommands.get(i).getName());
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

				.setSlashCommandOptions(
						Arrays.asList(SlashCommandOption.createStringOption("type", "test_description", true, true),
								SlashCommandOption.createStringOption("series", "test_description", true, true),
								SlashCommandOption.createStringOption("name", "test_description", true, true)))
				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getCommandName().equals(commandName)) {

				// Spot for command response

				int amiiboID = AAssist.findAmiiboID(
						slashCommandInteraction.getArguments().get(0).getStringValue().get(),
						slashCommandInteraction.getArguments().get(1).getStringValue().get(),
						slashCommandInteraction.getArguments().get(2).getStringValue().get());
				JSONObject amiibo = amiiboData.getAmiibo(amiiboID).getJSONObject("amiibo");

				EmbedBuilder embed = new EmbedBuilder().setTitle(amiibo.getString("name"))
						.setImage(amiibo.getString("image_imgix_full_card"))
						.addField("Release Dates:",
								"🇯🇵: " + amiibo.getString("release_jp") + "\n🇺🇸: " + amiibo.getString("release_na")
										+ "\n🇪🇺: " + amiibo.getString("release_eu") + "\n🇦🇺: "
										+ amiibo.getString("release_au"))
						.addField("**Retailers with Stock**", "stock data here")
						.setColor(new Color(Integer.parseInt(amiibo.getString("background_color").substring(1), 16)))
						.addField("\u200b",
								"**Average Current Listed Prices** \n *prices are an estimate based on collected data*")
						.addInlineField("Average Price NiB",
								formatPrices(amiibo.getDouble("average_listed_this_month_us_new"), 0) + "\n"
										+ formatPrices(amiibo.getDouble("average_listed_this_month_uk_new"), 1))
						.addInlineField("Average Price OoB",
								formatPrices(amiibo.getDouble("average_listed_this_month_us_used"), 0) + "\n"
										+ formatPrices(amiibo.getDouble("average_listed_this_month_uk_used"), 1));

				slashCommandInteraction.createImmediateResponder().addEmbed(embed).respond();
			}
		});

		api.addAutocompleteCreateListener(event -> {
			// System.out.println("Type = " +
			// event.getAutocompleteInteraction().getArguments().get(0).getStringValue().get());

			switch (event.getAutocompleteInteraction().getFocusedOption().getName()) {
			case "type":
				//System.out.println("Typing in 'Type'");
				event.getAutocompleteInteraction()
						.respondWithChoices(arrayToSlashCommandOptionChoiceList(AAssist.getTypeList()))
						.exceptionally(ExceptionLogger.get());
				break;

			case "series":
				//System.out.println("Typing in 'Series'");
				event.getAutocompleteInteraction()
						.respondWithChoices(arrayToSlashCommandOptionChoiceList(AAssist.getSeriesList(
								event.getAutocompleteInteraction().getArguments().get(0).getStringValue().get())))
						.exceptionally(ExceptionLogger.get());
				break;

			case "name":
				//System.out.println("Typing in 'Name'");
				event.getAutocompleteInteraction()
						.respondWithChoices(arrayToSlashCommandOptionChoiceList(AAssist.getNameList(
								event.getAutocompleteInteraction().getArguments().get(0).getStringValue().get(),
								event.getAutocompleteInteraction().getArguments().get(1).getStringValue().get(),
								event.getAutocompleteInteraction().getArguments().get(2).getStringValue().get())))
						.exceptionally(ExceptionLogger.get());
				break;
			}
		});
	}

	private String formatPrices(double price, int country) {
		String[] currency = { "$", "£" };
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		if (price == 0) {
			return "*Lack of Data*";
		}
		return currency[country] + twoPlaces.format(price);
	}

	private List<SlashCommandOptionChoice> arrayToSlashCommandOptionChoiceList(ArrayList<String> list) {
		return IntStream.range(0, list.size()).mapToObj(i -> SlashCommandOptionChoice.create(list.get(i), list.get(i)))
				.collect(Collectors.toList());
	}
}
