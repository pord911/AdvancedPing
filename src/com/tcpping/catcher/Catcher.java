package com.tcpping.catcher;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tcpping.connection.CreateTCPConnection;
import com.tcpping.connection.TCPConnection;
import com.tcpping.main.ConnType;
import com.tcpping.message.MessageInputOutput;
import com.tcpping.time.TimingClass;

public class Catcher {
	private String hostName;
	private int port;
	private MessageInputOutput msgHandler;
	private TCPConnection connection;
	private StringBuilder msgAppend;
	private int dropPacket = 0;
	
	public Catcher(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		msgAppend = new StringBuilder();
	}
	
	public void startTCPApp() {
		try {
			while ((connection = CreateTCPConnection.createTCPConnection(ConnType.CATCHER, hostName, port)) != null) {
				msgHandler = new MessageInputOutput(connection.getClientSocket());
				(new Thread(new CatcherThread(msgHandler))).start();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				msgHandler.closeMessageStream();
				connection.closeConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
