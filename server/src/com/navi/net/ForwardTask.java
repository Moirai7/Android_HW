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
import com.navi.dao.MessageDao;
import com.navi.model.Friends;
import com.navi.model.Info;
import com.navi.model.Moments;
import com.navi.model.RequestFriend;
import com.navi.model.User;
import com.navi.service.FriendsService;
import com.navi.service.InfoService;
import com.navi.service.MomentsService;
import com.navi.service.UserService;
import com.navi.util.FriendUtil;

public class ForwardTask extends Task {
	// 閺夆晜鐟ら柌娣梐shMap闁哄嫷鍨抽弫銈夊级閵夈儳鎽犻柡锟藉亾閻︹剝绋夐悮鍒糲ket閺夆晝鍋炵敮鎾儍閿燂拷
	private static HashMap<String, Socket> map = new HashMap<String, Socket>();
	private String name = null;
	private String ip;
	// 閺夊牊鎸搁崣鍡椕归敓锟�
	private BufferedReader in;
	// 閺夊牊鎸搁崵顓灻归敓锟�
	private PrintWriter out;
	// Socket閻庣數顢婇挅锟�
	private Socket socket;
	// 闁活枌鍔嶅鐢碉拷濡粯鏉归柟鎭掑劜閺佸綊鎯冮崟顏冪箚闁诡叏鎷�
	private JSONObject message;
	// 閻犲洭鏀遍惇鎵尵鐠囪尙锟�
	private int requestType;
	// 闁硅矇鍐ㄧ厬run闁哄倽顬冪涵鍫曟煂瀹�锟界暠while鐎甸偊浜為獮锟�
	private boolean onWork = true;

	// 闁规亽鍎查弫瑙勭▔閿熶粙鍤婼ocket閻庣數顢婇挅鍕儍閸曨剛锟介梺顐ゅУ閺岀喎鈻旈敓锟�
	public ForwardTask(Socket socket) {
		this.socket = socket;
		try {
			// 闁告帗绻傞‖濠囧礌閺嶎剛缈婚柛蹇嬪劜缁侊箓宕畝鍐炕闁告垹鍎ょ粊锟�
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
			String sender = message.getString("sender");// 閸欐垿锟介惃鍕眽
			String detail = message.getString("detail");// 閸欐垿锟介惃鍕眽
			Moments friends = new Moments(sender, detail);
			new MomentsService().saveMoments(friends);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 发送消息
	private void handSendMessage() {
		try {
			int senderid = message.getInt("senderid");// 閸欐垿锟介惃鍕眽
			int receiverid = message.getInt("receiverid");// 閸欐垿锟介惃鍕眽
			String detail = message.getString("detail");// 閸欐垿锟介惃鍕眽

			MessageDao dao = new MessageDao();
			boolean result = dao.sendmessage(senderid, receiverid, detail);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
			if (result == true)
				obj.put(Config.RESULT, Config.SUCCESS);
			if (result == false)
				obj.put(Config.RESULT, Config.FAIl);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 接受消息
	private void handGetMessage() {
		try {
			int senderid = message.getInt("senderid");// 閸欐垿锟介惃鍕眽
			int receiverid = message.getInt("receiverid");// 閸欐垿锟介惃鍕

			MessageDao dao = new MessageDao();
			JSONArray messagelist = dao.getmessage(senderid, receiverid);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_GET_MESSAGE);
			obj.put(Config.RESULT, Config.SUCCESS);
			obj.put("messagelist", messagelist);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handDownloadMoments() {
		try {
			String userid = message.getString("userid");// 閸欐垿锟介惃鍕眽
			String first = message.getString("first");// 閸欐垿锟介惃鍕眽
			String end = message.getString("end");// 閸欐垿锟介惃鍕眽
			JSONArray arr = new MomentsService().downloadMoments(userid, first,
					end);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD_FRIEND);
			obj.put("list", arr);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handDownloadFriend() {
		try {
			String userid = message.getString("userid");// 閸欐垿锟介惃鍕眽
			JSONArray arr = new FriendsService().downloadFriends(userid);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
			obj.put("list", arr);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handSendRequestFriend() {
		try {
			String sender = message.getString("name");// 閸欐垿锟介惃鍕眽
			String time = message.getString("time");// 閸欐垿锟介惃鍕眽
			RequestFriend r = new RequestFriend(sender, time);
			FriendUtil f = FriendUtil.getInstance();
			f.addRequest(r);
			String name = f.matchRequest(sender);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD_FRIEND);
			obj.put("name", name);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handAddFriend() {
		try {
			String send = message.getString("send");// 閸欐垿锟介惃鍕眽
			String reciver = message.getString("reciver");// 閸欐垿锟介惃鍕眽
			int answer = message.getInt("answer");// 閸欐垿锟介惃鍕眽
			Friends history = new Friends(send, reciver, answer);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_REQUIRE_FRIEND);
			if (new FriendsService().setState(history)) {
				obj.put(Config.RESULT, Config.SUCCESS);
			} else {
				obj.put(Config.RESULT, Config.FAIl);
			}
			out.println(obj.toString());
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
			if (userService.login(username, password) && result) {
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
			String name = message.getString("username");
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD_INFO);

			JSONArray arr = new InfoService().downloadNews(name);
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
			String userid = message.getString("userid");// 閸欐垿锟介惃鍕眽
			String reciver = message.getString("reciver");// 閸欐垿锟介惃鍕眽
			String detail = message.getString("detail");// 閸欐垿锟介惃鍕眽

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
