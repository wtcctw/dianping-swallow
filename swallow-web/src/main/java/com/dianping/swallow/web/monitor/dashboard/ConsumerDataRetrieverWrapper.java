package com.dianping.swallow.web.monitor.dashboard;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;


/**
 * @author mingdongli
 *
 * 2015年7月10日上午9:04:54
 */
@Component
public class ConsumerDataRetrieverWrapper {
	
	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;
	
	private static final String TOTAL = "total";
	
	public Set<String> getKey(String ... keys){
		
		return consumerDataRetriever.getKeys(new CasKeys(keys));
	}
	
	public Object getValue(String ... keys){
		
		return consumerDataRetriever.getValue(new CasKeys(keys));
	}
	
	public Set<String> getKeyWithoutTotal(String ... keys){
		
		Set<String> set = consumerDataRetriever.getKeys(new CasKeys(keys));
		removeTotal(set);
		return set;
	}
	
	private void removeTotal(Set<String> set){
		
		if (set.contains(TOTAL)) {
			set.remove(TOTAL);
		}
	}

}
