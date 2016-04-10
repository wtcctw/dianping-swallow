package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 *         2015年8月5日 上午10:45:54
 */
public enum AlarmType {

	/**
	 * producer server service type
	 */
	PRODUCER_SERVER_PIGEON_SERVICE(1),

	/**
	 * producer server service repaired type
	 */
	PRODUCER_SERVER_PIGEON_SERVICE_OK(2),

	/**
	 * producer server data sender type
	 */
	PRODUCER_SERVER_SENDER(3),

	/**
	 * producer server data sender repaired type
	 */
	PRODUCER_SERVER_SENDER_OK(4),

	/**
	 * producer server statis data qps peak type
	 */
	PRODUCER_SERVER_QPS_PEAK(5),

	/**
	 * producer server statis data qps valley type
	 */
	PRODUCER_SERVER_QPS_VALLEY(6),

	/**
	 * producer server statis data qps fluctuation type
	 */
	PRODUCER_SERVER_QPS_FLUCTUATION(7),

	/**
	 * producer server statis data qps ok type
	 */
	PRODUCER_SERVER_QPS_OK(8),

	/**
	 * consumer server data sender type
	 */
	CONSUMER_SERVER_SENDER(9),

	/**
	 * consumer server data sender repaired type
	 */
	CONSUMER_SERVER_SENDER_OK(10),

	/**
	 * consumer server slave port open type
	 */
	CONSUMER_SERVER_SLAVEPORT_OPENED(11),

	/**
	 * consumer server slave and master port both open type
	 */
	CONSUMER_SERVER_BOTHPORT_OPENED(12),

	/**
	 * consumer server slave and master port both open type
	 */
	CONSUMER_SERVER_BOTHPORT_UNOPENED(13),

	/**
	 * consumer server slave port open repaired type
	 */
	CONSUMER_SERVER_PORT_OPENED_OK(14),

	/**
	 * consumer server slave service is start up
	 */
	CONSUMER_SERVER_SLAVESERVICE_STARTED(15),

	/**
	 * consumer server slave service is start up repaired
	 */
	CONSUMER_SERVER_SLAVESERVICE_STARTED_OK(16),

	/**
	 * consumer server statis data send qps peak type
	 */
	CONSUMER_SERVER_SENDQPS_PEAK(17),

	/**
	 * consumer server statis data send qps valley type
	 */
	CONSUMER_SERVER_SENDQPS_VALLEY(18),

	/**
	 * consumer server statis data send qps fluctuation type
	 */
	CONSUMER_SERVER_SENDQPS_FLUCTUATION(19),

	/**
	 * consumer server statis data send qps ok type
	 */
	CONSUMER_SERVER_SENDQPS_OK(20),

	/**
	 * consumer server statis data ack qps peak type
	 */
	CONSUMER_SERVER_ACKQPS_PEAK(21),

	/**
	 * consumer server statis data ack qps valley type
	 */
	CONSUMER_SERVER_ACKQPS_VALLEY(22),

	/**
	 * consumer server statis data ack qps fluctuation type
	 */
	CONSUMER_SERVER_ACKQPS_FLUCTUATION(23),

	/**
	 * consumer server statis data send qps ok type
	 */
	CONSUMER_SERVER_ACKQPS_OK(24),

	/**
	 * 
	 */
	SERVER_MONGO_CONFIG(25),
	
	/**
	 * 
	 */
	SERVER_MONGO_CONFIG_OK(26),

	/**
	 *
	 */
	SERVER_BROKER_STATE(27),

	/**
	 *
	 */
	SERVER_BROKER_STATE_OK(28),

	/**
	 *
	 */
	SERVER_CONTROLLER_STATE(29),

	/**
	 *
	 */
	SERVER_CONTROLLER_MULTI_STATE(30),

	/**
	 *
	 */
	SERVER_CONTROLLER_STATE_OK(31),

	/**
	 *
	 */
	SERVER_CONTROLLER_ELECTION_STATE(32),

	/**
	 *
	 */
	SERVER_UNDERREPLICA_STATE(33),

	/**
	 *
	 */
	SERVER_UNDERREPLICA_STATE_OK(34),

	/**
	 *
	 */
	SERVER_UNDERREPLICA_PARTITION_STATE(35),

	/**
	 *
	 */
	SERVER_UNDERREPLICA_PARTITION_STATE_OK(36),

	/**
	 * producer topic statis data qps peak type
	 */
	PRODUCER_TOPIC_QPS_PEAK(1001),

	/**
	 * producer topic statis data qps valley type
	 */
	PRODUCER_TOPIC_QPS_VALLEY(1002),

	/**
	 * producer topic statis data qps fluctuation type
	 */
	PRODUCER_TOPIC_QPS_FLUCTUATION(1003),

	/**
	 * producer topic statis data delay type
	 */
	PRODUCER_TOPIC_MESSAGE_DELAY(1004),

	/**
	 * consumer topic statis data send qps peak type
	 */
	CONSUMER_TOPIC_SENDQPS_PEAK(1005),

	/**
	 * consumer topic statis data send qps valley type
	 */
	CONSUMER_TOPIC_SENDQPS_VALLEY(1006),

	/**
	 * consumer topic statis data send qps fluctuation type
	 */
	CONSUMER_TOPIC_SENDQPS_FLUCTUATION(1007),

	/**
	 * consumer topic statis data send delay type
	 */
	CONSUMER_TOPIC_SENDMESSAGE_DELAY(1008),

	/**
	 * consumer topic statis data ack qps peak type
	 */
	CONSUMER_TOPIC_ACKQPS_PEAK(1009),

	/**
	 * consumer topic statis data ack qps valley type
	 */
	CONSUMER_TOPIC_ACKQPS_VALLEY(1010),

	/**
	 * consumer topic statis data ack qps fluctuation type
	 */
	CONSUMER_TOPIC_ACKQPS_FLUCTUATION(1011),

	/**
	 * consumer topic statis data ack delay type
	 */
	CONSUMER_TOPIC_ACKMESSAGE_DELAY(1012),

	/**
	 * consumer consumerid statis data send qps peak type
	 */
	CONSUMER_CONSUMERID_SENDQPS_PEAK(1013),

	/**
	 * consumer consumerid statis data send qps valley type
	 */
	CONSUMER_CONSUMERID_SENDQPS_VALLEY(1014),

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_SENDQPS_FLUCTUATION(1015),

	/**
	 * consumer consumerid statis data send delay type
	 */
	CONSUMER_CONSUMERID_SENDMESSAGE_DELAY(1016),

	/**
	 * consumer consumerid statis data send accumulation type
	 */
	CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION(1017),

	/**
	 * consumer consumerid statis data ack qps peak type
	 */
	CONSUMER_CONSUMERID_ACKQPS_PEAK(1018),

	/**
	 * consumer consumerid statis data ack qps valley type
	 */
	CONSUMER_CONSUMERID_ACKQPS_VALLEY(1019),

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION(1020),

	/**
	 * consumer consumerid statis data ack delay type
	 */
	CONSUMER_CONSUMERID_ACKMESSAGE_DELAY(1021),

	/**
	 * producer client send data type
	 */
	PRODUCER_CLIENT_SENDER(1022),

	/**
	 * consumer client receive data type
	 */
	CONSUMER_CLIENT_RECEIVER(1023),

	/**
	 * producer message size
	 */
	PRODUCER_TOPIC_MESSAGE_SIZE(1024);

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
