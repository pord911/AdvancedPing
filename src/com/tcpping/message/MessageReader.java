package com.tcpping.message;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.tcpping.connection.TCPConnection;
import com.tcpping.time.TimingClass;

public class MessageReader implements Runnable {
	private final String CLOSE_STREAM = "OKBYE";
	private MessageInput messageIO;
	private BlockingQueue<Message> queue;

	/**
	 * Create an object for reading messages.
	 * @param messageIO    Stream IO reference.
	 * @param queue        Blocking queue.
	 * @throws IOException
	 */
	public MessageReader(TCPConnection connection, BlockingQueue<Message> queue) throws IOException {
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
		try {
			while ((line = messageIO.readMessage()) != null) {
				/* Ok, we can close the stream. */
				if (CLOSE_STREAM.equals(line)) {
					break;
				}
				message = new Message(line);
				queue.put(message);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
