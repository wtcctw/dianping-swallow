package com.dianping.swallow.common.server.monitor.visitor.impl;

import com.dianping.swallow.common.server.monitor.visitor.KeyBasedVisitor;

/**
 * @author mengwenchao
 *
 * 2015年7月8日 下午2:46:10
 */
public abstract class AbstractKeyBasedVisitor implements KeyBasedVisitor{
	
	@SuppressWarnings("unused")
	private String key = "topic1.consumerid1";
	
	private String []keys ;
	private int 	keyIndex = 0;

	
	public AbstractKeyBasedVisitor(String ...keys) {
		
		this.keys = keys;
	}

	public AbstractKeyBasedVisitor(String key) {
		
		this.key = key;
		this.keys = key.split("\\s*.\\s*");
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
}
