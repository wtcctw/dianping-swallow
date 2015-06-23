package com.dianping.swallow.common.server.lifecycle;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.internal.lifecycle.SelfManagement;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午9:10:44
 */
public class LifecycleComponentManager implements ApplicationContextAware, InitializingBean, DisposableBean{

	protected final Logger logger     = LoggerFactory.getLogger(getClass());
	
	private ApplicationContext applicationContext;

	@Override
	public void destroy() throws Exception {
		
		
		List<ComponentWrapper> componentWrappers  = getComponents();
		for(int i = componentWrappers.size() -1 ; i>=0 ; i--){
			
			ComponentWrapper componentWrapper = componentWrappers.get(i); 
					
			String name = componentWrapper.getName();
			Lifecycle lifecycleComponent = componentWrapper.getComponent(); 
			if(selfManagement(componentWrapper)){
				continue;
			}
			
			if(logger.isInfoEnabled()){
				logger.info("[stop]" + name);
			}
			lifecycleComponent.stop();
		}

		for(int i = componentWrappers.size() -1 ; i>=0 ; i--){
			
			ComponentWrapper componentWrapper = componentWrappers.get(i); 
			
			String name = componentWrapper.getName();
			Lifecycle lifecycleComponent = componentWrapper.getComponent(); 
			if(selfManagement(componentWrapper)){
				continue;
			}
			
			if(logger.isInfoEnabled()){
				logger.info("[dispose]" + name);
			}
			lifecycleComponent.dispose();;
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
				
		for(ComponentWrapper componentWrapper : getComponents()){
			
			String name = componentWrapper.getName();
			Lifecycle lifecycleComponent = componentWrapper.getComponent(); 
			if(selfManagement(componentWrapper)){
				continue;
			}
			
			if(logger.isInfoEnabled()){
				logger.info("[init]" + name);
			}
			lifecycleComponent.initialize();
		}

		for(ComponentWrapper componentWrapper : getComponents()){
			
			String name = componentWrapper.getName();
			Lifecycle lifecycleComponent = componentWrapper.getComponent(); 
			if(selfManagement(componentWrapper)){
				continue;
			}
			
			if(logger.isInfoEnabled()){
				logger.info("[start]" + name);
			}
			lifecycleComponent.start();
		}
	}

	private boolean selfManagement(ComponentWrapper component) {
		
		if(component.getComponent() instanceof SelfManagement){
			if(logger.isInfoEnabled()){
				logger.info("[selfManagement]" + component.getName());
			}
			return true;
		}

		return false;
	}

	private List<ComponentWrapper> getComponents() {
		
		Map<String, Lifecycle> components = applicationContext.getBeansOfType(Lifecycle.class);
		List<ComponentWrapper> result = new LinkedList<LifecycleComponentManager.ComponentWrapper>();
		for(Entry<String, Lifecycle> entry : components.entrySet()){
			
			result.add(new ComponentWrapper(entry.getKey(), entry.getValue()));
		}
		
		Collections.sort(result, new Comparator<ComponentWrapper>() {

			@Override
			public int compare(ComponentWrapper o1, ComponentWrapper o2) {
				return o1.getComponent().getOrder() <= o2.getComponent().getOrder() ? -1 : 1;
			}
		});
		
		if(logger.isInfoEnabled()){
			logger.info("[getComponents]" + result);
		}
		
		return result;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {

		this.applicationContext = applicationContext;
	}

	
	class ComponentWrapper{
		
		private String name;
		private Lifecycle component;
		
		public ComponentWrapper(String name, Lifecycle component) {
			this.name = name;
			this.component = component;
		}
		
		public String getName() {
			return name;
		}
		
		public Lifecycle getComponent() {
			return component;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
