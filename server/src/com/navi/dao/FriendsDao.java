package com.navi.dao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.constant.Config;
import com.navi.model.Friends;
import com.navi.model.Info;
import com.navi.net.ForwardTask;
import com.navi.util.DaoUtil;

public class FriendsDao {
	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //Modified
    final String driver = "com.mysql.jdbc.Driver";//Driver name
    final String uri = "jdbc:mysql://localhost:3306/swt?useUnicode=true&characterEncoding=utf8";//mysql DB

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
    /*已测**************************************************/
	public JSONArray downloadAllFriends(String name){
		JSONArray chengyus = new JSONArray();
//		List<Friends> chengyus = new ArrayList<Friends>();
		getConnection();
		String sql = "select * from friendlist where username = '" + name  + "' order by id DESC" ;
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				JSONObject history = new JSONObject();
				//Friends history = new Friends();
				history.put("user", rs.getString("username"));
				history.put("friend",rs.getString("friendname"));
				chengyus.put(history);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
		return chengyus;
	}
	
	/**暂时不知道干嘛************************************/
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
	
/*	//*********************已测
		public void yaoyiyao(String sendname){
			Calendar rightNow = Calendar.getInstance();
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
			String time = fmt.format(rightNow.getTime());
			
			String name2 = null;
			System.out.println(sendname+"发送摇一摇");

			getConnection();
			try {
				String sqlsel = "select * from requestFriends";
				String sqlins = "insert into requestFriends (name,sendtime,status)values('"
						+ sendname + "','" + time + "',0)";
			//	System.out.println(sqlins);
				conn.setAutoCommit(false);
				stmt.execute(sqlins);
				conn.commit();
				ResultSet fs;

				fs = stmt.executeQuery(sqlsel);
				while (fs.next()) {
					if (!fs.getString("name").equals(sendname)) {
						name2 = fs.getString("name");
						String sqlmat = "insert into matchFriends (name1,name2,status)values('"
								+ fs.getString("name") + "','" + sendname + "',0)";
						stmt.execute(sqlmat);
						conn.commit();
						// *********锟斤拷sendname锟斤拷name2锟斤拷锟斤拷锟斤拷雍锟斤拷锟斤拷锟斤拷锟�*******
						try {
							PrintWriter out;

							out = new PrintWriter(new BufferedWriter(
									new OutputStreamWriter(ForwardTask.map.get(
											sendname).getOutputStream(), "UTF-8")),
									true);
							
							System.out.println(ForwardTask.map.get(
											sendname.toString()));
						//	System.out.println("************"+ForwardTask.map.get(sendname));
							
							JSONObject obj = new JSONObject();
							obj.put(Config.REQUEST_TYPE, Config.RESULT_YAOYIYAO);
							
							obj.put("name", name2);
							obj.put(Config.RESULT, Config.SUCCESS);
							out.println(obj);
							System.out.println("sucsess"+sendname+name2);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String sqldel1 = "delete from requestFriends where name ='"
								+ sendname + "'";
						String sqldel2 = "delete from requestFriends where name ='"
								+ name2 + "'";
						stmt.execute(sqldel1);
						stmt.execute(sqldel2);
						conn.commit();
						break;

					}
				}
				if (name2 == null) {
					Thread.currentThread().sleep(2000);
					
					System.out.println("失败的分支");
					String sqlstatus = "select * from requestFriends";
					
					ResultSet sta = null;
					sta = stmt.executeQuery(sqlstatus);
					while (sta.next()) {
						if (!sta.getString("name").equals(sendname)) {
							sendname = sta.getString("name");
							System.out.println(sendname);
							PrintWriter out2;
							out2 = new PrintWriter(new BufferedWriter(
									new OutputStreamWriter(ForwardTask.map.get(
											sendname).getOutputStream(), "UTF-8")),
									true);
					//		System.out.println("123123555"+ForwardTask.map.get(name2));
							JSONObject obj2 = new JSONObject();
							obj2.put(Config.REQUEST_TYPE, Config.RESULT_YAOYIYAO);
							obj2.put(Config.RESULT, Config.FAIl);
							obj2.put("name", name2);
							out2.println(obj2);

							String sqldel1 = "delete from requestFriends where name ='"
									+ sendname + "'";
							String sqldel2 = "delete from requestFriends where name ='"
									+ name2 + "'";
							stmt.execute(sqldel1);
							stmt.execute(sqldel2);
							conn.commit();
							break;
						}
					}
				}

				conn.commit();
			} catch (SQLException | InterruptedException e) {
				e.printStackTrace();
				if (conn != null) {
					try {
						conn.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DaoUtil.closeConnection(conn, stmt, rs);
			}
		}

	// 锟斤拷锟杰伙拷芫锟斤拷锟斤拷
	
	//*********************已测
	public void aor(String name1, String name2, int toj) {// 0为未锟斤拷应锟斤拷1为同锟解，-1为锟杰撅拷

		try {
			getConnection();
			conn.setAutoCommit(false);
			ResultSet fs;
			int id = 0, status = 0;

			String sql1 = "select * from matchFriends where name1 = '" + name1
					+ "'and name2 = '" + name2 + "'";
			fs = stmt.executeQuery(sql1);
			while (fs.next()) {
				id = fs.getInt("id");
				status = fs.getInt("status");
			}
			if (id == 0) {
				String sql2 = "select * from matchFriends where name1 = '"
						+ name2 + "'and name2 = '" + name1 + "'";
				fs = stmt.executeQuery(sql2);
				while (fs.next()) {
					id = fs.getInt("id");
					status = fs.getInt("status");
				}
			}
			if (toj == -1) {
				String sqlreject = "update matchFriends set status=-1 where id = '"
						+ id + "'";
				stmt.execute(sqlreject);
				conn.commit();
				System.out.println("一个人拒绝了");
			} else if (toj == 1) {
				if (status == 0) {
					String sqlagree = "update matchFriends set status=1 where id = '"
							+ id + "'";
					stmt.execute(sqlagree);
					conn.commit();
					System.out.println("第一个人同意了");
				} else if (status == 1) {
					String sqldel = "delete from matchFriends where id = '"
							+ id + "'";
					stmt.execute(sqldel);
					String sqladfr1 = "insert into friendlist(username,friendname)values('"+name1+"','"+name2+"')";
					String sqladfr2 = "insert into friendlist(username,friendname)values('"+name2+"','"+name1+"')";
					stmt.execute(sqladfr1);
					stmt.execute(sqladfr2);
					conn.commit();
					System.out.println("都同意了，添加好友");
					// *****************name1,name2,success*********************
					PrintWriter out, out2;

					out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name1)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj = new JSONObject();
					obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					
					obj.put("name", name2);
					obj.put(Config.RESULT, Config.SUCCESS);
					out.println(obj);
					
					out2 = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name2)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj2 = new JSONObject();
					obj2.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					obj2.put("name", name1);
					obj2.put(Config.RESULT, Config.SUCCESS);
					out2.println(obj2);
					
					System.out.println("1111111"+name1+name2);
					
				} else if (status == -1) {
					String sqldel = "delete from matchFriends where id = '"
							+ id + "'";
					stmt.execute(sqldel);
					conn.commit();
					System.out.println("对方拒绝了");
					// *****************锟皆凤拷锟窖撅拷锟杰撅拷锟斤拷锟轿拷锟斤拷锟�*****************
					// 锟斤拷锟揭拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷母锟斤拷锟斤拷没锟斤拷锟街伙拷锟揭拷锟揭伙拷锟斤拷锟斤拷锟绞撅拷苑锟斤拷芫锟酵匡拷锟皆ｏ拷name1,name2锟斤拷锟侥革拷要锟斤拷锟酵伙拷锟斤拷锟斤拷么锟斤拷锟斤拷
					PrintWriter out, out2;

					out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name1)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj = new JSONObject();
					obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					
					obj.put("name", name2);
					obj.put(Config.RESULT, Config.FAIl);
					out.println(obj);

					out2 = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name2)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj2 = new JSONObject();
					obj2.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					obj2.put("name", name1);
					obj2.put(Config.RESULT, Config.FAIl);
					out2.println(obj2);
					
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
	}*/
	
