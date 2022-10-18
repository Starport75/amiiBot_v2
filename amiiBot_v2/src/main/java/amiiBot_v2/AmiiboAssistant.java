package amiiBot_v2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class AmiiboAssistant {
	JSONObject fullJSON;
	JSONArray amiiboList;
	JSONObject tsmList = new JSONObject();

	public AmiiboAssistant(JSONObject nFullJSON) {
		String curType;
		String curSeries;
		String curName;
		
		fullJSON = nFullJSON;
		amiiboList = fullJSON.getJSONArray("amiibo");
		for (int i = 0; i < amiiboList.length(); i++) {
			curType = amiiboList.getJSONObject(i).getJSONObject("type").getString("type");
			curSeries = amiiboList.getJSONObject(i).getJSONObject("amiibo_series").getString("name");
			curName = amiiboList.getJSONObject(i).getString("name");
			if (tsmList.keySet().contains(curType) == false) {
				tsmList.put(curType, new JSONObject());
			}
			if (tsmList.getJSONObject(curType).keySet().contains(curSeries) == false) {
				tsmList.getJSONObject(curType).put(curSeries, new JSONObject());
			}
			tsmList.getJSONObject(curType).getJSONObject(curSeries).put(curName, amiiboList.getJSONObject(i).get("amiibo_id"));
		}
	}

	public int findAmiiboID(String type, String series, String name) {
		System.out.println("amiibo ID: " + tsmList.getJSONObject(type).getJSONObject(series).getInt(name));
		return tsmList.getJSONObject(type).getJSONObject(series).getInt(name);
	}

	public ArrayList<String> getTypeList() {
		return new ArrayList<String>(tsmList.keySet());
	}

	public ArrayList<String> getSeriesList(String type) {
		System.out.println(tsmList.getJSONObject(type).keySet());
		return new ArrayList<String>(tsmList.getJSONObject(type).keySet());
	}

	public ArrayList<String> getNameList(String type, String series) {
		return new ArrayList<String>(tsmList.getJSONObject(type).getJSONObject(series).keySet());
	}
}
