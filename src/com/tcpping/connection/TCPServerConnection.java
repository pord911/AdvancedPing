package com.tcpping.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPServerConnection implements TCPConnection {
	ServerSocket serverSocket = null;

	public TCPServerConnection(String ipAddress, int port) throws UnknownHostException, IOException {
		InetAddress address = InetAddress.getByName(ipAddress);
		serverSocket = new ServerSocket(port, 1, address);
	}

	public Socket getClientSocket() {
		return null;
	}

	public Socket acceptClientConnection() throws IOException {
		return serverSocket.accept();
	}

	public void closeConnection() throws IOException {
		if (serverSocket != null)
			serverSocket.close();
	}
}
