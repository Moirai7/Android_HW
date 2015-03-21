package com.navi.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.dao.PathDao;
import com.navi.model.Path;

public class PathService {
	
	public JSONArray download() {
		List<Path> pathDao = new PathDao().getPaths();
		JSONArray arr = new JSONArray();
		for (int i = 0; i < pathDao.size(); i++) {
			JSONObject obj = new JSONObject();
			Path path = pathDao.get(i);
			try {
				obj.put("pointID", path.getPointID());
				obj.put("streetID", path.getStreetID());
				obj.put("pointname", path.getPointname());
				obj.put("pointSurroundingInfo", path.getPointSurroundingInfo());
				obj.put("pointSurroundingStreet",
						path.getPointSurroundingStreet());
				obj.put("pointLongitude", path.getPointLongitude());
				obj.put("pointLatitude", path.getPointLatitude());
				obj.put("type", path.getType());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			arr.put(obj);
		}
		return arr;
	}

	public JSONArray getPathInfo(String pointid) {
		Path path = new PathDao().getPathInfo(pointid);
		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();
		try {
			obj.put("pointID", path.getPointID());
			obj.put("streetID", path.getStreetID());
			obj.put("pointname", path.getPointname());
			obj.put("pointSurroundingInfo", path.getPointSurroundingInfo());
			obj.put("pointSurroundingStreet", path.getPointSurroundingStreet());
			obj.put("pointLongitude", path.getPointLongitude());
			obj.put("pointLatitude", path.getPointLatitude());
			obj.put("type", path.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		arr.put(obj);

		return arr;
	}
}
