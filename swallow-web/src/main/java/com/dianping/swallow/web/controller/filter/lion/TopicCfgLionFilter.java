package com.dianping.swallow.web.controller.filter.lion;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月22日上午8:55:35
 */
@Component
public class TopicCfgLionFilter extends AbstractLionFilter {

	private static final String PRE_TOPIC_KEY = "swallow.topiccfg.";

	private static final String PRE_MONGO = "mongodb://";

	@Override
	public ResponseStatus doFilterHelper(LionFilterEntity lionFilterEntity, LionFilterResult result,
			FilterChain<LionFilterEntity, LionFilterResult> chain) {

		String topic = lionFilterEntity.getTopic();
		boolean isTest = lionFilterEntity.isTest();
		
		topicResourceService.loadCachedTopicToAdministrator().put(topic, new HashSet<String>());
		String key = PRE_TOPIC_KEY + topic;
		MongoConfigBean mongoConfigBean = new MongoConfigBean();
		LionConfigure lionConfigure = lionFilterEntity.getLionConfigure();
		if (lionConfigure == null) {
			return ResponseStatus.EMPTYARGU;
		}
		String mongoURL = PRE_MONGO + lionConfigure.getMongoServer();
		mongoConfigBean.setMongoUrl(mongoURL);
		mongoConfigBean.setSize(lionConfigure.getSize4sevenday());

		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		String value = jsonBinder.toJson(mongoConfigBean);

		return doEditLion(key, value, "", isTest, null);
	}

}
