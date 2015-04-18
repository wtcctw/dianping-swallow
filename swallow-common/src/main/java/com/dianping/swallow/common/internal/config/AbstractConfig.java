package com.dianping.swallow.common.internal.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;

/**
 * @author mengwenchao
 * 
 *         2015年4月18日 下午10:04:45
 */
public class AbstractConfig {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected void loadLocalConfig(String fileName) {
		
		InputStream ins = DefaultMongoManager.class.getClassLoader().getResourceAsStream(fileName);
		if(ins == null){
			logger.warn("[loadLocalConfig][file not found]" + fileName);
			return;
		}
		loadLocalConfig(ins);

	}

	private void loadLocalConfig(InputStream in) {
		Properties props = new Properties();
		try {
			props.load(in);
			in.close();
		} catch (IOException e1) {
			throw new RuntimeException(e1.getMessage(), e1);
		}

		Class<?> clazz = this.getClass();
		for (String key : props.stringPropertyNames()) {
			if (logger.isInfoEnabled()) {
				logger.info("[loadLocalConfig][key:value]" + key + ":" + props.getProperty(key));
			}
			Field field = null;
			try {
				field = clazz.getDeclaredField(key.trim());
			} catch (Exception e) {
				logger.error("unknown property found: " + key);
				continue;
			}
			field.setAccessible(true);
			if (field.getType().equals(Integer.TYPE)) {
				try {
					field.set(this,
							Integer.parseInt(props.getProperty(key).trim()));
				} catch (Exception e) {
					logger.error("can not parse property " + key, e);
					continue;
				}
			} else if (field.getType().equals(Long.TYPE)) {
				try {
					field.set(this,
							Long.parseLong(props.getProperty(key).trim()));
				} catch (Exception e) {
					logger.error("can not set property " + key, e);
					continue;
				}
			} else if (field.getType().equals(String.class)) {
				try {
					field.set(this, props.getProperty(key).trim());
				} catch (Exception e) {
					logger.error("can not set property " + key, e);
					continue;
				}
			} else {
				try {
					field.set(this,
							Boolean.parseBoolean(props.getProperty(key).trim()));
				} catch (Exception e) {
					logger.error("can not set property " + key, e);
					continue;
				}
			}
		}

		if (logger.isInfoEnabled()) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				f.setAccessible(true);
				if (!Modifier.isStatic(f.getModifiers())) {
					try {
						logger.info(f.getName() + "=" + f.get(this));
					} catch (Exception e) {
					}
				}
			}
		}
	}

}
