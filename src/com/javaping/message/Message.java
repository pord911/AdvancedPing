package com.javaping.message;

import com.javaping.time.TimingClass;

public class Message {
	private long timeStamp;
	private String message;
	private int messageId;

	public Message() {
		this.timeStamp = TimingClass.getTime();
	}
	public Message(String message) {
		this.message = message;
		timeStamp = TimingClass.getTime();
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
