package com.tcpping.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageInput {
	private BufferedReader reader;

	/**
	 * Create an input stream.
	 * @param socketRef    Client socket reference.
	 * @throws IOException
	 */
	public MessageInput(Socket socketRef) throws IOException {
		reader = new BufferedReader(new InputStreamReader(socketRef.getInputStream()));
	}

	/**
	 * Read message.
	 * @return    Message string.
	 * @throws IOException
	 */
	public String readMessage() throws IOException {
		return reader.readLine();
	}

	/**
	 * Close input stream.
	 * @throws IOException
	 */
	public void closeInputMessageStream() throws IOException {
		reader.close();
	}
}
