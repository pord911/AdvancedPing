package com.javaping.message;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.javaping.connection.TCPConnection;

public class MessageReader implements Runnable {
	private final long ONE_SECOND = 1000000000;
	private final String CLOSE_STREAM = "OKBYE";
	private MessageInput messageIO;
	private BlockingQueue<BufferQueueElement<Message>> queue;

	/**
	 * Create an object for reading messages.
	 * @param messageIO    Stream IO reference.
	 * @param queue        Blocking queue.
	 * @throws IOException
	 */
	public MessageReader(TCPConnection connection, BlockingQueue<BufferQueueElement<Message>> queue) throws IOException {
		this.messageIO = new MessageInput(connection.getClientSocket());
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
		Message message;
		BufferQueueElement<Message> messageList = new BufferQueueElement<Message>();
		long t1 = System.nanoTime(), t2 = 0;
		try {
			while ((line = messageIO.readMessage()) != null) {
				t2 = System.nanoTime();
				/* Ok, we can close the stream. But process the last message list */
				if (CLOSE_STREAM.equals(line)) {
					messageList.setStreamClosed(true);
					queue.put(messageList);
					break;
				} else if((t2 - t1) < ONE_SECOND) {
					message = new Message(line);
					messageList.setMessageElement(message);
				} else {
					queue.put(messageList);
					messageList = new BufferQueueElement<Message>();
					t1 = System.nanoTime();
					message = new Message(line);
					messageList.setMessageElement(message);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
