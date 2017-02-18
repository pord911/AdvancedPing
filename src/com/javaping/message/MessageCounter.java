package com.javaping.message;

public class MessageCounter {
	private static int sentMessages = 0;
	
	/**
	 * Increment when message is sent
	 */
	public static synchronized void incrementSentMessages() {
		sentMessages++;
	}

	public static synchronized int getSentMessages() {
		return sentMessages;
	}
}
