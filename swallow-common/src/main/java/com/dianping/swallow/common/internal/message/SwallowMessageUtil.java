package com.dianping.swallow.common.internal.message;


/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午5:06:56
 */
public class SwallowMessageUtil {
	
	public static long getSaveTime(SwallowMessage message){
		
		String time = message.getInternalProperty(InternalProperties.SAVE_TIME);
		if(time == null){
			return 0;
		}
		return Long.parseLong(time);
	}

}
