package com.dianping.swallow.common.internal.config;


import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.common.internal.util.StringUtils;


/**
 * 如果lion有配置，覆盖本地配置文件配置
 * @author mengwenchao
 * 
 *         2015年4月18日 下午10:04:45
 */
public class AbstractLionConfig extends AbstractConfig implements ConfigChange, ObjectConfig{

	protected final Logger logger = LogManager.getLogger(getClass());
	
	public static final String SPLIT = ".";
	
	public static final String BASIC_SUFFIX = "swallow";

	private final String fullSuffix;
	
	private ConfigCache cc;
	
	private boolean isUseLion = true;
	
	private List<ObjectConfigChangeListener> listeners = new LinkedList<ObjectConfigChangeListener>();

	public AbstractLionConfig(String localFileConfig, String suffix, boolean isUseLion) {
		
		super(localFileConfig);
		
		if(!StringUtils.isEmpty(suffix)){
			fullSuffix = StringUtils.join(SPLIT, BASIC_SUFFIX, suffix);
		}else{
			fullSuffix = BASIC_SUFFIX;
		}
		this.isUseLion = isUseLion;
		
		if(isUseLion){
			cc = ConfigCache.getInstance();
			cc.addChange(this);
		}
		

	}
	
	public AbstractLionConfig(String localFileConfig, String suffix) {
		this(localFileConfig, suffix, true);
	}
	
	@Override
	protected String getValue(String key, Properties props) {
		
		if(isUseLion){
			String lionKey = getLionKey(key);
			String value = cc.getProperty(lionKey);
			if(value != null){
				if(logger.isInfoEnabled()){
					logger.info("[getValue][load value from lion]" + lionKey + ":" + value);
				}
				return value; 
			}
		}
		return super.getValue(key, props);
	}

	private String getLionKey(String key) {
		
		return StringUtils.join(SPLIT, fullSuffix, key);
	}

	private String getRawKey(String lionKey) {

		return lionKey.substring(fullSuffix.length() + 1);
	}

	@Override
	public void onChange(String lionKey, String value) {
		
		if(lionKey.startsWith(fullSuffix)){
			
			if(logger.isInfoEnabled()){
				logger.info("[onChange]" + lionKey + "," + value);
			}
			String rawKey = getRawKey(lionKey); 
			setFieldValue(rawKey, value);
			notifyListeners(rawKey);
		}
	}

	public synchronized void addChangeListener(ObjectConfigChangeListener listener){

		listeners.add(listener);
	}
	
	public synchronized void notifyListeners(String key){
		
		for(ObjectConfigChangeListener listener : listeners){
			try{
				listener.onChange(this, key);
			}catch(Throwable th){
				logger.error("[notifyListeners]" + listener + "," + key, th);
			}
		}
	}
}
