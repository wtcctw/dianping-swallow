package com.dianping.swallow.web.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.LionHttpService;
import com.dianping.swallow.web.service.LionHttpService.LionHttpResponse;
import com.dianping.swallow.web.service.TopicApplyService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月9日下午12:04:31
 */
@Controller
public class TopicApplyController {

	private static final String PRE_TOPIC_KEY = "swallow.topiccfg.";

	private static final String PRE_MONGO = "mongodb://";

	private static final String SUCCESS = "success";

	private static final String DEFAULT_ENV = EnvZooKeeperConfig.getEnv();

	private static final String DEFAULT_PROJECT = "swallow";

	private static final String DEFAULT_DESC = "topic%20config%20from%20LionUtilImpl";

	private static final String CREATE_ERROR = "already exists";

	private static Set<Integer> SIZE_SET = new TreeSet<Integer>();

	private int DEFAULT_ID = 2;

	private String DEFAULT_GROUP = "test";

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;
	
	@Resource(name = "topicApplyService")
	private TopicApplyService topicApplyService;

	@Resource(name = "lionHttpService")
	private LionHttpService lionHttpService;

	@Autowired
	private UserUtils userUtils;

	private Logger logger = LoggerFactory.getLogger(getClass());

	static { // 最少500M，最大50G
		SIZE_SET.add(500);
		SIZE_SET.add(1000);
		SIZE_SET.add(2000);
		SIZE_SET.add(3000);
		SIZE_SET.add(5000);
		SIZE_SET.add(10000);
		SIZE_SET.add(20000);
		SIZE_SET.add(30000);
		SIZE_SET.add(40000);
		SIZE_SET.add(50000);
	}

	@RequestMapping(value = "/api/topic/apply", method = RequestMethod.POST)
	@ResponseBody
	public Object applyTopic(@RequestBody TopicApplyDto topicApplyDto) {

		boolean test = topicApplyDto.isTest();

		ResponseStatus responseStatus = validate(topicApplyDto);
		if (responseStatus != ResponseStatus.SUCCESS) {
			if (logger.isInfoEnabled() && test) {
				logger.info(String.format("Error when validate topicApplyDto with response status %d and message %s",
						responseStatus.getStatus(), responseStatus.getMessage()));
			}
			return responseStatus;
		}

		Pair<String, ResponseStatus> pair;
		String mongoChose;
		String consumerServerChose;

		boolean search = topicApplyDto.isSearch();
		String bestMongo = null;
		
		if(search){
			bestMongo = topicApplyService.getSearchMongo();
		}else{
			bestMongo = topicApplyService.getBestMongo();
		}
		
		if (StringUtils.isNotBlank(bestMongo)) {
			mongoChose = bestMongo;
		} else {
			
			if(search){
				pair = topicApplyService.chooseSearchMongoDb();
			}else{
				pair = topicApplyService.chooseMongoDbWithoutSearch();
			}

			if (pair.getSecond() == ResponseStatus.SUCCESS) {
				String mongoFetched = pair.getFirst();
				bestMongo = mongoFetched;
				mongoChose = mongoFetched;
			} else {
				return pair.getSecond();
			}

		}

		String bestConsumerServer = topicApplyService.getBestConsumerServer();
		if (StringUtils.isNotBlank(bestConsumerServer)) {
			consumerServerChose = bestConsumerServer;
		} else {
			pair = topicApplyService.chooseConsumerServer();

			if (pair.getSecond() == ResponseStatus.SUCCESS) {
				String consumerServerFetched = pair.getFirst();
				bestConsumerServer = consumerServerFetched;
				consumerServerChose = consumerServerFetched;
			} else {
				return pair.getSecond();
			}

		}

		LionConfigBean lionConfigBean = new LionConfigBean();

		float amount = topicApplyDto.getAmount();
		int size = topicApplyDto.getSize();
		int size4sevenday = (int) (amount * size * 7 * 10);
		for (Integer s : SIZE_SET) {
			if (s >= size4sevenday) {
				size4sevenday = s;
				break;
			}
		}

		String topic = topicApplyDto.getTopic().trim();
		lionConfigBean.setTopic(topic);
		lionConfigBean.setMongo(mongoChose);
		lionConfigBean.setConsumerServer(consumerServerChose);
		lionConfigBean.setSize(size4sevenday);
		if("product".equals(DEFAULT_ENV)){
			DEFAULT_GROUP = "";
			DEFAULT_ID = 67;
		}
		lionConfigBean.setGroup(DEFAULT_GROUP);
		lionConfigBean.setTest(test);

		responseStatus = editSwallowLionConfiguration(lionConfigBean);
		if (responseStatus != ResponseStatus.SUCCESS) {
			// 若操作lion失败，则将缓存中的topic清除，再次操作时就不会出现topic重复的错误
			topicResourceService.loadCachedTopicToWhiteList().remove(topic);
			return responseStatus;
		}

		responseStatus = addApplicantToTopicAdmin(topic, topicApplyDto.getApplicant());
		if (responseStatus != ResponseStatus.SUCCESS) {
			return responseStatus;
		}

		return ResponseStatus.SUCCESS;
	}

