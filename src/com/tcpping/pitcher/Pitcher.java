package com.tcpping.pitcher;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.cli.ParseException;

import com.tcpping.connection.ConnType;
import com.tcpping.connection.CreateTCPConnection;
import com.tcpping.connection.TCPConnection;
import com.tcpping.message.BufferQueueElement;
import com.tcpping.message.Message;
import com.tcpping.message.MessageContainer;
import com.tcpping.message.MessageHandler;
import com.tcpping.message.MessageInput;
import com.tcpping.message.MessageOutput;
import com.tcpping.message.MessageReader;
import com.tcpping.tcpapp.TcpAppInterface;

public class Pitcher implements TcpAppInterface {
	private String hostName;
	private int port;
	private int messageNumber;
	private int messageSize;

	/**
	 * Create Pitcher object.
	 * @param hostName      Host name or IP address.
	 * @param port          Port number.
	 * @param messageNumber Number of messages to send each second
	 * @param messageSize   Size of each message.
	 */
	public Pitcher(String hostName, int port, int messageNumber, int messageSize) {
		this.hostName = hostName;
		this.messageNumber = messageNumber;
		this.messageSize = messageSize;
		this.port = port;
	}

	/**
	 * Start running Pitcher object. Create a generator
	 * which will run in a separate thread, and a message handler
	 * which will receive back the sent messages.
	 */
	public void startTCPApp() {
		Timer timer = new Timer();
		MessageGenerator msgGenerator = null;
		TCPConnection connection = null;
		MessageHandler msgHandler;
		MessageContainer msgContainer;
		BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

		try {

			connection = CreateTCPConnection.createTCPConnection(ConnType.PITCHER, hostName, port);
			msgContainer = new MessageContainer();
			msgHandler = new MessageHandler(queue, msgContainer);
			msgGenerator = new MessageGenerator(messageSize, messageNumber, connection, msgContainer);
			(new Thread(new MessageReader(connection, queue))).start();
			timer.schedule(msgGenerator, 0, 1000);
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
				if (connection != null)
					connection.closeConnection();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
