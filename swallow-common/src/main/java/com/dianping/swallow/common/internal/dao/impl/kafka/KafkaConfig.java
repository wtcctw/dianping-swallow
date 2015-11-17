package com.dianping.swallow.common.internal.dao.impl.kafka;

import com.dianping.swallow.common.internal.config.AbstractLionConfig;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午3:55:16
 */
public class KafkaConfig extends AbstractLionConfig{
	
	private static final String KAFKA_CONIFG_BASIC_SUFFIX = "kafkaconfig";

	private boolean readFromMaster = true;

	public KafkaConfig(String fileName, String suffix, boolean isUseLion) {
		super(fileName, StringUtils.join(SPLIT, KAFKA_CONIFG_BASIC_SUFFIX, suffix), isUseLion);
		loadConfig();
	}

	public KafkaConfig(String localFileConfig, String suffix) {
		this(localFileConfig, suffix, true);
	}
	
	
	
	
	
	

}
