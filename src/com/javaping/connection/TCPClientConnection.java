package com.javaping.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClientConnection implements TCPConnection {
	private Socket clientSocket;

	/**
	 * Create a TCP client connection.
	 * @param hostName     Host name or IP address.
	 * @param port         Port number.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TCPClientConnection(String hostName, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(hostName, port);
	}

	/**
	 * @return Return a socket reference
	 */
	public Socket getClientSocket() {
		return clientSocket;
	}

	/**
	 * Close the socket connection
	 */
	public void closeConnection() throws IOException {
		clientSocket.close();
	}
}
