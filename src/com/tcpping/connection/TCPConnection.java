package com.tcpping.connection;

import java.io.IOException;
import java.net.Socket;

//TODO: kreirati abstract klasu umjesto interface-a
public interface TCPConnection {
	public Socket getClientSocket();
	public Socket acceptClientConnection() throws IOException;
	public void closeConnection() throws IOException;
}
