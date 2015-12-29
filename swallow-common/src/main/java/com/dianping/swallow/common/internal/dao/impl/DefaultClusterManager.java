package com.dianping.swallow.common.internal.dao.impl;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.ClusterFactory;
import com.dianping.swallow.common.internal.dao.ClusterManager;
import com.dianping.swallow.common.internal.observer.impl.AbstractObservableLifecycle;
import com.dianping.swallow.common.internal.observer.impl.ElementAdded;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:24:35
 */
public class DefaultClusterManager extends AbstractObservableLifecycle implements ClusterManager{
	
	private Set<Cluster>  clusterSet = new HashSet<Cluster>(); 
	
	private Map<String, Cluster>  clusterMap = new HashMap<String, Cluster>();
	
	private List<ClusterFactory> clusterFactories;
	
	private SwallowConfig swallowConfig;
	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
	}
	
	@Override
	public Cluster getCluster(String url) throws ClusterCreateException {
		
		Cluster cluster = clusterMap.get(url); 

		if(cluster == null ){
			synchronized (this) {
				
				cluster = clusterMap.get(url);
				if(cluster != null){
					return cluster;
				}
				try{
					cluster = createOrUseExistingCluster(url);
					clusterSet.add(cluster);
					clusterMap.put(url, cluster);
					updateObservers(new ElementAdded<Cluster>(cluster));
				}catch(Exception e){
					logger.error("[getCluster]" + url, e);
					throw new ClusterCreateException(url, e);
				}
			}
		}
		
		return cluster;
	}
	
	

	private Cluster createOrUseExistingCluster(String url) throws Exception {

		Cluster cluster = null;
		for(ClusterFactory clusterFactory : clusterFactories){
			
			if(clusterFactory.accepts(url)){
				
				cluster = clusterFactory.createCluster(url);
				
				for(Cluster currentCluster : clusterSet){
					if(seedsIn(cluster.getSeeds(), currentCluster.getSeeds())){
						
						if(logger.isInfoEnabled()){
							logger.info("[createOrUseExistingCluster][use current]" + url + "," + currentCluster);
						}
						
						return currentCluster;
					}
				}
				break;
			}
		}
		
		if(cluster != null){
			if(logger.isInfoEnabled()){
				logger.info("[createOrUseExistingCluster][create][init]" + url);
			}
			initializeCluster(cluster);
		}

		return cluster;
	}

	
	private boolean seedsIn(List<InetSocketAddress> current, List<InetSocketAddress> toTest) {
		
		if(toTest.containsAll(current)){
			return true;
		}
		
		return false;
	}
	private void initializeCluster(Cluster cluster) throws Exception {
		
		if(logger.isInfoEnabled()){
			logger.info("[initializeCluster]" + cluster);
		}
		cluster.setSwallowConfig(swallowConfig);
		cluster.initialize();
	}

	
	@Override
	protected void doDispose() throws Exception {
		super.doDispose();
		
		for(Cluster cluster : clusterSet){
			
			try{
				disposeCluster(cluster);
			}catch(Exception e){
				logger.error("[doDispose]", e);
			}
		}
		
		clusterSet.clear();
		clusterMap.clear();
	}
	
	private void disposeCluster(Cluster cluster) throws Exception {
		
		if(logger.isInfoEnabled()){
			logger.info("[disposeCluster]" + cluster);
		}
		cluster.dispose();
	}
	@Override
	public Set<Cluster> allClusters() {
		
		return new HashSet<Cluster>(clusterSet);
	}

	public void setClusterFactories(List<ClusterFactory> clusterFactories) {
		this.clusterFactories = clusterFactories;
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	public SwallowConfig getSwallowConfig() {
		return swallowConfig;
	}

	public void setSwallowConfig(SwallowConfig swallowConfig) {
		this.swallowConfig = swallowConfig;
	}
	
	
}
