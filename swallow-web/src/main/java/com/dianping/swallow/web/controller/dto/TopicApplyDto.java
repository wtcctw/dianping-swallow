package com.dianping.swallow.web.controller.dto;

/**
 * @author mingdongli
 *
 *         2015年9月7日上午11:17:49
 */
public class TopicApplyDto {

	private String topic; // 1个

	private int size; // KB/消息

	private float amount; // 条/天

	private String approver; // 批准人
	
	private String applicant; //申请人
	
	private String type; //topic类型
	
	private boolean test;

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

}
