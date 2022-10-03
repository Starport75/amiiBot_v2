package amiiBot_v2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class AmiiboAssistant {
	JSONObject fullJSON;
	JSONArray amiiboList;

	public AmiiboAssistant(JSONObject nFullJSON) {
		fullJSON = nFullJSON;
		amiiboList = fullJSON.getJSONArray("amiibo");
	}

	public int findAmiiboID(String type, String series, String name) {
		for (int i = 0; i < amiiboList.length(); i++) {
			System.out.println(amiiboList.getJSONObject(i).getJSONObject("type").getString("type") +
			amiiboList.getJSONObject(i).getJSONObject("amiibo_series").getString("name") == series +
			amiiboList.getJSONObject(i).getString("name"));
			if (amiiboList.getJSONObject(i).getJSONObject("type").getString("type") == type
					&& amiiboList.getJSONObject(i).getJSONObject("amiibo_series").getString("name") == series
					&& amiiboList.getJSONObject(i).getString("name") == name) {
				return i;
			}
		}
		return 0;
	}

	public ArrayList<String> getTypeList() {
		ArrayList<String> typeList = new ArrayList<String>();
		String currType = null;
		for (int i = 0; i < amiiboList.length(); i++) 
			currType = amiiboList.getJSONObject(i).getJSONObject("type").getString("type");
			if (!typeList.contains(currType)){
				typeList.add(currType);
		}

	}

	public String[] getSeriesList() {
		
	}

	public String[] getNameList() {

	}
}
