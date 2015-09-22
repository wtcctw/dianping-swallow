package com.dianping.swallow.web.controller.chain.lion;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午8:55:35
 */
@Component
public class TopicCfgLionEditor extends AbstractLionEditor{
	
	private static final String PRE_TOPIC_KEY = "swallow.topiccfg.";

	private static final String PRE_MONGO = "mongodb://";

	public TopicCfgLionEditor(){
		super();
	}

	public TopicCfgLionEditor(AbstractLionEditor nextSuccessor) {
		super(nextSuccessor);
	}

	@Override
	protected ResponseStatus editLionHelper(LionConfigBean lionConfigBean) {
		
		String topic = lionConfigBean.getTopic();
		boolean test = lionConfigBean.isTest();
		
		topicResourceService.loadCachedTopicToAdministrator().put(topic, new HashSet<String>());
		String key = PRE_TOPIC_KEY + topic;
		MongoConfigBean mongoConfigBean = new MongoConfigBean();
		String mongoURL = PRE_MONGO + lionConfigBean.getConfigureResult().getMongoServer();
		mongoConfigBean.setMongoUrl(mongoURL);
		mongoConfigBean.setSize(lionConfigBean.getConfigureResult().getSize4servenday());

		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		String value = jsonBinder.toJson(mongoConfigBean);

		if (test) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Create lion key %s with value %s successfully", key, value));
			}
			return ResponseStatus.SUCCESS;
		} else {
			ResponseStatus responseStatus = doEditLion(key, value, "");
			if (responseStatus != ResponseStatus.SUCCESS) {
				topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			}
			
			return responseStatus;
		}
	}

}
