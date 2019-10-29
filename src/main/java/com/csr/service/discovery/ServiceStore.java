package com.csr.service.discovery;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.csr.service.discovery.utils.FileHelper;

public class ServiceStore {
	
	private static String storeDirectory = "Data"+File.separator+"Discovery"+File.separator;
	private static String storeFile = "store.json";
	private static FileHelper fileHelper;

	private JSONObject runningInstances;
	private Map<String, Object> temp;
//	private Map<String, String> instance;
	
	protected ServiceStore() {
		runningInstances = new JSONObject();
		try {
			initialize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initialize() throws IOException {
		fileHelper = new FileHelper(storeDirectory, storeFile);
//		if(fileHelper.exists())
//			runningInstances = getSavedData();
		
		fileHelper.setOverwrite(true);
			
	}
	
	protected void register(String hostname, Object info) throws IOException{
		runningInstances = getSavedData();
		if(!runningInstances.has(hostname)){
			runningInstances.put(hostname, info);
			saveData(runningInstances);
		}
		runningInstances = null;
	}
	
	protected void unRegister(String hostname) throws IOException{
		runningInstances = getSavedData();
		if(runningInstances.has(hostname)){
			runningInstances.remove(hostname);
			saveData(runningInstances);
		}
		runningInstances = null;
	}
	
	protected Map<String, Object> get(String... hostnames) throws JSONException, IOException{
		temp = new HashMap<>();
		runningInstances = getSavedData();
		Arrays.asList(hostnames).forEach(host -> {
			try {
				temp.put(host, get(host));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		runningInstances = null;
		return temp;
	}
	
	protected Object get(String host) throws JSONException, IOException{
		return getSavedData().get(host);
	}
	
	protected JSONObject get() throws JSONException, IOException {
		return getSavedData();
	}

	private JSONObject getSavedData() throws JSONException, IOException{
		String saved = fileHelper.readAll().trim();
		if(saved.isEmpty()) return new JSONObject();
		return (new JSONObject(saved));
	}
	
	private void saveData(JSONObject runningInstances) throws IOException{
		fileHelper.write(runningInstances.toString());
	}
}
