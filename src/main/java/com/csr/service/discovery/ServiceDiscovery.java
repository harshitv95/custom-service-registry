package com.csr.service.discovery;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;

import org.json.JSONException;
import org.json.JSONObject;

import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.custom.RegistryListener;
import com.csr.service.discovery.protocols.CSRDataProtocol;
import com.csr.service.discovery.protocols.Http;
import com.csr.service.discovery.protocols.Tcp;
import com.csr.service.discovery.utils.connection.tcp.TcpServer;

public class ServiceDiscovery {
	public enum Modes {
		HTTP(Http.class),
		TCP(Tcp.class, () -> {
			try {
				TcpServer server = new TcpServer(Config.masterPortNum, (config) -> {
					if (config.message.equals("register")) {
						config.out.print("registered");
						System.out.println("Slave "+config.client.getInetAddress().getHostAddress()+" requesting registration");
						try {
							storeHelper.register(config.client.getInetAddress().getHostAddress(), config.message);
							System.out.println("Slave "+config.client.getInetAddress().getHostAddress()+" successfully registered");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}),
		WebSocket(null);
		
		private Class<? extends CSRDataProtocol> protocolClass;
		BooleanSupplier f;
		
		Modes(Class<? extends CSRDataProtocol> clazz) {
			this(clazz, null);
		}
		
		Modes(Class<? extends CSRDataProtocol> clazz, BooleanSupplier f) {
			
			this.protocolClass = clazz;
			this.f = f;
		}
		
		public CSRDataProtocol initializeProtocol() {
			if (protocolClass == null)
				throw new RuntimeException(this.name() + " API for Custom Service Registry is not implemented yet");
			try {
				return (CSRDataProtocol) this.protocolClass.getConstructors()[0].newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
		}
		public int initialSetup() {
			if (f != null) return f.getAsBoolean() ? 1 : 0;
			return -1;
		}
	}
	
	private static StoreHelper storeHelper;
	private Manager manager;
	
	public ServiceDiscovery(RegistryListener userDefinedListeners, String infoUrl){
		storeHelper = new StoreHelper(userDefinedListeners);
		this.manager = new Manager(storeHelper);
		
		Config.setInfoUrl(infoUrl);
	}
	public ServiceDiscovery(int slaveCustomPortNum, String infoUrl){
		this(slaveCustomPortNum, null, infoUrl);
	}
	public ServiceDiscovery(int slaveCustomPortNum, RegistryListener userDefinedListeners, String infoUrl){
		this(userDefinedListeners, infoUrl);
		Config.setSlavePortNum(slaveCustomPortNum);
	}
	
	public void start () {
		manager.start();
	}
	
	public void stop () {
		manager.stop();
	}
	
	/**
	 * This will add <code>hsotName</code> to the list of registered components.
	 * <code>hostName</code> will be mapped to <code>info</code>.
	 * Use your desired request mapping to register, and call this method.
	 * @param hostName
	 * @param info
	 * @throws IOException 
	 */
	public void register(String hostName, Object info) {
		try {
			storeHelper.register(hostName, info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unRegister(String hostName) {
		try {
			storeHelper.unRegister(hostName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject getRegistered(){
		try {
			return storeHelper.getStore().get();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}

}
