package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;


/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:46:28
 */
public class MessageInfoStatis extends AbstractStatisable<MessageInfo> implements Statisable<MessageInfo>{
	
	protected transient final Logger logger = LoggerFactory.getLogger(getClass());

	private NavigableMap<Long, MessageInfo>  col = new ConcurrentSkipListMap<Long, MessageInfo>();

	private NavigableMap<Long, Long>  qpxMap = new ConcurrentSkipListMap<Long, Long>();
	
	private NavigableMap<Long, Long>  delayMap = new ConcurrentSkipListMap<Long, Long>();

	@Override
	public synchronized void add(Long key, MessageInfo rawAdded) {
		
		if(!(rawAdded instanceof MessageInfo)){
			throw new IllegalArgumentException("not MessageInfo, but " + rawAdded.getClass());
		}
		
		MessageInfo added = null;
		try {
			added = (MessageInfo) rawAdded.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("[add]", e);
		} 
		
		MessageInfo messageInfo = col.get(key);
	
		if(messageInfo == null){
			col.put(key, added);
		}else{
			messageInfo.merge(added);
		}
		
	}

	@Override
	public void build(QPX qpx, Long startKey, Long endKey, int intervalCount) {
		
		SortedMap<Long, MessageInfo> sub = col.subMap(startKey, true, endKey, true);
		ajustData(sub, startKey, endKey);
		
		buildDelay(sub, intervalCount, qpx);
		buildQpx(sub, intervalCount, qpx);
		
		//统计完，删除原始数据，为了方便debug，保留120条数据
		removeBefore(sub.lastKey() - 120, col, "col,build");
		
	}
	
	protected void ajustData(SortedMap<Long, MessageInfo> sub,
			Long startKey, Long endKey) {

		Long lastDelay = 0L, lastTotal = 0L;
		for(Long i =startKey ; i <= endKey; i++){
			
			MessageInfo currentMessageInfo = sub.get(i);
			
			if(currentMessageInfo == null){
				if(logger.isDebugEnabled()){
					logger.debug("[insertLackedData]" + i);
				}
				currentMessageInfo = new MessageInfo();
				sub.put(i, currentMessageInfo);
			}
			
			if(i > startKey){
				if(currentMessageInfo.getTotal() < lastTotal || currentMessageInfo.getTotalDelay() < lastDelay){
					currentMessageInfo.markDirty();
				}
			}
			
			lastDelay = currentMessageInfo.getTotalDelay();
			lastTotal = currentMessageInfo.getTotal();
		}
	}

	@Override
	public void doRemoveBefore(Long key) {
		
		removeBefore(key, col, "col");
		removeBefore(key, qpxMap, "qpxMap");
		removeBefore(key, delayMap, "delayMap");
	}

	private void removeBefore(Long key, NavigableMap<Long, ?> map, String desc) {
		
		SortedMap<Long, ?>  toDelete = map.headMap(key);
		for(Long id : toDelete.keySet()){
			if(logger.isDebugEnabled()){
				logger.debug("[removeBefore]" + id + "," + key + "," + desc);
			}
			map.remove(id);
		}
	}

	@Override
	public NavigableMap<Long, Long> getDelay(StatisType type) {
		
		return delayMap;
	}

	@Override
	public NavigableMap<Long, Long> getQpx(StatisType type) {
		
		return qpxMap;
	}

	private void buildQpx(SortedMap<Long, MessageInfo> rawData, int intervalCount, QPX qpx) {

		
		int step = 0;
		long count = 0;
		Long startKey = rawData.firstKey();
		MessageInfo lastMessageInfo = null;
		int realIntervalCount = 0;
		
		for(Entry<Long, MessageInfo> entry: rawData.entrySet()){
			
			Long key = entry.getKey();
			MessageInfo info = entry.getValue(); 
			
			if(step != 0){
				if(isDataLegal(info, lastMessageInfo)){
					
					count += info.getTotal() - lastMessageInfo.getTotal();
					realIntervalCount++;
				}
			}
			
			lastMessageInfo = info;
			
			if(step >= intervalCount){
				
				if(count < 0){
					count = 0;
				}
				
				int realintervalTimeSeconds = realIntervalCount * AbstractCollector.SEND_INTERVAL;
				double realIntervalTimeMinutes = (double)realintervalTimeSeconds/60;
				
				if(realintervalTimeSeconds == 0){
					qpxMap.put(startKey, 0L);
				}else{
					switch(qpx){
						case SECOND:
							qpxMap.put(startKey, (long) (count/realintervalTimeSeconds));
						break;
						case MINUTE:
							qpxMap.put(startKey, (long)(count/realIntervalTimeMinutes));
						break;
					}
				}
				step  = 1;
				count = 0;
				startKey = key;
				realIntervalCount = 0;
				continue;
			}
			step++;

		}
		
	}

	private boolean isDataLegal(MessageInfo info, MessageInfo lastMessageInfo) {
		
		return !info.isDirty() && !lastMessageInfo.isDirty() && info.getTotal() >0 && lastMessageInfo.getTotal() > 0;
	}

	private void buildDelay(SortedMap<Long, MessageInfo> rawData, int intervalCount, QPX qpx){
		
		int step = 0;
		long delay = 0;
		long count = 0;
		Long startKey = rawData.firstKey();
		MessageInfo lastMessageInfo = null;
		
		for(Entry<Long, MessageInfo> entry: rawData.entrySet()){
			
			Long key = entry.getKey();
			MessageInfo info = entry.getValue(); 

			if(step != 0){
				
				if(isDataLegal(info, lastMessageInfo)){//有效数据
					
					count += info.getTotal() - lastMessageInfo.getTotal();
					delay += info.getTotalDelay() - lastMessageInfo.getTotalDelay();
				}
			}
			
			lastMessageInfo = info;
			
			
			if(step >= intervalCount){
				if(delay < 0){
					delay = 0;
				}
				if(count > 0){
					delayMap.put(startKey, delay/count);
				}else{
					delayMap.put(startKey, 0L);
				}
				step  = 1;
				count = 0;
				delay = 0;
				startKey = key;
				continue;
			}
			step++;

		}
	}

	@Override
	public boolean isEmpty() {
		
		for(Long qpx : qpxMap.values()){
			if(qpx > 0){
				return false;
			}
		}
		
		for(MessageInfo info : col.values()){
			if(!info.isEmpty()){
				return false;
			}
		}
		return true;
	}

	@Override
	public void cleanEmpty() {
		//nothing need to be done
	}

	@Override
	public String toString() {
		return "[col]" + col + "\n" +
			   "[qpx]" + qpxMap + "\n" +
				"[delay]" + delayMap;
		
	}

	@Override
	protected Statisable<?> getValue(Object key) {
		
		throw  new UnsupportedOperationException("unsupported operation getValue()");
	}
}
