package com.navi.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.dao.InfoDao;
import com.navi.model.Info;

public class InfoService {
	public JSONArray downloadNews(String name){
		InfoDao infoDao = new InfoDao();
		JSONArray arr = new JSONArray();
		List<Info> infos = infoDao.getNews(name);
		for (int i = 0; i < infos.size(); i++) {
			JSONObject obj = new JSONObject();
			Info path = infos.get(i);
			try {
				obj.put("sendUser", path.getSendUser());
				obj.put("detail", path.getDetail());
				obj.put("time", path.getTime());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			arr.put(obj);
		}
		return arr;
	}
	public boolean uploadNews(Info history){
		InfoDao historyDao = new InfoDao();
		return historyDao.saveHistory(history);
	}
}
