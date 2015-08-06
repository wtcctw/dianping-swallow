package com.dianping.swallow.web.monitor.wapper;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.dashboard.DashboardContainerUpdater;


/**
 * @author mingdongli
 *
 * 2015年7月10日上午9:04:54
 */
@Component
public class ConsumerDataRetrieverWrapper {
	
	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;
	
	public static final String TOTAL = "total";
	
	public Set<String> getKey(String ... keys){
		
		return consumerDataRetriever.getKeys(new CasKeys(keys));
	}
	
	public Object getValue(String ... keys){
		
		return consumerDataRetriever.getValue(new CasKeys(keys));
	}
	
	public Set<String> getKeyWithoutTotal(String ... keys){
		
		Set<String> set = consumerDataRetriever.getKeys(new CasKeys(keys));
		
		if(set != null){
			removeTotal(set);
			return set;
		}else{
			return Collections.emptySet();
		}
		
	}
	
	public void registerListener(DashboardContainerUpdater dashboardContainerUpdater){
		
		consumerDataRetriever.registerListener(dashboardContainerUpdater);
	}
	
	private void removeTotal(Set<String> set){
		
		if (set.contains(TOTAL)) {
			set.remove(TOTAL);
		}
	}
	

}
