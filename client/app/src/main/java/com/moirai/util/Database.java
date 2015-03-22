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
import com.moirai.model.Info;
import com.moirai.model.User;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class Database {
	private SQLiteDatabase db;
	private static Database database = null;

	public void closeDatabase() {
		database = null;
		db.close();
	}

	public static Database getInstance() {
		if (database == null)
			return new Database();
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

	public boolean saveDownloadInfo(List<Info> list) {
        for (int i = 0; i < list.size(); i++) {
            ContentValues values = new ContentValues();
            Info path = list.get(i);
            values.put("pointID", path.getReceiver());
            values.put("streetID", path.getDetail());
            values.put("pointName", path.getTime());
            db.insert("PathInfo", null, values);
        }
		return true;
	}

	public boolean saveFriend(String id) {
		return true;
	}
}
