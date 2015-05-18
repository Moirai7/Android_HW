package com.navi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.navi.model.Info;
import com.navi.model.Moments;
import com.navi.service.FriendsService;
import com.navi.util.DaoUtil;

 
public class MomentsDao {
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
/*
public boolean saveMoments(Moments history) {
		getConnection();
		String sql = "insert into momentsInfo (id,sendid,receid, detail, time,status) values (S_S_Depart.Nextval, '"
				+ history.getSendUser()
				+ "', '"
				+ new FriendsService().downloadAllFriends(history.getSendUser())
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
*/
	/**已测********************/
	public JSONArray getMoments(String username) {
		JSONArray chengyus = new JSONArray();
		getConnection();
		String sql = "select * from momentsfriendview where friendname = '"
					+ username+ "'";
		String sql2 = "update moments set status = 1 where sendname = '"
				+ username + "'";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				JSONObject history = new JSONObject();
				history.put("send", rs.getString("sendname"));
				history.put("content", rs.getString("content"));
				history.put("time",rs.getString("time"));
				chengyus.put(history);
			}
			System.out.println();
			conn.setAutoCommit(false);
			stmt.execute(sql2);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return chengyus;
	}
	
	/*
	public void setReciver(String username) {
		getConnection();
		String sql = "update momentsInfo set receid = " + new FriendsService().downloadAllFriends(username) + " where username='"
				+ username + "'";
		try {
			conn.setAutoCommit(false);
			stmt.execute(sql);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
	}
	*/
    public void sendmoments(String sendname, String msg) {
    	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    	
		getConnection();
		String sql = "insert into moments (sendname,time,content,status)values('"
				+ sendname
				+ "','"
				+ df.format(new Date())
				+ "','"
				+ msg + "',0)" ;
		try {
			conn.setAutoCommit(false);
			stmt.execute(sql);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
	}
    
    
//    public JSONArray getMoments(String username) {
//		JSONArray moments = new JSONArray();
//		getConnection();
//		String sql = "select * from msglist where receivername = '" + username
//				+ "' and sendername = '" + sendername
//				+ "' union select * from msglist where receivername = '" + sendername
//				+ "' and sendername = '" + username
//				+ "' order by sendtime DESC" ;
//		String sql2 = "update msglist set status = 1 where receivername = '"
//				+ username + "' and sendername = '" + sendername + "' ";
//		String sql3 = "update msglist set status = 1 where receivername = '"
//				+ sendername + "' and sendername = '" + username + "' ";
//		try {
//			ResultSet rs = stmt.executeQuery(sql);
//			while (rs.next()) {
//				// Info msg = new Info();
//				JSONObject moment = new JSONObject();
//				moment.put("messageid", rs.getString("id"));
//				moment.put("senderid", rs.getString("sendername"));
//				moment.put("receiverid", rs.getString("receivername"));
//				moment.put("message", rs.getString("msg"));
//				moment.put("time", rs.getString("sendtime"));
//				moments.put(moment);
//			}
//			conn.setAutoCommit(false);
//			stmt.execute(sql2);
//			stmt.execute(sql3);
//			conn.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			DaoUtil.closeConnection(conn, stmt, rs);
//		}
//		return moments;
//	}
    
}


