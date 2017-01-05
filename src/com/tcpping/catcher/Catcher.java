package com.tcpping.catcher;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tcpping.connection.CreateTCPConnection;
import com.tcpping.connection.TCPConnection;
import com.tcpping.main.ConnType;

public class Catcher {
	private String hostName;
	private int port;
	private TCPConnection connection;
	private final ExecutorService workers;
	private boolean keepAlive = true;

	public Catcher(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		workers = Executors.newCachedThreadPool();
	}

	public void startTCPApp() {
		Socket clientConnection = null;
		try {
			connection = CreateTCPConnection.createTCPConnection(ConnType.CATCHER, hostName, port);
			while (keepAlive) {
				clientConnection = connection.acceptClientConnection();
				CatcherClientHandler clientHandler = new CatcherClientHandler(clientConnection);
				workers.execute(clientHandler);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.closeConnection();
				workers.shutdown();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
