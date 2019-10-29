package com.csr.service.discovery.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.csr.service.discovery.config.Config;

public class SlaveConnect {
	
	public static String getResponseAsString(String hostName) {
		try{
//			return getResponse((Config.getHttpUrl(hostName)));
			return getResponse(hostName);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getResponseAsString(String hostName, HashMap<String, String> params) throws Exception{
		return getResponseAsString(hostName, params.entrySet().stream().map(k -> k+"="+params.get(k)).collect(Collectors.toList()));
	}
	
	private static String getResponseAsString(String hostName, List<String> additionalParams) {
		String paramString = "?"+join(additionalParams,"&");
		try{
			return getResponse(Config.getHttpUrl(hostName)+"?"+paramString);
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String join(Collection<String> collection, String delimiter){
		return (String) collection.stream()
	            .map( Object::toString )
	            .collect( Collectors.joining( delimiter ) );
	}
	
	private static String getResponse(String link) throws IOException {
		try{
			System.out.println("Trying URL "+link);
			return Config.getConnection().ping(link);
		}catch(IOException e){
			System.err.print("URL "+link+" not valid. ");
			throw e;
		}
	}

}
