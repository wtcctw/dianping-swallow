package com.dianping.swallow.web.model.event;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.AbstractConfig;

/**
 * 
 * @author qiyin
 *
 *         2015年10月15日 下午6:14:40
 */
@Component
public class EventConfigImpl extends AbstractConfig implements EventConfig {

	private static final String ALARM_RECIEVER_FILE_NAME = "swallow-alarm-reciever.properties";

	private static final String COMMA_SPLIT = ",";

	private Set<String> devMobiles;

	private Set<String> devEmails;

	private String mobile;

	private String email;

	public EventConfigImpl() {
		super(ALARM_RECIEVER_FILE_NAME);
	}

	@PostConstruct
	public void initConfig() {
		loadConfig();
		devMobiles = new HashSet<String>();
		devEmails = new HashSet<String>();
		addElement(devMobiles, mobile);
		addElement(devEmails, email);
	}

	private void addElement(Set<String> elementSet, String strSource) {
		if (StringUtils.isBlank(strSource)) {
			return;
		}
		String[] elements = strSource.split(COMMA_SPLIT);
		if (elements != null) {
			for (String element : elements) {
				if (StringUtils.isNotBlank(element)) {
					elementSet.add(element);
				}
			}
		}
	}

	@Override
	public Set<String> getDevMobiles() {
		return devMobiles;
	}

	@Override
	public Set<String> getDevEmails() {
		return devEmails;
	}

}
