package com.dianping.swallow;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:52:52
 */
public abstract class AbstractSpringTest extends AbstractTest implements BeanFactoryPostProcessor{

	private ApplicationContext ctx;
	
	@Before
	public void beforeAbstractSpringTest(){
		
		ctx = new ClassPathXmlApplicationContext(getApplicationContextFile());
		
		registerBeans();
	}
	
	private void registerBeans() {
		
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();

		addBean(beanFactory, AutowiredAnnotationBeanPostProcessor.class);
		addBean(beanFactory, getClass());
		
	}

	@SuppressWarnings("unchecked")
	private <T> void  addBean(DefaultListableBeanFactory beanFactory, Class<T> clazz) {

		String[] beans = beanFactory.getBeanNamesForType(clazz);
		if(beans != null && beans.length > 0){
			logger.warn("[addBean][already exist]" + Arrays.toString(beans));
			return;
		}
		
		String beanName = clazz.getSimpleName() + "-springtest";
		BeanDefinitionBuilder bd = BeanDefinitionBuilder.rootBeanDefinition(clazz);
		beanFactory.registerBeanDefinition(beanName, bd.getBeanDefinition());
		
		T bean = (T) ctx.getBean(beanName);
		if(bean instanceof BeanPostProcessor){
			beanFactory.addBeanPostProcessor((BeanPostProcessor) bean);
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[addBean]" + bean);
		}
	}

	protected<T> T getBean(Class<T> clazz){
		
		return ctx.getBean(clazz);
	}
	
	protected abstract String getApplicationContextFile();


	@After
	public void afterAbstractSpringTest(){
		
	}


	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException{
		
		
		
	}

}
