package com.csr.service.discovery.utils.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class URLResponseReader {

	private String link;
	private URL url;
	private InputStream in;
	private BufferedReader br;
	
	public URLResponseReader(String link) throws IOException {
		setUrl(link);
	}
	
	public void setUrl(String link) throws IOException{
		this.link=link;
		url = new URL(link);
		in = url.openStream();
		br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
	}
	
	public String getResponseAsText() throws IOException{
		return readAll(br);
	}
	
	private String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

}
