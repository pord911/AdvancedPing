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
	private int pingCounter = 0;
	int sentMessages = 0;

	public MessageGenerator(int size, int msgPerSecond, MessageInputOutput msgHandler, MessageContainer msgContainer) {
		this.size = size;
		this.msgPerSecond = msgPerSecond;
		this.msgHandler = msgHandler;
		this.msgContainer = msgContainer;
	}

	private void sendMessages(String message) {
		try {
			msgHandler.writeMessage(message);
			msgContainer.storeMessage(messageId);
		} catch (IOException e) {
			System.out.println(e.getMessage());
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
		String message = RandomStringUtils.randomAlphabetic(size);

	    messageId++;
	    return messageId + "%" + timeStr + "-" + message;
	}

	@Override
	public void run() {
		int i;
		if (pingCounter < 5) {
			for (i = 0; i < msgPerSecond; i++) {
				sendMessages(createMessage());
			}
			sentMessages += i;
			pingCounter++;
		} else {
			sendMessages("BYE");
		}
	}
}