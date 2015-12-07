package com.dianping.swallow.common.internal.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mengwenchao
 * 
 *         2015年4月18日 下午10:04:45
 */
public class AbstractConfig {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private String localFileName; 
	
	public AbstractConfig(){
		
	}
	
	public AbstractConfig(String localFileName) {
		
		this.localFileName = localFileName;
	}

	protected void loadConfig() {
		
		if(localFileName == null){
			if(logger.isInfoEnabled()){
				logger.info("[loadConfig][localFileName null]");
			}
			return ;
		}
		
		InputStream ins = null;
		
		File file = new File(localFileName);
		
		if(!file.exists()){
			
			URL url = getClass().getClassLoader().getResource(localFileName);
			if(url != null){
				try {
					ins = url.openStream();
					if(logger.isInfoEnabled()){
						logger.info("[loadConfig]" + url);
					}
				} catch (IOException e) {
					logger.error("[loadLocalConfig]" + url, e);
				}
			}
			
		}else{
			try {
				ins = new FileInputStream(file);
				if(logger.isInfoEnabled()){
					logger.info("[loadConfig]" + file.getAbsolutePath());
				}
			} catch (FileNotFoundException e) {
				logger.error("[loadConfig]" + localFileName, e);
			}
		}
		
		if(ins == null){
			logger.warn("[loadLocalConfig][file not found]" + localFileName);
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

		for (String key : props.stringPropertyNames()) {
			
			String value = getValue(key, props).trim();
			setFieldValue(key, value);
		}

		if (logger.isInfoEnabled()) {
			Field[] fields = getClass().getDeclaredFields();
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

	protected void setFieldValue(String key, String value) {
		
		if(logger.isInfoEnabled()){
			logger.info("[setFieldValue]" + key + ":" + value);
		}
		
		Class<?> clazz = this.getClass();
		Field field = null;
		try {
			field = clazz.getDeclaredField(key.trim());
		} catch (Exception e) {
			logger.error("unknown property found: " + key);
			return;
		}
		
		field.setAccessible(true);
		try {
			
			if (field.getType().equals(Integer.TYPE)) {
				field.set(this, Integer.parseInt(value));
			} else if (field.getType().equals(Long.TYPE)) {
				field.set(this, Long.parseLong(value));
			} else if (field.getType().equals(String.class)) {
				field.set(this, value);
			} else {
				field.set(this, Boolean.parseBoolean(value));
			}
		} catch (Exception e) {
			logger.error("can not parse property " + key +"," + value, e);
		}
		
	}

	protected String getValue(String key, Properties props) {
		
		return props.getProperty(key);
	}

}
