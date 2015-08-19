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

	public void setMetaIds(List<Integer> metaIds) {
		this.metaIds = metaIds;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}

	public boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	public enum UpdateType{
		SMS,WEIXIN,MAIL,SWALLOW,BUSINESS;
	}

}
