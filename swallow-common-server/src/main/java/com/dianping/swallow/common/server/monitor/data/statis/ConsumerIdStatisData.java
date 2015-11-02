package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.MapRetriever;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;

import java.util.NavigableMap;
import java.util.Set;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午5:32:22
 */
public class ConsumerIdStatisData extends AbstractStatisable<ConsumerIdData> implements MapRetriever{
	
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

		return !(!sendMessages.isEmpty() || !ackMessages.isEmpty());
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
	public NavigableMap<Long, QpxData> getQpx(StatisType type) {
		
		switch(type){
			case SEND:
				return sendMessages.getQpx(type);
			case ACK:
				return ackMessages.getQpx(type);
			default:
				throw new IllegalStateException("unsupported type:" + type);
		}
	}

	@Override
	public Set<String> getKeys(CasKeys keys, StatisType type) {
		
		if(type  == null){
			return sendMessages.getKeys(keys, null);
		}
		switch(type){
			case SEND:
				return sendMessages.getKeys(keys, type);
			case ACK:
				return ackMessages.getKeys(keys, type);
			default:
				throw new IllegalStateException("unsupported type:" + type);
		}
	}

	@Override
	public Object getValue(CasKeys keys, StatisType type) {

		if(type == null){
			return sendMessages.getValue(keys, null);
		}
		
		switch(type){
		
			case SEND:
				return sendMessages.getValue(keys, type);
			case ACK:
				return ackMessages.getValue(keys, type);
			default:
				throw new IllegalStateException("unsupported type:" + type);
		}
	}

	@Override
	public Set<String> getKeys(CasKeys keys) {
		return getKeys(keys, null);
	}

	@Override
	public Object getValue(CasKeys keys) {
		return getValue(keys, null);
	}

	@Override
	public void merge(String key, KeyMergeable merge) {

		checkType(merge);

		ConsumerIdStatisData toMerge = (ConsumerIdStatisData) merge;
		sendMessages.merge(key, toMerge.sendMessages);
		ackMessages.merge(key, toMerge.ackMessages);
	}

	@Override
	public void merge(Mergeable merge) {

		checkType(merge);

		ConsumerIdStatisData toMerge = (ConsumerIdStatisData) merge;


		sendMessages.merge(toMerge.sendMessages);
		ackMessages.merge(toMerge.ackMessages);
	}

	private void checkType(Mergeable merge) {
		if(!(merge instanceof ConsumerIdStatisData)){
			throw new IllegalArgumentException("wrong type " + merge.getClass());
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException{
		ConsumerIdStatisData clone = (ConsumerIdStatisData) super.clone();
		clone.sendMessages = (MessageInfoTotalMapStatis) sendMessages.clone();
		clone.ackMessages = (MessageInfoTotalMapStatis) ackMessages.clone();
		return clone;
	}
}
