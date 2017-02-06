package com.tcpping.message;

import com.tcpping.time.TimingClass;

public class Message {
	private final long timeStamp = TimingClass.getTime();
	private String message;
	private int messageId;

	public Message() {}
	public Message(String message) {
		this.message = message;
	}

	public Message(String message, int messageId) {
		this.message = message;
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}
}
