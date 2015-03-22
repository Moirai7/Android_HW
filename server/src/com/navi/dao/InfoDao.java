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
import com.navi.util.DaoUtil;

public class InfoDao {
	// ��Ա����
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
			String user = "blind";// �û���,ϵͳĬ�ϵ��˻���
			String password = "123";// �㰲װʱѡ���õ�����
			conn = DriverManager.getConnection(uri, user, password);
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean saveHistory(Info history) {
		getConnection();
		String sql = "insert into historyInfo (id,sendid,receid, detail, time,status) values (S_S_Depart.Nextval, '"
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

	// ��ȡ����
	public List<Info> getNews(String userid) {
		List<Info> chengyus = new ArrayList<Info>();
		getConnection();
		String sql = "select * from historyInfo where userid = '" + userid
				+ "' and status = -1 order by infoid";
		String sql2 = "update historyInfo set status = 1 where userid = '"
				+ userid + "'";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Info history = new Info();
				history.setSendUser(rs.getString("senduser"));
				history.setReceiver(rs.getString("receiver"));
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
}
