package com.tcpping.message;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.tcpping.time.TimingClass;

public class MessageReader implements Runnable {
	private MessageInputOutput messageIO;
	private BlockingQueue<BufferQueueElement> queue;

	public MessageReader(MessageInputOutput messageIO, BlockingQueue<BufferQueueElement> queue) {
		this.messageIO = messageIO;
		this.queue = queue;
	}

	public void run() {
		String line = null;
		long initTime = TimingClass.getTime();
		long diff = 0;
		int messageAcc = 0;
		int numberOfMsgs = 0;
		long currentTime = 0;
		BufferQueueElement bufferElement = new BufferQueueElement();

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
					bufferElement.setMsgAcc(messageAcc);
					bufferElement.setMsgNumber(numberOfMsgs);
					queue.put((bufferElement));
					numberOfMsgs = 0;
					bufferElement = new BufferQueueElement();
					initTime = TimingClass.getTime();
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
