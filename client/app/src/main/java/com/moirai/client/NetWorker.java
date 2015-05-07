package com.moirai.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;

import com.moirai.model.Info;
import com.moirai.model.User;
import com.moirai.view.BaseActivity;

public class NetWorker extends Thread {
	// Context context;
	// private static final String IP = "59.65.171.333";
	private static final String IP = "172.24.10.52";

	private static final int PORT = 6666;

	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	int dataType;
	int flag = 0;

	private Boolean onWork = true;
	protected final byte connect = 1;
	protected final byte running = 2;
	protected byte state = connect;

	JSONObject jsonObject;
	JSONArray jsonArray;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		while (onWork) {

			switch (state) {
			case connect:
				connect();
				break;
			case running:
				receiveMsg();
				break;
			}

		}
	}

	private void connect() {
		try {
			// System.out.println("ganma ne ");
			socket = new Socket(IP, PORT);
			// //Log.i(Config.TAG, "连接到服务器�?");
			// System.out.println("连接到服务器啦！");
			state = running;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receiveMsg() {
		// //Log.i(Config.TAG, "�?直在等待接受服务器返回的信息�?");
		// System.out.println("�?直在等待接受服务器返回的信息�?");
		try {
			String msg = in.readLine();
			// //Log.i(Config.TAG, "从服务器返回的消息是�?" + msg);
			// System.out.println("从服务器返回的消息是�?" + msg);
			jsonObject = new JSONObject(msg);
			dataType = jsonObject.getInt("requestType");

			System.out.println("服务器返回的消息类型:" + dataType);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch (dataType) {
		case Config.REQUEST_LOGIN:
			handLogin();// OK
			break;
		case Config.REQUEST_REGISTER:
			handRegister();// OK
			break;
		case Config.REQUEST_EXIT:
			Message msg = new Message();
			int num = 7;
			msg.what = num;
			BaseActivity.sendMessage(msg);
			break;
		case Config.REQUEST_DOWNLOAD_INFO:
			handDownloadInfo();// OK
			break;
		// case Config.REQUEST_SET_INFO:
		// handPathInfo();//OK
		// break;
		case Config.REQUEST_REQUIRE_FRIEND:
			handSendRequestFriend();// OK
			break;
		case Config.REQUEST_DOWNLOAD_FRIEND:
			// handSetRequestInfo();//OK
			break;
		case Config.REQUEST_ADDFRIEND:
			handAddFriend();// OK
			break;
		case Config.REQUEST_DOWNLOAD_MOMENTS:
			// handGetHistory();
			break;
		case Config.REQUEST_IPLOAD_MOMENTS:
			// handGetHistory();
			break;
		case Config.CON_SUCCESS:
			handCon();
			break;

		// 发�?�消息的结果 4-14
		case Config.REQUEST_SEND_MESSAGE:
			handSendInfo();
			break;
		// 获取和某�?个人的消息列�?
		case Config.REQUEST_GET_MESSAGE:
			handgetmessage();
			break;
		// 传递获取最新消息
		case Config.REQUEST_DOWNLOAD_NEWINFO:
			handdownloadnewmessage();
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

	private void handCon() {
		Message msg = new Message();
		msg.what = Config.CON_SUCCESS;
		BaseActivity.sendMessage(msg);
	}

	// 返回发�?�消息的结果
	private void handSendInfo() {
		int result = 0;
		try {
			result = jsonObject.getInt("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.arg1 = result;
		msg.what = Config.REQUEST_SEND_MESSAGE;
		BaseActivity.sendMessage(msg);
		System.out.println("发送消息的结果:" + msg.arg1 + msg.what);

	}

	// 返回获取和某�?个人的消息列表的结果
	private void handgetmessage() {

		JSONArray jo = null;
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			jo = jsonObject.optJSONArray("list");
			for (int i = 0; i < jo.length(); i++) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("messageid", jo.getJSONObject(i).getInt("messageid"));
				item.put("senderid", jo.getJSONObject(i).getString("senderid"));
				item.put("receiverid", jo.getJSONObject(i)
						.getString("senderid"));
				item.put("message", jo.getJSONObject(i).getString("message"));
				item.put("time", jo.getJSONObject(i).getString("time"));
				list.add(item);
			}
			int result = jsonObject.getInt("result");
			Message msg = new Message();
			msg.obj = list;
			msg.arg1 = result;
			msg.what = Config.REQUEST_GET_MESSAGE;
			BaseActivity.sendMessage(msg);
			System.out.println("获取和某一个人的消息列表的结果:" + list.toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// //得到位置
	// public void getHistory(String userid){
	// System.out.println("发�?�位置的请求ddd");
	// // JSOn
	// JSONObject jo = new JSONObject();
	// try {
	// jo.put("requestType", Config.REQUEST_GATHISTORY);
	// jo.put("userid", userid);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// //Log.i(Config.TAG, "发�?�位置的请求为：" + jo.toString());
	//
	// out.println(jo.toString());
	// }
	// //传�?�得到位�?
	// public void handGetHistory() {
	// //Log.i(Config.TAG, "传�?�从服务器端返回的位置的请求");
	// System.out.println("传�?�从服务器端返回的位置的请求");
	// // JSOn
	// JSONArray jo = null;
	// List<History> list = new ArrayList<History>();
	// try {
	// jo = jsonObject.optJSONArray("list");
	// for(int i = 0 ; i < jo.length() ; i++){
	// History path = new History();
	// path.setPointID(jo.getJSONObject(i).getString("pointID"));
	// path.setUserID(jo.getJSONObject(i).getString("userID"));
	// path.setTime(jo.getJSONObject(i).getString("time"));
	// list.add(path);
	// }
	// Message msg = new Message();
	// msg.obj = list;
	// msg.what = Config.REQUEST_GATHISTORY;
	// BaseActivity.sendMessage(msg);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// //保存位置
	// public void saveHistory(String userid,String pointID,String time) {
	// System.out.println("发�?�位置的请求ddd");
	// // JSOn
	// JSONObject jo = new JSONObject();
	// try {
	// jo.put("requestType", Config.REQUEST_SAVEHISTORY);
	// jo.put("userid", userid);
	// jo.put("pointID", pointID);
	// jo.put("time", time);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// //Log.i(Config.TAG, "发�?�位置的请求为：" + jo.toString());
	//
	// out.println(jo.toString());
	// }
	// //传�?�保存位�?
	// public void handSaveHistory() {
	// //Log.i(Config.TAG, "传�?�从服务器端返回的位置的请求");
	// System.out.println("传�?�从服务器端返回的位置的请求");
	// int result = 0;
	// try {
	// result = jsonObject.getInt("result");
	// Message msg = new Message();
	// msg.arg1 = result;
	// msg.what = Config.REQUEST_SAVEHISTORY;
	// BaseActivity.sendMessage(msg);
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// 修改内容s

	// 摇一摇
	public void sendRequestFriend(String username) {
		// System.out.println("发�?�消息的请求");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.RESULT_YAOYIYAO);
			jo.put("name", username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Log.i(Config.TAG, "发�?�消息的请求为：" + jo.toString());

		out.println(jo.toString());
	}

	// 传�?�修改内�?
	public void handSendRequestFriend() {

		// //Log.i(Config.TAG, "传�?�从服务器端返回的消息的请求");
		System.out.println("handSendRequestFriend结果,摇一摇的结果"
				+ jsonObject.toString());
		// JSOn
		String name = null;
		try {
			int result = jsonObject.getInt(Config.RESULT);
			if (result == Config.SUCCESS)
				name = jsonObject.getString("name");
			Message msg = new Message();
			msg.obj = name;
			msg.arg1 = result;
			msg.what = Config.RESULT_YAOYIYAO;
			BaseActivity.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 发消�?
	public void addFriend(String username, String otherid, int answer) {
		// System.out.println("发�?�消息的请求");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_ADDFRIEND);
			jo.put("send", username);
			jo.put("reciver", otherid);
			jo.put("answer", answer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// //Log.i(Config.TAG, "发�?�消息的请求为：" + jo.toString());

		out.println(jo.toString());
	}

	// 传�?�发消息
	public void handAddFriend() {
		// //Log.i(Config.TAG, "传�?�从服务器端返回的消息的请求");
		// System.out.println("传�?�从服务器端返回的消息的请求");
		// JSOn
		int result = 0;
		try {
			result = jsonObject.getInt("result");
			Message msg = new Message();
			msg.arg1 = result;
			msg.what = Config.REQUEST_ADDFRIEND;
			BaseActivity.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 下载
	/*public void downloadInfo(String username) {

		// System.out.println("发�?�下载Info的请�?");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_DOWNLOAD_INFO);
			jo.put("username", username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Log.i(Config.TAG, "发�?�单个点的请求为�?" + jo.toString());

		out.println(jo.toString());
	}*/

	// 下载最新消息
	public void handdownloadnewmessage() {
		// //Log.i(Config.TAG, "传�?�从服务器端返回的消息的请求");
		// System.out.println("传�?�从服务器端返回的消息的请求");
		// JSOn
		int result = 0;
		try {
			result = jsonObject.getInt("result");
			Message msg = new Message();
			msg.arg1 = result;
			msg.what = Config.REQUEST_DOWNLOAD_INFO;
			JSONArray a = new JSONArray(jsonObject.getString("list"));
			msg.obj = a;
			BaseActivity.sendMessage(msg);
			System.out.println("下载最新消息的结果:" + a.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 获取和某人的最新消息
	public void getnewmessage(String name) {

		// System.out.println("发�?�下载Info的请�?");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_DOWNLOAD_INFO);
			jo.put("name", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Log.i(Config.TAG, "发�?�单个点的请求为�?" + jo.toString());

		out.println(jo.toString());
	}

	// 获取和某�?个人的消息列�?
	public void getmessage(String userid1, String userid2) {

		// System.out.println("获取和某�?个人的消息列�?");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_GET_MESSAGE);
			jo.put("userid1", userid1);
			jo.put("userid2", userid2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// //Log.i(Config.TAG, "发�?�单个点的请求为�?" + jo.toString());

		out.println(jo.toString());

	}

	// 发�?�消�? 4-14
	public void sendInfo(String sendid, String receiverid, String message) {

		// System.out.println("发�?�发送Info的请�?");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_SEND_MESSAGE);
			jo.put("sendid", sendid);
			jo.put("receiverid", receiverid);
			jo.put("message", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// //Log.i(Config.TAG, "发�?�单个点的请求为�?" + jo.toString());

		out.println(jo.toString());
	}

	// //传�?�下�?
    public void handDownloadInfo() {
        JSONArray jo = null;
        List<Info> list = new ArrayList<Info>();
        jo = jsonObject.optJSONArray("list");
        System.out.println(jo.toString());
        try {
            for (int i = 0; i < jo.length(); i++) {
                JSONObject json = new JSONObject();
                json = jo.getJSONObject(i);
                Info a = new Info();

                a.setSendUser(json.getString("sendername"));

                a.setReceiver(json.getString("receivername"));
                a.setDetail(json.getString("message"));
                a.setTime(json.getString("time"));
                list.add(a);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("封装后的list:" + list.toString());
        Message msg = new Message();
        msg.obj = list;
        msg.what = Config.REQUEST_DOWNLOAD_INFO;
        BaseActivity.sendMessage(msg);
    }

	// 登录
	public void login(String userName, String password) {

		System.out.println("发�?�登录的请求ddd");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_LOGIN);
			jo.put("username", userName);
			jo.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Log.i(Config.TAG, "发�?�登录的请求为：" + jo.toString());

		out.println(jo.toString());
	}

	// 传�?�登�?
	public void handLogin() {
		// //Log.i(Config.TAG, "传�?�从服务器端返回的登录的请求");
		// System.out.println("传�?�从服务器端返回的登录的请求");
		int result = 0;
		try {
			result = jsonObject.getInt("result");
			Message msg = new Message();
			msg.arg1 = result;
			msg.what = Config.REQUEST_LOGIN;
			BaseActivity.sendMessage(msg);

			System.out.println("登陆结果:" + msg.arg1 + msg.what);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 注册
	public void register(User user) {
		// Log.i(Config.TAG, "发�?�注册的请求dd");
		System.out.println("发�?�注册的请求dd");
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_REGISTER);
			jo.put("username", user.getUsername());
			jo.put("password", user.getPassword());
			jo.put("type", user.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("**********" + out.toString());

		out.println(jo.toString());
		// //Log.i(Config.TAG, "发�?�注册的请求为：" + jo.toString());
		// System.out.println("发�?�注册的请求为：" + jo.toString());
	}

	// 传�?�注�?
	private void handRegister() {
		// //Log.i(Config.TAG, "传�?�从服务器端返回的~注册~的请�?");
		// System.out.println("传�?�从服务器端返回的~注册~的请�?");
		int result = 0;
		try {
			result = jsonObject.getInt("result");
			Message msg = new Message();
			msg.arg1 = result;
			msg.what = Config.REQUEST_REGISTER;
			BaseActivity.sendMessage(msg);

			System.out.println("注册结果:" + msg.arg1 + msg.what);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// �?出游�?
	public void exitGame() {
		// Log.i(Config.TAG, "发�?��??出游戏的请求");
		System.out.println("发�?��??出游戏的请求");
		JSONObject jo = new JSONObject();
		try {
			jo.put("username", Constant.USERNAME);
			jo.put("requestType", Config.REQUEST_EXIT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.println(jo.toString());
		// Log.i(Config.TAG, "发�?��??出游戏的请求为：" + jo.toString());
		// System.out.println("发�?��??出游戏的请求为：" + jo.toString());
	}

	public void setOnWork(Boolean onWork) {
		this.onWork = onWork;
	}

	public void sendOffLine(String userName) {
		JSONObject jo = new JSONObject();
		try {

			jo.put("userName", Constant.USERNAME);
			jo.put("requestType", Config.REQUEST_EXIT);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.println(jo.toString());

	}

}
