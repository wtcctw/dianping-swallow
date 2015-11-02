package com.dianping.swallow.web.controller.dto;

import java.util.Date;
import java.util.List;

import com.dianping.swallow.web.model.alarm.AlarmLevelType;
import com.dianping.swallow.web.model.alarm.RelatedType;
import com.dianping.swallow.web.model.alarm.SendInfo;
/**
 * 
 * @author qiyin
 *
 * 2015年8月9日 下午5:12:02
 */
public class AlarmDto {

	private long eventId;

	private int number;

	private AlarmLevelType type;

	private String title;

	private String body;

	private String related;
	
	private String relatedUrl;

	private RelatedType relatedType;

	private List<SendInfo> sendInfos;

	private Date createTime;

	private String sourceIp;

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public AlarmLevelType getType() {
		return type;
	}

	public void setType(AlarmLevelType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getRelated() {
		return related;
	}

	public void setRelated(String related) {
		this.related = related;
	}

	public void setRelatedUrl(String relatedUrl) {
		this.relatedUrl = relatedUrl;
	}

	public RelatedType getRelatedType() {
		return relatedType;
	}

	public void setRelatedType(RelatedType relatedType) {
		this.relatedType = relatedType;
	}

	public void setSendInfos(List<SendInfo> sendInfos) {
		this.sendInfos = sendInfos;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

}
