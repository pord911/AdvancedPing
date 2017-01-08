package com.tcpping.pitcher;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;

import org.apache.commons.cli.ParseException;

import com.tcpping.connection.ConnType;
import com.tcpping.connection.CreateTCPConnection;
import com.tcpping.connection.TCPConnection;
import com.tcpping.message.MessageContainer;
import com.tcpping.message.MessageHandler;
import com.tcpping.message.MessageInputOutput;
import com.tcpping.tcpapp.TcpAppInterface;

public class Pitcher implements TcpAppInterface {
	private String hostName;
	private int port;
	private int messageNumber;
	private int messageSize;
	
	public Pitcher(String hostName, int port, int messageNumber, int messageSize) {
		this.hostName = hostName;
		this.messageNumber = messageNumber;
		this.messageSize = messageSize;
		this.port = port;
	}

	public void startTCPApp() {
		Timer timer = new Timer();
		MessageGenerator msgGenerator = null;
		MessageInputOutput messageIO = null;
		TCPConnection connection = null;
		MessageHandler msgHandler;
		MessageContainer msgContainer;

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
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				timer.cancel();
				if (messageIO != null)
					messageIO.closeMessageStream();
				if (connection != null)
					connection.closeConnection();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
