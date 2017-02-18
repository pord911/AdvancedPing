package com.javaping.connection;

import java.io.IOException;
import java.net.UnknownHostException;

public class CreateTCPConnection {

	/**
	 * Create a TCP connection based on the type of TCP handler.
	 * CATCHER or PITCHER.
	 * @param connectionType   Type of TCP connection
	 * @param host             Host name or IP address
	 * @param port             Port number
	 * @return                 Created connection, based on the type.
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static TCPConnection createTCPConnection(ConnType connectionType, String host, int port) throws UnknownHostException, IOException {
		switch (connectionType) {
		case PITCHER:
			return new TCPClientConnection(host, port);
		case CATCHER:
			return new TCPServerConnection(host, port);
		default:
			throw new IOException();
		}
	}
}
