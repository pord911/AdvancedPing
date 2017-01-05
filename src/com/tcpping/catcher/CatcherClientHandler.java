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
				System.out.println("Message arrived: " + msg);
				/*
				 * dropPacket++; if (dropPacket % 2 == 0) continue;
				 */
				if (msg == "BYE") {
					msgHandler.writeMessage("OKBYE");
					break;
				}
				msgHandler.writeMessage(appendTime(msg, catcherTime));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				msgHandler.closeMessageStream();
				if (connection != null)
					connection.close();
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

	public void run() {
		startReadingMessages();
	}
}
