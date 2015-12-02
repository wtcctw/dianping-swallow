package com.dianping.swallow.web.dao.impl;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.ClusterManager;
import com.dianping.swallow.common.internal.dao.impl.ClusterCreateException;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoCluster;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.mongodb.Mongo;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:04:18
 */
@Component
public class DefaultWebMongoManager implements WebMongoManager {

	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	public static final String TOPIC_COLLECTION = "c";
	public static final String PRE_MSG = "msg#";

	@Autowired
	private ClusterManager clusterManager;

	@Autowired
	private SwallowConfig swallowConfig;
	

	@SuppressWarnings("deprecation")
	@Override
	public MongoTemplate getMessageMongoTemplate(String topicName) {
		Mongo mongo = this.getMongoClient(topicName);
		MongoTemplate template = new MongoTemplate(new SimMongoDbFactory(mongo, PRE_MSG+ topicName));
		template.setReadPreference(mongo.getReadPreference());
		return template;
	}

	private Mongo getMongoClient(String topicName) {

		TopicConfig topicConfig = swallowConfig.getTopicConfig(topicName);
		String storeUrl = topicConfig.getStoreUrl();
		
		if(StringUtils.isEmpty(storeUrl)){
			storeUrl = swallowConfig.defaultTopicConfig().getStoreUrl();
		}
		try {
			Cluster cluster;
			cluster = clusterManager.getCluster(storeUrl);
			if(! (cluster  instanceof MongoCluster)){
				throw new IllegalStateException("cluster not supported:" + cluster.getClass() + ","+ cluster);
			}

			MongoCluster mongoCluster = (MongoCluster) cluster;
			
			return mongoCluster.getMongoClient();
		} catch (ClusterCreateException e) {
			logger.error("[getMongoClient]" + topicName + "," + storeUrl, e);
		}
		return null;
	}


}