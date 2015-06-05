package com.dianping.swallow.web.monitor.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.config.ObjectConfigChangeListener;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.MongoManager;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.ConsumerIdUtil;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.StatisDetailType;
import com.dianping.swallow.web.config.WebConfig;
import com.dianping.swallow.web.config.impl.DefaultWebConfig;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 * 2015年5月28日 下午3:06:46
 */
@Component
public class DefaultAccumulationRetriever extends AbstractRetriever implements AccumulationRetriever, ObjectConfigChangeListener{

	private Map<String, TopicAccumulation> topics = new ConcurrentHashMap<String, DefaultAccumulationRetriever.TopicAccumulation>();
	
	
	@Autowired
	private MessageDAO messageDao;
	
	@Autowired
	private MongoManager mongoManager;
	
	@Autowired
	private WebConfig webConfig;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;
	
	private ExecutorService executors;
	
	@PostConstruct
	public void postDefaultAccumulationRetriever(){
		
		int corePoolSize = mongoManager.getMongoCount() * mongoManager.getMongoOptions().getConnectionsPerHost();
		if(logger.isInfoEnabled()){
			logger.info("[postDefaultAccumulationRetriever]" + corePoolSize);
		}
		executors = Executors.newFixedThreadPool(corePoolSize, new MQThreadFactory("ACCUMULATION_RETRIEVER-"));
		webConfig.addChangeListener(this);
	}
	

