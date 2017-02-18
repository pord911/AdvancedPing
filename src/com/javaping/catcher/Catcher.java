package com.javaping.catcher;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.javaping.connection.ConnType;
import com.javaping.connection.CreateTCPConnection;
import com.javaping.connection.TCPConnection;
import com.javaping.tcpapp.TcpAppInterface;

public class Catcher implements TcpAppInterface {
	private String hostName;
	private int port;
	private boolean keepAlive = true;

	/**
	 * Catcher constructor.
	 * @param hostName   Host name or IP address
	 * @param port       Port number
	 */
	public Catcher(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}

	/**
	 * Start catcher functionality. Create a connection on which to listen for
	 * clients. Process each connection in a separate thread.
	 */
	public void startTCPApp() {
		TCPConnection connection = null;
		Socket clientConnection = null;
		final ExecutorService workers = Executors.newCachedThreadPool();

		try {
			connection = CreateTCPConnection.createTCPConnection(ConnType.CATCHER, hostName, port);

			/*
			 * keepAlive should probably be false at some point?
			 * or it is ok to kill the Catcher manually?
			 */
			while (keepAlive) {
				clientConnection = connection.getClientSocket();
				CatcherClientHandler clientHandler = new CatcherClientHandler(clientConnection);
				workers.execute(clientHandler);
			}
		} catch (UnknownHostException e) {
			System.out.print(e.getMessage());
		} catch (IOException e) {
			System.out.print(e.getMessage());
		} finally {
			try {
				if (connection != null)
					connection.closeConnection();
				workers.shutdown();
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
		}
	}
}
