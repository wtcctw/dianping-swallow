package com.dianping.swallow.web.controller.chain.lion;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.impl.PostHttpMethod;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月21日下午9:01:33
 */
public abstract class AbstractLionEditor {

	@Autowired
	protected LionUtil lionUtil;

	@Resource(name = "topicResourceService")
	protected TopicResourceService topicResourceService;

	protected AbstractLionEditor nextSuccessor;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public AbstractLionEditor() {

	}

	public AbstractLionEditor(AbstractLionEditor nextSuccessor) {

		this.nextSuccessor = nextSuccessor;
	}

	protected abstract ResponseStatus editLionHelper(final LionConfigBean lionConfigBean);

	public ResponseStatus editLion(final LionConfigBean lionConfigBean) {

		ResponseStatus responseStatus = editLionHelper(lionConfigBean);
		if (responseStatus != ResponseStatus.SUCCESS) {
			return responseStatus;
		}

		if (nextSuccessor != null) {
			return nextSuccessor.editLion(lionConfigBean);
		}

		return ResponseStatus.SUCCESS;
	}

	protected ResponseStatus doEditLion(String key, String newvalue, String oldvalue) {

		try {
			lionUtil.createOrSetConfig(key, newvalue, new PostHttpMethod());
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s successfully", oldvalue,
						newvalue, key));
			}
			return ResponseStatus.SUCCESS;
		} catch (Exception e) {// HttpURLConnection IllegalStateException
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s failed", oldvalue,
						newvalue, key));
			}
			return ResponseStatus.LIONEXCEPTION;
		}
	}

	public void setLionUtil(LionUtil lionUtil) {
		this.lionUtil = lionUtil;
	}

	public void setTopicResourceService(TopicResourceService topicResourceService) {
		this.topicResourceService = topicResourceService;
	}

	public AbstractLionEditor setNextSuccessor(AbstractLionEditor nextSuccessor) {
		this.nextSuccessor = nextSuccessor;
		return this;
	}

}
