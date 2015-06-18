package com.dianping.swallow.common.server.lifecycle;

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
		
		Map<String, Lifecycle> beans = getBeans();
		
		for(Entry<String, Lifecycle> entry : beans.entrySet()){
			
			if(selfManagement(entry)){
				continue;
			}
			
			if(logger.isInfoEnabled()){
				logger.info("[stop]" + entry.getKey());
			}
			entry.getValue().stop();
		}

		for(Entry<String, Lifecycle> entry : beans.entrySet()){

			if(selfManagement(entry)){
				continue;
			}

			if(logger.isInfoEnabled()){
				logger.info("[dispose]" + entry.getKey());
			}
			entry.getValue().dispose();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		Map<String, Lifecycle> beans = getBeans();
		
		for(Entry<String, Lifecycle> entry : beans.entrySet()){
			
			if(selfManagement(entry)){
				continue;
			}
			
			if(logger.isInfoEnabled()){
				logger.info("[init]" + entry.getKey());
			}
			entry.getValue().initialize();
		}

		for(Entry<String, Lifecycle> entry : beans.entrySet()){

			if(selfManagement(entry)){
				continue;
			}

			if(logger.isInfoEnabled()){
				logger.info("[start]" + entry.getKey());
			}
			entry.getValue().start();
		}

		
	}

	private boolean selfManagement(Entry<String, Lifecycle> entry) {
		
		if(entry.getValue() instanceof SelfManagement){
			if(logger.isInfoEnabled()){
				logger.info("[selfManagement]" + entry.getKey());
			}
			return true;
		}

		return false;
	}

	private Map<String, Lifecycle> getBeans() {
		
		return applicationContext.getBeansOfType(Lifecycle.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {

		this.applicationContext = applicationContext;
	}

}
