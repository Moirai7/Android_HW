package com.navi.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.constant.Config;
import com.navi.dao.PathDao;
import com.navi.dao.UserDao;
import com.navi.model.User;

public class UserService {
	//处理登录
	public boolean login(String username, String password){
		UserDao UserDao = new UserDao();
		User user = UserDao.findByUsernameAndPassword(username, password);
		if(user == null){
			return false;
		}else{
			UserDao.setState(Config.USER_STATE_ONLINE, username);
			return true;
		}
	}
	//处理注册
	public boolean register(User user){
		UserDao UserDao = new UserDao();
		if(UserDao.findByUsernameAndPassword(user.getUsername(), user.getPassword()) == null){
			UserDao.save(user);
			
			return true;
		}else{
			return false;
		}
	}
	//设置用户的状态为不在�?
	public void setStateToNonOnline(String username){
		UserDao UserDao = new UserDao();
		UserDao.setState(Config.USER_STATE_NON_ONLINE, username);
	}
	//设置用户的状态为在线
	public void setStateToOnline(String username){
		UserDao UserDao = new UserDao();
		UserDao.setState(Config.USER_STATE_ONLINE, username);
	}
	//获取用户的状�?
	public int getUsernameState(String username){
		UserDao UserDao = new UserDao();
		return UserDao.getState(username);
	}
	
	
	public JSONArray sendRequest(String username,String point) {
		JSONArray arr = new PathService().getPathInfo(point);
		JSONObject obj = new JSONObject();
		try {
			obj.put("detail", new UserDao().getDetail(username));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		arr.put(obj);
		return arr;
	}
	
	public void setRequestInfo(String username,String recevier,String detail){
		UserDao userdao = new UserDao();
		userdao.setInfo(username, recevier, detail);
	}
}
