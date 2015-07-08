package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *
 * 2015年7月8日 下午5:35:15
 */
public class CasKeys {

	private String key = "topic1.consumerid1";
	
	private String []keys ;
	private int 	keyIndex = 0;

	
	public CasKeys(String ...keys) {
		
		this.keys = keys;
		key = StringUtils.join(".", keys);
	}

	public CasKeys(String key) {

		this.key = key;
		this.keys = key.split("\\s*.\\s*");
		if(this.keys.length == 0){
			this.keys = new String[]{key};
		}
	}

	
	public String getNextKey(){
		
		if(keyIndex < keys.length){
			return keys[keyIndex++];
		}
		
		return null;
	}

	public boolean hasNextKey(){
		
		return keyIndex < keys.length; 
	}

	@Override
	public String toString() {
		return key;
	}
}
