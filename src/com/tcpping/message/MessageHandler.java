package com.tcpping.message;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {
	private MessageInputOutput messageIO;
	private BlockingQueue<String> queue;

	public MessageHandler(MessageInputOutput messageIO) {
		this.messageIO = messageIO;
		queue = new LinkedBlockingQueue<String>();
	}

	public void startReadingMessages() throws IOException {
		(new Thread(new ReadMessages(messageIO))).start();
		
/*		int initTime = (int) System.currentTimeMillis();
		String line = null;
		int messageId;
		int lastIndex;
		int firstIndex;
		int id;
		int time = 0;
		int diff = 0;

		while ((line = messageIO.readMessage()) != null) {
			diff = (int) System.currentTimeMillis() - initTime;
			lastIndex = line.lastIndexOf('-');
			firstIndex = line.indexOf('%');
			id = Integer.parseInt(line.substring(0, firstIndex));
			time = (int) System.currentTimeMillis() - Integer.parseInt(line.substring(firstIndex + 1, lastIndex));
			messageAcc++;
			numberOfMsgs++;

			if (diff > 1000) {
				//processMessages();
				initTime = 0;
				numberOfMsgs = 0;
			}
		}*/
	}
	
	private class ReadMessages implements Runnable {
		private MessageInputOutput messageIO;
		
		public ReadMessages(MessageInputOutput messageIO) {
			this.messageIO = messageIO;
		}
		
		public void run() {
			String line = null;
			int initTime = (int) System.currentTimeMillis();
			int diff = 0;
			int messageAcc = 0;
			int numberOfMsgs = 0;

			try {
				while ((line = messageIO.readMessage()) != null) {
					diff = (int) System.currentTimeMillis() - initTime;
					messageAcc++;
					numberOfMsgs++;
					if (diff > 1000) {
						initTime = (int) System.currentTimeMillis();
						queue.put(Integer.toString(numberOfMsgs));
						numberOfMsgs = 0;
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

	private class BufferObject {
		private List<String> lineList;
		private int msgAcc;
		private int msgNumber;

		public BufferObject() {
			lineList = new LinkedList<String>();
		}

		public List<String> getLineList() {
			return lineList;
		}

		public void addListElement(String line) {
			lineList.add(line);
		}

		public int getMsgAcc() {
			return msgAcc;
		}

		public void setMsgAcc(int msgAcc) {
			this.msgAcc = msgAcc;
		}

		public int getMsgNumber() {
			return msgNumber;
		}

		public void setMsgNumber(int msgNumber) {
			this.msgNumber = msgNumber;
		}
	}

	public void processMessages() throws InterruptedException {
		String num = null;
		while ((num = queue.take().toString()) != null)
			System.out.println("Rate:" + num + "/s");
	}
}
