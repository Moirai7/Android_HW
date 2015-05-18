package com.navi.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.constant.Config;
import com.navi.dao.FriendsDao;
import com.navi.dao.InfoDao;
import com.navi.dao.MomentsDao;
import com.navi.model.Info;
import com.navi.model.Moments;
import com.navi.model.User;
import com.navi.service.InfoService;
//import com.navi.service.MomentsService;
import com.navi.service.UserService;
//import com.navi.dao.MessageDao;
//import com.navi.service.InfoService;

public class ForwardTask extends Task {
	// 闁哄鏅滈悷銈夋煂濞ｆshMap闂佸搫瀚烽崹鎶藉极閵堝绾ч柕澶堝劤閹界娀鏌￠敓钘変壕闁伙腹鍓濈粙澶愭偖閸掔巢ket闁哄鏅濋崑鐐垫暜閹绢喗鍎嶉柨鐕傛嫹
	public static HashMap<String, Socket> map = new HashMap<String, Socket>();
	private String name = null;
	private String ip;
	// 闁哄鐗婇幐鎼佸矗閸℃褰掓晸閿燂拷
	private BufferedReader in;
	// 闁哄鐗婇幐鎼佸吹椤撶伝褰掓晸閿燂拷
	private PrintWriter out;
	// Socket闁诲海鏁搁、濠囨寘閿燂拷
	private Socket socket;
	// 闂佹椿鏋岄崝宥咁焽閻㈢鎷锋俊顖滅帛閺夊綊鏌熼幁鎺戝姕闁轰礁缍婇幆鍐礋椤忓啰绠氶梺璇″弿閹凤拷
	private JSONObject message;
	// 闁荤姴娲弨閬嶆儑閹殿喚灏甸悹鍥皺閿燂拷
	private int requestType;
	// 闂佺鐭囬崘銊у幀run闂佸搫鍊介‖鍐兜閸洘鐓傜?锟介敓鐣屾殸while閻庣敻鍋婃禍鐐虹嵁閿燂拷
	private boolean onWork = true;

