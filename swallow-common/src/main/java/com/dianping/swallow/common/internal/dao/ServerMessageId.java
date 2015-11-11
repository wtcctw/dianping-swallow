package com.dianping.swallow.common.internal.dao;

import com.dianping.swallow.common.message.MessageId;

/**
 * @author mengwenchao
 *
 * 2015年11月10日 下午6:01:32
 */
public interface ServerMessageId extends MessageId{
	
	/**
	 * Id和具体的cluster绑定，避免保存id时cluster不一致
	 * @return
	 */
	Cluster getCluster();

}
