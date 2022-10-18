package amiiBot_v2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class AmiiboAssistant {
	JSONObject fullJSON;
	JSONArray amiiboList;
	JSONObject tsnList = new JSONObject();

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
			if (tsnList.keySet().contains(curType) == false) {
				tsnList.put(curType, new JSONObject());
			}
			if (tsnList.getJSONObject(curType).keySet().contains(curSeries) == false) {
				tsnList.getJSONObject(curType).put(curSeries, new JSONObject());
			}
			tsnList.getJSONObject(curType).getJSONObject(curSeries).put(curName,
					amiiboList.getJSONObject(i).get("amiibo_id"));
		}
	}

	public int findAmiiboID(String type, String series, String name) {
		if (tsnList.keySet().contains(type) && tsnList.getJSONObject(type).keySet().contains(series)
				&& tsnList.getJSONObject(type).getJSONObject(series).keySet().contains(name)) {
			//System.out.println("amiibo ID: " + tsnList.getJSONObject(type).getJSONObject(series).getInt(name));
			return tsnList.getJSONObject(type).getJSONObject(series).getInt(name);
		} else {
			return -1;
		}
	}

	public ArrayList<String> getTypeList(String filter) {
		ArrayList<String> typeList = new ArrayList<String>();
		ArrayList<String> keySet = new ArrayList<String>(tsnList.keySet());
		for (int i = 0; i < tsnList.keySet().size(); i++) {
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
		if (tsnList.keySet().contains(type)) {
			ArrayList<String> keySet = new ArrayList<String>(tsnList.getJSONObject(type).keySet());
			for (int i = 0; i < tsnList.getJSONObject(type).keySet().size(); i++) {
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
		if (tsnList.keySet().contains(type) && tsnList.getJSONObject(type).keySet().contains(series)) {
			ArrayList<String> keySet = new ArrayList<String>(
					tsnList.getJSONObject(type).getJSONObject(series).keySet());
			for (int i = 0; i < tsnList.getJSONObject(type).getJSONObject(series).keySet().size(); i++) {
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
