package com.tcpping.pitcher;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;

import com.tcpping.connection.CreateTCPConnection;
import com.tcpping.connection.TCPConnection;
import com.tcpping.main.ConnType;
import com.tcpping.message.MessageContainer;
import com.tcpping.message.MessageHandler;
import com.tcpping.message.MessageInputOutput;

public class Pitcher {
	private String hostName;
	private int port;
	private int messageNumber;
	private int messageSize;
	private MessageInputOutput messageIO;
	private MessageHandler msgHandler;
	private TCPConnection connection;
	private MessageContainer msgContainer;
	
	public Pitcher(String hostName, int port, int messageNumber, int messageSize) {
		this.hostName = hostName;
		this.messageNumber = messageNumber;
		this.messageSize = messageSize;
		this.port = port;
	}

	public void startTCPApp() {
		Timer timer = new Timer();
		MessageGenerator msgGenerator = null;
		try {
			connection = CreateTCPConnection.createTCPConnection(ConnType.PITCHER, hostName, port);
			messageIO = new MessageInputOutput(connection.getClientSocket());
			msgContainer = new MessageContainer();
			msgHandler = new MessageHandler(messageIO, msgContainer);
			msgGenerator = new MessageGenerator(messageSize, messageNumber, messageIO, msgContainer);
			timer.schedule(msgGenerator, 0, 1000);
			msgHandler.startReadingMessages();
			msgHandler.processMessages();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (messageIO != null)
					messageIO.closeMessageStream();
				if (connection != null)
					connection.closeConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
