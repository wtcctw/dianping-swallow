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

    ConcurrentMap<String, Transaction> stringOfTransaction = new ConcurrentHashMap<String, Transaction>();

    private Transaction bootTransaction;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (!isInnerBean(beanName)) {
            Transaction transaction = Cat.newTransaction("Boot", beanName);
            Cat.logEvent("type", beanClass.getSimpleName());
            stringOfTransaction.put(beanName, transaction);
        }
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!isInnerBean(beanName)) {
            Transaction transaction = stringOfTransaction.get(beanName);
            if (transaction != null) {
                transaction.setStatus(Transaction.SUCCESS);
                transaction.complete();
                stringOfTransaction.remove(beanName);
            }
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bootTransaction.setStatus(Transaction.SUCCESS);
        bootTransaction.complete();
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