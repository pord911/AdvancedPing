package com.tcpping.connection;

import java.io.IOException;
import java.net.Socket;

public interface TCPConnection {
	public Socket getClientSocket() throws IOException;
	public void closeConnection() throws IOException;
}
