package com.csr.service.discovery;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.utils.SlaveConnect;

public class Manager {
	
	private boolean doCheck = false;
	private boolean isRunning = false;
	
	private StoreHelper storeHelper;

	protected Manager(StoreHelper storeHelper) {
		this.storeHelper = storeHelper;
	}
	
	protected void start () {
		int interval = Config.getPulseRate();
		this.doCheck = true;
		if(!isRunning){
			startThread(interval);
		}
	}
	
	private Thread startThread(int interval){
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				isRunning = true;
				while(doCheck){
					try{
						checkIfAlive();
					}catch(Exception e){
						e.printStackTrace();
					}
					try{Thread.sleep(interval*1000);}catch(InterruptedException e){e.printStackTrace();}
				}
				isRunning = false;
				Thread.currentThread().interrupt();
			}
		};
		
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
		return t;
	}
	
	protected void stop() {
		this.doCheck = false;
	}

	private void checkIfAlive() throws JSONException, IOException {
		JSONObject list = storeHelper.getStore().get();
		list.keySet().forEach(host -> {
			String response = SlaveConnect.getResponseAsString(host);
			try{
				if(response==null)
					storeHelper.unRegister(host);
				else{
					System.out.println(String.format("Ping to [%s] responded:\n%s", host, response));
					storeHelper.getStore().register(host, response);
					storeHelper.pulse(host, response);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		});
	}
	
}
