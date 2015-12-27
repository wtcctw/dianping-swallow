package com.dianping.swallow.common.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qi.yin
 *         2015/12/14  下午7:07.
 */
public class ServiceLoaderUtil {

    private static Map<Class<?>, Object> serviceMap = new ConcurrentHashMap<Class<?>, Object>();

    private static Map<Class<?>, List<?>> servicesMap = new ConcurrentHashMap<Class<?>, List<?>>();

	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> clazz) {
		
        if (!serviceMap.containsKey(clazz)) {
        	
            ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
            
            if (serviceLoader != null) {
            	
                for (T service : serviceLoader) {
                    serviceMap.put(clazz, service);
                    break;
                }
            }
        }
        
        return (T) serviceMap.get(clazz);
    }

	@SuppressWarnings("unchecked")
	public static <T> List<T> getServices(Class<T> clazz) {

        if (!servicesMap.containsKey(clazz)) {

            ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
            List<T> services = new ArrayList<T>();
            
            if (serviceLoader != null) {
                for (T service : serviceLoader) {
                    services.add(service);
                }
            }
            
            servicesMap.put(clazz, services);
        }
        
        return (List<T>) servicesMap.get(clazz);
    }


}
