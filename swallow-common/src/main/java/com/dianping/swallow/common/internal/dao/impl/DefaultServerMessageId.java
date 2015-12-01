package com.dianping.swallow.common.internal.dao.impl;

import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.ServerMessageId;
import com.dianping.swallow.common.internal.dao.TopicPartition;
import com.dianping.swallow.common.internal.message.DefaultMessageId;

/**
 * @author mengwenchao
 *
 * 2015年11月10日 下午6:01:07
 */
public class DefaultServerMessageId extends DefaultMessageId implements ServerMessageId{
	
	private Cluster cluster;

	public DefaultServerMessageId(Cluster cluster, TopicPartition topicPartition, Long messageId) {
		
		super(topicPartition, messageId);
		this.cluster = cluster;
	}

	@Override
	public Cluster getCluster() {
		return cluster;
	}

}
