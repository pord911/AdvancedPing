package com.javaping.catcher;

import java.io.IOException;
import java.net.Socket;

import com.javaping.message.MessageInput;
import com.javaping.message.MessageOutput;

public class CatcherClientHandler implements Runnable {
	private Socket connection;
	private MessageInput reader;
	private MessageOutput writer;

	/**
	 * Catcher handler.
	 * @param connection    Socket connection
	 * @throws IOException
	 */
	public CatcherClientHandler(Socket connection) throws IOException {
		this.connection = connection;
		writer = new MessageOutput(connection);
		reader = new MessageInput(connection);
	}

	/**
	 * Message reader. Reads each message, appends the necessary
	 * information and sends it back on the stream.
	 */
	private void startReadingMessages() {
		String msg;

		try {
			while ((msg = reader.readMessage()) != null) {

				/* If client wants to close the connection, close it! */
				if ("BYE".equals(msg)) {
					System.out.println("Sending BYE");
					writer.writeMessage("OKBYE");
					break;
				}
				writer.writeMessage(msg);
			}
		} catch (IOException e) {
			System.out.print(e.getMessage());
		} finally {
			try {
				if (writer != null)
					writer.closeOutputMessageStream();
				if (reader != null)
					reader.closeInputMessageStream();
				if (connection != null)
					connection.close();
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
		}
	}

	/**
	 * Start thread.
	 */
	public void run() {
		startReadingMessages();
	}
}
