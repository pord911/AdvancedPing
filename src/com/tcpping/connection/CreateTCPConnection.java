package com.tcpping.connection;

import java.io.IOException;
import java.net.UnknownHostException;

public class CreateTCPConnection {

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
