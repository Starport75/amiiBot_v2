package amiiBot_v2;

import java.io.IOException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Main {

	static boolean debugMode = true;

	public static void main(String[] args) throws IOException {

		final FileAccess file = new FileAccess(debugMode);
		final String token = file.getDiscordToken();
		final DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
		AmiiboHuntAccess access = new AmiiboHuntAccess(file.getAmiiboHuntToken());

		System.out.print("Loading commands");
		
		new CommandPing(api);
		System.out.print(".");
		new CommandShowInfo(api, access);
		System.out.print(".");
		new CommandGenerateImage(api, access);
		System.out.print(".");
		new CommandUpdateData(api, access);
		System.out.print(".");
		new CommandCredits(api);
		System.out.print(".");
		new CommandUpdateUsername(api, access);
		System.out.println(" Done!");

		System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());

	}
}
