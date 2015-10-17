package com.dianping.swallow.web.controller.filter.lion;

import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mingdongli
 *
 *         2015年9月22日上午8:52:24
 */
@Component
public class ConsumerServerLionFilter extends AbstractLionFilter {

	public static final String DEFAULT = "default";

	@Autowired
	private LionConfigManager lionConfigManager;

	@Override
	public synchronized ResponseStatus doFilterHelper(LionFilterEntity lionFilterEntity, LionFilterResult result,
			FilterChain<LionFilterEntity, LionFilterResult> chain) {

		String topic = lionFilterEntity.getTopic();
		boolean isTest = lionFilterEntity.isTest();
		LionConfigure lionConfigure = lionFilterEntity.getLionConfigure();
		if (lionConfigure == null) {
			return ResponseStatus.EMPTYARGU;
		}
		String consumerServer = lionConfigure.getConsumerServer();
		StringBuilder stringBuilder = new StringBuilder();

		String consumerServerConfig = (String) getValue(TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI, Boolean.FALSE);
		String defaultConfig = loadDefaultConfig(consumerServerConfig);
		if (StringUtils.isBlank(defaultConfig)) {
			topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			return ResponseStatus.NODEFAULT;
		}
		if (defaultConfig.equals(consumerServer)) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Set swallow.consumer.consumerServerURI of %s to default=%s", topic,
						defaultConfig));
			}
			return ResponseStatus.SUCCESS;
		}
		stringBuilder.append(consumerServerConfig).append(";\n").append(topic).append("=")
				.append(consumerServer);
		String newConsumerServerLionConfig = stringBuilder.toString();
		if (newConsumerServerLionConfig.length() < lionConfigManager.getConsumerServerUriLength()) {
			topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			return ResponseStatus.INVALIDLENGTH;
		}
		ResponseStatus status = doEditLion(TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI,
				newConsumerServerLionConfig, consumerServerConfig, isTest, null);
		return status;
	}

	private String loadDefaultConfig(String config) {
		Map<String, String> map = parseServerURIString(config);
		if (map != null) {
			boolean isContained = map.keySet().contains(DEFAULT);
			if (isContained) {
				return map.get(DEFAULT);
			}
		}
		return StringUtils.EMPTY;
	}

	public static Map<String, String> parseServerURIString(String value) {

		Map<String, String> result = new HashMap<String, String>();

		for (String topicNamesToURI : value.split("\\s*;\\s*")) {

			if (StringUtils.isEmpty(topicNamesToURI)) {
				continue;
			}

			String[] splits = topicNamesToURI.split("=");
			if (splits.length != 2) {
				continue;
			}
			String consumerServerURI = splits[1].trim();
			String topicNameStr = splits[0].trim();
			result.put(topicNameStr, consumerServerURI);
		}

		return result;
	}

}
