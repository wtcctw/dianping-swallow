package com.dianping.swallow.web.manager;

import com.dianping.swallow.web.model.event.ConsumerIdEvent;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerStatisEvent;
import com.dianping.swallow.web.model.event.TopicEvent;

/**
 * 
 * @author qiyin
 *
 */
public interface MessageManager {

	public void producerServerAlarm(ServerEvent event);

	public void producerServerStatisAlarm(ServerStatisEvent event);

	public void producerTopicStatisAlarm(TopicEvent event);

	public void consumerServerAlarm(ServerEvent event);

	public void consumerServerStatisAlarm(ServerStatisEvent event);

	public void consumerTopicStatisAlarm(TopicEvent event);

	public void consumerIdStatisAlarm(ConsumerIdEvent event);

}
