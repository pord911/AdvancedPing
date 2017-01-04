package com.tcpping.pitcher;

import java.io.IOException;
import java.util.TimerTask;

import org.apache.commons.lang3.RandomStringUtils;

import com.tcpping.message.MessageContainer;
import com.tcpping.message.MessageInputOutput;
import com.tcpping.time.TimingClass;


public class MessageGenerator extends TimerTask {
	private int size;
	private int msgPerSecond;
	private MessageInputOutput msgHandler;
	private int messageId = 0;
	private MessageContainer msgContainer;

	public MessageGenerator(int size, int msgPerSecond, MessageInputOutput msgHandler, MessageContainer msgContainer) {
		this.size = size;
		this.msgPerSecond = msgPerSecond;
		this.msgHandler = msgHandler;
		this.msgContainer = msgContainer;
	}

	private void sendMessages() {
		try {
			for (int i = 0; i < msgPerSecond; i++) {
				msgHandler.writeMessage(createMessage());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create a message with the following format
	 * MessageId%timeInMillis-randomChars
	 * @return
	 */
	private String createMessage() {
		long time = TimingClass.getTime();
		String timeStr = Long.toString(time);
		String message = "payload"; //RandomStringUtils.randomAlphabetic(size);

	    messageId++;
	    msgContainer.storeMessage(messageId);
	    return messageId + "%" + timeStr + "-" + message;
	}

	@Override
	public void run() {
		sendMessages();
	}
}
