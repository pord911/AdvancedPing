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
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.ParseException;

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
					if (line.equals("OKBYE")) {
						bufferElement.setMsgAcc(messageAcc);
					    bufferElement.setMsgNumber(numberOfMsgs);
						bufferElement.setCloseQueue(true);
						queue.put(bufferElement);
						break;
					}

					messageAcc++;
					numberOfMsgs++;
					bufferElement.addListElement(line, currentTime);

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
				System.out.println(e.getMessage());
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void processMessages() throws InterruptedException, ParseException, NumberFormatException  {
		BufferQueueElement obj;
		Map<String, Long> list;
		Set<String> keySet;
		int messageId;
		List<Long> roundTripList = new LinkedList<Long>();
		List<Long> frontDirectionTimeList = new LinkedList<Long>();
		List<Long> backDirectionTimeList = new LinkedList<Long>();
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
        StringBuilder printMessage = new StringBuilder();
        long time = 0;

		while ((obj = queue.poll(5000, TimeUnit.MILLISECONDS)) != null) {
			printMessage.append(dateFormat.format(date))
			            .append(": Total=" + Integer.toString(obj.getMsgAcc()))
			            .append(" Rate=" + Integer.toString(obj.getMsgNumber()) + "/s");

			list = obj.getLineList();
			keySet = list.keySet();
			System.out.println("Number of keys: " + keySet.size());
			for (String message : keySet) {
				time = list.get(message);
				messageId = (int) getValueFromMessage(message, null, "%");
				if (!msgContainer.checkMessageId(messageId)) {
					continue;
				}
				roundTripList.add(time - getValueFromMessage(message, "%", "-"));
				frontDirectionTimeList.add(getValueFromMessage(message, "&", "/"));
				backDirectionTimeList.add(time - getValueFromMessage(message, "/", null));
			}
			printMessage.append(calculateAvgAndMaxTime(roundTripList))
			            .append(calculateAvgDirectionTime(frontDirectionTimeList, " A->B="))
			            .append(calculateAvgDirectionTime(backDirectionTimeList, " B->A="));

			System.out.println(printMessage.toString());

			frontDirectionTimeList.clear();
			backDirectionTimeList.clear();
			roundTripList.clear();
			printMessage.setLength(0);
			// Reader thread finished reading, stop the consumer
			if (obj.isCloseQueue())
				break;
		}
		// TODO: Ovdje staviti poruku kao u stvarnom ping, broj poslanih broj primljenih, i broj izgubljenih
		System.out.println();
		System.out.println("Messages lost: " + msgContainer.getMessageListLenght());
	}

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
				throw new ParseException("Invalid char sequence in message " + firstChar);
		} else
			lastIndex = message.length();

		return Long.decode(message.substring(firstIndex, lastIndex));
	}

	private String calculateAvgAndMaxTime(List<Long> timeList) throws NumberFormatException {
		long accTime = 0;
		long maxTime = 0;
		String avgTime;

		for (Long time : timeList) {
			if (maxTime < time)
				maxTime = time;
			accTime += time;
		}
		avgTime = String.format("%.3f", (double) accTime/timeList.size());

		return " AvgRTT=" + avgTime + "ms" + " MaxRTT=" + Long.toString(maxTime) + "ms";
	}

	private String calculateAvgDirectionTime(List<Long> directionTimeList, String printMsgString) throws NumberFormatException {
		long accTime = 0;
		String avgTime;

		for (Long time : directionTimeList) {
			accTime += time;
		}
		avgTime = String.format("%.3f", (double) accTime/directionTimeList.size());

		return printMsgString + avgTime + "ms";
	}
}
