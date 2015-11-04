package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.Date;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig.SwallowConfigArgs;
import com.dianping.swallow.common.internal.dao.ClusterManager;
import com.dianping.swallow.common.internal.dao.HeartbeatDAO;
import com.dianping.swallow.common.internal.dao.impl.AbstractDao;
import com.dianping.swallow.common.internal.dao.impl.ClusterCreateException;
import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.internal.monitor.ComponentMonitable;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoHeartbeatDAO extends AbstractDao<MongoCluster> implements HeartbeatDAO<MongoCluster>, Observer, Lifecycle, ComponentMonitable{

	public static final String  TICK = "t";

	private SwallowConfig swallowConfig;
	
	private ClusterManager clusterManager;
	
	public MongoHeartbeatDAO() {
		this(null);
	}
	
	public MongoHeartbeatDAO(MongoCluster cluster) {
		super(cluster);
	}

   @Override
   public Date updateLastHeartbeat(String ip) {
	   
      DBCollection collection = getHeartbeatCollection(ip.replace('.', '_'));

      Date curTime = new Date();
      DBObject insert = BasicDBObjectBuilder.start().add(TICK, curTime).get();
      collection.insert(insert);
      return curTime;
   }

   @Override
   public Date findLastHeartbeat(String ip) {
	   
      DBCollection collection = getHeartbeatCollection(ip.replace('.', '_'));

      DBObject fields = BasicDBObjectBuilder.start().add(TICK, Integer.valueOf(1)).get();
      DBObject orderBy = BasicDBObjectBuilder.start().add(TICK, Integer.valueOf(-1)).get();
      DBCursor cursor = collection.find(null, fields).sort(orderBy).limit(1);
      try {
         if (cursor.hasNext()) {
            DBObject result = cursor.next();
            return (Date) result.get(TICK);
         }
      } finally {
         cursor.close();
      }
      return null;
   }

	private void createHeartbeatCluster() throws ClusterCreateException {

		String serverURI = swallowConfig.getHeartBeatMongo();
		
		MongoCluster cluster = (MongoCluster)clusterManager.getCluster(serverURI);
		
		if(cluster == null){
			throw new IllegalStateException("[createHeartbeatMongo][fail]" + serverURI);
		}

		this.cluster = cluster;
		if (logger.isInfoEnabled()) {
			logger.info("[createHeartbeatCluster]" + cluster);
		}
	}

	@Override
	public void update(Observable observable, Object rawArgs) {

		SwallowConfigArgs args = (SwallowConfigArgs) rawArgs;
		
		switch (args.getItem()) {
		
			case HEART_BEAT_STORE:
				if(logger.isInfoEnabled()){
					logger.info("[update]" + args);
				}
				try {
					createHeartbeatCluster();
				} catch (ClusterCreateException e) {
					logger.error("[update]", e);
				}
				break;
			default:
				//nothing need to do
		}
	}

	public DBCollection getHeartbeatCollection(String ip) {

		return cluster.getCollection(AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_SIZE, 
						AbstractSwallowConfig.DEFAULT_CAPPED_COLLECTION_MAX_DOC_NUM, 
						"heartbeat#" + ip,
						new BasicDBObject(MongoHeartbeatDAO.TICK, -1));
	}

	
	public void setSwallowConfig(SwallowConfig swallowConfig) {
		this.swallowConfig = swallowConfig;
	}

	public void setClusterManager(ClusterManager clusterManager){
		this.clusterManager = clusterManager;
	}

	@Override
	public void initialize() throws Exception {
		
		swallowConfig.addObserver(this);
		createHeartbeatCluster();
	}

	@Override
	public void start() throws Exception {
		
	}

	@Override
	public void stop() throws Exception {
		
	}

	@Override
	public void dispose() throws Exception {
		
	}

	@Override
	public int getOrder() {
		
		return Math.max(SwallowConfig.ORDER, ClusterManager.ORDER) + 1;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public Object getStatus() {
		return cluster.toString();
	}

}
