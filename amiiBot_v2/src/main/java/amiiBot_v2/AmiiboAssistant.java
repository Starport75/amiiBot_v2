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
			tsmList.getJSONObject(curType).getJSONObject(curSeries).put(curName,
					amiiboList.getJSONObject(i).get("amiibo_id"));
		}
	}

	public int findAmiiboID(String type, String series, String name) {
		if (tsmList.keySet().contains(type) && tsmList.getJSONObject(type).keySet().contains(series)
				&& tsmList.getJSONObject(type).getJSONObject(series).keySet().contains(name)) {
			System.out.println("amiibo ID: " + tsmList.getJSONObject(type).getJSONObject(series).getInt(name));
			return tsmList.getJSONObject(type).getJSONObject(series).getInt(name);
		} else {
			return -1;
		}
	}

	public ArrayList<String> getTypeList(String filter) {
		ArrayList<String> typeList = new ArrayList<String>();
		ArrayList<String> keySet = new ArrayList<String>(tsmList.keySet());
		for (int i = 0; i < tsmList.keySet().size(); i++) {
			if (keySet.get(i).toLowerCase().contains(filter.toLowerCase())) {
				typeList.add(keySet.get(i));
			}
			if (typeList.size() == 25) {
				return typeList;
			}
		}
		return typeList;
	}

	public ArrayList<String> getSeriesList(String type, String filter) {
		ArrayList<String> seriesList = new ArrayList<String>();
		if (tsmList.keySet().contains(type)) {
			ArrayList<String> keySet = new ArrayList<String>(tsmList.getJSONObject(type).keySet());
			for (int i = 0; i < tsmList.getJSONObject(type).keySet().size(); i++) {
				if (keySet.get(i).toLowerCase().contains(filter.toLowerCase())) {
					seriesList.add(keySet.get(i));
				}
				if (seriesList.size() == 25) {
					return seriesList;
				}
			}
		}
		return seriesList;
	}

	public ArrayList<String> getNameList(String type, String series, String filter) {
		ArrayList<String> nameList = new ArrayList<String>();
		if (tsmList.keySet().contains(type) && tsmList.getJSONObject(type).keySet().contains(series)) {
			ArrayList<String> keySet = new ArrayList<String>(
					tsmList.getJSONObject(type).getJSONObject(series).keySet());
			for (int i = 0; i < tsmList.getJSONObject(type).getJSONObject(series).keySet().size(); i++) {
				if (keySet.get(i).toLowerCase().contains(filter.toLowerCase())) {
					nameList.add(keySet.get(i));
				}
				if (nameList.size() == 25) {
					return nameList;
				}
			}
		}
		return nameList;
	}
}
