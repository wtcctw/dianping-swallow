/**
 * 
 */
package com.dianping.swallow.web.manager;

import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;

/**
 * @author mingdongli
 *
 * 2015年4月20日 下午9:45:54
 */
@Component
public class MongoManager{
	private String name;
	
	
	MongoClient getMongo(String topic) throws UnknownHostException{
		return new MongoClient();
		
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
}