	//*********************已测
	public void yaoyiyao(String sendname){
		Calendar rightNow = Calendar.getInstance();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		String time = fmt.format(rightNow.getTime());
		
		String name2 = null;
		System.out.println(sendname+"发送摇一摇");

		getConnection();
		try {
			String sqlsel = "select * from requestfriends";
			String sqlins = "insert into requestFriends (name,sendtime,status)values('"
					+ sendname + "','" + time + "',0)";
		//	System.out.println(sqlins);
			conn.setAutoCommit(false);
			stmt.execute(sqlins);
			conn.commit();
			ResultSet fs;

			fs = stmt.executeQuery(sqlsel);
			while (fs.next()) {
				if (!fs.getString("name").equals(sendname)) {
					name2 = fs.getString("name");
					String sqlmat = "insert into matchFriends (name1,name2,status)values('"
							+ fs.getString("name") + "','" + sendname + "',0)";
					stmt.execute(sqlmat);
					conn.commit();
					// *********锟斤拷sendname锟斤拷name2锟斤拷锟斤拷锟斤拷雍锟斤拷锟斤拷锟斤拷锟�*******
					try {
						PrintWriter out;

						out = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(ForwardTask.map.get(
										sendname).getOutputStream(), "UTF-8")),
								true);
						
						System.out.println(ForwardTask.map.get(
										sendname.toString()));
						
						System.out.println(sendname+"succsess");
						JSONObject obj = new JSONObject();
						obj.put(Config.REQUEST_TYPE, Config.RESULT_YAOYIYAO);
						
						obj.put("name", name2);
						obj.put(Config.RESULT, Config.SUCCESS);
						out.println(obj);
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						DaoUtil.closeConnection(conn, stmt, rs);
					}
					break;

				}
			}
			if (name2 == null) {
		//		System.out.println("nofriend");
				Thread.currentThread().sleep(2000);
		//		System.out.println("delete");
				getConnection();
				conn.setAutoCommit(false);
				//System.out.println(sendname+"第二次搜索");
				
				String sqlstatus = "select * from requestfriends";
				ResultSet sta;
				sta = stmt.executeQuery(sqlstatus);
				boolean b = sta.first();
				if(b){   System.out.println(sta.getString("name"));}
				while (sta.next()) {
				//	System.out.println(sendname+"找到了一条数据"+sta.getString("name"));
					if (!sta.getString("name").equals(sendname)) {
						name2 = sta.getString("name");
					//	System.out.println(sendname+"找到了"+name2);
						String sqldel1 = "delete from requestfriends where name ='"
								+ sendname + "'";
						String sqldel2 = "delete from requestfriends where name ='"
								+ name2 + "'";
						stmt.execute(sqldel1);
						stmt.execute(sqldel2);
						conn.commit();
						
						try {
							PrintWriter out2;
							out2 = new PrintWriter(new BufferedWriter(
									new OutputStreamWriter(ForwardTask.map.get(
											sendname).getOutputStream(), "UTF-8")),
										true);
				//			System.out.println("123123555"+ForwardTask.map.get(name2));
							JSONObject obj2 = new JSONObject();
							obj2.put(Config.REQUEST_TYPE, Config.RESULT_YAOYIYAO);
							obj2.put(Config.RESULT, Config.SUCCESS);
							obj2.put("name", name2);
							out2.println(obj2);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
				if(name2 == null){
					System.out.println(sendname+"配对失败");
					String sqldel1 = "delete from requestFriends where name ='"
							+ sendname + "'";
					
					stmt.execute(sqldel1);
					conn.commit();
					
					PrintWriter out3;
					out3 = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(
									sendname).getOutputStream(), "UTF-8")),
							true);
			//		System.out.println("123123555"+ForwardTask.map.get(name2));
					JSONObject obj3 = new JSONObject();
					obj3.put(Config.REQUEST_TYPE, Config.RESULT_YAOYIYAO);
					obj3.put(Config.RESULT, Config.FAIl);
					
					out3.println(obj3);
				}
			}

//			conn.commit();
		} catch (SQLException | InterruptedException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
	}
	
	//*********************已测
	public void aor(String name1, String name2, int toj) {// 0为未锟斤拷应锟斤拷1为同锟解，-1为锟杰撅拷

		try {
			getConnection();
			conn.setAutoCommit(false);
			ResultSet fs;
			int id = 0, status = 0;

			String sql1 = "select * from matchFriends where name1 = '" + name1
					+ "'and name2 = '" + name2 + "'";
			fs = stmt.executeQuery(sql1);
			while (fs.next()) {
				id = fs.getInt("id");
				status = fs.getInt("status");
			}
			if (id == 0) {
				String sql2 = "select * from matchFriends where name1 = '"
						+ name2 + "'and name2 = '" + name1 + "'";
				fs = stmt.executeQuery(sql2);
				while (fs.next()) {
					id = fs.getInt("id");
					status = fs.getInt("status");
				}
			}
			if (toj == -1) {
				if(status !=1 ){
					String sqlreject = "update matchFriends set status=-1 where id = '"
							+ id + "'";
					stmt.execute(sqlreject);
					conn.commit();
					System.out.println(name1+"第一个人拒绝了,不返回消息");
				}else{
					System.out.println(name1+"第一个人同一个但是第二个拒绝了,给第一个人返回失败");
					PrintWriter out0;
					out0 = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name2)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj0 = new JSONObject();
					obj0.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					obj0.put("name", name1);
					obj0.put(Config.RESULT, Config.FAIl);
					out0.println(obj0);
				}
			} else if (toj == 1) {
				if (status == 0) {
					String sqlagree = "update matchFriends set status=1 where id = '"
							+ id + "'";
					stmt.execute(sqlagree);
					conn.commit();
					System.out.println(name1+"第一个人同意了");
				} else if (status == 1) {
					String sqldel = "delete from matchFriends where id = '"
							+ id + "'";
					stmt.execute(sqldel);
					String sqladfr1 = "insert into friendlist(username,friendname)values('"+name1+"','"+name2+"')";
					String sqladfr2 = "insert into friendlist(username,friendname)values('"+name2+"','"+name1+"')";
					stmt.execute(sqladfr1);
					stmt.execute(sqladfr2);
					conn.commit();
					System.out.println("都同意了，添加好友");
					// *****************name1,name2,success*********************
					PrintWriter out, out2;

					out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name1)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj = new JSONObject();
					obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					
					obj.put("name", name2);
					obj.put(Config.RESULT, Config.SUCCESS);
					out.println(obj);
					
					out2 = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name2)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj2 = new JSONObject();
					obj2.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					obj2.put("name", name1);
					obj2.put(Config.RESULT, Config.SUCCESS);
					out2.println(obj2);
					
					System.out.println("1111111"+name1+name2);
					
				} else if (status == -1) {
					String sqldel = "delete from matchFriends where id = '"
							+ id + "'";
					stmt.execute(sqldel);
					conn.commit();
					System.out.println(name1+"的对方拒绝了，给"+name1+"返回失败");
					// *****************锟皆凤拷锟窖撅拷锟杰撅拷锟斤拷锟轿拷锟斤拷锟�*****************
					// 锟斤拷锟揭拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷母锟斤拷锟斤拷没锟斤拷锟街伙拷锟揭拷锟揭伙拷锟斤拷锟斤拷锟绞撅拷苑锟斤拷芫锟酵匡拷锟皆ｏ拷name1,name2锟斤拷锟侥革拷要锟斤拷锟酵伙拷锟斤拷锟斤拷么锟斤拷锟斤拷
					PrintWriter out2;

//					out = new PrintWriter(new BufferedWriter(
//							new OutputStreamWriter(ForwardTask.map.get(name1)
//									.getOutputStream(), "UTF-8")), true);
//					JSONObject obj = new JSONObject();
//					obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
//					
//					obj.put("name", name2);
//					obj.put(Config.RESULT, Config.FAIl);
//					out.println(obj);

					out2 = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(ForwardTask.map.get(name1)
									.getOutputStream(), "UTF-8")), true);
					JSONObject obj2 = new JSONObject();
					obj2.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
					obj2.put("name", name2);
					obj2.put(Config.RESULT, Config.FAIl);
					out2.println(obj2);
					
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DaoUtil.closeConnection(conn, stmt, rs);
		}
	}
}
