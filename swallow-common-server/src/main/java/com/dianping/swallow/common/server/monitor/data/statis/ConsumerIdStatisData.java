package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.NavigableMap;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午5:32:22
 */
public class ConsumerIdStatisData extends AbstractStatisable<ConsumerIdData>{
	
	private MessageInfoTotalMapStatis sendMessages = new MessageInfoTotalMapStatis();
	
	private MessageInfoTotalMapStatis ackMessages = new MessageInfoTotalMapStatis();
	
	@Override
	public void add(Long time, ConsumerIdData added) {
		
		sendMessages.add(time, added.getSendMessages());
		ackMessages.add(time, added.getAckMessages());
		
	}

	@Override
	public void doRemoveBefore(Long time) {
		
		sendMessages.removeBefore(time);
		ackMessages.removeBefore(time);
	}

	@Override
	public void build(QPX qpx, Long startKey, Long endKey, int intervalCount) {
		
		sendMessages.build(qpx, startKey, endKey, intervalCount);
		ackMessages.build(qpx, startKey, endKey, intervalCount);
	}

	@Override
	public void cleanEmpty() {
		sendMessages.cleanEmpty();
		ackMessages.cleanEmpty();
	}

	@Override
	public boolean isEmpty() {
		
		if(!sendMessages.isEmpty() || !ackMessages.isEmpty()){
			return false;
		}
		return true;
	}

	@Override
	protected Statisable<?> getValue(Object key) {
		
		throw new IllegalArgumentException("unsupported method");
	}

	@Override
	public NavigableMap<Long, Long> getDelay(StatisType type) {
		switch(type){
			case SEND:
				return sendMessages.getDelay(type);
			case ACK:
				return ackMessages.getDelay(type);
			default:
				throw new IllegalStateException("unsupported type:" + type);
		}
	}

	@Override
	public NavigableMap<Long, Long> getQpx(StatisType type) {
		
		switch(type){
			case SEND:
				return sendMessages.getQpx(type);
			case ACK:
				return ackMessages.getQpx(type);
			default:
				throw new IllegalStateException("unsupported type:" + type);
		}
	}

}
