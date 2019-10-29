package com.csr.service.discovery;

import java.io.IOException;

import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.custom.RegistryListener;

public class StoreHelper {
	
	private RegistryListener userDefinedActions = null;
	private static ServiceStore store = null;

	protected StoreHelper() {
//		 new Config();
		 store = new ServiceStore();
	}
	protected StoreHelper(RegistryListener userdefinedListeners) {
		 this();
		 this.userDefinedActions = userdefinedListeners;
	}
	
	protected void register(String hostName, Object info) throws IOException{
		store.register(hostName, info);
		
		if(this.userDefinedActions!=null)
			userDefinedActions.onRegister(hostName, info);
	}
	
	protected void unRegister(String hostName) throws IOException{
		store.unRegister(hostName);
		
		if(this.userDefinedActions!=null)
			userDefinedActions.onUnRegister(hostName);
	}
	
	protected void pulse(String hostName, Object response){
		if(this.userDefinedActions!=null)
			userDefinedActions.onPulse(hostName, response);
	}
	
	public void setListeners(RegistryListener userdefinedListeners){
		this.userDefinedActions = userdefinedListeners;
	}
	
	public void unsetListeners(){
		this.userDefinedActions = null;
	}
	
	protected ServiceStore getStore() {
		return StoreHelper.store;
	}

}
