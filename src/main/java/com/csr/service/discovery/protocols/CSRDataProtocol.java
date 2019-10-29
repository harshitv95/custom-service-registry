package com.csr.service.discovery.protocols;

import java.io.Closeable;
import java.io.IOException;

public interface CSRDataProtocol extends Closeable {
	public String ping(String host) throws IOException;
	public String hostUrl(String host, int port);
}
