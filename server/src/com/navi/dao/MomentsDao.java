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

	// ��ȡ����
	public List<Moments> getMoments(String userid,String first,String end) {
		List<Moments> chengyus = new ArrayList<Moments>();
		getConnection();
		String sql = "select * from (select momentsInfo.*,rownum as rno from momentsInfo order by id) where userid = '" + userid
				+ "' and rno> '"+first+"' and rno< '"+ end+"'";
		String sql2 = "update momentsInfo set status = 1 where userid = '"
				+ userid + "'";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Moments history = new Moments();
				history.setSendUser(rs.getString("sendid"));
				history.setReciver(rs.getString("receid"));
				history.setDetail(rs.getString("detail"));
				history.setTime(rs.getString("time"));
				chengyus.add(history);
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
}
