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
	JSONObject baseList = sendGET("https://www.amiibohunt.com/api/discord/v1/getAmiiboData", "205877471067766784")
;

	public AmiiboHuntAccess(String nToken) throws IOException {
		token = nToken;
		System.out.println(nToken);
		System.out.println(baseList.toString());
		
	}
	
	public JSONObject getBaseList() {
		return baseList;
	}
	
	private JSONObject sendGET(String url, String discordID) throws IOException {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
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