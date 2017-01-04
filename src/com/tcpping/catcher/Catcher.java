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
			connection = CreateTCPConnection.createTCPConnection(ConnType.CATCHER, hostName, port);
			msgHandler = new MessageInputOutput(connection.getClientSocket());
			String msg;
			long catcherTime = 0;
			while ((msg = msgHandler.readMessage()) != null) {
				catcherTime = TimingClass.getTime();
				System.out.println("Message arrived: " + msg);
				dropPacket++;
				if (dropPacket % 2 == 0)
					continue;
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

	private String appendTime(String message, long catcherTime) {
		String catcherMessage;
		msgAppend.append(message);
		System.out.println("Time on B: " + catcherTime);
		msgAppend.append("&" + getDirectionTime(message, catcherTime))
		         .append("/" + catcherTime);
		catcherMessage = msgAppend.toString();
		msgAppend.setLength(0);
		return catcherMessage;
	}

	private String getDirectionTime(String message, long catcherTime) {
		int firstIndex = message.indexOf("%");
		int lastIndex = message.indexOf("-");
		long directionTime = Long.decode(message.substring(firstIndex + 1, lastIndex));
		
		System.out.println("A->B time is:" + (catcherTime - directionTime));
		return Long.toString(catcherTime - directionTime);
	}
}
