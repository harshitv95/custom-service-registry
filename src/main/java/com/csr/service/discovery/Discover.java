package com.csr.service.discovery;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

import com.csr.service.discovery.ServiceDiscovery.Modes;
import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.custom.RegistryListener;
import com.csr.service.discovery.utils.NodeHealth;
import com.csr.service.discovery.utils.connection.tcp.TcpServer;
import com.csr.service.discovery.utils.connection.tcp.TcpServer.RequestMeta;

public class Discover {
	
	public enum MessageWords {
		Register("register"),
		Health("health"),
		Registered("registered")
		;
		public final String text;
		MessageWords(String txt) {
			text = txt;
		}
		
	}
	
	public static class Slave implements Closeable {

		
		final int retries = 3;
//		final NodeHealth health;
		private TcpServer slaveServer;
//		private final Modes networkMode;
		
		@SuppressWarnings("unused")
		private Slave() {
			// Do this in your external class:
//			new Discover.Slave(Modes.TCP).registerToMaster(Config.masterHost, Config.masterPort);
//			new TcpServer(Config.slavePort, Discover.Slave.onRequestAccept(() -> Config.getHealth().toString()));
		}
		
		public Slave(Modes networkMode) throws IOException {
//			this(networkMode, null);
			Config.setMode(networkMode);
		}
		
//		public Slave(Modes networkMode, Consumer<RequestMeta> onAccept) throws IOException {
//			this(networkMode, onAccept);
//		}
//		
//		protected Slave(Modes networkMode, Consumer<RequestMeta> onAccept) throws IOException {
//			this.networkMode = networkMode;
//			Config.setMode(networkMode);
//			slaveServer = onAccept != null ?
//					new TcpServer(slavePortNum, onAccept) :
//					new TcpServer(slavePortNum, (request) -> {
//						String message = request.message;
//						if (message.trim().equals(MessageWords.Health.text)) {
//							request.out.println(health.get());
//						}
//					});
//			this.health = health;
//		}
		
		public static Consumer<RequestMeta> onRequestAccept(NodeHealth health) {
			return (request) -> {
				String message = request.message;
				System.out.println("Master requesting health");
				if (message.trim().equals(MessageWords.Health.text)) {
					String h = health.get();
					System.out.println("Responding with:\n" + h);
					request.out.println(h);
				}
			};
		}
		
		public void registerToMaster(String masterHost, int masterPort) throws IOException {
			Config.setSlavePortNum(masterPort);
			Config.setInfoUrl(MessageWords.Register.text);
			pingMaster(masterHost, retries);
		}
		
		private void pingMaster(String masterHost, int retries) throws IOException {
			String response = Config.getConnection().ping(masterHost);
			if (!response.equals(MessageWords.Registered.text)) {
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
