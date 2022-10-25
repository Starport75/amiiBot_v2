package amiiBot_v2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.json.JSONObject;

public class AmiiboHuntAccess {

	String token;
	JSONObject baseList;;

	public AmiiboHuntAccess(String nToken) throws IOException {
		token = nToken;
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", "205877471067766784"));
		baseList = sendGET("https://www.amiibohunt.com/api/discord/v1/getCollectionById", parameters);

		// Output the base list
		// System.out.println(baseList.toString());

	}

	public AmiiboAssistant assist(JSONObject obj) {
		return new AmiiboAssistant(obj);
	}

	public AmiiboAssistant assist() {
		return new AmiiboAssistant(baseList);
	}

	public JSONObject getBaseList() {
		return baseList;
	}

	public JSONObject updateBaseList() throws IOException {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", "205877471067766784"));
		return baseList = sendGET("https://www.amiibohunt.com/api/discord/v1/getCollectionById", parameters);
	}

	public JSONObject getUserList(String discordID) throws IOException {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", discordID));
		return baseList = sendGET("https://www.amiibohunt.com/api/discord/v1/getCollectionById", parameters);
	}

	public JSONObject getAmiibo(int amiiboID) {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", "205877471067766784"));
		parameters.add(new BasicNameValuePair("amiibo_id", "" + amiiboID));
		JSONObject amiibo = null;
		try {
			amiibo = sendGET("https://www.amiibohunt.com/api/discord/v1/getAmiiboData", parameters);
		} catch (IOException e) {
			System.out.println("Failed to get amiibo data from AmiiboHunt");
			e.printStackTrace();
		}
		return amiibo;
	}

	public JSONObject getFigureImage(String ID) {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", ID));
		JSONObject output = null;
		try {
			output = sendGET("https://www.amiibohunt.com/api/discord/v1/getCollectionImageById", parameters);
		} catch (IOException e) {
			System.out.println("Failed to get figure collection image from AmiiboHunt");
			e.printStackTrace();
		}
		return output;
	}

	public JSONObject getCardImage(String ID, String series, String subset) {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", ID));
		parameters.add(new BasicNameValuePair("series", series));
		if (subset != null) {
			parameters.add(new BasicNameValuePair(subset, "true"));
		}
		JSONObject output = null;
		try {
			output = sendGET("https://www.amiibohunt.com/api/discord/v1/getCollectionImageCardSeries", parameters);
		} catch (IOException e) {
			System.out.println("Failed to get card collection image from amiiboHunt");
			e.printStackTrace();
		}
		return output;
	}

	public JSONObject addAmiibo(String ID, String amiiboID, boolean isBoxed){
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", ID));
		parameters.add(new BasicNameValuePair("is_boxed", isBoxed + ""));
		parameters.add(new BasicNameValuePair("amiibo_id", amiiboID));
		try {
			return sendGET("https://www.amiibohunt.com/api/discord/v1/addAmiiboToCollection", parameters);
		} catch (IOException e) {
			System.out.println("Failed to access AmiiboHunt");
			e.printStackTrace();
			return null;
		}
	}

	public JSONObject removeAmiibo(String ID, String amiiboID){
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("discord_id", ID));
		parameters.add(new BasicNameValuePair("amiibo_id", amiiboID));
		try {
			return sendGET("https://www.amiibohunt.com/api/discord/v1/removeAmiiboFromCollection", parameters);
		} catch (IOException e) {
			System.out.println("Failed to access AmiiboHunt");
			e.printStackTrace();
			return null;
		}
	}

	private JSONObject sendGET(String url, List<NameValuePair> params) throws IOException {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url + "?api_key=" + token);

		params.add(new BasicNameValuePair("api_key", token));

		httpPost.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse response = client.execute(httpPost);
		HttpEntity entity = response.getEntity();
		InputStream instream = entity.getContent();
		byte[] bytes = IOUtils.toByteArray(instream);
		String result = new String(bytes, "UTF-8");
		return new JSONObject(result);
	}
}