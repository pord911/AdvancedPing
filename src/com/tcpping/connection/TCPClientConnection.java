package com.tcpping.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClientConnection implements TCPConnection {
	Socket clientSocket;
	
	public TCPClientConnection(String hostName, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(hostName, port);
	}
	
	public Socket getClientSocket() {
		return clientSocket;
	}
	
	public void closeConnection() throws IOException {
		clientSocket.close();
	}
}