	private ResponseStatus editSwallowLionConfiguration(LionConfigBean lionConfigBean) {

		String topic = lionConfigBean.getTopic();
		String consumerServer = lionConfigBean.getConsumerServer();
		String group = lionConfigBean.getGroup();
		boolean test = lionConfigBean.isTest();
		boolean success = setLionValue(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topic, ";", test, group);
		Map<String,Set<String>> topicToWhiteList = topicResourceService.loadCachedTopicToWhiteList();

		if (success) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(topic).append("=").append(consumerServer);
			String consumerValue = stringBuilder.toString();
			success = setLionValue(TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI, consumerValue, "\\s*;\\s*",
					test, group);

			if (success) {
				//在else失败时，清除了缓存的topic，再次申请时lion没有更新，所以lion不会通知，需要手动增加
				topicToWhiteList.put(topic, new HashSet<String>());
				String key = PRE_TOPIC_KEY + topic;
				MongoConfigBean mongoConfigBean = new MongoConfigBean();
				String mongoURL = PRE_MONGO + lionConfigBean.getMongo();
				mongoConfigBean.setMongoUrl(mongoURL);
				mongoConfigBean.setSize(lionConfigBean.getSize());

				JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
				String value = jsonBinder.toJson(mongoConfigBean);

				success = createLionValue(key, value, test, group);
				if (success) {
					return ResponseStatus.SUCCESS;
				}
			}

		}

