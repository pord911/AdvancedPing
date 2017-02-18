package com.javaping.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPServerConnection implements TCPConnection {
	private ServerSocket serverSocket = null;

	/**
	 * Create a TCP server connection. Accept max 10 connections.
	 * @param ipAddress    Host name or IP address
	 * @param port         Port number
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TCPServerConnection(String ipAddress, int port) throws UnknownHostException, IOException {
		InetAddress address = InetAddress.getByName(ipAddress);
		serverSocket = new ServerSocket(port, 10, address);
	}

	/**
	 * Close a socket connection.
	 */
	public void closeConnection() throws IOException {
		serverSocket.close();
	}

	/**
	 * Accept a client connection.
	 */
	public Socket getClientSocket() throws IOException {
		return serverSocket.accept();
	}
}
