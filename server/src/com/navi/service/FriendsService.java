package com.navi.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.constant.Config;
import com.navi.dao.FriendsDao;
import com.navi.dao.InfoDao;
import com.navi.model.Friends;
import com.navi.model.Info;

public class FriendsService {
	public boolean requireFriend(Friends name){
		JSONArray arr = new JSONArray();
		new FriendsDao().saveFriend(name);
		return true;
	}
	public boolean setState(Friends history){
		FriendsDao friendsDao = new FriendsDao();
		if(history.getState()==Config.FAIl){
			friendsDao.deleteFriend(history);
		}else{
			friendsDao.saveState(history);
			new MomentsService().setReciver(history.getReciver());
		}
		return true;
	}
	public JSONArray downloadFriends(String name){
		JSONArray arr = new JSONArray();
		List<Friends> infos = new FriendsDao().downloadFriends(name);
		for (int i = 0; i < infos.size(); i++) {
			JSONObject obj = new JSONObject();
			Friends path = infos.get(i);
			try {
				obj.put("sendUser", path.getSend());
				obj.put("reciver", path.getReciver());
				obj.put("state", path.getState());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			arr.put(obj);
		}
		return arr;
	}
	public JSONArray downloadAllFriends(String name){
		JSONArray arr = new JSONArray();
		List<Friends> infos = new FriendsDao().downloadAllFriends(name);
		for (int i = 0; i < infos.size(); i++) {
			JSONObject obj = new JSONObject();
			Friends path = infos.get(i);
			try {
				obj.put("sendUser", path.getSend());
				obj.put("reciver", path.getReciver());
				obj.put("state", path.getState());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			arr.put(obj);
		}
		return arr;
	}
}
