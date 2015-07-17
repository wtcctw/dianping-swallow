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
	PRODUCER_SERVER_SERVICE(1),

	/**
	 * producer server data sender type
	 */
	PRODUCER_SERVER_SENDER(2),

	/**
	 * producer server statis data qps peak type
	 */
	PRODUCER_SERVER_STATIS_QPS_P(3),

	/**
	 * producer server statis data qps valley type
	 */
	PRODUCER_SERVER_STATIS_QPS_V(4),

	/**
	 * producer server statis data qps fluctuation type
	 */
	PRODUCER_SERVER_STATIS_QPS_F(5),

	/**
	 * producer topic statis data qps peak type
	 */
	PRODUCER_TOPIC_STATIS_QPS_P(6),

	/**
	 * producer topic statis data qps valley type
	 */
	PRODUCER_TOPIC_STATIS_QPS_V(7),

	/**
	 * producer topic statis data qps fluctuation type
	 */
	PRODUCER_TOPIC_STATIS_QPS_F(8),

	/**
	 * producer topic statis data delay type
	 */
	PRODUCER_TOPIC_STATIS_DELAY(9),

	/**
	 * consumer server data sender type
	 */
	CONSUMER_SERVER_SENDER(10),

	/**
	 * consumer server slave port open type
	 */
	CONSUMER_SERVER_PORT(11),

	/**
	 * consumer server slave and master port both open type
	 */
	CONSUMER_SERVER_PORT_BOTH(12),

	/**
	 * consumer server slave and master port both open type
	 */
	CONSUMER_SERVER_PORT_BOTH_F(13),

	/**
	 * consumer server statis data send qps peak type
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_P(14),

	/**
	 * consumer server statis data send qps valley type
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_V(15),

	/**
	 * consumer server statis data send qps fluctuation type
	 */
	CONSUMER_SERVER_STATIS_SENDQPS_F(16),

	/**
	 * consumer server statis data ack qps peak type
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_P(17),

	/**
	 * consumer server statis data ack qps valley type
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_V(18),

	/**
	 * consumer server statis data ack qps fluctuation type
	 */
	CONSUMER_SERVER_STATIS_ACKQPS_F(19),

	/**
	 * consumer topic statis data send qps peak type
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_P(20),

	/**
	 * consumer topic statis data send qps valley type
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_V(21),

	/**
	 * consumer topic statis data send qps fluctuation type
	 */
	CONSUMER_TOPIC_STATIS_SENDQPS_F(22),

	/**
	 * consumer topic statis data send delay type
	 */
	CONSUMER_TOPIC_STATIS_SEND_DELAY(23),

	/**
	 * consumer topic statis data ack qps peak type
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_P(24),

	/**
	 * consumer topic statis data ack qps valley type
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_V(25),

	/**
	 * consumer topic statis data ack qps fluctuation type
	 */
	CONSUMER_TOPIC_STATIS_ACKQPS_F(26),

	/**
	 * consumer topic statis data ack delay type
	 */
	CONSUMER_TOPIC_STATIS_ACK_DELAY(27),

	/**
	 * consumer consumerid statis data send qps peak type
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_P(28),

	/**
	 * consumer consumerid statis data send qps valley type
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_V(29),

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_STATIS_SENDQPS_F(30),

	/**
	 * consumer consumerid statis data send delay type
	 */
	CONSUMER_CONSUMERID_STATIS_SEND_DELAY(31),

	/**
	 * consumer consumerid statis data send accumulation type
	 */
	CONSUMER_CONSUMERID_STATIS_SEND_ACCU(32),

	/**
	 * consumer consumerid statis data ack qps peak type
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_P(33),

	/**
	 * consumer consumerid statis data ack qps valley type
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_V(34),

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_STATIS_ACKQPS_F(35),

	/**
	 * consumer consumerid statis data ack delay type
	 */
	CONSUMER_CONSUMERID_STATIS_ACK_DELAY(36);

	private int number;

	private AlarmType() {
		
	}

	private AlarmType(int number) {
		this.number = number;
	}

	public int getNumber() {
		return this.number;
	}

}
