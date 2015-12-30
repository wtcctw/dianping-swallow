package com.dianping.swallow.common.internal.config.impl.file;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractDynamicConfig;

/**
 * @author mengwenchao
 *
 * 2015年12月30日 下午5:08:18
 */
public class FileDynamicConfig extends AbstractDynamicConfig implements DynamicConfig{
	
	
	private Properties properties = new Properties();
	
	public FileDynamicConfig(String localConfigFileName){
		
		try{
			// 如果本地文件存在，则使用Lion本地文件
			URL resource = getClass().getClassLoader().getResource(localConfigFileName);
			
			if (resource != null) {
				if(logger.isInfoEnabled()){
					logger.info("[FileDynamicConfig]" + resource);
				}
				InputStream in = resource.openStream();
				try {
					properties.load(in);
					if (logger.isInfoEnabled()) {
						logger.info("Load Lion local config file :" + localConfigFileName);
					}
				} finally {
					in.close();
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(String key) {
		
		return properties.getProperty(key);
	}

	@Override
	public Map<String, String> getProperties(String prefix) {
		
		Map<String, String> result = new HashMap<String, String>();
		for (Entry<Object, Object> entry : properties.entrySet()) {

			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			if (key.startsWith(prefix)) {
				result.put(key, value);
			}
		}

		return result;
	}

	@Override
	protected Object doAddConfigChangeListener(ConfigChangeListener listener) {
		return null;
	}

	@Override
	protected void doRemoveConfigChangeListener(Object change) {
		
	}

}
