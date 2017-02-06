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
import com.tcpping.message.MessageInput;
import com.tcpping.message.MessageOutput;
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
		MessageInput reader = null;
		MessageOutput writer = null;
		TCPConnection connection = null;
		MessageHandler msgHandler;
		MessageContainer msgContainer;

		try {

			connection = CreateTCPConnection.createTCPConnection(ConnType.PITCHER, hostName, port);
			reader = new MessageInput(connection.getClientSocket());
			writer = new MessageOutput(connection.getClientSocket());
			msgContainer = new MessageContainer();
			msgHandler = new MessageHandler(reader, msgContainer);
			msgGenerator = new MessageGenerator(messageSize, messageNumber, writer, msgContainer);
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
				if (reader != null)
					reader.closeInputMessageStream();
				if (writer != null)
					writer.closeOutputMessageStream();
				if (connection != null)
					connection.closeConnection();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