	@Override
	protected void doBuild() {
		
		SwallowActionWrapper actionWrapper = new CatActionWrapper("DefaultAccumulationRetriever", "doBuild");
		
		actionWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				
				buildAllAccumulations();
			}
		});
	}

	protected void buildAllAccumulations() {
		
		Map<String, Set<String>> topics = consumerDataRetriever.getAllTopics();
		
		if(logger.isInfoEnabled()){
			logger.info("[buildAllAccumulations][begin]");
		}
		
		final CountDownLatch latch = new CountDownLatch(latchSize(topics));
		for(Entry<String, Set<String>> entry : topics.entrySet()){
			
			final String topicName = entry.getKey();
			final Set<String> consumerIds = entry.getValue();
			
			executors.execute(new Runnable(){

				@Override
				public void run() {
					try{
						putAccumulation(topicName, consumerIds);
					}finally{
						latch.countDown();
					}
				}
			});
		}
		try {
			boolean result = latch.await(getBuildInterval(), TimeUnit.SECONDS);
			if(!result){
				logger.error("[buildAllAccumulations][wait returned, but task has not finished yet!]");
			}
		} catch (InterruptedException e) {
			logger.error("[buildAllAccumulations]", e);
		}
	}
	
	private int latchSize(Map<String, Set<String>> topics) {
		
		return topics.size();
	}

	private void putAccumulation(final String topicName, final Set<String> consumerIds) {
		
		CatActionWrapper catAction = new CatActionWrapper("putAccumulationTopic", topicName);
		
		catAction.doAction(new SwallowAction() {
			
			@Override
			public void doAction() throws SwallowException {
				
				for(String consumerId : consumerIds){
					
					putAccumulation(topicName, consumerId);
					TopicAccumulation topic = topics.get(topicName);
					topic.retain(consumerIds);
				}
			}
		});
	}

	protected void putAccumulation(final String topicName, final String consumerId) {

		if(ConsumerIdUtil.isNonDurableConsumerId(consumerId)){
			return; 
		}

		CatActionWrapper catAction = new CatActionWrapper("putAccumulationConsumerId",  topicName + ":" + consumerId);
		
		catAction.doAction(new SwallowAction() {
			
			@Override
			public void doAction() throws SwallowException {
				
				long size = 0;
				try{
					size = messageDao.getAccumulation(topicName, consumerId);
				}catch(Exception e){
					logger.error("[putAccumulation]" + topicName + "," + consumerId, e);
				}
				TopicAccumulation topicAccumulation = MapUtil.getOrCreate(topics, topicName, TopicAccumulation.class);
				topicAccumulation.addConsumerId(consumerId, size);
			}
		});
		
	}


	@Override
	protected void doRemove(long toKey) {

		for(TopicAccumulation topicAccumulation : topics.values()){
			topicAccumulation.removeBefore(toKey);
		}
	}
	
	@Override
	protected Set<String> getTopicsInMemory(long start, long end) {
		
		return topics.keySet();
	}

	@Override
	public Map<String, StatsData> getAccumulationForAllConsumerId(String topic,
			long start, long end) {
		
		if(dataExistInMemory(start, end)){
			return getAccumulationForAllConsumerIdInMemory(topic, start, end);
		}
		
		return getAccumulationForAllConsumerIdInDb(topic, start, end);

	}

	private Map<String, StatsData> getAccumulationForAllConsumerIdInDb(
			String topic, long start, long end) {
		return getAccumulationForAllConsumerIdInMemory(topic, start, end);
	}

	private Map<String, StatsData> getAccumulationForAllConsumerIdInMemory(
			String topic, long start, long end) {
		
		Map<String, StatsData> result = new HashMap<String, StatsData>();
		TopicAccumulation topicAccumulation = topics.get(topic);
		for(Entry<String, ConsumerIdAccumulation> entry : topicAccumulation.consumers.entrySet()){
			
			String consumerId = entry.getKey();
			ConsumerIdAccumulation consumerIdAccumulation = entry.getValue();
			
			StatsDataDesc desc = new ConsumerStatsDataDesc(topic, consumerId, StatisDetailType.ACCUMULATION);
			result.put(consumerId, createStatsData(desc, consumerIdAccumulation.getAccumulations(getSampleIntervalCount()), start, end));
		}
		
		return result;
	}

	@Override
	public Map<String, StatsData> getAccumulationForAllConsumerId(String topic){
		
		return getAccumulationForAllConsumerId(topic, getDefaultStart(), getDefaultEnd());
	}

	public static class TopicAccumulation{
		
		private Map<String, ConsumerIdAccumulation> consumers = new ConcurrentHashMap<String, DefaultAccumulationRetriever.ConsumerIdAccumulation>();
		
		public void addConsumerId(String consumerId, long accumulation){
			
			ConsumerIdAccumulation consumerIdAccumulation = MapUtil.getOrCreate(consumers, consumerId, ConsumerIdAccumulation.class);
			consumerIdAccumulation.add(accumulation);
		}
		
		public void retain(Set<String> consumerIds) {
			
			Set<String> currentIds = new HashSet<String>(consumers.keySet());
			currentIds.removeAll(consumerIds);
			
			for(String removeId : currentIds){
				
				consumers.remove(removeId);
			}
		}

		public void removeBefore(Long toKey){
			
			for(ConsumerIdAccumulation consumer : consumers.values()){
				
				consumer.removeBefore(toKey);
			}
		}
		
		public void remove(String consumerId){
			
			consumers.remove(consumerId);
		}
		
		public Map<String, ConsumerIdAccumulation> consumers(){
			return consumers;
		}
	}
	
	public static class ConsumerIdAccumulation{
		
		private NavigableMap<Long, Long> accumulations = new ConcurrentSkipListMap<Long, Long>();
		
		protected final Logger logger     = LoggerFactory.getLogger(getClass());
		
		protected long lastInsertTime = System.currentTimeMillis();

		public void add(long accumulation) {
			
			
			Long key = getKey(System.currentTimeMillis());
			if(logger.isDebugEnabled()){
				logger.debug("[add]" + key + ":" + accumulation);
			}
			accumulations.put(key, accumulation);
		}
		
		/**
		 * For unit test
		 * @param key
		 * @param accumulation
		 */
		@Deprecated
		public void add(Long key, long accumulation) {
			
			if(logger.isDebugEnabled()){
				logger.debug("[add]" + key + ":" + accumulation);
			}
			accumulations.put(key, accumulation);
		}

		
		private void ajustData(int intervalCount) {
			
			Long current = System.currentTimeMillis();
			
			Long lastKey = getKey(lastInsertTime);
			Long currentKey = getKey(current);
			
			NavigableMap<Long, Long> sub = accumulations.subMap(lastKey, true, currentKey, false);
			
			Long last = -1L;
			
			for(Long key : sub.keySet()){
				
				if(last != -1){
					Long add = (key - last)/intervalCount -1;
					
					for(int i=0 ; i<add ; i++){
						accumulations.put(last + intervalCount, 0L);
					}
				}
				last = key;
			}
			
			lastInsertTime = current;
		}

		public NavigableMap<Long, Long> getAccumulations(int intervalCount) {
			
			ajustData(intervalCount);
			return accumulations;
		}

		public List<Long> data() {
			
			List<Long> result = new LinkedList<Long>();
			result.addAll(accumulations.values());
			return result;
		}

		public void removeBefore(Long toKey){
			
			Map<Long, Long> toDelete = accumulations.headMap(toKey);
			for(Long key : toDelete.keySet()){
				if(logger.isDebugEnabled()){
					logger.debug("[removeBefore]" + key);
				}
				accumulations.remove(key);
			}
		}
	}
	
	@Override
	protected long getBuildInterval() {
		
		return webConfig.getAccumulationBuildInterval();
	}

	@Override
	protected int getSampleIntervalTime() {
		
		return webConfig.getAccumulationBuildInterval();
	}

	

	@Override
	public void onChange(Object config, String key) throws Exception {
		
		if(key.equals(DefaultWebConfig.FIELD_ACCUMULATION)){
			stop();
			start();
		}
	}
}
