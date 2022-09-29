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
import org.json.JSONObject;

public class AmiiboHuntAccess {

	String token;
	JSONObject baseList;
;

	public AmiiboHuntAccess(String nToken) throws IOException {
		token = nToken;
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		baseList = sendGET("https://www.amiibohunt.com/api/discord/v1/getCollectionById", parameters);
		
		//Output the base list
		//System.out.println(baseList.toString());
		
	}
	
	public JSONObject getBaseList() {
		return baseList;
	}
	
	public JSONObject getAmiibo(int amiiboID) {
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("amiibo_id", "1"));
		JSONObject amiibo = null;
		try {
			amiibo = sendGET("https://www.amiibohunt.com/api/discord/v1/getAmiiboData", parameters);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("amiibo: " + amiibo);
		return amiibo;
	}
	
	private JSONObject sendGET(String url, List<NameValuePair> params) throws IOException {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url + "?api_key=" + token);

		params.add(new BasicNameValuePair("api_key", token));
		params.add(new BasicNameValuePair("discord_id", "205877471067766784"));

		httpPost.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse response = client.execute(httpPost);
		HttpEntity entity = response.getEntity();
		InputStream instream = entity.getContent();
		byte[] bytes = IOUtils.toByteArray(instream);
		String result = new String(bytes, "UTF-8");
		return new JSONObject(result);
	}
}