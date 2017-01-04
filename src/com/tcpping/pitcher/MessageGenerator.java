package com.tcpping.pitcher;

import java.io.IOException;
import java.util.TimerTask;

import org.apache.commons.lang3.RandomStringUtils;

import com.tcpping.message.MessageInputOutput;


public class MessageGenerator extends TimerTask {
	private int size;
	private int msgPerSecond;
	private MessageInputOutput msgHandler;
	private int messageId = 0;

	public MessageGenerator(int size, int msgPerSecond, MessageInputOutput msgHandler) {
		this.size = size;
		this.msgPerSecond = msgPerSecond;
		this.msgHandler = msgHandler;
	}

	private void sendMessages() {
		//TODO: možda staviti for petlju unutar try
		//System.out.println("Start writing messages");
		for (int i = 0; i < msgPerSecond; i++) {
			try {
				msgHandler.writeMessage(createMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create a message with the following format
	 * MessageId%timeInMillis-randomChars
	 * @return
	 */
	private String createMessage() {
		// TODO: provjeriti da li je dobro ovdje prebacivati u int
		int time = (int)System.currentTimeMillis();
		String timeStr = Integer.toString(time);
		int timeLength = timeStr.length();
		String message = "payload"; //RandomStringUtils.randomAlphabetic(size - timeLength - 3);
		
	    messageId++;
	    return messageId + "%" + timeStr + "-" + message;
	}

	@Override
	public void run() {
		sendMessages();
	}
}
