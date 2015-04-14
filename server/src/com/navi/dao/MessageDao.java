package com.navi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.json.JSONArray;

public class MessageDao {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	// ������
	final String driver = "oracle.jdbc.driver.OracleDriver";
	final String uri = "jdbc:oracle:" + "thin:@127.0.0.1:1521:XE";

	// ��ȡ����
	private void getConnection() {
		try {
			Class.forName(driver);
			String user = "androidHW";// �û���,ϵͳĬ�ϵ��˻���
			String password = "123456";// �㰲װʱѡ���õ�����
			conn = DriverManager.getConnection(uri, user, password);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean sendmessage(int senderid, int receiverid, String message) {
		
		
		
		

		return true;

	}

	public JSONArray getmessage(int senderid, int receiverid) {
		JSONArray messagelist = new JSONArray();
		
		
		

		return messagelist;

	}

}
