package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public enum AlarmType {
	
	/**
	 * producer server service type
	 */
	PRODUCER_SERVER_SERVICE,
	
	/**
	 * producer server data sender type
	 */
	PRODUCER_SERVER_SENDER, 
	
	/**
	 * 
	 */
	PRODUCER_SERVER_STATIS_QPS_P,
	
	/**
	 * 
	 */
	PRODUCER_SERVER_STATIS_QPS_V,

	/**
	 * 
	 */
	PRODUCER_TOPIC_STATIS_QPS_P, 

	/**
	 * 
	 */
	PRODUCER_TOPIC_STATIS_QPS_V, 

	/**
	 * 
	 */
	PRODUCER_TOPIC_STATIS_QPS_F, 

	/**
	 * 
	 */
	PRODUCER_TOPIC_STATIS_DELAY, 

	/**
	 * 
	 */
	CONSUMER_SERVER_SENDER,

	/**
	 * 
	 */
	CONSUMER_SERVER_PORT, 

	/**
	 * 
	 */
	CONSUMER_SERVER_PORT_BOTH, 

	/**
	 * 
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_P, 

	/**
	 * 
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_V, 

	/**
	 * 
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_P, 

	/**
	 * 
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_V, 

	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_P, 

	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_V, 

	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_F, 

	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_SEND_DELAY, 
	
	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_P, 
	
	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_V, 
	
	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_F,
	
	/**
	 * 
	 */
	CONSUMER_TOPIC_STATIS_ACK_DELAY, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_P, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_V, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_F, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_SEND_DELAY, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_SEND_ACCU, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_P, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_V, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_F, 
	
	/**
	 * 
	 */
	CONSUMER_CONSUMERID_STATIS_ACK_DELAY,
}
