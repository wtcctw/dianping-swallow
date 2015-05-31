package com.dianping.swallow.common.internal.message;

import java.util.Map;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午5:06:56
 */
public class SwallowMessageUtil {
	
	public static long getSaveTime(SwallowMessage message){
		
		Map<String, String> internalProperties = message.getInternalProperties();
		
		if(internalProperties == null){
			return 0;
		}
		
		String time = internalProperties.get(MessageDAOImpl.SAVE_TIME);
		if(time == null){
			return 0;
		}
		return Long.parseLong(time);
	}

}
