package com.navi.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.dao.HistoryDao;
import com.navi.model.History;
import com.navi.model.Path;

public class HistoryService {
	
	public boolean saveHistory(History history){
		HistoryDao historyDao = new HistoryDao();
		return historyDao.saveHistory(history);
	}
	
	public JSONArray getHistory(String history){
		HistoryDao historyDao = new HistoryDao();
		JSONArray arr = new JSONArray();
		List<History> historys = historyDao.getHistory(history);
		for (int i = 0; i < historys.size(); i++) {
			JSONObject obj = new JSONObject();
			History path = historys.get(i);
			try {
				obj.put("pointID", path.getPointID());
				obj.put("userID", path.getUserID());
				obj.put("time", path.getTime());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			arr.put(obj);
		}
		return arr;
	}

}
