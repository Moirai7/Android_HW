package com.navi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.navi.net.ForwardTask;
import com.navi.net.ThreadPool;

public class TestServer {
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(6666);
			ThreadPool threadPool = ThreadPool.getInstance();
			while(true){
				Socket socket = serverSocket.accept();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println(socket.toString());
				ForwardTask task = new ForwardTask(socket);
				threadPool.addTask(task);
				task.handConSuccess();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
