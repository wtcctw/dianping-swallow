package com.dianping.swallow.common.server.monitor.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.context.ApplicationContext;

import com.dianping.swallow.common.internal.monitor.ComponentMonitable;

/**
 * @author mengwenchao
 *
 * 2015年7月1日 下午4:11:10
 */
@Path("/")
public class ComponentMonitor {
	
	public ApplicationContext context;
	
	public ComponentMonitor(ApplicationContext context){
		
		this.context = context;
	}
	
	@Path("/names")
	@GET
	public String[] names(){
		
		return context.getBeanNamesForType(ComponentMonitable.class);
	}
	
	@Path("/name/{name}")
	@Produces("application/json")
	@GET
	public Object getStatus(@PathParam("name") String name){
		
		Object bean = context.getBean(name);
		if(! (bean instanceof ComponentMonitable)){
			return "bean not monitable :" + bean;
		}
		ComponentMonitable monitorable = (ComponentMonitable) bean;
		return monitorable.getStatus();
	}

}
