package com.dianping.swallow.common.server.monitor.data;

import java.util.concurrent.ConcurrentHashMap;


/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午4:08:04
 */
@SuppressWarnings("hiding")
public abstract class TotalConcurrentHashMap<String, V extends Mergeable> extends ConcurrentHashMap<String, V> implements Mergeable{

	private static final long serialVersionUID = 1L;
	
	protected V total;
	
	@SuppressWarnings("unchecked")
	public TotalConcurrentHashMap(){
		super();
		
		total = createValue();
		put((String) MonitorData.TOTAL_KEY, total);
	}

	protected abstract V createValue();

	public V getTotal(){
		
		return total;
	}

	public void merge(Mergeable merge){
		
		if(!(merge instanceof TotalConcurrentHashMap)){
			throw new IllegalArgumentException("wrong type : " + merge.getClass());
		}
		
		@SuppressWarnings("unchecked")
		TotalConcurrentHashMap<String, Mergeable> toMerge = (TotalConcurrentHashMap<String, Mergeable>) merge;
		
		for(java.util.Map.Entry<String, V> entry : entrySet()){

			String key = entry.getKey();
			V value = entry.getValue();
			
			toMerge.get(key).merge(value);
		}
	}
}
