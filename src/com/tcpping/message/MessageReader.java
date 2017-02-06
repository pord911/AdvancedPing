package com.tcpping.message;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.tcpping.time.TimingClass;

public class MessageReader implements Runnable {
	private MessageInput messageIO;
	private BlockingQueue<BufferQueueElement> queue;

	/**
	 * Create an object for reading messages.
	 * @param messageIO    Stream IO reference.
	 * @param queue        Blocking queue.
	 */
	public MessageReader(MessageInput messageIO, BlockingQueue<BufferQueueElement> queue) {
		this.messageIO = messageIO;
		this.queue = queue;
	}

	/**
	 * Start a thread for reading messages. The thread reads
	 * a message, stores it in a blocking queue and notifies the main
	 * thread to process the message. The intention is to remove the
	 * processing load from the receiving thread so that time measurements could
	 * be more accurate.
	 */
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

				/* Ok, we can close the stream, but process the last message
				 * before closing. */
				if ("OKBYE".equals(line)) {
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

					/* Notify the main thread to process the message. */
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
