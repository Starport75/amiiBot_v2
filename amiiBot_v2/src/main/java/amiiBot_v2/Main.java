package amiiBot_v2;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	
	static boolean debugMode = true;
	
	public static void main(String[] args) {		
		FileAccess file = new FileAccess(debugMode);
		String token = file.getDiscordToken();
		DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

		System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
		
		new CommandPing(api);
		}
}
