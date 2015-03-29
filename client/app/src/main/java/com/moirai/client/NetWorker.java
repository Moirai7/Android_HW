package com.moirai.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;

import com.moirai.model.Info;
import com.moirai.model.User;
import com.moirai.view.BaseActivity;

public class NetWorker extends Thread {
	// Context context;
	private static final String IP = "172.24.3.63";
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
			System.out.println("ganma ne ");
			socket = new Socket(IP, PORT);
			Log.i(Config.TAG, "连接到服务器啦");
			System.out.println("连接到服务器啦！");
			state = running;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void receiveMsg() {
		Log.i(Config.TAG, "一直在等待接受服务器返回的信息！");
		System.out.println("一直在等待接受服务器返回的信息！");
		try {
			String msg = in.readLine();
			//Log.i(Config.TAG, "从服务器返回的消息是：" + msg);
			System.out.println("从服务器返回的消息是：" + msg);
			jsonObject = new JSONObject(msg);
			dataType = jsonObject.getInt("requestType");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch (dataType) {
		case Config.REQUEST_LOGIN: 
			handLogin();//OK
			break;
		case Config.REQUEST_REGISTER: 
			handRegister();//OK
			break;
		case Config.REQUEST_EXIT: 
			Message msg = new Message();
			int num = 7;
			msg.what = num;
			BaseActivity.sendMessage(msg);
			break;
		case Config.REQUEST_DOWNLOAD_INFO:
			handDownloadInfo();//OK
			break;
//		case Config.REQUEST_SET_INFO:
//			handPathInfo();//OK
//			break;
		case Config.REQUEST_REQUIRE_FRIEND:
            handSendRequestFriend();//OK
			break;
		case Config.REQUEST_DOWNLOAD_FRIEND:
			//handSetRequestInfo();//OK
			break;
		case Config.REQUEST_ADDFRIEND:
			handAddFriend();//OK
			break;
		case Config.REQUEST_DOWNLOAD_MOMENTS:
			//handGetHistory();
			break;
        case Config.REQUEST_IPLOAD_MOMENTS:
            //handGetHistory();
            break;
		case Config.CON_SUCCESS:
			handCon();
			break;
		default:
			 /* System.out.println("default");
				onWork=false;
				socket.close();
				socket=null;*/
			break;
		}
	}
	private void handCon() {
		Message msg = new Message();
		msg.what = Config.CON_SUCCESS;
		BaseActivity.sendMessage(msg);
	}

//	//得到位置
//	public void getHistory(String userid){
//		System.out.println("发送位置的请求ddd");
//		// JSOn
//		JSONObject jo = new JSONObject();
//		try {
//			jo.put("requestType", Config.REQUEST_GATHISTORY);
//			jo.put("userid", userid);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		Log.i(Config.TAG, "发送位置的请求为：" + jo.toString());
//
//		out.println(jo.toString());
//	}
//	//传递得到位置
//	public void handGetHistory() {
//		Log.i(Config.TAG, "传递从服务器端返回的位置的请求");
//		System.out.println("传递从服务器端返回的位置的请求");
//		// JSOn
//		JSONArray jo = null;
//		List<History> list = new ArrayList<History>();
//		try {
//			jo = jsonObject.optJSONArray("list");
//			for(int i = 0 ; i < jo.length() ; i++){
//				History path = new History();
//				path.setPointID(jo.getJSONObject(i).getString("pointID"));
//				path.setUserID(jo.getJSONObject(i).getString("userID"));
//				path.setTime(jo.getJSONObject(i).getString("time"));
//				list.add(path);
//			}
//			Message msg = new Message();
//			msg.obj = list;
//			msg.what = Config.REQUEST_GATHISTORY;
//			BaseActivity.sendMessage(msg);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	//保存位置
//	public void saveHistory(String userid,String pointID,String time) {
//		System.out.println("发送位置的请求ddd");
//		// JSOn
//		JSONObject jo = new JSONObject();
//		try {
//			jo.put("requestType", Config.REQUEST_SAVEHISTORY);
//			jo.put("userid", userid);
//			jo.put("pointID", pointID);
//			jo.put("time", time);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		Log.i(Config.TAG, "发送位置的请求为：" + jo.toString());
//
//		out.println(jo.toString());
//	}
//	//传递保存位置
//	public void handSaveHistory() {
//		Log.i(Config.TAG, "传递从服务器端返回的位置的请求");
//		System.out.println("传递从服务器端返回的位置的请求");
//		int result = 0;
//		try {
//			result = jsonObject.getInt("result");
//			Message msg = new Message();
//			msg.arg1 = result;
//			msg.what = Config.REQUEST_SAVEHISTORY;
//			BaseActivity.sendMessage(msg);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//	}
//
	//修改内容s
	public void sendRequestFriend(String username,String time){
        System.out.println("发送消息的请求");
        // JSOn
        JSONObject jo = new JSONObject();
        try {
            jo.put("requestType", Config.REQUEST_REQUIRE_FRIEND);
            jo.put("username", username);
            jo.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(Config.TAG, "发送消息的请求为：" + jo.toString());

        out.println(jo.toString());
	}
	//传递修改内容
	public void handSendRequestFriend() {
        Log.i(Config.TAG, "传递从服务器端返回的消息的请求");
        System.out.println("传递从服务器端返回的消息的请求");
        // JSOn
        String name = null;
        try {
            name = jsonObject.getString("name");
            Message msg = new Message();
            msg.obj = name;
            msg.what = Config.REQUEST_REQUIRE_FRIEND;
            BaseActivity.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	//发消息
	public void addFriend(String username,String otherid,int answer){
		System.out.println("发送消息的请求");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_ADDFRIEND);
			jo.put("send", username);
			jo.put("reciver", otherid);
            jo.put("answer",answer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(Config.TAG, "发送消息的请求为：" + jo.toString());

		out.println(jo.toString());
	}

	//传递发消息
	public void handAddFriend() {
		Log.i(Config.TAG, "传递从服务器端返回的消息的请求");
		System.out.println("传递从服务器端返回的消息的请求");
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
	public void downloadInfo(String username) {

		System.out.println("发送下载Info的请求");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_DOWNLOAD_INFO);
            jo.put("username",username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(Config.TAG, "发送单个点的请求为：" + jo.toString());

		out.println(jo.toString());
	}
//	//传递下载
	public void handDownloadInfo() {
		Log.i(Config.TAG, "传递从服务器端返回的下载Info的请求");
		// JSOn
		JSONArray jo = null;
		List<Info> list = new ArrayList<Info>();
		try {
			jo = jsonObject.optJSONArray("list");
			for(int i = 0 ; i < jo.length() ; i++){
				Info path = new Info();
                path.setSendUser(jo.getJSONObject(i).getString("sendUser"));
                path.setDetail(jo.getJSONObject(i).getString("detail"));
				path.setTime(jo.getJSONObject(i).getString("time"));
				list.add(path);
			}
			Message msg = new Message();
			msg.obj = list;
			msg.what = Config.REQUEST_DOWNLOAD_INFO;
			BaseActivity.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 登录
	public void login(String userName, String password) {

		System.out.println("发送登录的请求ddd");
		// JSOn
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_LOGIN);
			jo.put("username", userName);
			jo.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(Config.TAG, "发送登录的请求为：" + jo.toString());

		out.println(jo.toString());
	}

	// 传递登录
	public void handLogin() {
		Log.i(Config.TAG, "传递从服务器端返回的登录的请求");
		System.out.println("传递从服务器端返回的登录的请求");
		int result = 0;
		try {
			result = jsonObject.getInt("result");
			Message msg = new Message();
			msg.arg1 = result;
			msg.what = Config.REQUEST_LOGIN;
			BaseActivity.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 注册
	public void register(User user) {
		Log.i(Config.TAG, "发送注册的请求dd");
		System.out.println("发送注册的请求dd");
		JSONObject jo = new JSONObject();
		try {
			jo.put("requestType", Config.REQUEST_REGISTER);
			jo.put("username", user.getUsername());
			jo.put("password", user.getPassword());
            jo.put("type", user.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.println(jo.toString());
		Log.i(Config.TAG, "发送注册的请求为：" + jo.toString());
		System.out.println("发送注册的请求为：" + jo.toString());
	}

	// 传递注册
	private void handRegister() {
		Log.i(Config.TAG, "传递从服务器端返回的~注册~的请求");
		System.out.println("传递从服务器端返回的~注册~的请求");
		int result = 0;
		try {
			result = jsonObject.getInt("result");
			Message msg = new Message();
			msg.arg1 = result;
			msg.what = Config.REQUEST_REGISTER;
			BaseActivity.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 退出游戏
	public void exitGame() {
		Log.i(Config.TAG, "发送退出游戏的请求");
		System.out.println("发送退出游戏的请求");
		JSONObject jo = new JSONObject();
		try {
			jo.put("username", Constant.USERNAME);
			jo.put("requestType", Config.REQUEST_EXIT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		out.println(jo.toString());
		Log.i(Config.TAG, "发送退出游戏的请求为：" + jo.toString());
		System.out.println("发送退出游戏的请求为：" + jo.toString());
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
