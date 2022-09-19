package amiiBot_v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileAccess {
	
	String discordToken;
	String amiiboHuntToken;
	
	public FileAccess(boolean debugMode){
		
		try (InputStream inputStream = getClass().getResourceAsStream("/tokens.txt");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			    String contents = reader.lines().collect(Collectors.joining(System.lineSeparator()));
			    String[] split = contents.split("-");
			    if (debugMode) {
			    	discordToken = split[1];
			    } else {
			    	discordToken = split[0];
			    }
			    amiiboHuntToken = split[2];
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public String getDiscordToken() {
		return discordToken;
	}
	
	public String getAmiiboHuntToken() {
		return amiiboHuntToken;
	}
}
