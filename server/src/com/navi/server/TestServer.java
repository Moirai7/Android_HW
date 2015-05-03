package com.navi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.navi.net.ForwardTask;
import com.navi.net.ThreadPool;

public class TestServer {
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(8888);
			ThreadPool threadPool = ThreadPool.getInstance();
			while(true){
				Socket socket = serverSocket.accept();
				System.out.println(socket.toString()+"¡¨…œ¡À");
				ForwardTask task = new ForwardTask(socket);
				threadPool.addTask(task);
				task.handConSuccess();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
