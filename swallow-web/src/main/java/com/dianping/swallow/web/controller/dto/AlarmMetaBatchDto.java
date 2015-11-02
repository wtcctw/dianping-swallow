package com.dianping.swallow.web.controller.dto;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 * 2015年8月17日 下午3:37:42
 */
public class AlarmMetaBatchDto {
	
	private List<Integer> metaIds;
	
	private UpdateType updateType;
	
	private boolean isOpen;

	public List<Integer> getMetaIds() {
		return metaIds;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public boolean getIsOpen() {
		return isOpen;
	}

	public enum UpdateType{
		SMS,WEIXIN,MAIL,SWALLOW,BUSINESS;
	}

}
