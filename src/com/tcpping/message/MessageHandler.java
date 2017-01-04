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

public class MessageHandler {
	private MessageInputOutput messageIO;
	private LinkedBlockingQueue<BufferQueueElement> queue;

	public MessageHandler(MessageInputOutput messageIO) {
		this.messageIO = messageIO;
		queue = new LinkedBlockingQueue<BufferQueueElement>();
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
			int initTime = (int) System.currentTimeMillis();
			int diff = 0;
			int messageAcc = 0;
			int numberOfMsgs = 0;
			int currentTime = 0;
			bufferElement = new BufferQueueElement();

			try {
				while ((line = messageIO.readMessage()) != null) {
					currentTime = (int) System.currentTimeMillis();
					diff = currentTime - initTime;
					messageAcc++;
					numberOfMsgs++;
					bufferElement.addListElement(currentTime, line);
					if (diff > 1000) {
						initTime = (int) System.currentTimeMillis();
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
		Map<Integer, String> list = null;
		Set<Integer> keySet = null;
		int lostMessages = 0;
		List<Integer> roundTripList = new LinkedList<Integer>();
		List<Integer> frontDirectionTimeList = new LinkedList<Integer>();
		List<Integer> backDirectionTimeList = new LinkedList<Integer>();
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
			for (Integer timeKey : keySet) {
				message = list.get(timeKey);
				if (!checkIdFromMessage(message)) {
					lostMessages++;
					continue;
				}
			/*	System.out.println("Message is: " + message);
				System.out.println("Time of start: " + getTimeFromMessage(message, "%", "-"));
				System.out.println("A->B Difference: " + getTimeFromMessage(message, "&", "/")); */
				System.out.println("Time on B: " + getTimeFromMessage(message, "/", null)); 
				System.out.println("Packet arrival time: " + timeKey);
				roundTripList.add(timeKey - getTimeFromMessage(message, "%", "-"));
				frontDirectionTimeList.add(getTimeFromMessage(message, "&", "/"));
				backDirectionTimeList.add(timeKey - getTimeFromMessage(message, "/", null));
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

	private boolean checkIdFromMessage(String message) {
		return true;
	}

	private int getTimeFromMessage(String message, String firstChar, String secondChar) {
		// TODO: dodati NoCharFoundException
		int firstIndex = message.indexOf(firstChar);
		int lastIndex;

		if (secondChar != null)
			lastIndex = message.indexOf(secondChar);
		else
			lastIndex = message.length();
		
		return Integer.parseInt(message.substring(firstIndex + 1, lastIndex));
	}

	private String calculateAvgAndMaxTime(List<Integer> timeList) {
		int accTime = 0;
		int maxTime = 0;
		String avgTime = null;

		for (Integer time : timeList) {
			if (maxTime < time)
				maxTime = time;
			accTime += time;
		}
		avgTime = Integer.toString(accTime/timeList.size());

		return " AvgRTT=" + avgTime + "ms" + " MaxRTT=" + Integer.toString(maxTime);
	}

	private String calculateAvgDirectionTime(List<Integer> directionTimeList, String printMsgString) {
		int accTime = 0;

		for (Integer time : directionTimeList) {
			accTime += time;
		}
		//System.out.println("Sum is:" + accTime + " Size of list:" + directionTimeList.size());
		return printMsgString + Integer.toString(accTime/directionTimeList.size());
	}
	
	
}
