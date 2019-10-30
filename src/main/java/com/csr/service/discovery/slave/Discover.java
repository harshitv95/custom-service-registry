package com.csr.service.discovery.slave;

import java.io.IOException;

import com.csr.service.discovery.ServiceDiscovery;
import com.csr.service.discovery.ServiceDiscovery.Modes;
import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.custom.RegistryListener;

public class Discover {
	public static class Slave {
		
		final int retries = 3;
		
		public Slave() {
			
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
