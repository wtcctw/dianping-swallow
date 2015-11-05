package com.dianping.swallow.web.common;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author mingdongli
 *         15/11/5 上午9:22
 */
@Component
public class BeanInitializingMonitor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationListener<ContextRefreshedEvent>, InitializingBean {

    private static final String SPRING_PREFIX = "org.springframework";

    private static final String INNER_BEAN = "inner bean";

    ConcurrentMap<String, Long> timeOfBeans = new ConcurrentHashMap<String, Long>();

    ConcurrentMap<String, Transaction> stringOfTransaction = new ConcurrentHashMap<String, Transaction>();

    Set<String> timeOfBeansProcessed = new HashSet<String>();

    private Transaction bootTransaction;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        Transaction bootTransaction = Cat.newTransaction("Boot", beanName);
        Cat.logEvent("type", beanClass.getSimpleName());
        stringOfTransaction.put(beanName, bootTransaction);
        if (!isInnerBean(beanName) && !timeOfBeansProcessed.contains(beanName)) {
            timeOfBeans.put(beanName, System.currentTimeMillis());
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Transaction bootTransaction = stringOfTransaction.get(beanName);
        bootTransaction.complete();
        if (timeOfBeans.containsKey(beanName)) {
            if (!timeOfBeansProcessed.contains(beanName)) {

                timeOfBeans.put(beanName, System.currentTimeMillis() - timeOfBeans.get(beanName));
                timeOfBeansProcessed.add(beanName);
            }
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        bootTransaction.complete();

        System.out.println("----------begin onApplicationEvent----------");
        if (event.getApplicationContext().getParent() == null) {//root application context 没有parent，他就是老大.
            for (Map.Entry<String, Long> entry : timeOfBeans.entrySet()) {
                System.out.println(entry.getKey() + "  ->  " + entry.getValue());
            }
        }
        timeOfBeansProcessed = null;
        System.out.println("-----------end onApplicationEvent---------------------------");

    }

    private Boolean isInnerBean(String beanName) {
        if (StringUtils.isNotBlank(beanName) && (beanName.startsWith(SPRING_PREFIX) || beanName.indexOf(INNER_BEAN) != -1)) {
            return true;
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        bootTransaction = Cat.newTransaction("Spring", "Boot");
    }
}
