package com.tcpping.message;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


import org.apache.commons.cli.ParseException;

/* TODO: maybe put a generic for message here */
public class MessageHandler {
	private BlockingQueue<BufferQueueElement<Message>> queue;

	/**
	 * Create message handler for handling each received message.
	 * @param messageIO    Stream IO reference.
	 * @param msgContainer Container of sent messages.
	 */
	public MessageHandler(BlockingQueue<BufferQueueElement<Message>> queue) {
		this.queue = queue;
	}

	/**
	 * Process received messages. Extract the timings, format and print the required message.
	 * @param  Number of messages sent
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public void processMessages(int numOfMessages) throws InterruptedException, ParseException, NumberFormatException  {
		BufferQueueElement<Message> messageList = null;
		int sum = 0;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		StringBuilder buildPrintMsg = new StringBuilder();
		RTTClass rtt;
		int padLength = Integer.toString(numOfMessages).length();
		int messageNum = 0;
        /* Wait 5s for the reading thread to notify
         * that a certain number of msgs have been received. */
		while ((messageList = queue.poll(5000, TimeUnit.MILLISECONDS)) != null) {
			if ((rtt = calculateAvgAndMaxTime(messageList)) == null) {
				System.out.println("Object returned from list is null, something is wrong");
				continue;
			}
			messageNum = messageList.getListLength();
			sum += messageNum;
			
			buildPrintMsg.setLength(0);
			buildPrintMsg.append(dateFormat.format(new Date()))
			             .append(" Messages: ")
			             .append(String.format("%-" + padLength + "d%s", messageNum, "/s"))
			             .append(" Average RTT: ")
			             .append(rtt.getAverageRTT())
			             .append("ms Max RTT: ")
			             .append(rtt.getMaxRTT())
			             .append("ms");
			System.out.println(buildPrintMsg.toString());
			if (messageList.isStreamClosed())
				break;
		}
		
		System.out.println("Messages received: " + sum );
		System.out.println("Messages lost: " + (MessageCounter.getSentMessages() - sum));
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
	 * @return             RTTClass object which holds average RTT time and max RTT time.
	 * @throws NumberFormatException
	 * @throws ParseException 
	 */
	private RTTClass calculateAvgAndMaxTime(BufferQueueElement<Message> messageList) throws NumberFormatException, ParseException {
		long accTime = 0;
		long maxTime = 0;
		long timeDiff = 0;
		int listSize = messageList.getListLength();

		if (listSize == 0)
			return null;

		RTTClass rtt = new RTTClass();
		for (Message msg : messageList.getMessageList()) {
			timeDiff = msg.getTimeStamp() - getValueFromMessage(msg.getMessage(), "%", "-");
			if (maxTime < timeDiff)
				maxTime = timeDiff;
			accTime += timeDiff;
		}

		rtt.setAverageRTT((double)accTime/(double)listSize);
		rtt.setMaxRTT(maxTime);
		return rtt;
	}
	
	private class RTTClass {
		private double averageRTT = 0;
		private long maxRTT = 0;

		public String getAverageRTT() {
			return String.format("%.3f", averageRTT);
		}
		public void setAverageRTT(double averageRTT) {
			this.averageRTT = averageRTT;
		}
		public String getMaxRTT() {
			return String.format("%.3f", (double)maxRTT/1000.0);
		}
		public void setMaxRTT(long maxRTT) {
			this.maxRTT = maxRTT;
		}
	}
}
