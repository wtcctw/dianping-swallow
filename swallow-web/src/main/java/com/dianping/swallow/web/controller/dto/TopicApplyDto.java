package com.dianping.swallow.web.controller.dto;

import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.web.controller.handler.data.Treatable;
import org.apache.commons.lang.StringUtils;

/**
 * @author mingdongli
 *
 *         2015年9月7日上午11:17:49
 */
public class TopicApplyDto implements Treatable {

	public static final String KAFKA_SCHEMA = "kafka-";

	private String topic; // 1个

	private int size; // KB/消息

	private float amount; // 条/天

	private String approver; // 批准人
	
	private String applicant; //申请人
	
	private String type; //topic类型

	private String kafkaTopicType; //EFFICIENCY_FIRST or DURABLE_FIRST
	
	private boolean test;

	public String getKafkaTopicType() {
		return kafkaTopicType;
	}

	public void setKafkaTopicType(String kafkaTopicType) {
		this.kafkaTopicType = kafkaTopicType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public boolean isKafkaType(){
		if(StringUtils.isNotBlank(type) && type.startsWith(KAFKA_SCHEMA)){
			return true;
		}
		return false;
	}

	public String buildKafkaTopicType(){

		if(StringUtils.isNotBlank(kafkaTopicType)){
			for(TOPIC_TYPE topic_type : TOPIC_TYPE.values()){
				if(topic_type.toString().equalsIgnoreCase(kafkaTopicType)){
					return topic_type.toString();
				}
			}
		}
		return StringUtils.EMPTY;
	}
}
