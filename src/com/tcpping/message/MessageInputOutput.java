package com.tcpping.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageInputOutput {
	/**
	 * Printwriter ima metodu println koja stavlja \n na kraj svake poruke.
	 * Newline je ovdje neovisan o platformi. Samim time BufferedReader odmah proèita
	 * vrijednost iz buffer-a ako se u poruci nalazi \n. Ovime smo
	 * izbjegli dodatno kašnjenje pri èitanju što više daje konzistentnost pri mjerenju kašnjenja
	 * pomoæu ping-a.
	 */
	private PrintWriter writer;
	private BufferedReader reader;

	public MessageInputOutput(Socket socketRef) throws IOException {
		writer = new PrintWriter(new OutputStreamWriter(socketRef.getOutputStream()));
		reader = new BufferedReader(new InputStreamReader(socketRef.getInputStream()));
	}

	public String readMessage() throws IOException {
		return reader.readLine();
	}

	public void writeMessage(String message) throws IOException {
		writer.println(message);
		writer.flush();
	}

	public void closeMessageStream() throws IOException {
		writer.close();
		reader.close();
	}
}
