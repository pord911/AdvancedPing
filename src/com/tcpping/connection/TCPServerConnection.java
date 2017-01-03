package com.tcpping.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPServerConnection implements TCPConnection {

	Socket clientSocket;
	ServerSocket serverSocket;

	public TCPServerConnection(String ipAddress, int port) throws UnknownHostException, IOException {
		InetAddress address = InetAddress.getByName(ipAddress);
		serverSocket = new ServerSocket(port, 1, address);
		clientSocket = serverSocket.accept();
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void closeConnection() throws IOException {
		if (clientSocket != null)
			clientSocket.close();
		if (serverSocket != null)
			serverSocket.close();
	}
}
