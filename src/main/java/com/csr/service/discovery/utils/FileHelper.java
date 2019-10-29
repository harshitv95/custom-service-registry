package com.csr.service.discovery.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHelper {

	private File file = null;
	private String filename;
	private boolean overwrite = false;
	
//	private FileWriter fw=null;
//	private BufferedWriter bw=null;
	private PrintWriter pw=null;
	
//	private FileReader fr=null;
	private BufferedReader br=null;
	
	public FileHelper(String filename) throws IOException {
		this.filename = filename;
		file = new File(filename);
		overwrite = false;
		config(file);
	}
	
	public FileHelper(String filename, boolean overwrite) throws IOException {
		this.filename = filename;
		file = new File(filename);
		this.overwrite = overwrite;
		config(file);
	}
	
	public FileHelper(File file) throws IOException{
		this.file = file;
		config(file);
	}
	
	public FileHelper() {
		
	}
	
	public FileHelper(String directoryToCreate, String filename) throws IOException {
		this.filename = filename;
		config(directoryToCreate,filename);
	}
	
	public void setFilename(String dir, String filename) throws IOException{
		config(dir, filename);
	}
	
	private void config(File file) throws IOException{
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
	}
	
	private void config(String dir, String filename) throws IOException{
		file = new File(dir+filename);
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
	}
	
	public boolean exists(){
		return file==null?false : file.exists();
	}
	
	public void setOverwrite(boolean overwrite){
		this.overwrite = overwrite;
		try {
			closeWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeln(String text) throws IOException{
		if(pw==null)
			initWriter();
		pw.println(text);
		pw.flush();
		closeWriter();
	}
	
	public void initReader() throws FileNotFoundException{
//		fr = new FileReader(file);
		br = new BufferedReader(new FileReader(file));
	}
	
	public void initWriter() throws IOException{
//		fw = new FileWriter(file,!overwrite);
//		bw = new BufferedWriter(fw);
		pw = new PrintWriter(new BufferedWriter(new FileWriter(file,!overwrite)));
	}
	
	public void initWriter(boolean overWrite) throws IOException{
//		fw = new FileWriter(file,!overWrite);
//		bw = new BufferedWriter(fw);
		pw = new PrintWriter(new BufferedWriter(new FileWriter(file,!overwrite)));
	}
	
	public void write(String text) throws IOException{
		if(pw==null)
			initWriter();
		pw.print(text);
		pw.flush();
		closeWriter();
	}
	
	public void close() throws IOException{
		closeReader();
		closeWriter();
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void closeReader() throws IOException{
		if(br!=null)
			br.close();
//		if(fr!=null)
//			fr.close();
		br = null;
//		fr=null;
	}
	
	public void closeWriter() throws IOException {
		if(pw!=null)
			pw.close();
//		if(fw!=null)
//			fw.close();
//		if(bw!=null)
//			bw.close();
		pw=null;
//		fw=null;
//		bw=null;
	}
	
	public String readLine() throws IOException{
		String line = "";
		if(br==null) initReader();
		line = br.readLine();
		if(line==null) closeReader();
		return line;
	}
	
	public String readAll() throws IOException{
		StringBuffer sb = new StringBuffer();
		if(br==null) initReader();
		String line = "";
		while((line=br.readLine())!=null) sb.append(line+"\n");
		closeReader();
		return sb.toString();
	}
	
	public String readFileSingleLine() throws IOException{
		StringBuffer sb = new StringBuffer();
		if(br==null) initReader();
		String line = "";
		while((line=br.readLine())!=null) sb.append(line);
		closeReader();
		return sb.toString();
	}
	
	public String readAllWithEndCharacter(String character) throws IOException {
		StringBuffer sb = new StringBuffer();
		if(br==null) initReader();
		String line = "";
		while((line=br.readLine())!=null) sb.append(line+character);
		closeReader();
		closeReader();
		return sb.toString();
	}

}
