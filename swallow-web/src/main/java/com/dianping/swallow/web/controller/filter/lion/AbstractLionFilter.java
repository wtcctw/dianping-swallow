package com.dianping.swallow.web.controller.filter.lion;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author mingdongli
 *
 *         2015年9月21日下午9:01:33
 */
public abstract class AbstractLionFilter implements Filter<LionFilterEntity, LionFilterResult> {

	@Autowired
	protected LionUtil lionUtil;

	@Resource(name = "topicResourceService")
	protected TopicResourceService topicResourceService;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected abstract ResponseStatus doFilterHelper(LionFilterEntity lionFilterEntity, LionFilterResult result,
			FilterChain<LionFilterEntity, LionFilterResult> chain);

	@Override
	public void doFilter(LionFilterEntity lionFilterEntity, LionFilterResult result,
			FilterChain<LionFilterEntity, LionFilterResult> chain) {

		String topic = lionFilterEntity.getTopic();
		ResponseStatus responseStatus = doFilterHelper(lionFilterEntity, result, chain);
		
		if (responseStatus == ResponseStatus.SUCCESS) {
			chain.doFilter(lionFilterEntity, result, chain);
		}else{
			topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			buildResult(result, responseStatus.getMessage(), responseStatus.getStatus());
			return;
		}
	}

	protected synchronized ResponseStatus doEditLion(String key, String newValue, String oldValue, boolean test, String env) {
		
		try {
			if(!test){
				lionUtil.createOrSetConfig(key, newValue, "post", env);
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s successfully", oldValue,
						newValue, key));
			}
			return ResponseStatus.SUCCESS;
		} catch (Exception e) {// HttpURLConnection IllegalStateException
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s failed", oldValue,
						newValue, key));
			}
			return ResponseStatus.LIONEXCEPTION;
		}
	}

	protected Object getValue(String key, boolean split){

		String value = lionUtil.getValue(key);
		if(split){
			return splitString(value);
		}

		return value;
	}

	private Set<String> splitString(String value) {

		if (StringUtils.isBlank(value)) {
			return null;
		}

		List<String> topicList = com.dianping.swallow.common.internal.util.StringUtils.splitByDelimiter(value, Pattern.compile("\\s*;\\s*"));
		return new HashSet<String>(topicList);
	}

	protected void buildResult(LionFilterResult result, String message, int status) {
		result.setMessage(message);
		result.setStatus(status);
	}

	public void setLionUtil(LionUtil lionUtil) {
		this.lionUtil = lionUtil;
	}

	public void setTopicResourceService(TopicResourceService topicResourceService) {
		this.topicResourceService = topicResourceService;
	}

}
