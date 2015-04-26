package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.common.server.monitor.data.Totalable;

/**
 * @author mengwenchao
 *
 * 2015年4月25日 上午10:40:08
 */
public abstract class AbstractTotalable implements Totalable{
	
	private boolean isTotal = false;
	
	
	@Override
	public void setTotal(){
		isTotal = true;
	}
	
	public boolean isTotal(){
		return isTotal;
	}

	

}
