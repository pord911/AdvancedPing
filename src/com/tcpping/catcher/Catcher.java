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
	private StringBuilder msgAppend;
	
	public Catcher(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		msgAppend = new StringBuilder();
	}
	
	public void startTCPApp() {
		try {
			connection = CreateTCPConnection.createTCPConnection(ConnType.CATCHER, hostName, port);
			msgHandler = new MessageInputOutput(connection.getClientSocket());
			String msg;
			int catcherTime = 0;
			while ((msg = msgHandler.readMessage()) != null) {
				catcherTime = (int) System.currentTimeMillis();
				System.out.println("Message arrived: " + msg);
				msgHandler.writeMessage(appendTime(msg, catcherTime));
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
	
	private String appendTime(String message, int catcherTime) {
		String catcherMessage;
		msgAppend.append(message);
		System.out.println("Time on B: " + catcherTime);
		msgAppend.append("&" + getDirectionTime(message, catcherTime))
		         .append("/" + catcherTime);
		catcherMessage = msgAppend.toString();
		msgAppend.setLength(0);
		return catcherMessage;
	}
	
	private String getDirectionTime(String message, int catcherTime) {
		int firstIndex = message.indexOf("%");
		int lastIndex = message.indexOf("-");
		int directionTime = Integer.parseInt(message.substring(firstIndex + 1, lastIndex));
		
		System.out.println("A->B time is:" + (catcherTime - directionTime));
		return Integer.toString(catcherTime - directionTime);
	}
}