	// 闂佽浜介崕鏌ュ极鐟欏嫮鈻旈柨鐔剁矙閸ゅ┘ocket闁诲海鏁搁、濠囨寘閸曨垱鍎嶉柛鏇ㄥ墰閿熶粙姊洪銈呅ｉ柡宀?枎閳绘棃鏁撻敓锟?
	public ForwardTask(Socket socket) {
		this.socket = socket;
		try {
			// 闂佸憡甯楃换鍌炩?婵犲洤绀岄柡宥庡墰缂堝鏌涜箛瀣姕缂佷緤绠撳畷顏勭暆閸愵亞鐐曢梺鍛婂灩閸庛倗绮婇敓锟?
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8")), true);
			ip = socket.getInetAddress().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (onWork) {
			try {
				receiveMessage();
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println(ip + " Client is close");
				if (name != null) {
					System.out.println(ip + ":" + name + " is close");
					map.remove(name);
					new UserService().setStateToNonOnline(name);
				}
				break;
			}
		}
		try {
			if (socket != null) {
				socket.close();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			socket = null;
			in = null;
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setWorkState(boolean state) {
		this.onWork = state;
	}

	public void receiveMessage() {
		try {
			message = new JSONObject(in.readLine());
			System.out.println(ip + ": receive" + message);
			requestType = message.getInt(Config.REQUEST_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		switch (requestType) {

		case Config.REQUEST_LOGIN:
			handLogin();// OK
			break;
		case Config.REQUEST_REGISTER:
			handRegister();// OK
			break;
		case Config.REQUEST_EXIT:
			handExit();
			break;
		case Config.REQUEST_DOWNLOAD_INFO:
			handDownloadInfo();// OK
			break;
		case Config.REQUEST_SET_INFO:
			handSetInfo();// OK
			break;
		case Config.REQUEST_REQUIRE_FRIEND:
			handSendRequestFriend();// OK
			break;
		case Config.REQUEST_ADDFRIEND:
			handAddFriend();// OK
			break;
		case Config.REQUEST_DOWNLOAD_FRIEND:
			handDownloadFriend();// OK
			break;
		case Config.REQUEST_DOWNLOAD_MOMENTS:
			handDownloadMoments();
			break;
		case Config.REQUEST_UPLOAD_MOMENTS:
			handUploadMoments();
			break;

		case Config.REQUEST_SEND_MESSAGE:

			handSendMessage();
			break;
		case Config.REQUEST_GET_MESSAGE:

			handGetMessage();
			break;
		case Config.RESULT_YAOYIYAO:
			handSendRequestFriend();
			break;

		default:
			/*
			 * System.out.println("default"); onWork=false; socket.close();
			 * socket=null;
			 */
			break;
		}
	}

	public void handConSuccess() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(Config.REQUEST_TYPE, Config.CON_SUCCESS);
			out.println(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handUploadMoments() {
		try {
			String sender = message.getString("sender");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			String detail = message.getString("detail");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			Moments friends = new Moments(sender, detail);
//			new MomentsService().saveMoments(friends);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 鍙戦?娑堟伅
	private void handSendMessage() {
		try {
			String senderid = message.getString("sendid");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			String receiverid = message.getString("receiverid");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			String detail = message.getString("message");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?

			// MessageDao dao = new MessageDao();
			// boolean result = dao.sendmessage(senderid, receiverid, detail);
			InfoDao dao = new InfoDao();
			boolean result = dao.getSendMsg(senderid, receiverid, detail);

			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_SEND_MESSAGE);
			if (result == true)
				obj.put(Config.RESULT, Config.SUCCESS);
			if (result == false)
				obj.put(Config.RESULT, Config.FAIl);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 鎺ュ彈娑堟伅
	private void handGetMessage() {
		try {
			// int senderid = message.getInt("userid1");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			// int receiverid = message.getInt("userid2");// 闁告瑦鍨块敓浠嬫儍閸曨亝

			// MessageDao dao = new MessageDao();
			// JSONArray messagelist = dao.getmessage(senderid, receiverid);

			String senderid = message.getString("userid1");// 閸欐垿锟介惃鍕眽
			String receiverid = message.getString("userid2");// 閸欐垿锟介惃鍕

			System.out.println("********此处测试*****************");
			System.out.println(message.toString());

			InfoDao dao = new InfoDao();
			JSONArray messagelist = dao.getfriendNews(senderid, receiverid);

			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_GET_MESSAGE);
			obj.put(Config.RESULT, Config.SUCCESS);
			obj.put("list", messagelist);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//已测
	private void handDownloadMoments() {
		try {
			String name = message.getString("name");
			MomentsDao a=new MomentsDao();
			JSONArray arr = a.getMoments(name);
			System.out.println(arr.toString());
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD_MOMENTS);
//			System.out.println(Config.REQUEST_DOWNLOAD_MOMENTS);
			obj.put("list", arr);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取朋友列表
	private void handDownloadFriend() {
		try {
			String userid = message.getString("name");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?

			FriendsDao dao = new FriendsDao();
			JSONArray arr = dao.downloadAllFriends(userid);
			// JSONArray arr = new FriendsService().downloadFriends(userid);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD_FRIEND);
			obj.put("list", arr);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 摇一摇请求
	private void handSendRequestFriend() {
		try {
			String sender = message.getString("name");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			FriendsDao dao = new FriendsDao();
			map.put(sender, socket);
			dao.yaoyiyao(sender);

			System.out.println("handSendRequestFriend:" + sender);
			// JSONObject obj = new JSONObject();
			// obj.put(Config.REQUEST_TYPE, Config.RESULT_YAOYIYAO);
			// obj.put("name", name);
			// out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 摇一摇之后,是否添加好友的请求
	private void handAddFriend() {
		try {
			String send = message.getString("send");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			String reciver = message.getString("reciver");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			int answer = message.getInt("answer");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			FriendsDao a = new FriendsDao();
			a.aor(send, reciver, answer);

			//
			// 这里接入
			//
			// Friends history = new Friends(send, reciver, answer);
			// JSONObject obj = new JSONObject();
			// obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
			// if (new FriendsService().setState(history)) {
			// obj.put(Config.RESULT, Config.SUCCESS);
			// } else {
			// obj.put(Config.RESULT, Config.FAIl);
			// }
			// out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handRegister() {
		try {
			System.out.println(ip + "check in");

			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_REGISTER);

			User user = new User();
			user.setUsername(message.getString("username"));
			user.setPassword(message.getString("password"));
			user.setType(message.getString("type"));

			if (new UserService().register(user)) {
				obj.put(Config.RESULT, Config.SUCCESS);
				System.out.println(ip + ":" + name + "register success");
			} else {
				obj.put(Config.RESULT, Config.FAIl);
				System.out.println(ip + ":" + name + "register fail");
			}
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handLogin() {
		try {
			System.out.println(ip + "check in");

			String username = message.getString("username");
			String password = message.getString("password");

			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_LOGIN);

			UserService userService = new UserService();
			boolean result = userService.getUsernameState(username) == Config.USER_STATE_NON_ONLINE;
			if (userService.login(username, password)) {
				userService.setStateToOnline(username);
				name = username;
				map.put(username, socket);
				obj.put(Config.RESULT, Config.SUCCESS);
				System.out.println(ip + ":" + username + "login success");
			} else {
				obj.put(Config.RESULT, Config.FAIl);
				System.out.println(ip + ":" + username + "login fail");
			}
			out.println(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handExit() {
		try {
			System.out.println(ip + "check in");
			String username = message.getString("username");

			setWorkState(false);
			name = null;
			map.remove(username);
			new UserService().setStateToNonOnline(username);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_EXIT);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
			System.out.println(ip + ":" + username + " exit " + obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handDownloadInfo() {
		try {
			System.out.println(ip + "check in");
			String name = message.getString("name");
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD_INFO);
			JSONArray arr = new InfoDao().getNews(name);
			// JSONArray arr = new InfoService().downloadNews(name);
			obj.put("list", arr);
			out.println(obj.toString());
			System.out.println(ip + "download Info ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handSetInfo() {
		try {
			System.out.println(ip + "check in");
			String userid = message.getString("userid");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			String reciver = message.getString("reciver");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?
			String detail = message.getString("detail");// 闁告瑦鍨块敓浠嬫儍閸曨亝鐪?

			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_SET_INFO);
			Info info = new Info(userid, reciver, detail);
			if (new InfoService().uploadNews(info)) {
				obj.put(Config.RESULT, Config.SUCCESS);
			} else {
				obj.put(Config.RESULT, Config.FAIl);
			}
			System.out.println(ip + "set Info ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean needExecuteImmediate() {
		// TODO Auto-generated method stub
		return false;
	}
}
