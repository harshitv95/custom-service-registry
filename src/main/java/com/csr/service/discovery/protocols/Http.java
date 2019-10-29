package com.csr.service.discovery.protocols;

import java.io.IOException;

import com.csr.service.discovery.config.Config;
import com.csr.service.discovery.utils.connection.URLResponseReader;

public class Http implements CSRDataProtocol {

	@Override
	public void close() throws IOException { }
	
	@Override
	public String hostUrl(String host, int port) {
		return Config.getHttpUrl(host, port);
	}

	@Override
	public String ping(String hostName) throws IOException {
		return new URLResponseReader(hostUrl(hostName, Config.getSlavePortNum())).getResponseAsText();
	}

}
