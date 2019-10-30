package com.csr.service.discovery.slave;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

import com.csr.service.discovery.ServiceDiscovery;
import com.csr.service.discovery.ServiceDiscovery.Modes;
import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.custom.RegistryListener;
import com.csr.service.discovery.utils.NodeHealth;
import com.csr.service.discovery.utils.connection.tcp.TcpServer;
import com.csr.service.discovery.utils.connection.tcp.TcpServer.RequestMeta;

public class Discover {
	public static class Slave implements Closeable {
		
		final int retries = 3;
		final NodeHealth health;
		private TcpServer slaveServer;
		
		@SuppressWarnings("unused")
		private Slave() {this.health = null;}
		
		public Slave(int slavePortNum, NodeHealth health) throws IOException {
			this(slavePortNum, health, null);
		}
		
		public Slave(int slavePortNum, Consumer<RequestMeta> onAccept) throws IOException {
			this(slavePortNum, null, onAccept);
		}
		
		protected Slave(int slavePortNum, NodeHealth health, Consumer<RequestMeta> onAccept) throws IOException {
			slaveServer = onAccept != null ?
					new TcpServer(slavePortNum, onAccept) :
					new TcpServer(slavePortNum, (request) -> {
						String message = request.message;
						if (message.trim().equals("health")) {
							request.out.println(health.get());
						}
					});
			this.health = health;
		}
		
		public void registerToMaster(String masterHost, int masterPort, Modes networkMode) throws IOException {
			Config.setSlavePortNum(masterPort);
			Config.setMode(networkMode);
			Config.setInfoUrl("register");
			pingMaster(masterHost, retries);
		}
		
		private void pingMaster(String masterHost, int retries) throws IOException {
			String response = Config.getConnection().ping(masterHost);
			if (!response.equals("registered")) {
				if (retries-- <= 0) pingMaster(masterHost, retries);
				else {
					retries = 3;
					throw new RuntimeException("Failed to Register to Master Node.\n" + response);
				}
			} else {
				System.out.println("REGISTERED SUCCESSFULLY!");
			}
		}

		@Override
		protected void finalize() throws Throwable {
			this.close();
			super.finalize();
		}

		@Override
		public void close() throws IOException {
			this.slaveServer.close();
		}
		
	}
	
	public static class Master {
		protected ServiceDiscovery discovery;
		public Master(int slaveCustomPortNum, RegistryListener userDefinedListeners, String infoUrl, Modes networkMode, int pulseRate) throws IOException {
			Config.setPulseRate(pulseRate);
			Config.setMode(networkMode);
			networkMode.initialSetup();
			discovery = new ServiceDiscovery(slaveCustomPortNum, userDefinedListeners, infoUrl);
			discovery.start();
		}
	}
}
