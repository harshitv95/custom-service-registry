package com.csr.service.discovery.config;

import java.io.IOException;

import com.csr.service.discovery.ServiceDiscovery.Modes;
import com.csr.service.discovery.protocols.CSRDataProtocol;

public class Config {
	private static Config instance = null;
	
	private Config() {
		Config.instance = this;
	}
//	public static Config instance() {return Config.instance;}
	
	private static int slavePortNum = -1;
	private static int pulseRate = 20;
	private static String infoUrl = "info";
	private static Modes mode;
	private static CSRDataProtocol protocol;
	public static int masterPortNum = 8888;
	
	public static void setMode(Modes mode) throws IOException {
		if (protocol != null) protocol.close();
		protocol = null;
		Config.mode = mode;
	}
	
	public static CSRDataProtocol getConnection() {
		if (protocol == null)
			protocol = mode.initializeProtocol();
		return protocol;
	}
	
	public static void setInfoUrl(String info){
		infoUrl = info;
	}
	
	public static int getPulseRate() {
		return pulseRate;
	}

	/**
	 * @param slaveCheckFrequency Seconds
	 */
	public static void setPulseRate(int slaveCheckFrequency) {
		pulseRate = slaveCheckFrequency;
	}
	
//	public boolean isCustomSlavePortNum() {
//		return customSlavePortNum;
//	}
//
//	public void setCustomSlavePortNum(boolean customSlavePortNum) {
//		this.customSlavePortNum = customSlavePortNum;
//	}

	public static int getSlavePortNum() {
		return slavePortNum;
	}

	public static void setSlavePortNum(int slavePortNum) {
		Config.slavePortNum = slavePortNum;
	}

	public static String getHttpUrl(String hostName) { return getHttpUrl(hostName, slavePortNum); }
	
	public static String getHttpUrl(String hostName, int port) {
		if(!hostName.startsWith("http://") && !(hostName.startsWith("https://")))
			hostName = "http://"+hostName;
		return hostName+":"+((port>-1)?port:"")+"/"+infoUrl;
	}
	
	public static String getPingMessage() {
		return infoUrl;
	}
	public static void setPingMessage(String msg) {
		infoUrl = msg;
	}

}
