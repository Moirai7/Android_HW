package com.navi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.constant.Config;
import com.navi.model.Friends;
import com.navi.model.Info;
import com.navi.util.DaoUtil;

public class FriendsDao {
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	// 驱动名
	final String driver = "oracle.jdbc.driver.OracleDriver";
	final String uri = "jdbc:oracle:" + "thin:@127.0.0.1:1521:XE";

	// 获取连接
	private void getConnection() {
		try {
			Class.forName(driver);
			String user = "blind";// 用户名,系统默认的账户名
			String password = "123";// 你安装时选设置的密码
			conn = DriverManager.getConnection(uri, user, password);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public List<Friends> downloadAllFriends(String name){
		List<Friends> chengyus = new ArrayList<Friends>();
		getConnection();
		String sql = "select * from friendsInfo where userid = '" + name  + "'" ;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Friends history = new Friends();
				history.setSend(rs.getString("send"));
				history.setReciver(rs.getString("reciver"));
				history.setState(rs.getInt("state"));
				chengyus.add(history );
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return chengyus;
	}
	public List<Friends> downloadFriends(String name){
		List<Friends> chengyus = new ArrayList<Friends>();
		getConnection();
		String sql = "select * from friendsInfo where userid = '" + name  + "' and status = -1" ;
		String sql2 = "update friendsInfo set status = 1 where userid = '" + name  + "'";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Friends history = new Friends();
				history.setSend(rs.getString("send"));
				history.setReciver(rs.getString("reciver"));
				history.setState(rs.getInt("state"));
				chengyus.add(history );
			}

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
	public void saveFriend(Friends history){
		getConnection();
		String sql = "insert into friendsInfo (send, reciver, state,status) values ('"
				+ history.getSend() + "', '" + history.getReciver() + "', '" + 1 + "', '" + -1 + ")";
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
	public void saveState(Friends history){
		getConnection();
		String sql = "update friendsInfo set state = " + history.getState() + " where reciver='"
				+ history.getReciver() + " and send='" + history.getSend() + "'";
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
	
	public void deleteFriend(Friends history){
		getConnection();
		String sql = "delete from friendsInfo where reciver='"
				+ history.getReciver() + " and send='" + history.getSend() + "'";
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
}
