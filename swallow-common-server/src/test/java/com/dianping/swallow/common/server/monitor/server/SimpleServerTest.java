package com.dianping.swallow.common.server.monitor.server;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.util.CommonUtils;

/**
 * @author mengwenchao
 *
 * 2015年9月10日 下午3:23:34
 */
public class SimpleServerTest extends AbstractTest{
	
	
	private ExecutorService executors = Executors.newCachedThreadPool();
	 
	
	public void createHttpServer(final int port) throws IOException{
		
		
		executors.execute(new Runnable(){

			@Override
			public void run() {
				
				ServerSocket server;
				
				try {
					server = new ServerSocket(port);
					while(true){
						Socket socket = server.accept();
						if(logger.isInfoEnabled()){
							logger.info("[createHttpServer]" + socket);
						}
						executors.execute(new SocketHangTask(socket));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				
			}
		});
		
	}
	
	
	public static class SocketHangTask implements Runnable{

		private Socket socket;
		
		public SocketHangTask(Socket socket){
			
			this.socket = socket;
		}
		
		@Override
		public void run() {
			
			try {
				
				InputStream ins = socket.getInputStream();
				DataInputStream dis = new DataInputStream(ins);
				while(true){
					String line = dis.readLine();
					if( line == null ){
						break;
					}
					System.out.println(line);
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			
		}
		
	}
	
	
	@Test
	public void testHttpServer() throws ClientProtocolException, IOException, InterruptedException{
		
		int port = 2222;
		
		createHttpServer(port);
		
		HttpClient httpClient = createHttpClient();
		
		if(logger.isInfoEnabled()){
			logger.info("[testHttpServer][execute]");
		}
		HttpResponse response = httpClient.execute(new HttpPost("http://localhost:" + port));
		if(logger.isInfoEnabled()){
			logger.info("[testHttpServer][response]");
		}
		
		System.out.println(EntityUtils.toString(response.getEntity()));
		if(logger.isInfoEnabled()){
			logger.info("[testHttpServer][end]");
		}
		TimeUnit.SECONDS.sleep(1000);
	}
	
	
	@Test
	public void testScheduled() throws InterruptedException{
		
		
		ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(4);
		
		Future<?> future = scheduled.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(Thread.currentThread() + ":" +new Date());
				System.out.println("sleep for ever:" + 600);
				throw new RuntimeException("abc");
				
			}
		}, 0, 5, TimeUnit.SECONDS);
		
		
		TimeUnit.SECONDS.sleep(10);
		
		System.out.println("cancel");
		future.cancel(true);
		future = scheduled.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(Thread.currentThread() + ":" +new Date());
				System.out.println("sleep for ever:" + 600);
				throw new RuntimeException("abc");
				
			}
		}, 0, 5, TimeUnit.SECONDS);
		
		

		TimeUnit.SECONDS.sleep(600);
	}
	

	
	private HttpClient createHttpClient() {
		
        HttpParams params = new BasicHttpParams();
        
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(CommonUtils.getCpuCount()));
        ConnManagerParams.setTimeout(params, 1000);
        HttpConnectionParams.setSoTimeout(params, 1000);

        
        
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        HttpClient httpClient = new DefaultHttpClient(connectionManager, params);
        
        return httpClient;
	}

}
