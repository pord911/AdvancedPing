package com.tcpping.catcher;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tcpping.connection.CreateTCPConnection;
import com.tcpping.connection.TCPConnection;
import com.tcpping.main.ConnType;
import com.tcpping.message.MessageInputOutput;

public class Catcher {
	private String hostName;
	private int port;
	private MessageInputOutput msgHandler;
	private TCPConnection connection;
	
	public Catcher(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}
	
	public void startTCPApp() {
		try {
			connection = CreateTCPConnection.createTCPConnection(ConnType.CATCHER, hostName, port);
			msgHandler = new MessageInputOutput(connection.getClientSocket());
			String msg;
			while ((msg = msgHandler.readMessage()) != null) {
				msgHandler.writeMessage(msg);
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
