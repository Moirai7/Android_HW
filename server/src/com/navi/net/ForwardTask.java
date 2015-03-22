package com.navi.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.navi.constant.Config;
import com.navi.model.Friends;
import com.navi.model.Moments;
import com.navi.model.Info;
import com.navi.model.User;
import com.navi.service.FriendsService;
import com.navi.service.MomentsService;
import com.navi.service.InfoService;
import com.navi.service.UserService;

public class ForwardTask extends Task{
	//鏉╂瑤閲淗ashMap閺勵垳鏁ら弶銉ョ摠閺�偓鐦℃稉鐚刼cket鏉╃偞甯撮惃锟�
	private static HashMap<String, Socket> map = new HashMap<String, Socket>();
	private String name=null;
	private String ip;
	//鏉堟挸鍙嗗ù锟�
	private BufferedReader in;
	//鏉堟挸鍤ù锟�
	private PrintWriter out;
	//Socket鐎电钖�
	private Socket socket;
	//閻劍娼电�妯绘杹閹恒儲鏁归惃鍕繆閹拷
	private JSONObject message;
	//鐠囬攱鐪扮猾璇茬�
	private int requestType;
	//閹貉冨煑run閺傝纭堕柌宀�畱while瀵邦亞骞�
	private boolean onWork=true;
	
	//閹恒儲鏁规稉锟介嚋Socket鐎电钖勯惃鍕�闁姵鏌熷▔锟�
	public ForwardTask(Socket socket){
		this.socket = socket;
		try {
			//閸掓繂顬婇崠鏍翻閸忋儲绁﹂崪宀冪翻閸戠儤绁�
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
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
//				e.printStackTrace();
				System.out.println( ip +" Client is close");
				if(name!=null){
					System.out.println(ip+":"+name+" is close");
					map.remove(name); 
					new UserService().setStateToNonOnline(name);
				}
				break; 
			}
		}
		try {
			if(socket != null){
				socket.close();
			}
			if(in != null){
				in.close();
			}
			if(out != null){
				out.close();
			}
			socket = null;
			in = null;
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setWorkState(boolean state){
		this.onWork = state;
	}
	
	
	public void receiveMessage() {
		try{
			message = new JSONObject(in.readLine());
			System.out.println(ip +": receive"+message);
			requestType = message.getInt(Config.REQUEST_TYPE);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		switch (requestType) {
		case Config.REQUEST_LOGIN: 
			handLogin();//OK
			break;
		case Config.REQUEST_REGISTER: 
			handRegister();//OK
			break;
		case Config.REQUEST_EXIT: 
			handExit();
			break;
		case Config.REQUEST_DOWNLOAD_INFO:
			handDownloadInfo();//OK
			break;
		case Config.REQUEST_SET_INFO:
			handSetInfo();//OK
			break;
		case Config.REQUEST_REQUIRE_FRIEND:
			handSendRequestFriend();//OK
			break;
		case Config.REQUEST_ADDFRIEND:
			handAddFriend();//OK
			break;
		case Config.REQUEST_DOWNLOAD_FRIEND:
			handDownloadFriend();//OK
			break;
		case Config.REQUEST_DOWNLOAD_MOMENTS:
			handDownloadMoments();
			break;
		case Config.REQUEST_UPLOAD_MOMENTS:
			handUploadMoments();
			break;
		default:
			 /* System.out.println("default");
				onWork=false;
				socket.close();
				socket=null;*/
			break;
		}
	}

	public void handConSuccess(){
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
			String sender = message.getString("sender");//鍙戦�鐨勪汉
			String detail = message.getString("detail");//鍙戦�鐨勪汉
			Moments friends = new Moments(sender,detail);
			new MomentsService().saveMoments(friends);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void handDownloadMoments() {
		try {
			String userid = message.getString("userid");//鍙戦�鐨勪汉
			String first = message.getString("first");//鍙戦�鐨勪汉
			String end = message.getString("end");//鍙戦�鐨勪汉
			JSONArray arr = new MomentsService().downloadMoments(userid,first,end);
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
			String userid = message.getString("userid");//鍙戦�鐨勪汉
			JSONArray arr = new FriendsService().downloadFriends(userid);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD_FRIEND);
			obj.put("list", arr);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void handSendRequestFriend() {
		try {
			String send = message.getString("send");//鍙戦�鐨勪汉
			String reciver = message.getString("reciver");//鍙戦�鐨勪汉
			int answer = message.getInt("answer");//鍙戦�鐨勪汉
			Friends history = new Friends(send,reciver,answer);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_REQUIRE_FRIEND);
			if(new FriendsService().setState(history)){
				obj.put(Config.RESULT, Config.SUCCESS);
			}else{
				obj.put(Config.RESULT, Config.FAIl);
			}
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void handAddFriend() {
		try {
			String sender = message.getString("username");//鍙戦�鐨勪汉
			String receiver = message.getString("reciver");//鍙戦�鐨勪汉
			Friends friends = new Friends(sender,receiver);
			new FriendsService().requireFriend(friends);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_ADDFRIEND);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void handRegister() {
		try {
			System.out.println(ip+"check in");
			
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_REGISTER);
			
			User user = new User();
			user.setUsername(message.getString("username"));
			user.setPassword(message.getString("password"));
			user.setType(message.getString("type"));
			
			if(new UserService().register(user)){
				obj.put(Config.RESULT, Config.SUCCESS);
				System.out.println(ip + ":" + name + "register success");
			}else{
				obj.put(Config.RESULT, Config.FAIl);
				System.out.println(ip + ":" + name + "register fail");
			}
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handLogin(){
		try {
			System.out.println(ip + "check in");
			
			String username = message.getString("username");
			String password = message.getString("password");
			
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_LOGIN);
			
			UserService userService = new UserService();
			boolean result=userService.getUsernameState(username)==Config.USER_STATE_NON_ONLINE;
			if(userService.login(username, password)&&result){
				userService.setStateToOnline(username);
				name = username;
				map.put(username, socket);
				obj.put(Config.RESULT, Config.SUCCESS);
				System.out.println(ip+":"+ username + "login success");
			}else{
				obj.put(Config.RESULT, Config.FAIl);
				System.out.println(ip+":"+ username + "login fail");
			}
			out.println(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handExit(){
		try {
			System.out.println(ip+"check in");
			String username = message.getString("username");

			setWorkState(false);
			name = null;
			map.remove(username);
			new UserService().setStateToNonOnline(username);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_EXIT);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
			System.out.println(ip + ":" + username + " exit "+obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handDownloadInfo() {
		try {
			System.out.println(ip+"check in");
			
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
	
	private void handSetInfo(){
		try {
			System.out.println(ip+"check in");
			String userid = message.getString("userid");//鍙戦�鐨勪汉
			String reciver = message.getString("reciver");//鍙戦�鐨勪汉
			String detail = message.getString("detail");//鍙戦�鐨勪汉
			
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_SET_INFO);
			Info info = new Info(userid,reciver,detail);
			if(new InfoService().uploadNews(info)){
				obj.put(Config.RESULT, Config.SUCCESS);
			}else{
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
