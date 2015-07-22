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
	PRODUCER_SERVER_PIGEON_SERVICE(1),

	/**
	 * producer server data sender type
	 */
	PRODUCER_SERVER_SENDER(2),

	/**
	 * producer server statis data qps peak type
	 */
	PRODUCER_SERVER_QPS_PEAK(3),

	/**
	 * producer server statis data qps valley type
	 */
	PRODUCER_SERVER_QPS_VALLEY(4),

	/**
	 * producer server statis data qps fluctuation type
	 */
	PRODUCER_SERVER_QPS_FLUCTUATION(5),

	/**
	 * producer topic statis data qps peak type
	 */
	PRODUCER_TOPIC_QPS_PEAK(6),

	/**
	 * producer topic statis data qps valley type
	 */
	PRODUCER_TOPIC_QPS_VALLEY(7),

	/**
	 * producer topic statis data qps fluctuation type
	 */
	PRODUCER_TOPIC_QPS_FLUCTUATION(8),

	/**
	 * producer topic statis data delay type
	 */
	PRODUCER_TOPIC_MESSAGE_DELAY(9),

	/**
	 * consumer server data sender type
	 */
	CONSUMER_SERVER_SENDER(10),

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
	 * consumer server statis data send qps peak type
	 */
	CONSUMER_SERVER_SENDQPS_PEAK(14),

	/**
	 * consumer server statis data send qps valley type
	 */
	CONSUMER_SERVER_SENDQPS_VALLEY(15),

	/**
	 * consumer server statis data send qps fluctuation type
	 */
	CONSUMER_SERVER_SENDQPS_FLUCTUATION(16),

	/**
	 * consumer server statis data ack qps peak type
	 */
	CONSUMER_SERVER_ACKQPS_PEAK(17),

	/**
	 * consumer server statis data ack qps valley type
	 */
	CONSUMER_SERVER_ACKQPS_VALLEY(18),

	/**
	 * consumer server statis data ack qps fluctuation type
	 */
	CONSUMER_SERVER_ACKQPS_FLUCTUATION(19),

	/**
	 * consumer topic statis data send qps peak type
	 */
	CONSUMER_TOPIC_SENDQPS_PEAK(20),

	/**
	 * consumer topic statis data send qps valley type
	 */
	CONSUMER_TOPIC_SENDQPS_VALLEY(21),

	/**
	 * consumer topic statis data send qps fluctuation type
	 */
	CONSUMER_TOPIC_SENDQPS_FLUCTUATION(22),

	/**
	 * consumer topic statis data send delay type
	 */
	CONSUMER_TOPIC_SENDMESSAGE_DELAY(23),

	/**
	 * consumer topic statis data ack qps peak type
	 */
	CONSUMER_TOPIC_ACKQPS_PEAK(24),

	/**
	 * consumer topic statis data ack qps valley type
	 */
	CONSUMER_TOPIC_ACKQPS_VALLEY(25),

	/**
	 * consumer topic statis data ack qps fluctuation type
	 */
	CONSUMER_TOPIC_ACKQPS_FLUCTUATION(26),

	/**
	 * consumer topic statis data ack delay type
	 */
	CONSUMER_TOPIC_ACKMESSAGE_DELAY(27),

	/**
	 * consumer consumerid statis data send qps peak type
	 */
	CONSUMER_CONSUMERID_SENDQPS_PEAK(28),

	/**
	 * consumer consumerid statis data send qps valley type
	 */
	CONSUMER_CONSUMERID_SENDQPS_VALLEY(29),

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_SENDQPS_FLUCTUATION(30),

	/**
	 * consumer consumerid statis data send delay type
	 */
	CONSUMER_CONSUMERID_SENDMESSAGE_DELAY(31),

	/**
	 * consumer consumerid statis data send accumulation type
	 */
	CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION(32),

	/**
	 * consumer consumerid statis data ack qps peak type
	 */
	CONSUMER_CONSUMERID_ACKQPS_PEAK(33),

	/**
	 * consumer consumerid statis data ack qps valley type
	 */
	CONSUMER_CONSUMERID_ACKQPS_VALLEY(34),

	/**
	 * consumer consumerid statis data send qps fluctuation type
	 */
	CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION(35),

	/**
	 * consumer consumerid statis data ack delay type
	 */
	CONSUMER_CONSUMERID_ACKMESSAGE_DELAY(36),

	/**
	 * consumer server slave service is start up
	 */
	CONSUMER_SERVER_SLAVESERVICE_STARTED(37),

	/**
	 * producer server service repaired type
	 */
	PRODUCER_SERVER_PIGEON_SERVICE_OK(38),

	/**
	 * producer server data sender repaired type
	 */
	PRODUCER_SERVER_SENDER_OK(39),
	/**
	 * consumer server data sender repaired type
	 */
	CONSUMER_SERVER_SENDER_OK(40),

	/**
	 * consumer server slave port open repaired type
	 */
	CONSUMER_SERVER_PORT_OPENED_OK(41),

	/**
	 * consumer server slave service is start up repaired
	 */
	CONSUMER_SERVER_SLAVESERVICE_STARTED_OK(42),

	/**
	 * producer server statis data qps ok type
	 */
	PRODUCER_SERVER_QPS_OK(43),

	/**
	 * consumer server statis data send qps ok type
	 */
	CONSUMER_SERVER_SENDQPS_OK(44),

	/**
	 * consumer server statis data send qps ok type
	 */
	CONSUMER_SERVER_ACKQPS_OK(45);

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
