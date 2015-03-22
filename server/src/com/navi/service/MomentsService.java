package com.navi.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.dao.InfoDao;
import com.navi.dao.MomentsDao;
import com.navi.model.Info;
import com.navi.model.Moments;

public class MomentsService {
	public JSONArray downloadMoments(String name,String first,String end){
		MomentsDao infoDao = new MomentsDao();
		JSONArray arr = new JSONArray();
		List<Moments> infos = infoDao.getMoments(name,first,end);
		for (int i = 0; i < infos.size(); i++) {
			JSONObject obj = new JSONObject();
			Moments path = infos.get(i);
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
	public boolean saveMoments(Moments history){
		MomentsDao historyDao = new MomentsDao();
		return historyDao.saveMoments(history);
	}
	public boolean setReciver(String name){
		new MomentsDao().setReciver(name);
		return true;
	}
}
