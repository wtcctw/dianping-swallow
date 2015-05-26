package com.dianping.swallow.common.server.monitor.data.structure;

import org.springframework.data.annotation.Transient;

import com.dianping.swallow.common.server.monitor.data.Totalable;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年4月25日 上午10:40:08
 */
public abstract class AbstractTotalable implements Totalable{
	
	@JsonIgnore
	@Transient
	private boolean isTotal = false;
	
	
	@Override
	public void setTotal(){
		isTotal = true;
	}
	
	public boolean isTotal(){
		return isTotal;
	}

	

}
