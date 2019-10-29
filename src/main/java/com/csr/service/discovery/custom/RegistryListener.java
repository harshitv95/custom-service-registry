package com.csr.service.discovery.custom;

public abstract class RegistryListener {
	public abstract void onRegister(String hostName, Object info);
	public abstract void onUnRegister(String hostName);
	public abstract void onPulse(String hostName, Object newResponse);
}
