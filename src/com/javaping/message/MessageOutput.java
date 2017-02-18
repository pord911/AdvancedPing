package com.javaping.message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageOutput {
	private PrintWriter writer;

	/**
	 * Create an output stream.
	 * @param socketRef    Client socket reference.
	 * @throws IOException
	 */
	public MessageOutput(Socket socketRef) throws IOException {
		writer = new PrintWriter(new OutputStreamWriter(socketRef.getOutputStream()));
	}

	/**
	 * Write message
	 * @param message    Message string.
	 * @throws IOException
	 */
	public void writeMessage(String message) throws IOException {
		writer.println(message);
		writer.flush();
	}

	/**
	 * Close output stream.
	 * @throws IOException
	 */
	public void closeOutputMessageStream() throws IOException {
		writer.close();
	}
}
