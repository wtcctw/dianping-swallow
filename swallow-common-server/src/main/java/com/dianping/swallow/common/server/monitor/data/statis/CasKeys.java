package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.util.StringUtils;

import java.util.Arrays;

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

	public void reset(){
		keyIndex = 1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CasKeys casKeys = (CasKeys) o;

		if (keyIndex != casKeys.keyIndex) return false;
		if (key != null ? !key.equals(casKeys.key) : casKeys.key != null) return false;
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(keys, casKeys.keys);

	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (keys != null ? Arrays.hashCode(keys) : 0);
		result = 31 * result + keyIndex;
		return result;
	}

	@Override
	public String toString() {
		return key;
	}
}
