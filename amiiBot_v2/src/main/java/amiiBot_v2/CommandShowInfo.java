package amiiBot_v2;

import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.JSONObject;

public class CommandShowInfo {
	SlashCommand thisCommand;
	String savedAmiiboID;
	InteractionOriginalResponseUpdater oldMessage;

	// Spot to set the name and description of the command
	String commandName = "show_info";
	String commandDescription = "Returns information on the given amiibo!";

	public CommandShowInfo(DiscordApi api, AmiiboHuntAccess amiiboData) {
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
				// Spot to add any unique stuff to the command

				.setSlashCommandOptions(Arrays.asList(
						SlashCommandOption.createStringOption("type", "The type of the amiibo you're looking for", true,
								true),
						SlashCommandOption.createStringOption("series", "The series of the amiibo you're looking for",
								true, true),
						SlashCommandOption.createStringOption("name", "The name of the amiibo you're looking for", true,
								true)))
				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			if (slashCommandInteraction.getCommandName().equals(commandName)) {

				// Spot for command response

				int amiiboID = amiiboData.assist().findAmiiboID(
						slashCommandInteraction.getArguments().get(0).getStringValue().get(),
						slashCommandInteraction.getArguments().get(1).getStringValue().get(),
						slashCommandInteraction.getArguments().get(2).getStringValue().get());
				if (amiiboID == -1) {
					EmbedBuilder embed = new EmbedBuilder()
							.addField("Error:",
									"Invalid amiibo! Please correct your type, series, and name and try again.")
							.setColor(Color.red);
					slashCommandInteraction.createImmediateResponder().addEmbed(embed).setFlags(MessageFlag.EPHEMERAL)
							.respond();
				} else {
					if (oldMessage != null) {
						oldMessage.removeAllComponents().update();
					}
					savedAmiiboID = amiiboID + "";
					JSONObject amiibo = amiiboData.getAmiibo(amiiboID).getJSONObject("amiibo");
					// System.out.println(amiibo.toString());
					EmbedBuilder embed = new EmbedBuilder().setTitle(amiibo.getString("name"))
							.setImage(amiibo.getString("image_imgix_full_card"))
							.addField("Release Dates:",
									"🇯🇵: " + formatDates(amiibo.get("release_jp")) + "\n🇺🇸: "
											+ formatDates(amiibo.get("release_na")) + "\n🇪🇺: "
											+ formatDates(amiibo.get("release_eu")) + "\n🇦🇺: "
											+ formatDates(amiibo.get("release_au")))
							// .addField("**Retailers with Stock**", "stock data here")
							.setColor(new Color(
									Integer.parseInt(formatColor(amiibo.get("background_color")).substring(1), 16)))
							.addField("\u200b",
									"**Average Current Listed Prices** \n *prices are purely an estimate based on collected data*")
							.addInlineField("Average Price NiB",
									formatPrices(amiibo.getDouble("average_listed_this_month_us_new"), 0) + "\n"
											+ formatPrices(amiibo.getDouble("average_listed_this_month_uk_new"), 1))
							.addInlineField("Average Price OoB",
									formatPrices(amiibo.getDouble("average_listed_this_month_us_used"), 0) + "\n"
											+ formatPrices(amiibo.getDouble("average_listed_this_month_uk_used"), 1));

					slashCommandInteraction.createImmediateResponder().addEmbed(embed)
							/*
							 * .addComponents( ActionRow.of(Button.primary("addNiB",
							 * "Add one to collection In Box)"), Button.success("addOoB",
							 * "Add one to collection (Out of Box)")), ActionRow.of(Button.danger("remove",
							 * "Remove one from collection")))
							 */
							.respond();
				}
			}
		});

		api.addAutocompleteCreateListener(event -> {
			// System.out.println("Type = " +
			// event.getAutocompleteInteraction().getArguments().get(0).getStringValue().get());

			switch (event.getAutocompleteInteraction().getFocusedOption().getName()) {
			case "type":
				// System.out.println("Typing in 'Type'");
				event.getAutocompleteInteraction()
						.respondWithChoices(arrayToSlashCommandOptionChoiceList(amiiboData.assist().getTypeList(
								event.getAutocompleteInteraction().getArguments().get(0).getStringValue().get())))
						.exceptionally(ExceptionLogger.get());
				break;

			case "series":
				// System.out.println("Typing in 'Series'");
				event.getAutocompleteInteraction()
						.respondWithChoices(arrayToSlashCommandOptionChoiceList(amiiboData.assist().getSeriesList(
								event.getAutocompleteInteraction().getArguments().get(0).getStringValue().get(),
								event.getAutocompleteInteraction().getArguments().get(1).getStringValue().get())))
						.exceptionally(ExceptionLogger.get());
				break;

			case "name":
				// System.out.println("Typing in 'Name'");
				event.getAutocompleteInteraction()
						.respondWithChoices(arrayToSlashCommandOptionChoiceList(amiiboData.assist().getNameList(
								event.getAutocompleteInteraction().getArguments().get(0).getStringValue().get(),
								event.getAutocompleteInteraction().getArguments().get(1).getStringValue().get(),
								event.getAutocompleteInteraction().getArguments().get(2).getStringValue().get())))
						.exceptionally(ExceptionLogger.get());
				break;
			}
		});
		/*
		 * api.addMessageComponentCreateListener(event -> { MessageComponentInteraction
		 * messageComponentInteraction = event.getMessageComponentInteraction();
		 * messageComponentInteraction.respondLater().thenAccept(
		 * interactionOriginalResponseUpdater -> { String customId =
		 * messageComponentInteraction.getCustomId();
		 * 
		 * EmbedBuilder embed = new EmbedBuilder();
		 * 
		 * JSONObject data = null; try { data =
		 * amiiboData.getUserList(event.getInteraction().getUser().getIdAsString()); }
		 * catch (IOException e) { System.out.println("Failed to access AmiiboHunt");
		 * e.printStackTrace(); }
		 * 
		 * if (!data.get("error").equals("none")) { embed.setColor(Color.red); switch
		 * (data.get("error").toString()) {
		 * 
		 * case "user profile is not public": embed.addField("Error:",
		 * "Your AmiiboHunt account is set to private! Click [here](https://www.amiibohunt.com/settings) to visit your account settings. From there, select 'Privacy Settings', and toggle 'Public Profile' on!"
		 * ); break;
		 * 
		 * case "discord ID not found": case "invalid discord ID":
		 * embed.addField("Error:",
		 * "You don't have an AmiiboHunt account linked to your Discord account! Click [here](https://www.amiibohunt.com/oauth/discord/redirect) to link and/or create your account!"
		 * ); break; }
		 * interactionOriginalResponseUpdater.addEmbed(embed).setFlags(MessageFlag.
		 * EPHEMERAL).update() .exceptionally(ExceptionLogger.get()); } else {
		 * JSONObject returned = null; String username =
		 * event.getInteraction().getUser().getName();
		 * 
		 * switch (customId) {
		 * 
		 * case "addNiB":
		 * 
		 * returned =
		 * amiiboData.addAmiibo(event.getInteraction().getUser().getIdAsString(),
		 * savedAmiiboID, true); embed.addField("amiibo added!", username + " now has "
		 * + returned.getInt("qty_owned") + " in box " +
		 * returned.getString("amiibo_name") + " amiibo in their collection!"); break;
		 * 
		 * case "addOoB":
		 * 
		 * returned =
		 * amiiboData.addAmiibo(event.getInteraction().getUser().getIdAsString(),
		 * savedAmiiboID, false); embed.addField("amiibo added! ", username +
		 * " now has " + returned.getInt("qty_owned") + " out of box " +
		 * returned.getString("amiibo_name") + " amiibo in their collection!"); break;
		 * 
		 * case "remove":
		 * 
		 * returned =
		 * amiiboData.removeAmiibo(event.getInteraction().getUser().getIdAsString(),
		 * savedAmiiboID); embed.addField("amiibo removed!", "Removed a single " +
		 * returned.getString("amiibo_name") + " amiibo from " + username +
		 * "'s collection!"); break; }
		 * 
		 * embed.setColor(Color.gray);
		 * interactionOriginalResponseUpdater.addEmbed(embed).setFlags(MessageFlag.
		 * EPHEMERAL).update() .exceptionally(ExceptionLogger.get()); } }); });
		 */
	}

	private String formatPrices(double price, int country) {
		String[] currency = { "$", "£" };
		DecimalFormat twoPlaces = new DecimalFormat("0.00");
		if (price == 0) {
			return "*Lack of Data*";
		}
		return currency[country] + twoPlaces.format(price);
	}

	private String formatDates(Object input) {
		if (input.toString() == "null") {
			return "N/A";
		}
		return input + "";
	}

	private String formatColor(Object input) {
		if (input.toString() == "null") {
			return "#FFFFFF";
		}
		return input + "";
	}

	private List<SlashCommandOptionChoice> arrayToSlashCommandOptionChoiceList(ArrayList<String> list) {
		return IntStream.range(0, list.size()).mapToObj(i -> SlashCommandOptionChoice.create(list.get(i), list.get(i)))
				.collect(Collectors.toList());
	}
}
