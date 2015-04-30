package com.navi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.navi.model.Info;
import com.navi.util.DaoUtil;

/**
 * Last Modified By Yu, WANG on 2015-04-15 10:46
 */
public class InfoDao {
	// ��Ա����
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	//Modified
	final String driver = "com.mysql.jdbc.Driver";//Driver name
	final String uri = "jdbc:mysql://localhost:3306/swt";//mysql DB

	//Modified
	private void getConnection() {
		try {
			Class.forName(driver).newInstance();//Load Driver
			String user = "root";//User of Mysql
			String password = "";//Pwd of Mysql
			conn = DriverManager.getConnection(uri, user, password);//Get Connection Object
			stmt = conn.createStatement();//Execute SQL statement
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean saveHistory(Info history) {
		getConnection();
		String sql = "insert into historyInfo (id,sendid,receid, detail, time,status) values (S_S_HISTORY.Nextval, '"
				+ history.getSendUser()
				+ "', '"
				+ history.getReceiver()
				+ "','"
				+ history.getDetail()
				+ "','"
				+ df.format(new Date())
				+ "',-1)";
		try {
			conn.setAutoCommit(false);
			stmt.execute(sql);
			conn.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return false;
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
	}


    /**
     * Modified by Yu, WANG on 2015-4-15
     * @param userid
     * @return
     */
    public JSONArray getNews(String userid) {
		//List<Info> chengyus = new ArrayList<Info>();
    	JSONArray msgs = new JSONArray();
		getConnection();
		String sql = "select * from historyInfo where receid = '" + userid
				+ "' and status = -1 order by id";
		String sql2 = "update historyInfo set status = 1 where receid = '"
				+ userid + "'";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
//				Info history = new Info();
//				history.setSendUser(rs.getString("sendid"));
//				history.setReceiver(rs.getString("receid"));
//				history.setDetail(rs.getString("detail"));
//				history.setTime(rs.getString("time"));
//				chengyus.add(history);
				JSONObject msg = new JSONObject();
				msg.put("messageid", rs.getString("id"));
				msg.put("sendername", rs.getString("sendername"));
				msg.put("receivername", rs.getString("receivername"));
				msg.put("message", rs.getString("msg"));
				msg.put("time", rs.getString("sendtime"));
				msgs.put(msg);

			}
			conn.setAutoCommit(false);
			stmt.execute(sql2);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return msgs;
	}
    
    public JSONArray getfriendNews(String username, String sendername) {
		// List<Info> msgss = new ArrayList<Info>();
		JSONArray msgs = new JSONArray();
		getConnection();
		String sql = "select * from msglist where receiverid = '" + username
				+ "' and senderid = '" + sendername
				+ "' order by sendtime DESC";
		String sql2 = "update msglist set ifRead = 1 where receiverid = '"
				+ username + "' and senderid = '" + sendername + "' ";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				// Info msg = new Info();
				JSONObject msg = new JSONObject();
				msg.put("messageid", rs.getString("id"));
				msg.put("sendername", rs.getString("sendername"));
				msg.put("receivername", rs.getString("receivername"));
				msg.put("message", rs.getString("msg"));
				msg.put("time", rs.getString("sendtime"));
				msgs.put(msg);
			}
			conn.setAutoCommit(false);
			stmt.execute(sql2);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return msgs;
	}
    
    public boolean getSendMsg(String sendername, String receivername, String msg) {
		getConnection();
		String sql = "insert into msglist (sendername,receivername,msg,sendtime,ifRead)values('"
				+ sendername
				+ "','"
				+ receivername
				+ "','"
				+ msg
				+ "','"
				+ df.format(new Date()) + "',0)";
		try {
			conn.setAutoCommit(false);
			stmt.execute(sql);

			conn.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return false;
	}
    
}
