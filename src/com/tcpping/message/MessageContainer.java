package com.tcpping.message;

import java.util.LinkedHashMap;

public class MessageContainer {
	private LinkedHashMap<Integer, String> messageIdList;

	public MessageContainer() {
		messageIdList = new LinkedHashMap<Integer, String>();
	}

	public synchronized void storeMessage(int messageId) {
		messageIdList.put(messageId, Integer.toString(messageId));
	}

	public synchronized boolean checkMessageId(int messageId) {
		if (messageIdList.containsKey(messageId)) {
			messageIdList.remove(messageId);
			return true;
		}
		return false;
	}

	public synchronized int getMessageListLenght() {
		return messageIdList.size();
	}
}
