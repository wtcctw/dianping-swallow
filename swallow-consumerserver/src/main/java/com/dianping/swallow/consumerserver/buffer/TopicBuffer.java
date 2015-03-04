package com.dianping.swallow.consumerserver.buffer;

import java.util.Iterator;

import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * 缓存消息，作为全局唯一缓存
 * @author mengwenchao
 *
 * 2015年3月3日 下午6:19:22
 */
public interface TopicBuffer {
	
	
	/**
	 * 获取大于startMessageId的iterator
	 * @param startMessageId
	 * @return
	 */
	Iterator<SwallowMessage> iterator(Long startMessageId);
	
	void setMemoryUsage(MemoryUsage memoryUsage);
		
}
