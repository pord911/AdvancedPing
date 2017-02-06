package com.tcpping.message;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageContainer {
	private Map<Integer, Message> messageIdList;
	private int sentMessages = 0;

	/**
	 * Create a container object which will store each sent message
	 */
	public MessageContainer() {
		messageIdList = new LinkedHashMap<Integer, Message>();
	}

	/**
	 * Store each message.
	 * @param messageId    Message id.
	 */
	public synchronized void storeMessage(Message message) {
		messageIdList.put(message.getMessageId(), message);
		sentMessages++;
	}

	/**
	 * Retrieve number of sent messages.
	 * @return   Number of sent messages
	 */
	public synchronized int getSentMessages() {
		return sentMessages;
	}

	/**
	 * Check if message exists.
	 * @param messageId    Message id.
	 * @return
	 */
	public synchronized boolean checkMessageId(int messageId) {
		if (messageIdList.containsKey(messageId)) {
			messageIdList.remove(messageId);
			return true;
		}
		return false;
	}

	/**
	 * Get length of message list.
	 * @return
	 */
	public synchronized int getMessageListLenght() {
		return messageIdList.size();
	}
}
