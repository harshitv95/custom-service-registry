package com.csr.service.discovery.protocols;

import java.io.IOException;
import java.util.HashMap;

import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.utils.connection.tcp.TcpClient;

public class Tcp implements CSRDataProtocol {
	
	private HashMap<String, TcpClient> connections = new HashMap<>();

	@Override
	public void close() throws IOException {
		connections.forEach((host, con) -> {
			try {
				con.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		connections.clear();
	}

	@Override
	public String ping(String host) throws IOException {
		TcpClient client = connections.get(host);
		if (client == null) {
			client = new TcpClient(host, Config.getSlavePortNum());
			connections.put(host, client);
		}
		return client.ping(Config.getPingMessage());
	}

	@Override
	public String hostUrl(String host, int port) { return host; }

}
