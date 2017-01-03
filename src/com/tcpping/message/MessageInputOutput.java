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
	 * Newline je ovdje neovisan o platformi. Samim time BufferedReader odmah pro�ita
	 * vrijednost iz buffer-a ako se u poruci nalazi \n. Ovime smo
	 * izbjegli dodatno ka�njenje pri �itanju �to vi�e daje konzistentnost pri mjerenju ka�njenja
	 * pomo�u ping-a.
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
