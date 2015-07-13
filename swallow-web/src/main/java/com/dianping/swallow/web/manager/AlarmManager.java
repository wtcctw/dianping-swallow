package com.dianping.swallow.web.manager;

/**
 * 
 * @author qiyin
 *
 */
public interface AlarmManager {

	void producerServiceAlarm(String ip);

	void producerSenderAlarm(String ip);

	void producerServerStatisQpsPAlarm(String serverIp, long qpx);

	void producerServerStatisQpsVAlarm(String serverIp, long qpx);

	void producerServerStatisQpsFAlarm(String serverIp, long qpx, long expected);

	void producerTopicStatisQpsPAlarm(String topic, long qpx);

	void producerTopicStatisQpsVAlarm(String topic, long qpx);

	void producerTopicStatisQpsFAlarm(String topic, long qpx, long expected);

	void producerTopicStatisQpsDAlarm(String topic, long delay, long expected);

	void consumerPortAlarm(String masterIp, String slaveIp, boolean isBoth);
	
	void consumerSenderAlarm(String ip);

	void consumerServerStatisSQpsPAlarm(String serverIp, long qpx);

	void consumerServerStatisSQpsVAlarm(String serverIp, long qpx);

	void consumerServerStatisSQpsFAlarm(String serverIp, long qpx, long expected);

	void consumerServerStatisAQpsPAlarm(String serverIp, long qpx);

	void consumerServerStatisAQpsVAlarm(String serverIp, long qpx);

	void consumerServerStatisAQpsFAlarm(String serverIp, long qpx, long expected);

	void consumerTopicStatisSQpsPAlarm(String topic, long qpx);

	void consumerTopicStatisSQpsVAlarm(String topic, long qpx);

	void consumerTopicStatisSQpsFAlarm(String topic, long qpx, long expected);

	void consumerTopicStatisSQpsDAlarm(String topic, long delay, long expected);

	void consumerTopicStatisAQpsPAlarm(String topic, long qpx);

	void consumerTopicStatisAQpsVAlarm(String topic, long qpx);

	void consumerTopicStatisAQpsFAlarm(String topic, long qpx, long expected);

	void consumerTopicStatisAQpsDAlarm(String topic, long delay, long expected);

	void consumerIdStatisSQpsPAlarm(String topic, String consumerId, long qpx);

	void consumerIdStatisSQpsVAlarm(String topic, String consumerId, long qpx);

	void consumerIdStatisSQpsFAlarm(String topic, String consumerId, long qpx, long expected);

	void consumerIdStatisSQpsDAlarm(String topic, String consumerId, long delay, long expected);

	void consumerIdStatisAQpsPAlarm(String topic, String consumerId, long qpx);

	void consumerIdStatisAQpsVAlarm(String topic, String consumerId, long qpx);

	void consumerIdStatisAQpsFAlarm(String topic, String consumerId, long qpx, long expected);

	void consumerIdStatisAQpsDAlarm(String topic, String consumerId, long delay, long expected);
	
	void consumerIdStatisSAccuAlarm(String topic,String consumerId ,long accumulation,long expected);

}
