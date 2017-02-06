package com.tcpping.message;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;


public class MessageHandler {
	private BlockingQueue<Message> queue;
	private MessageContainer msgContainer;

	/**
	 * Create message handler for handling each received message.
	 * @param messageIO    Stream IO reference.
	 * @param msgContainer Container of sent messages.
	 */
	public MessageHandler(BlockingQueue<Message> queue, MessageContainer msgContainer) {
		this.msgContainer = msgContainer;
		this.queue = queue;
	}

	/**
	 * Process received messages. Extract the timings, format and print the required message.
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public void processMessages() throws InterruptedException, ParseException, NumberFormatException  {
		Message obj;
		int messageAcc = 0;
		//int msgPerSec = 0;
		//long t1 = System.nanoTime();
        /* Wait 5s for the reading thread to notify
         * that a certain number of msgs have been received. */
		while ((obj = queue.poll(5000, TimeUnit.MILLISECONDS)) != null) {
			messageAcc++;
			System.out.println("Received message:" + obj.getMessage());
		}
		System.out.println("Total messages received:" + messageAcc);
	}

	/**
	 * Retrieve time from a received message based on the syntax.
	 * E.g. Retrieve a value between '&' and '/'
	 * @param message     Received message.
	 * @param firstChar   Starting character.
	 * @param secondChar  Ending character.
	 * @return            Retrieved time.
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	private long getValueFromMessage(String message, String firstChar, String secondChar) throws ParseException, NumberFormatException {
		int firstIndex = 0;
		int lastIndex = 0;

		if (firstChar != null) {
			if ((firstIndex = message.indexOf(firstChar)) != -1)
				firstIndex = firstIndex + 1;
			else
				throw new ParseException("Invalid char sequence in message " + firstChar);
		} else
			firstIndex = 0;

		if (secondChar != null) {
			if ((lastIndex = message.indexOf(secondChar)) == -1)
				throw new ParseException("Invalid char sequence in message " + secondChar);
		} else
			lastIndex = message.length();

		return Long.decode(message.substring(firstIndex, lastIndex));
	}

	/**
	 * Calculate the average and max time for the number of received messages.
	 * @param timeList     List of time stamps within 1 second.
	 * @return             String format for printing the average time and max time.
	 * @throws NumberFormatException
	 */
	private String calculateAvgAndMaxTime(List<Long> timeList) throws NumberFormatException {
		long accTime = 0;
		long maxTime = 0;
		String avgTime;

		for (Long time : timeList) {
			if (maxTime < time)
				maxTime = time;
			accTime += time;
		}

		if (timeList.size() == 0) {
			System.out.println("RTT timing list is emty.");
			return "";
		}
		avgTime = String.format("%.3f", (double) accTime/timeList.size());

		return " AvgRTT=" + avgTime + "ms" + " MaxRTT=" + Long.toString(maxTime) + "ms";
	}

	/**
	 * Calculate average B->A or A->B time for each message.
	 * @param directionTimeList   List of time stamps.
	 * @param printMsgString      Print format for A->B or B->A.
	 * @return                    Calculated and formated average time.
	 * @throws NumberFormatException
	 */
	private String calculateAvgDirectionTime(List<Long> directionTimeList, String printMsgString) throws NumberFormatException {
		long accTime = 0;
		String avgTime;

		for (Long time : directionTimeList)
			accTime += time;

		if (directionTimeList.size() == 0) {
			System.out.println("Direction timing list is emty.");
			return "";
		}
		avgTime = String.format("%.3f", (double) accTime/directionTimeList.size());

		return printMsgString + avgTime + "ms";
	}
}
