package com.moirai.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import com.moirai.client.Constant;
import com.moirai.client.R;
import com.moirai.model.Info;
import com.moirai.model.Moments;
import com.moirai.model.User;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class Database {
    private static Context context;
	private SQLiteDatabase db;
	private static Database database = null;

	public void closeDatabase() {
		database = null;
		db.close();
	}

    public static Database getInstance(Context context) {
        if (database == null)
            return new Database(context);
        else
            return database;
    }

    public boolean setUserInfo(String userName,String password,String type){
        Cursor rs = db.rawQuery("select * from UserInfo", null);
        if (rs.getCount() != 0) {
            db.execSQL("DELETE FROM UserInfo");
        }
        ContentValues values = new ContentValues();
        values.put("username", userName);
        values.put("password", password);
        values.put("type", type);
        db.insert("UserInfo", "username", values);
        return true;
    }

    public boolean getUserInfo() {
        Cursor rs = db.rawQuery("select * from UserInfo", null);
        if (rs != null) {
            if (rs.moveToFirst()) {
                Constant.USERNAME = rs.getString(rs.getColumnIndex("username"));
                Constant.PASSWORD = rs.getString(rs
                        .getColumnIndex("password"));
                Constant.ID = rs.getString(rs.getColumnIndex("type"));
            }
            return true;
        }
        return false;
    }

	public boolean saveAllHistory(List<Info> list) {
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            Info path = list.get(i);
            values.put("sendid", path.getSendUser());
            values.put("receid", path.getReceiver());
            values.put("detail", path.getDetail());
            values.put("time", path.getTime());
            db.insert("HistoryInfo", null, values);
        }
		return true;
	}

    public List<Info> getAllHistory(){
        Cursor rs = db.rawQuery("select * from HistoryInfo order by time desc", null);
        List<Info> list = new ArrayList<Info>();
        if (rs != null) {
            if (rs.moveToFirst()) {
                do {
                    Info road=new Info();
                    road.setDetail(rs.getString(rs.getColumnIndex("detail")));
                    road.setReceiver(rs.getString(rs.getColumnIndex("receid")));
                    road.setSendUser(rs.getString(rs.getColumnIndex("sendid")));
                    road.setTime(rs.getString(rs.getColumnIndex("time")));
                    list.add(road);
                } while (rs.moveToNext());
            }
        }
        return list;
    }

    public List<Info> getFriendHistory(String name){
        Cursor rs = db.rawQuery("select * from HistoryInfo where receid = ? or sendid = ? order by time asc", new String[]{name,name});
        List<Info> list = new ArrayList<Info>();
        if (rs != null) {
            if (rs.moveToFirst()) {
                do {
                    Info road=new Info();
                    road.setDetail(rs.getString(rs.getColumnIndex("detail")));
                    road.setReceiver(rs.getString(rs.getColumnIndex("receid")));
                    road.setSendUser(rs.getString(rs.getColumnIndex("sendid")));
                    road.setTime(rs.getString(rs.getColumnIndex("time")));
                    list.add(road);
                } while (rs.moveToNext());
            }
        }
        return list;
    }

    public boolean saveFriendHistory(Info list) {
            ContentValues values = new ContentValues();
            values.put("sendid", list.getSendUser());
            values.put("receid", list.getReceiver());
            values.put("detail", list.getDetail());
            values.put("time", list.getTime());
            db.insert("HistoryInfo", null, values);
        return true;
    }

	public boolean saveFriend(String id) {
        ContentValues values = new ContentValues();
        values.put("name", id);
        db.insert("FriendInfo", null, values);
		return true;
	}

    public boolean saveFriends(List<String> list){
        Cursor rs = db.rawQuery("select * from FriendInfo", null);
        if (rs.getCount() != 0) {
            db.execSQL("DELETE FROM FriendInfo");
        }
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            String path = list.get(i);
            values.put("name", path);
            db.insert("FriendInfo", null, values);
        }
        return true;
    }

    public List<Moments> getMoments() {
        Cursor rs = db.rawQuery("select * from MomentsInfo order by time asc",null);
        List<Moments> list = new ArrayList<Moments>();
        if (rs != null) {
            if (rs.moveToFirst()) {
                do {
                    Moments road=new Moments();
                    road.setSendUser(rs.getString(rs.getColumnIndex("sendid")));
                    road.setTime(rs.getString(rs.getColumnIndex("time")));
                    road.setContent(rs.getString(rs.getColumnIndex("content")));
                    list.add(road);
                } while (rs.moveToNext());
            }
        }
        return list;
    }

    public boolean saveMoments(Moments list) {
        ContentValues values = new ContentValues();
        values.put("sendid", list.getSendUser());
        values.put("content", list.getContent());
        values.put("time", list.getTime());
        db.insert("MomentsInfo", null, values);
        return true;
    }

    public List<String> getAllFriend() {
        Cursor rs = db.rawQuery("select * from FriendInfo", null);
        List<String> list = new ArrayList<String>();
        if (rs != null) {
            if (rs.moveToFirst()) {
                do {
                    String road;
                    road = rs.getString(rs.getColumnIndex("name"));
                    list.add(road);
                } while (rs.moveToNext());
            }
        }
        return list;
    }

    public boolean saveAllMoments(List<Moments> list) {
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            Moments path = list.get(i);
            values.put("sendid", path.getSendUser());
            values.put("content", path.getContent());
            values.put("time", path.getTime());
            db.insert("HistoryInfo", null, values);
        }
        return true;
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
