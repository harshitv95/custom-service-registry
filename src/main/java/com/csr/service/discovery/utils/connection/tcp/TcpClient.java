package com.csr.service.discovery.utils.connection.tcp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient implements Closeable {
	protected Socket client;
	protected PrintWriter out;
	protected BufferedReader in;
	protected String host;
	protected int port;
	protected boolean closed = true;
	
	public TcpClient(String host, int port) {
		this.host = host; this.port = port;
		try {
			initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(
				String.format("Exception while initializing %s connection.\nHost: %s\nPort:%d\nException Message:%s", this.getClass().getName(), host, port, e.getMessage())
			);
		}
	}
	
	protected void initialize() throws UnknownHostException, IOException {
		client = new Socket(host, port);
		out = new PrintWriter(client.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		additionalInitialize();
		closed = false;
	}
	
	protected void additionalInitialize() { }
	
	public TcpClient reopen() throws IOException {
		if (!closed) close();
		initialize();
		return this;
	}
	
	public String ping(String message) throws IOException {
		if (closed) throw new RuntimeException("Cannot ping closed Socket");
		this.out.println(message);
		return this.in.readLine();
	}

	@Override
	public void close() throws IOException {
		if (this.in != null ) {
			this.in.close();
			this.in = null;
		}
		if (this.out != null ) {
			this.out.close();
			this.out = null;
		}
		if (this.client != null ) {
			this.client.close();
			this.client = null;
		}
		additionalClose();
		closed = true;
	}
	
	protected void additionalClose() throws IOException { }

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	
}
