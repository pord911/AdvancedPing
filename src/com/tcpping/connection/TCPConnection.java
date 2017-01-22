package com.tcpping.connection;

import java.io.IOException;
import java.net.Socket;

public interface TCPConnection {
	/**
	 * Return a client connection
	 * @return    Socket reference
	 * @throws IOException
	 */
	public Socket getClientSocket() throws IOException;

	/**
	 * Close a socket connection.
	 * @throws IOException
	 */
	public void closeConnection() throws IOException;
}
