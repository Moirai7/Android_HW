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
import com.navi.model.History;
import com.navi.model.Path;
import com.navi.model.User;
import com.navi.service.HistoryService;
import com.navi.service.PathService;
import com.navi.service.UserService;

public class ForwardTask extends Task{
	//杩欎釜HashMap鏄敤鏉ュ瓨鏀炬瘡涓猄ocket杩炴帴鐨�
	private static HashMap<String, Socket> map = new HashMap<String, Socket>();
	private String name=null;
	private String ip;
	//杈撳叆娴�
	private BufferedReader in;
	//杈撳嚭娴�
	private PrintWriter out;
	//Socket瀵硅薄
	private Socket socket;
	//鐢ㄦ潵瀛樻斁鎺ユ敹鐨勪俊鎭�
	private JSONObject message;
	//璇锋眰绫诲瀷
	private int requestType;
	//鎺у埗run鏂规硶閲岀殑while寰幆
	private boolean onWork=true;
	
	//鎺ユ敹涓�釜Socket瀵硅薄鐨勬瀯閫犳柟娉�
	public ForwardTask(Socket socket){
		this.socket = socket;
		try {
			//鍒濆鍖栬緭鍏ユ祦鍜岃緭鍑烘祦
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
		case Config.REQUEST_DOWNLOAD:
			handDownload();//OK
			break;
		case Config.REQUEST_PATHINFO:
			handGetPath();//OK
			break;
		case Config.REQUEST_SENDREQUEST:
			handSendRequest();//OK
			break;
		case Config.REQUEST_SETREQUESTINFO:
			handSetRequestInfo();//OK
			break;
		case Config.REQUEST_SAVEHISTORY:
			handSaveHistory();//OK
			break;
		case Config.REQUEST_GATHISTORY:
			handGetHistory();
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
	
	private void handGetHistory() {
		try {
			String userid = message.getString("userid");//发送的人
			JSONArray arr = new HistoryService().getHistory(userid);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_GATHISTORY);
			obj.put("list", arr);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void handSaveHistory() {
		try {
			String userid = message.getString("userid");//发送的人
			String pointID = message.getString("pointID");//发送的人
			String time = message.getString("time");//发送的人
			History history = new History(userid,pointID,time);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_SAVEHISTORY);
			if(new HistoryService().saveHistory(history)){
				obj.put(Config.RESULT, Config.SUCCESS);
			}else{
				obj.put(Config.RESULT, Config.FAIl);
			}
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void handSetRequestInfo() {
		try {
			String sender = message.getString("username");//发送的人
			String receiver = message.getString("reciver");//发送的人
			String detail = message.getString("detail");//发送的人
			new UserService().setRequestInfo(sender, receiver, detail);
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_SETREQUESTINFO);
			obj.put(Config.RESULT, Config.SUCCESS);
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void handSendRequest() {
		try {
			String sender = message.getString("username");//发送的人
			String receiver = message.getString("reciver");//发送的人
			String point = message.getString("pointid");//发送的人
			Socket sendSocket = map.get(receiver);
			PrintWriter outSend = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(sendSocket.getOutputStream(),
							"UTF-8")), true);
			JSONObject sendObject = new JSONObject();
			sendObject.put("requestType", Config.REQUEST_SENDREQUEST);
			JSONArray model = new UserService().sendRequest(sender, point);
			sendObject.put("info", model);
			//out.println(sendObject.toString());
			outSend.println(sendObject.toString());
			System.out.println(sendObject.toString()+"这是发送请求时的json");
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
			String name = message.getString("username");
			user.setUsername(name);
			user.setPassword(message.getString("password"));
			
			if(new UserService().register(user)){
				obj.put(Config.RESULT, Config.SUCCESS);
				System.out.println(ip + ":" + name + "注册成功");
			}else{
				obj.put(Config.RESULT, Config.FAIl);
				System.out.println(ip + ":" + name + "注册失败");
			}
			out.println(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void handLogin(){
		//out.println("服务器给的");
		try {
			System.out.println(ip + "check in");
			
			String username = message.getString("username");
			String password = message.getString("password");
			
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_LOGIN);
			
			UserService userService = new UserService();
			boolean result=userService.getUsernameState(username)==Config.USER_STATE_NON_ONLINE;
			if(userService.login(username, password)&&result){
				name = username;
				map.put(username, socket);
				obj.put(Config.RESULT, Config.SUCCESS);
				System.out.println(ip+":"+ username + "成功");
			}else{
				obj.put(Config.RESULT, Config.FAIl);
				System.out.println(ip+":"+ username + "失败");
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

	private void handDownload() {
		try {
			System.out.println(ip+"check in");
			
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_DOWNLOAD);
			
			JSONArray arr = new PathService().download();
			obj.put("list", arr);
			out.println(obj.toString());
			System.out.println(ip + "获取map成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handGetPath(){
		try {
			System.out.println(ip+"check in");
			String pointid = message.getString("pointid");
			
			JSONObject obj = new JSONObject();
			obj.put(Config.REQUEST_TYPE, Config.REQUEST_PATHINFO);
			
			JSONArray arr = new PathService().getPathInfo(pointid);
			obj.put("list", arr);
			out.println(obj.toString());
			System.out.println(ip + "获取map成功");
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
