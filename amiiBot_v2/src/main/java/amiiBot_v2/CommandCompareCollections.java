package amiiBot_v2;

import java.awt.Color;
import java.io.IOException;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.Set;

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
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.interaction.SlashCommandUpdater;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommandCompareCollections {
	SlashCommand thisCommand;
	long consentID;
	InteractionOriginalResponseUpdater oldMessage;
	EmbedBuilder bigEmbed = new EmbedBuilder();

	// Spot to set the name and description of the command
	String commandName = "compare_collections";
	String commandDescription = "Generates a username tag that shows how many amiibo you have in your collection!";

	public CommandCompareCollections(DiscordApi api, AmiiboHuntAccess access) {
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
				.setSlashCommandOptions(Arrays.asList(
						SlashCommandOption.create(SlashCommandOptionType.USER, "user",
								"The other user you want to compare collections with", true),
						SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "series",
								"The series of cards you want to compare!", true,
								Arrays.asList(SlashCommandOptionChoice.create("Animal Crossing Series 1", "0"),
										SlashCommandOptionChoice.create("Animal Crossing Series 2", "1"),
										SlashCommandOptionChoice.create("Animal Crossing Series 3", "2"),
										SlashCommandOptionChoice.create("Animal Crossing Series 4", "3"),
										SlashCommandOptionChoice.create("Animal Crossing Series 5", "4"),
										SlashCommandOptionChoice.create("Animal Crossing: Welcome amiibo", "5"),
										SlashCommandOptionChoice.create("Mario Sports Superstars", "-1")))))
				// Spot to add any unique stuff to the command

				.updateGlobal(api).join();

		api.addSlashCommandCreateListener(event -> {
			SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
			
			if (slashCommandInteraction.getCommandName().equals(commandName)) {
				slashCommandInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
					JSONObject user1AmiiboJSON = null;
					JSONObject user2AmiiboJSON = null;
					String fullSeriesName = "";
					EmbedBuilder embed = new EmbedBuilder();
					
					try {
						user1AmiiboJSON = access.getUserList(event.getInteraction().getUser().getIdAsString());
						user2AmiiboJSON = access.getUserList(
								slashCommandInteraction.getArguments().get(0).getUserValue().get().getIdAsString());
					} catch (IOException e) {
						System.out.println("Error accessing AmiiboHunt");
					}
					
					String p1Name = event.getInteraction().getUser().getName();
					String p2Name = slashCommandInteraction.getArguments().get(0).getUserValue().get().getName();
					consentID = slashCommandInteraction.getArguments().get(0).getUserValue().get().getId();
					
					if (!user1AmiiboJSON.get("error").equals("none")) {
						embed.setColor(Color.red);
						switch (user1AmiiboJSON.get("error").toString()) {

						case "user profile is not public":
							embed.addField("Error:", p1Name
									+ "'s AmiiboHunt account is set to private! Click [here](https://www.amiibohunt.com/settings) to visit the account settings. From there, select 'Privacy Settings', and toggle 'Public Profile' on!");
							break;

						case "discord ID not found":
						case "invalid discord ID":
							embed.addField("Error:", p2Name
									+ " doesn't have an AmiiboHunt account linked to their Discord account! Click [here](https://www.amiibohunt.com/oauth/discord/redirect) to link and/or create an account!");
							break;
						}
					}

					else if (!user2AmiiboJSON.get("error").equals("none")) {
						embed.setColor(Color.red);
						switch (user2AmiiboJSON.get("error").toString()) {

						case "user profile is not public":
							embed.addField("Error:", p2Name
									+ "'s AmiiboHunt account is set to private! Click [here](https://www.amiibohunt.com/settings) to visit the account settings. From there, select 'Privacy Settings', and toggle 'Public Profile' on!");
							break;

						case "discord ID not found":
						case "invalid discord ID":
							embed.addField("Error:", p2Name
									+ " doesn't have an AmiiboHunt account linked to their Discord account! Click [here](https://www.amiibohunt.com/oauth/discord/redirect) to link and/or create an account!");
							break;
						}
					} else {
						
						if (oldMessage != null) {
							oldMessage.removeAllComponents().update();
						}

						JSONArray P1amiiboArray = user1AmiiboJSON.getJSONArray("amiibo");
						JSONArray P2amiiboArray = user2AmiiboJSON.getJSONArray("amiibo");

						// System.out.println("P1 = " + p1Name + " & P2 = " + p2Name);

						int seriesInt = Integer
								.valueOf(slashCommandInteraction.getArguments().get(1).getStringValue().get());
						String seriesName = "Animal Crossing";

						if (seriesInt == -1) {
							seriesInt = 1;
							seriesName = "Mario Sports Superstars";
							fullSeriesName = seriesName;
						} else if (seriesInt == 5) {
							fullSeriesName = "Animal Crossing: Welcome amiibo";
						} else {
							fullSeriesName = "Animal Crossing Series " + (seriesInt + 1);
						}

						String p1Array[] = new String[100];
						String p2Array[] = new String[100];
						Arrays.fill(p1Array, "");
						Arrays.fill(p2Array, "");

						for (int i = 0; i < P1amiiboArray.length(); i++) {
							if (P1amiiboArray.getJSONObject(i).getJSONObject("type").getString("type").equals("Card")
									&& P1amiiboArray.getJSONObject(i).getJSONObject("game_series").get("name")
											.equals(seriesName)) {
								int amiiboNumber = P1amiiboArray.getJSONObject(i).getJSONObject("card_number")
										.getInt("animal_crossing_number");
								if (amiiboNumber > (seriesInt * 100) && amiiboNumber <= ((seriesInt + 1) * 100)) {
									if (P1amiiboArray.getJSONObject(i).getJSONArray("owned_data").length() > 1
											&& P2amiiboArray.getJSONObject(i).getJSONArray("owned_data")
													.length() == 0) {
										p2Array[P1amiiboArray.getJSONObject(i).getJSONObject("card_number")
												.getInt("animal_crossing_number") - ((seriesInt * 100) + 1)] = "#"
														+ P1amiiboArray.getJSONObject(i).getJSONObject("card_number")
																.getInt("animal_crossing_number")
														+ " " + P1amiiboArray.getJSONObject(i).getString("name") + "\n";
									}

									if (P2amiiboArray.getJSONObject(i).getJSONArray("owned_data").length() > 1
											&& P1amiiboArray.getJSONObject(i).getJSONArray("owned_data")
													.length() == 0) {
										p1Array[P1amiiboArray.getJSONObject(i).getJSONObject("card_number")
												.getInt("animal_crossing_number") - ((seriesInt * 100) + 1)] = "#"
														+ P1amiiboArray.getJSONObject(i).getJSONObject("card_number")
																.getInt("animal_crossing_number")
														+ " " + P1amiiboArray.getJSONObject(i).getString("name") + "\n";

									}
								}
							}
						}

						String p1Out = "";
						String p2Out = "";
						int p1Size = 0;
						int p2Size = 0;

						for (int i = 0; i < 100; i++) {
							if (!p1Array[i].equals("")) {
								p1Out = p1Out + p1Array[i];
								p1Size++;
							}
							if (!p2Array[i].equals("")) {
								p2Out = p2Out + p2Array[i];
								p2Size++;
							}
						}

						// System.out.println("P1: " + p1Out);
						// System.out.println("P2: " + p2Out);

						String p1Output = fullSeriesName + " amiibo that *" + p1Name + "* is missing that *" + p2Name
								+ "* has duplicates of:\n";

						String p2Output = fullSeriesName + " amiibo that *" + p2Name + "* is missing that *" + p1Name
								+ "* has duplicates of:\n";

						if (p1Size == 0) {
							p1Out = "There are no amiibo in " + fullSeriesName + " that **" + p2Name
									+ "** has duplicates of that **" + p1Name + "** is missing.";
						}
						if (p2Size == 0) {
							p2Out = "There are no amiibo in " + fullSeriesName + " that **" + p1Name
									+ "** has duplicates of that **" + p2Name + "** is missing.";
						}

						bigEmbed.removeAllFields();
						bigEmbed.addField(p1Output, p1Out).addField(p2Output, p2Out).setColor(Color.gray);

						embed = new EmbedBuilder().addField("Inquiry:", "Hello **" + p2Name + "**! **" + p1Name
								+ "** would like to compare cards from " + fullSeriesName
								+ " with you... Do you agree to this? If you do not, you may simply ignore this message.")
								.setColor(Color.gray);
						interactionOriginalResponseUpdater
								.addComponents(ActionRow.of(Button.success("consent", "I agree!")));
						oldMessage = interactionOriginalResponseUpdater;
					}

					interactionOriginalResponseUpdater.addEmbed(embed).update().exceptionally(ExceptionLogger.get());
				});
			}
		});
		api.addMessageComponentCreateListener(event -> {
			MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
			String customId = messageComponentInteraction.getCustomId();
			switch (customId) {
			case "consent":

				if (event.getInteraction().getUser().getId() == consentID) {
					messageComponentInteraction.createImmediateResponder().addEmbed(bigEmbed).respond()
							.exceptionally(ExceptionLogger.get());
					oldMessage.removeAllComponents().update();
				} else {
					EmbedBuilder notYouEmbed = new EmbedBuilder().setColor(Color.red).addField("Error:",
							"You're not the user the button is looking for!");
					messageComponentInteraction.createImmediateResponder().addEmbed(notYouEmbed)
							.setFlags(MessageFlag.EPHEMERAL).respond().exceptionally(ExceptionLogger.get());
				}

				break;
			}
		});
	}
}
