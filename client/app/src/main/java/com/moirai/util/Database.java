package com.moirai.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import com.moirai.view.R;
import com.moirai.client.Constant;
import com.moirai.model.Path;
import com.moirai.model.User;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class Database {
	private Context context;
	private SQLiteDatabase db;
	private static Database database = null;

	public void closeDatabase() {
		context = null;
		database = null;
		db.close();
	}

	public static Database getInstance(Context context) {
		if (database == null)
			return new Database(context);
		else
			return database;
	}

	public boolean setReceiverAndDetail(String receiver, String detail) {
		ContentValues values = new ContentValues();
		values.put("receiver", receiver);
		values.put("detail", detail);
		db.update("UserInfo", values, "username=?",
				new String[] { Constant.userName });
		return true;
	}

	public boolean getReceiverAndDetail() {
		return getUserInfo();
	}

	public boolean getUserInfo() {
		Cursor rs = db.rawQuery("select * from UserInfo", null);
		if (rs != null) {
			if (rs.moveToFirst()) {
				Constant.userName = rs.getString(rs.getColumnIndex("username"));
				Constant.userPassword = rs.getString(rs
						.getColumnIndex("password"));
				Constant.receiver = rs.getString(rs.getColumnIndex("receiver"));
				Constant.detail = rs.getString(rs.getColumnIndex("detail"));
			}
			return true;
		}
		return false;
	}

	public boolean setUserInfo(String username, String password) {
		Cursor rs = db.rawQuery("select * from UserInfo", null);
		if (rs.getCount() != 0) {
			db.execSQL("DELETE FROM UserInfo");
		}
		ContentValues values = new ContentValues();
		values.put("username", username);
		values.put("password", password);
		values.put("receiver", "0");
		values.put("detail", "0");
		db.insert("UserInfo", "username", values);
		return true;
	}

	public boolean writePaths(List<Path> list) {
		db.execSQL("DELETE FROM PathInfo");
		for (int i = 0; i < list.size(); i++) {
			ContentValues values = new ContentValues();
			Path path = list.get(i);
			values.put("pointID", path.getPointID());
			values.put("streetID", path.getStreetID());
			values.put("pointName", path.getPointname());
			values.put("pointSurroundingInfo", path.getPointSurroundingInfo());
			values.put("pointSurroundingStreet",
					path.getPointSurroundingStreet());
			values.put("pointLongitude", path.getPointLongitude());
			values.put("pointLatitude", path.getPointLatitude());
			values.put("type", path.getType());
			db.insert("PathInfo", "pointID", values);
		}
		return true;
	}

	// fill the ID->place
	public boolean setPlace() {
		db.execSQL("DELETE FROM PlaceInfo");
		Cursor rs = db.rawQuery(
				"select pointID,pointSurroundingInfo from PathInfo ", null);
		List<String> pointInfo = new ArrayList<String>();
		try {
			if (rs != null) {
				if (rs.moveToFirst()) {
					do {
						String pointID = rs.getString(rs
								.getColumnIndex("pointID"));
						String pointSI = rs.getString(rs
								.getColumnIndex("pointSurroundingInfo"));
						if (pointSI.equals("0"))
							continue;
						JSONArray ja = new JSONArray(pointSI);
						for (int i = 0; i < ja.length(); i++) {
							ContentValues values = new ContentValues();
							values.put("NodeID", pointID);
							values.put("PID", ja.getString(i));
							db.insert("PlaceInfo", "NodeID", values);
						}
					} while (rs.moveToNext());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

	// fill the roads
	public boolean setRoads() {
		db.execSQL("DELETE FROM RoadInfo");
		Cursor rs = db.rawQuery(
				"select streetID from PathInfo group by streetID", null);
		List<String> streetIDF = new ArrayList<String>();
		List<String> streetW = new ArrayList<String>();
		List<String> nodesID = new ArrayList<String>();
		try {
			if (rs != null)
				if (rs.moveToFirst()) {
					do {
						String str = rs
								.getString(rs.getColumnIndex("streetID"));
						JSONArray ja = new JSONArray(str);
						streetIDF.add(str);
						streetW.add(ja.getString(1));
					} while (rs.moveToNext());
				}
			for (int i = 0; i < streetIDF.size(); i++) {
				rs = db.rawQuery(
						"select pointID,type from PathInfo where streetID='"
								+ streetIDF.get(i)
								+ "' and (type='1' or type ='2' or type='3' or type='4')",
						null);
				nodesID.clear();
				if (rs != null)
					if (rs.moveToFirst()) {
						do {
							nodesID.add(rs.getString(rs
									.getColumnIndex("pointID")));

						} while (rs.moveToNext());
					}
				ContentValues values = new ContentValues();
				values.put("RID", streetIDF.get(i));
				values.put("start", nodesID.get(0));
				values.put("second", nodesID.get(1));
				values.put("third", nodesID.get(2));
				values.put("end", nodesID.get(3));
				values.put("weight", streetW.get(i));
				db.insert("RoadInfo", "RID", values);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return true;
	}

	// get all cross node
	public Set<String> getNodes() {
		Cursor rs = db.rawQuery("select start,end from RoadInfo", null);
		Set<String> nodes = new HashSet<String>();
		if (rs != null) {
			if (rs.moveToFirst()) {
				do {
					String node = rs.getString(rs.getColumnIndex("start"));
					if (nodes.contains(node))
						;
					else
						nodes.add(node);
					node = rs.getString(rs.getColumnIndex("end"));
					if (nodes.contains(node))
						;
					else
						nodes.add(node);
				} while (rs.moveToNext());
			}
		}
		return nodes;
	}

	// get all roads
	public Set<String> getRoads() {
		Cursor rs = db.rawQuery("select RID from RoadInfo", null);
		Set<String> roads = new HashSet<String>();
		if (rs != null) {
			if (rs.moveToFirst()) {
				do {
					roads.add(rs.getString(rs.getColumnIndex("RID")));
				} while (rs.moveToNext());
			}
		}
		return roads;
	}

	// get node from placeinfo by placename
	public List<String> getCertainNode(String place) {
		Cursor rs = db.rawQuery("select NodeID from PlaceInfo where PID = '"
				+ place + "'", null);
		List<String> nodes = new ArrayList<String>();
		if (rs != null) {
			if (rs.moveToFirst()) {
				do {
					nodes.add(rs.getString(rs.getColumnIndex("NodeID")));
				} while (rs.moveToNext());
			}
		}
		return nodes;

	}

	// get the nodes' childs
	public Map<String, Double> getChild(String father) {
		Cursor rs = db
				.rawQuery("select end,weight from RoadInfo where start ='"
						+ father + "'", null);
		Map<String, Double> child = new HashMap<String, Double>();
		if (rs != null) {
			if (rs.moveToFirst()) {
				do {
					child.put(rs.getString(rs.getColumnIndex("end")), Double
							.valueOf(rs.getString(rs.getColumnIndex("weight"))));
				} while (rs.moveToNext());
			}
		}
		rs = db.rawQuery("select start,weight from RoadInfo where end ='"
				+ father + "'", null);
		if (rs != null) {
			if (rs.moveToFirst()) {
				do {
					child.put(rs.getString(rs.getColumnIndex("start")), Double
							.valueOf(rs.getString(rs.getColumnIndex("weight"))));
				} while (rs.moveToNext());
			}
		}

		return child;
	}

	// get specific road info
	public List<String> getRoad(String roadID) {
		Cursor rs = db.rawQuery(
				"select start,second,third,end,weight from RoadInfo where RID='"
						+ roadID + "'", null);
		List<String> road = new ArrayList<String>();
		if (rs != null) {
			if (rs.moveToFirst()) {
				road.add(rs.getString(rs.getColumnIndex("start")));
				road.add(rs.getString(rs.getColumnIndex("second")));
				road.add(rs.getString(rs.getColumnIndex("third")));
				road.add(rs.getString(rs.getColumnIndex("end")));
				road.add(rs.getString(rs.getColumnIndex("weight")));

			}
		}
		return road;
	}

	public Path readPathInfo(String pointID) {
		Cursor rs = db.rawQuery("select * from PathInfo", null);
		Path path = new Path();
		if (rs != null) {
			if (rs.moveToFirst()) {
				do {
					path.setPointID(rs.getString(rs.getColumnIndex("pointID")));
					path.setStreetID(rs.getString(rs.getColumnIndex("streetID")));
					path.setPointname(rs.getString(rs
							.getColumnIndex("pointName")));
					path.setPointSurroundingInfo(rs.getString(rs
							.getColumnIndex("pointSurroundingInfo")));
					path.setPointSurroundingStreet(rs.getString(rs
							.getColumnIndex("pointSurroundingStreet")));
					path.setPointLongitude(rs.getString(rs
							.getColumnIndex("pointLongitude")));
					path.setPointLatitude(rs.getString(rs
							.getColumnIndex("pointLatitude")));
					path.setType(rs.getString(rs.getColumnIndex("type")));
				} while (rs.moveToNext());
			}
		}
		System.out.println(path.toString());
		return path;
	}

	public List<Path> readPaths() {
		Cursor rs = db.rawQuery("select * from PathInfo", null);
		List<Path> paths = new ArrayList<Path>();
		if (rs != null) {
			if (rs.moveToFirst()) {
				do {
					Path chengyus = new Path();
					chengyus.setPointID(rs.getString(rs
							.getColumnIndex("pointID")));
					chengyus.setStreetID(rs.getString(rs
							.getColumnIndex("streetID")));
					chengyus.setPointname(rs.getString(rs
							.getColumnIndex("pointName")));
					chengyus.setPointSurroundingInfo(rs.getString(rs
							.getColumnIndex("pointSurroundingInfo")));
					chengyus.setPointSurroundingStreet(rs.getString(rs
							.getColumnIndex("pointSurroundingStreet")));
					chengyus.setPointLongitude(rs.getString(rs
							.getColumnIndex("pointLongitude")));
					chengyus.setPointLatitude(rs.getString(rs
							.getColumnIndex("pointLatitude")));
					chengyus.setType(rs.getString(rs.getColumnIndex("type")));
					paths.add(chengyus);
				} while (rs.moveToNext());
			}
		}

		System.out.println(paths.toString());
		return paths;
	}

	private Database(Context context) {
		this.context = context;
		String dbPath = Environment.getExternalStorageDirectory() + "/blind.db";

		File dbFile = new File(dbPath);
		if (!dbFile.exists()) {
			copyDataBase(1);
		}

		db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);

		// db = SQLiteDatabase.openDatabase(dbPath, null,
		// SQLiteDatabase.OPEN_READWRITE);
		// db = openOrCreateDatabase(dbPath, SQLiteDatabase.OPEN_READWRITE,
		// null);
	}

	/**
	 * ������Դ����ݿ�
	 * 
	 * @param where
	 *            1SDCARD,2LOCAL
	 */
	@SuppressLint("SdCardPath")
	private void copyDataBase(int where) {
		// ÿ��Ӧ�ö���һ����ݿ�Ŀ¼����λ�� /data/data/blind/databases/Ŀ¼��
		String packageName = (String) context.getResources().getText(
				R.string.packageName);
		; // xml�����õ�
		String dbName = "blind.db";
		String dbPath = null;
		if (where == 1) { // sdcard
			dbPath = Environment.getExternalStorageDirectory() + File.separator
					+ dbName;
		} else { // local
			dbPath = "/data/data/" + packageName + "/databases/" + dbName;
		}

		if (where == 2) {
			new File("/data/data/" + packageName + "/databases/").mkdirs();
		}

		if (where == 1
				&& !Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {
			return;
		}

		File dbFile = new File(dbPath);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		try {
			dbFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		try {
			InputStream is = context.getResources()
					.openRawResource(R.raw.blind);
			OutputStream os = new FileOutputStream(dbPath);

			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}

			os.flush();
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
