package com.navi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.navi.constant.Config;
import com.navi.model.User;
import com.navi.util.DaoUtil;

public class UserDao {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;

	final String driver = "oracle.jdbc.driver.OracleDriver";
	final String uri = "jdbc:oracle:" + "thin:@127.0.0.1:1521:XE";

	private void getConnection() {
		try {
			Class.forName(driver);
			String user = "blind";// �û���,ϵͳĬ�ϵ��˻���
			String password = "123";// �㰲װʱѡ���õ�����
			conn = DriverManager.getConnection(uri, user, password);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save(User user) {
		getConnection();
		String sql = "insert into USERINFO (id,username, password, type) values (S_S_Depart.Nextval,'"
				+ user.getUsername() + "', '" + user.getPassword() + "', '" + user.getType() + "',"
				+ Config.USER_STATE_NON_ONLINE + ")";
		try {
			conn.setAutoCommit(false);
			stmt.executeUpdate(sql);
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

	public User findByUsernameAndPassword(String username, String password) {
		getConnection();
		User user = new User();
		String sql = "select * from USERINFO where username = '" + username
				+ "' and password = '" + password + "'";
		try {
			rs = stmt.executeQuery(sql);
			if (!rs.next()) {
				return null;
			} else {
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setType(rs.getString("type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return user;
	}

	public List<String> findOnlineUsers() {
		getConnection();
		List<String> list = new ArrayList<String>();

		try {
			String sql = "select * from USERINFO where state="
					+ Config.USER_STATE_ONLINE + "";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString("username"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		System.out.println(list.size());
		return list;
	}

	public int getState(String username){
		getConnection();
		int state =0;
		String sql = "select state from USERINFO where username="+"'"+username+"'";
		try {
			rs=stmt.executeQuery(sql);
			while(rs.next()){
			  state = rs.getInt("state");
			}
			return state;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0 ;
	}

	public void setState(int state, String username) {
		getConnection();
		String sql = "update USERINFO set state = " + state + " where username='"
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
	
	public String getType(String username){
		getConnection();
		String detail = null;
		String sql = "select type from USERINFO where username="+"'"+username+"'";
		try {
			rs=stmt.executeQuery(sql);
			while(rs.next()){
				detail = rs.getString("type");
			}
			return detail;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return detail ;
	}
	
	public void setType(String username,String type){
		getConnection();
		String sql = "update USERINFO set type = " + type + " where username='"
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
