package com.tcpping.catcher;

import java.io.IOException;
import java.net.Socket;

import com.tcpping.message.MessageInputOutput;
import com.tcpping.time.TimingClass;

public class CatcherClientHandler implements Runnable {
	private MessageInputOutput msgHandler;
	private Socket connection;
	private StringBuilder msgAppend;

	public CatcherClientHandler(Socket connection) throws IOException {
		this.connection = connection;
		msgHandler = new MessageInputOutput(connection);
		msgAppend = new StringBuilder();
	}

	private void startReadingMessages() {
		String msg;
		long catcherTime = 0;
		try {
			while ((msg = msgHandler.readMessage()) != null) {
				catcherTime = TimingClass.getTime();
				if (msg.equals("BYE")) {
					System.out.println("Sending BYE");
					msgHandler.writeMessage("OKBYE");
					break;
				}
				msgHandler.writeMessage(appendTime(msg, catcherTime));
			}
		} catch (IOException e) {
			System.out.print(e.getMessage());
		} finally {
			try {
				msgHandler.closeMessageStream();
				if (connection != null)
					connection.close();
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
		}
	}

	private String appendTime(String message, long catcherTime) {
		msgAppend.setLength(0);
		msgAppend.append(message);
		msgAppend.append("&" + getDirectionTime(message, catcherTime))
		         .append("/" + catcherTime);
		return msgAppend.toString();
	}

	private String getDirectionTime(String message, long catcherTime) {
		int firstIndex = message.indexOf("%");
		int lastIndex = message.indexOf("-");
		long directionTime = Long.decode(message.substring(firstIndex + 1, lastIndex));

		return Long.toString(catcherTime - directionTime);
	}

	public void run() {
		startReadingMessages();
	}
}
