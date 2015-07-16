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
	 * producer server statis data qps peak type
	 */
	PRODUCER_SERVER_STATIS_QPS_P,

	/**
	 * producer server statis data qps valley type
	 */
	PRODUCER_SERVER_STATIS_QPS_V,
	
	/**
	 * producer server statis data qps fluctuation type
	 */
	PRODUCER_SERVER_STATIS_QPS_F,

	/**
	 * producer topic statis data qps peak type
	 */
	PRODUCER_TOPIC_STATIS_QPS_P,

	/**
	 * producer topic statis data qps valley type
	 */
	PRODUCER_TOPIC_STATIS_QPS_V,

	/**
	 * producer topic statis data qps fluctuation type
	 */
	PRODUCER_TOPIC_STATIS_QPS_F,

	/**
	 * producer topic statis data delay type
	 */
	PRODUCER_TOPIC_STATIS_DELAY,

	/**
	 * consumer server data sender type
	 */
	CONSUMER_SERVER_SENDER,

	/**
	 * consumer server slave port open type
	 */
	CONSUMER_SERVER_PORT,

	/**
	 * consumer server slave and master port both open type
	 */
	CONSUMER_SERVER_PORT_BOTH,
	
	/**
	 * consumer server slave and master port both open type
	 */
	CONSUMER_SERVER_PORT_BOTH_F,

	/**
	 * consumer server statis data send qps peak type
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_P,

	/**
	 * consumer server statis data send qps valley type
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_V,
	
	/**
	 * consumer server statis data send qps fluctuation type
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_F,

	/**
	 * consumer server statis data ack qps peak type
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_P,

	/**
	 * consumer server statis data ack qps valley type
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_V,
	
	/**
	 * consumer server statis data ack qps fluctuation type
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_F,

	/**
	 * consumer topic statis data send qps peak type
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_P,

	/**
	 * consumer topic statis data send qps valley type
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_V,

	/**
	 * consumer topic statis data send qps fluctuation type
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_F,

	/**
	 * consumer topic statis data send delay type
	 */
	CONSUMER_TOPIC_STATIS_SEND_DELAY,

	/**
	 * consumer topic statis data ack qps peak type
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_P,

	/**
	 * consumer topic statis data ack qps valley type
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_V,

	/**
	 * consumer topic statis data ack qps fluctuation type
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_F,

	/**
	 * consumer topic statis data ack delay type
	 */
	CONSUMER_TOPIC_STATIS_ACK_DELAY,

	/**
	 * consumer consumerid statis data send qps peak type
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_P,

	/**
	 * consumer consumerid statis data send qps valley type
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_V,

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_F,

	/**
	 * consumer consumerid statis data send delay type
	 */
	CONSUMER_CONSUMERID_STATIS_SEND_DELAY,

	/**
	 * consumer consumerid statis data send accumulation type
	 */
	CONSUMER_CONSUMERID_STATIS_SEND_ACCU,

	/**
	 * consumer consumerid statis data ack qps peak type
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_P,

	/**
	 * consumer consumerid statis data ack qps valley type
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_V,

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_F,

	/**
	 * consumer consumerid statis data ack delay type
	 */
	CONSUMER_CONSUMERID_STATIS_ACK_DELAY,
}
