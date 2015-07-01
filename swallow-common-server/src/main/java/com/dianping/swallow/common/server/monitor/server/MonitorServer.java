package com.dianping.swallow.common.server.monitor.server;

import java.io.Closeable;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.simple.SimpleContainerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;

/**
 * @author mengwenchao
 *
 * 2015年6月26日 下午6:06:59
 */
public class MonitorServer extends AbstractLifecycle implements ApplicationContextAware{
	
	private int port = 8080;
	
	private Closeable server;
	
	private ApplicationContext context;
	
	private List<Object> objects;
	
	@Override
	protected void doInitialize() throws Exception {
		
		ResourceConfig rc = new ResourceConfig();
		rc.register(JacksonFeature.class);
		rc.register(JacksonProvider.class);
		registerUserClasses(rc);
	    server = SimpleContainerFactory.create(getUri(), rc);
	    if(logger.isInfoEnabled()){
	    	logger.info("[doInitialize][monitor server start at]" + port);
	    }
	}

	private void registerUserClasses(ResourceConfig rc) {
	
		rc.register(new ComponentMonitor(context));
		
		if(objects == null){
			return;
		}
		
		for(Object o : objects){
			if(logger.isInfoEnabled()){
				logger.info("[registerUserClasses]" + o);
			}
			rc.register(o);
		}
	}

	private URI getUri() {
		return UriBuilder.fromUri("http://localhost/").port(port).build();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	protected void doDispose() throws Exception {
		super.doDispose();

		server.close();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
		this.context = applicationContext;
	}

	public List<Object> getObjects() {
		return objects;
	}

	public void setObjects(List<Object> objects) {
		this.objects = objects;
	}

}
