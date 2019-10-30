package com.csr.service.discovery.utils.connection.tcp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TcpServer extends TcpClient implements Closeable {

	protected ServerSocket server;
	protected Consumer<RequestMeta> onAccept;

	public TcpServer(int port) throws IOException {
		super(null, port);
	}

	public TcpServer(int port, Consumer<RequestMeta> onAccept) throws IOException {
		this(port);
		this.onAccept = onAccept;
	}

	protected void initialize() throws IOException {
		server = new ServerSocket(port);
		new Thread(() -> {
			while (true) {
				try {
					client = server.accept();
					out = new PrintWriter(client.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					additionalInitialize();
//					String message = in.lines().collect(Collectors.joining("\n"));
					String message = in.readLine();
					if (this.onAccept != null) {
						this.onAccept.accept(new RequestMeta(client, message, in, out));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		closed = false;
	}

	@Override
	protected void additionalClose() throws IOException {
		if (this.server != null) {
			this.server.close();
			this.server = null;
		}
	}
	
	public static class RequestMeta {
		public final Socket client;
		public final String message;
		public final BufferedReader in;
		public final PrintWriter out;
		
		public RequestMeta (
				Socket client,
				String message,
				BufferedReader inputStream,
				PrintWriter outputWriter
				) {
			this.client = client;
			this.message = message;
			this.in = inputStream;
			this.out = outputWriter;
		}
	}
}
