package amiiBot_v2;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	
	static boolean debugMode = true;
	
	public static void main(String[] args) {
		// Insert your bot's token here
		
		FileAccess file = new FileAccess(debugMode);
		String token = file.getDiscordToken();

		DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

		// Add a listener which answers with "Pong!" if someone writes "!ping"
		api.addMessageCreateListener(event -> {
			if (event.getMessageContent().equalsIgnoreCase("!ping")) {
				event.getChannel().sendMessage("Pong!");
			}
		});

		// Print the invite url of your bot
		System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
	}
}
