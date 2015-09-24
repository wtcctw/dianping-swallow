package com.dianping.swallow.web.controller.filter.lion;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.impl.PostHttpMethod;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

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

	protected ResponseStatus doEditLion(String key, String newValue, String oldValue, boolean test) {
		
		try {
			if(!test){
				lionUtil.createOrSetConfig(key, newValue, new PostHttpMethod());
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