		//失败后缓存的whitelist中清除相应的topic，再次操作则保证不报-11错误
		topicToWhiteList.remove(topic);
		return ResponseStatus.LIONEXCEPTION;
	}

	private boolean setLionValue(String key, String value, String delimitor, boolean test, String group) {

		LionHttpResponse lionHttpResponse = lionHttpService.get(DEFAULT_ID, DEFAULT_ENV, key, group);

		if (SUCCESS.equals(lionHttpResponse.getStatus())) {
			String oldValue = lionHttpResponse.getResult();
			if (oldValue == null || StringUtils.isBlank(oldValue)) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Error when get value of key %s from lion ", key));
				}
				return false;
			}
			String[] oldValues = oldValue.split(delimitor);
			for (String t : oldValues) { // exists
				if (t.equals(value)) {
					return true;
				}
			}
			StringBuilder stringBuilder = new StringBuilder();
			int length = delimitor.length();
			if (length > 1) { // 通配空格
				char[] dArray = delimitor.toCharArray();
				delimitor = dArray[length / 2] + "\n";
			}
			stringBuilder.append(oldValue).append(delimitor).append(value);
			String newValue = stringBuilder.toString();

			if (test) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Set value from [%s] to [%s] of lion key %s in env %s with id %d",
							oldValue, newValue, key, DEFAULT_ENV, DEFAULT_ID));
				}
				return true;
			} else {
				lionHttpResponse = lionHttpService.setUsingPost(DEFAULT_ID, DEFAULT_ENV, key, newValue, group);
			}

			if (SUCCESS.equals(lionHttpResponse.getStatus())) {
				return true;
			}
		}

		return false;
	}

	private boolean createLionValue(String key, String value, boolean test, String group) {

		if (test) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Create lion key %s with desc %s in env %s with id %d", key, DEFAULT_DESC,
						DEFAULT_ENV, DEFAULT_ID));
				logger.info(String.format("Set value to %s of lion key %s in env %s with id %d", value, key,
						DEFAULT_ENV, DEFAULT_ID));
			}
			return true;
		} else {
			LionHttpResponse lionHttpResponse = lionHttpService.create(DEFAULT_ID, DEFAULT_PROJECT, key, DEFAULT_DESC);
			String status = lionHttpResponse.getStatus();
			String message = lionHttpResponse.getMessage(); // 已经创建
			if (SUCCESS.equals(status) || (message != null && message.endsWith(CREATE_ERROR))) {

				lionHttpResponse = lionHttpService.setUsingPost(DEFAULT_ID, DEFAULT_ENV, key, value, group);
				if (SUCCESS.equals(lionHttpResponse.getStatus())) {
					return true;
				}
			}
		}

		return false;
	}

	private ResponseStatus validate(TopicApplyDto topicApplyDto) {

		String approver = topicApplyDto.getApprover();
		boolean pass = validateAuthentication(approver);
		if (!pass) {
			return ResponseStatus.UNAUTHENTICATION;
		}

		String topic = topicApplyDto.getTopic();
		pass = validateTopicName(topic.trim());
		if (!pass) {
			return ResponseStatus.INVALIDTOPICNAME;
		}

		int size = topicApplyDto.getSize();
		float amount = topicApplyDto.getAmount();
		pass = validateCapSize(size, amount);
		if (!pass) {
			return ResponseStatus.TOOLARGEQUOTA;
		}

		return ResponseStatus.SUCCESS;
	}

	private boolean validateAuthentication(String approver) {

		return userUtils.isTrueAdministrator(approver);
	}

	private boolean validateTopicName(String topic) {

		if (StringUtils.isBlank(topic)) {
			return false;
		}
		if (!NameCheckUtil.isTopicNameValid(topic)) {
			return false;
		}
		Set<String> allTopics = topicResourceService.loadCachedTopicToWhiteList().keySet();
		if (allTopics.contains(topic)) {
			return false;
		}

		return true;
	}

	private boolean validateCapSize(long size, float amount) {

		if (size > 500 || size <= 0 || amount <= 0) {
			return false;
		} else {
			return size * amount <= 700.0f;
		}
	}

	private ResponseStatus addApplicantToTopicAdmin(String topic, String applicant) {

		TopicResource topicResource = topicResourceService.findByTopic(topic);

		if (topicResource != null) { // 修改白名单时已经添加
			String oldAdmin = topicResource.getAdministrator();

			if (StringUtils.isBlank(oldAdmin)) {
				oldAdmin = applicant;
				topicResource.setAdministrator(oldAdmin);
			} else {
				String[] adminArray = oldAdmin.split(",");
				for (String admin : adminArray) {
					if (admin.equals(applicant)) {
						topicResourceService.update(topicResource);
						return ResponseStatus.SUCCESS;
					}
				}
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(oldAdmin).append(",").append(applicant);
				topicResource.setAdministrator(stringBuilder.toString());
			}

		} else {
			topicResource = topicResourceService.buildTopicResource(topic);
			topicResource.setAdministrator(applicant);
		}

		boolean success = topicResourceService.update(topicResource);

		if (!success) {
			return ResponseStatus.MONGOWRITE;
		} else {
			return ResponseStatus.SUCCESS;
		}

	}

}
