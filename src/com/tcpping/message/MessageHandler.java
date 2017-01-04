package com.tcpping.message;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.tcpping.time.TimingClass;

public class MessageHandler {
	private MessageInputOutput messageIO;
	private LinkedBlockingQueue<BufferQueueElement> queue;
	private MessageContainer msgContainer;

	public MessageHandler(MessageInputOutput messageIO, MessageContainer msgContainer) {
		this.messageIO = messageIO;
		queue = new LinkedBlockingQueue<BufferQueueElement>();
		this.msgContainer = msgContainer;
	}

	public void startReadingMessages() throws IOException {
		(new Thread(new ReadMessages(messageIO))).start();
	}

	private class ReadMessages implements Runnable {
		private MessageInputOutput messageIO;
		private BufferQueueElement bufferElement;

		public ReadMessages(MessageInputOutput messageIO) {
			this.messageIO = messageIO;
		}

		public void run() {
			String line = null;
			long initTime = TimingClass.getTime();
			long diff = 0;
			int messageAcc = 0;
			int numberOfMsgs = 0;
			long currentTime = 0;
			bufferElement = new BufferQueueElement();

			try {
				while ((line = messageIO.readMessage()) != null) {
					currentTime = TimingClass.getTime();
					diff = currentTime - initTime;
					messageAcc++;
					numberOfMsgs++;
					bufferElement.addListElement(currentTime, line);
					if (diff > 1000) {
						initTime = TimingClass.getTime();
						bufferElement.setMsgAcc(messageAcc);
						bufferElement.setMsgNumber(numberOfMsgs);
						queue.put((bufferElement));
						numberOfMsgs = 0;
						bufferElement = new BufferQueueElement();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void processMessages() throws InterruptedException {
		BufferQueueElement obj = null;
		Map<Long, String> list = null;
		Set<Long> keySet = null;
		int lostMessages = 0;
		int messageId;
		List<Long> roundTripList = new LinkedList<Long>();
		List<Long> frontDirectionTimeList = new LinkedList<Long>();
		List<Long> backDirectionTimeList = new LinkedList<Long>();
		String message = null;
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
        StringBuilder printMessage = new StringBuilder();

		while ((obj = queue.take()) != null) {
			printMessage.append(dateFormat.format(date))
			            .append(": Total=" + Integer.toString(obj.getMsgAcc()))
			            .append(" Rate=" + Integer.toString(obj.getMsgNumber()) + "/s");

			list = obj.getLineList();
			keySet = list.keySet();
			for (Long timeKey : keySet) {
				message = list.get(timeKey);
				messageId = (int)getValueFromMessage(message, null, "%");
				if (!msgContainer.checkMessageId(messageId)) {
					lostMessages++;
					continue;
				}
				roundTripList.add(timeKey - getValueFromMessage(message, "%", "-"));
				frontDirectionTimeList.add(getValueFromMessage(message, "&", "/"));
				backDirectionTimeList.add(timeKey - getValueFromMessage(message, "/", null));
			}
			printMessage.append(" Lost=" + lostMessages)
			            .append(calculateAvgAndMaxTime(roundTripList))
			            .append(calculateAvgDirectionTime(frontDirectionTimeList, " A->B="))
			            .append(calculateAvgDirectionTime(backDirectionTimeList, " B->A="));

			System.out.println(printMessage.toString());

			frontDirectionTimeList.clear();
			backDirectionTimeList.clear();
			roundTripList.clear();
			printMessage.setLength(0);
			lostMessages = 0;
		}
	}

	private long getValueFromMessage(String message, String firstChar, String secondChar) {
		// TODO: dodati NoCharFoundException
		int firstIndex;
		int lastIndex;

		if (firstChar != null)
			firstIndex = message.indexOf(firstChar) + 1;
		else
			firstIndex = 0;

		if (secondChar != null)
			lastIndex = message.indexOf(secondChar);
		else
			lastIndex = message.length();
		
		return Long.decode(message.substring(firstIndex, lastIndex));
	}

	private String calculateAvgAndMaxTime(List<Long> timeList) {
		long accTime = 0;
		long maxTime = 0;
		String avgTime = null;

		for (Long time : timeList) {
			if (maxTime < time)
				maxTime = time;
			accTime += time;
		}
		avgTime = Long.toString(accTime/timeList.size());

		return " AvgRTT=" + avgTime + "ms" + " MaxRTT=" + Long.toString(maxTime);
	}

	private String calculateAvgDirectionTime(List<Long> directionTimeList, String printMsgString) {
		long accTime = 0;

		for (Long time : directionTimeList) {
			accTime += time;
		}

		return printMsgString + Long.toString(accTime/directionTimeList.size());
	}
	
	
}
