package amiiBot_v2;

import java.io.IOException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {
	
	static boolean debugMode = true;
	
	public static void main(String[] args) throws IOException {		
		FileAccess file = new FileAccess(debugMode);
		AmiiboHuntAccess access = new AmiiboHuntAccess(file.getAmiiboHuntToken());
		String token = file.getDiscordToken();
		DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

		System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
		
		new CommandPing(api, access);
		new CommandShowInfo(api, access);
		}
}
