package com.navi.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.navi.model.Path;
import com.navi.util.DaoUtil;

public class PathDao {

	// 成员变量
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
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

	// 获取当前道路信息
	public Path getPathInfo(String num) {
		Path chengyus = new Path();
		getConnection();
		String sql = "select * from PathInfo where pointID = '" + num + "'";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				chengyus.setPointID(num);
				chengyus.setStreetID(rs.getString("streetID"));
				chengyus.setPointname(rs.getString("pointname"));
				chengyus.setPointSurroundingInfo( rs.getString("pointSurroundingInfo"));
				chengyus.setPointSurroundingStreet( rs.getString("pointSurroundingStreet"));
				chengyus.setPointLongitude( rs.getString("pointLongitude"));
				chengyus.setPointLatitude( rs.getString("pointLatitude"));
				chengyus.setType(rs.getString("type"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return chengyus;
	}

	// 获取道路信息
	public List<Path> getPaths() {
		List<Path> paths = new ArrayList<Path>();
		getConnection();
		String sql = "select * from PathInfo";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Path chengyus = new Path();
				chengyus.setPointID(rs.getString("pointID"));
				chengyus.setStreetID(rs.getString("streetID"));
				chengyus.setPointname(rs.getString("pointname"));
				chengyus.setPointSurroundingInfo( rs.getString("pointSurroundingInfo"));
				chengyus.setPointSurroundingStreet( rs.getString("pointSurroundingStreet"));
				chengyus.setPointLongitude( rs.getString("pointLongitude"));
				chengyus.setPointLatitude( rs.getString("pointLatitude"));
				chengyus.setType(rs.getString("type"));
				paths.add(chengyus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return paths;
	}

}
