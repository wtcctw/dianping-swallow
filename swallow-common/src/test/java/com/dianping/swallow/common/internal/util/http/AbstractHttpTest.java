package com.dianping.swallow.common.internal.util.http;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;

import com.dianping.swallow.AbstractTest;

/**
 * @author mengwenchao
 *
 * 2015年12月24日 下午7:27:27
 */
public class AbstractHttpTest extends AbstractTest{

	
	private int portStart = 7000;
		
	private Map<Integer, AtomicInteger> connectionCount = new ConcurrentHashMap<Integer, AtomicInteger>();
	
	private List<ServerSocket> servers = new LinkedList<ServerSocket>();
	

	@Before
	public void beforeAbstractHttpTest(){
		
	}
	
	public int getConnectionCount(int port) {
		
		AtomicInteger count = connectionCount.get(port); 
		if( count == null ){
			return 0;
		}
		return count.get();
	}
	
	
	@After
	public void afterAbstractHttpTest(){
		
		for(ServerSocket ss : servers){
			try {
				if(logger.isInfoEnabled()){
					logger.info("[afterAbstractHttpTest][close]" + ss);
				}
				ss.close();
			} catch (IOException e) {
				logger.error("[afterAbstractHttpTest][close]" + ss);
			}
		}
		
	}

	
	protected int startSimpleHttpServer() {
		for(int i = portStart;;i++){
			
			try {
				final ServerSocket serverSocket = new ServerSocket(i);
				
				if(logger.isInfoEnabled()){
					logger.info("[startSimpleHttpServer][start, port]" + i);
				}
				
				servers.add(serverSocket);
				connectionCount.put(i, new AtomicInteger());
				executors.execute(new Runnable(){

					@Override
					public void run() {

						try {

							while(true){
								Socket socket = serverSocket.accept();
								
								if(logger.isInfoEnabled()){
									logger.info("[run][new socket]" + socket);
								}
								
								connectionCount.get(serverSocket.getLocalPort()).incrementAndGet();
								executors.execute(new SimpleHttpTask(socket));
							}
						} catch (IOException e) {
							logger.error("[run]" + serverSocket, e);
						}
					}
				});
				return i;
			} catch (IOException e) {
				logger.error("[startSimpleHttpServer]" + i, e);
			}
		}
	}

	
	public class SimpleHttpTask implements Runnable{

		private Socket socket;
		
		public SimpleHttpTask(Socket socket){
			this.socket = socket;
		}
		
		@Override
		public void run() {
			
			try {
				while(true){
					InputStream ins = socket.getInputStream();
					
					read(ins);
					write(socket.getOutputStream());
				}
			} catch (IOException e) {
				logger.error("[run]" + socket,  e);
			}
		}

		private void read(InputStream ins) throws IOException {
			
			int state = 0;
			StringBuilder line = new StringBuilder();
			int contentLength = -1, realLength = 0;
			boolean beginContent = false;
			
			while(true){
				
				if(contentLength >=0 && realLength >= contentLength){
					break;
				}
				
				int data = ins.read();
				if(data == -1){
					break;
				}
				
				if(beginContent){
					realLength++;
				}
				
				System.out.print((char)data);
				line.append((char)data);
				
				if(data == '\r' || data == '\n'){
					state++;
				}else{
					state = 0;
				}
				
				
				
				
				if(state == 2){
					String []header = line.toString().split("\\s*:\\s*");
					if(header.length == 2){
						if(header[0].equalsIgnoreCase("Content-Length")){
							contentLength = Integer.parseInt(header[1].trim());
							if(logger.isInfoEnabled()){
								logger.info("[Content-Length]" + contentLength);
							}
						}
					}
					line = new StringBuilder();
				}
				
				if(state == 4){
					if(logger.isInfoEnabled()){
						logger.info("[read][begin content]");
					}
					beginContent = true;
				}
			}
			
			System.out.println();
		}

		private void write(OutputStream outputStream) throws IOException {
			
			String data = "HTTP/1.1 200 OK\r\n" 
					+ "Content-Type: application/json\r\n"
					+ "Content-Length: 1\r\n"
					+ "Keep-Alive: timeout=1\r\n"
					+ "\r\na";
			outputStream.write(data.getBytes());
		}
		
	}

}
