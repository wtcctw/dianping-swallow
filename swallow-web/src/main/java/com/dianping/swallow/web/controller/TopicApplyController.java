package com.dianping.swallow.web.controller;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.monitor.collector.PerformanceIndexCollector;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.LionHttpService;
import com.dianping.swallow.web.service.LionHttpService.LionHttpResponse;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月9日下午12:04:31
 */
@Controller
public class TopicApplyController{
 
	//测试完成后需要修改 PRE_TOPIC_KEY DEFAULT_ENV TopicResourceServiceImpl
	private static final String PRE_TOPIC_KEY = "swallow.topiccfgtest.applytopictest.";

	private static final String PRE_MONGO = "mongodb://";

	private static final String SUCCESS = "success";

	private static final String DEFAULT_ENV = "alpha";

	private static final String DEFAULT_PROJECT = "swallow";

	private static final String DEFAULT_DESC = "topic%20config%20from%20LionUtilImpl";

	private static final String CREATE_ERROR = "already exists";

	private static final int DEFAULT_ID = 2;

	private static Set<Integer> SIZE_SET = new TreeSet<Integer>();

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Resource(name = "lionHttpService")
	private LionHttpService lionHttpService;

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	private PerformanceIndexCollector performanceIndexCollector;

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

//	@RequestMapping(value = "/api/topic/apply", method = RequestMethod.POST)
//	@ResponseBody
	public Object applyTopic(@RequestBody TopicApplyDto topicApplyDto) {

		ResponseStatus responseStatus = validate(topicApplyDto);
		if (responseStatus != ResponseStatus.SUCCESS) {
			return responseStatus;
		}

		Pair<String, ResponseStatus> pair;
		String mongoChose;
		String consumerServerChose;

		String bestMongo = performanceIndexCollector.getBestMongo();
		if (StringUtils.isNotBlank(bestMongo)) {
			mongoChose = bestMongo;
		} else {
			pair = performanceIndexCollector.chooseMongoDb();

			if (pair.getSecond() == ResponseStatus.SUCCESS) {
				String mongoFetched = pair.getFirst();
				bestMongo = mongoFetched;
				mongoChose = mongoFetched;
			} else {
				return pair.getSecond();
			}

		}

		String bestConsumerServer = performanceIndexCollector.getBestConsumerServer();
		if (StringUtils.isNotBlank(bestConsumerServer)) {
			consumerServerChose = bestConsumerServer;
		} else {
			pair = performanceIndexCollector.chooseConsumerServer();

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
		
		responseStatus = editSwallowLionConfiguration(lionConfigBean);
		if(responseStatus != ResponseStatus.SUCCESS){
			//若在操作lion失败，则将缓存中的topic清楚，再次操作时就不会出现topic重复的错误
			topicResourceService.loadCachedTopicToWhiteList().remove(topic); 
			return responseStatus;
		}
		
		responseStatus = addApplicantToTopicAdmin(topic, topicApplyDto.getApplicant());
		if(responseStatus != ResponseStatus.SUCCESS){
			return responseStatus;
		}
		
		return ResponseStatus.SUCCESS;
	}

	private ResponseStatus editSwallowLionConfiguration(LionConfigBean lionConfigBean) {

		String topic = lionConfigBean.getTopic();
		String consumerServer = lionConfigBean.getConsumerServer();
		boolean success = setLionValue(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topic, ";");

		if (success) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(topic).append("=").append(consumerServer);
			String consumerValue = stringBuilder.toString();
			success = setLionValue(TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI, consumerValue, "\\s*;\\s*");

			if (success) {
				String key = PRE_TOPIC_KEY + topic;
				MongoConfigBean mongoConfigBean = new MongoConfigBean();
				String mongoURL = PRE_MONGO + lionConfigBean.getMongo();
				mongoConfigBean.setMongoUrl(mongoURL);
				mongoConfigBean.setSize(lionConfigBean.getSize());
				ObjectMapper mapper = new ObjectMapper();
				String value;

				try {
					value = mapper.writeValueAsString(mongoConfigBean);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("Error when json MongoConfigBean");
					}
					return ResponseStatus.LIONEXCEPTION;
				}

				success = createLionValue(key, value);
				if (success) {
					return ResponseStatus.SUCCESS;
				}
			}

		}

		return ResponseStatus.LIONEXCEPTION;
	}

	private boolean setLionValue(String key, String value, String delimitor) {

		LionHttpResponse lionHttpResponse = lionHttpService.get(DEFAULT_ID, DEFAULT_ENV, key);

		if (SUCCESS.equals(lionHttpResponse.getStatus())) {
			String oldValue = lionHttpResponse.getResult();
			String[] oldValues = oldValue.split(delimitor);
			for (String t : oldValues) { // exists
				if (t.equals(value)) {
					return true;
				}
			}
			StringBuilder stringBuilder = new StringBuilder();
			int length = delimitor.length();
			if(length > 1){ //通配空格
				char[] dArray = delimitor.toCharArray();
				delimitor =  dArray[length / 2] + "\n";
			}
			stringBuilder.append(oldValue).append(delimitor).append(value);
			String updateWhiteList = stringBuilder.toString();
			lionHttpResponse = lionHttpService.setUsingPost(DEFAULT_ID, DEFAULT_ENV, key, updateWhiteList);

			if (SUCCESS.equals(lionHttpResponse.getStatus())) {
				return true;
			}
		}

		return false;
	}

	private boolean createLionValue(String key, String value) {

		LionHttpResponse lionHttpResponse = lionHttpService.create(DEFAULT_ID, DEFAULT_PROJECT, key, DEFAULT_DESC);
		String status = lionHttpResponse.getStatus();
		String message = lionHttpResponse.getMessage(); // 已经创建
		if (SUCCESS.equals(status) || message.endsWith(CREATE_ERROR)) {

			lionHttpResponse = lionHttpService.setUsingPost(DEFAULT_ID, DEFAULT_ENV, key, value);
			if (SUCCESS.equals(lionHttpResponse.getStatus())) {
				return true;
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
		Set<String> allTopics = topicResourceService.loadCachedTopicToWhiteList().keySet();
		if (!NameCheckUtil.isTopicNameValid(topic)) {
			return false;
		}
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

	private ResponseStatus addApplicantToTopicAdmin(String topic, String applicant){
		
		TopicResource topicResource = topicResourceService.findByTopic(topic);
		
		if(topicResource != null){ //修改白名单时已经添加
			String oldAdmin = topicResource.getAdministrator();
			
			if(StringUtils.isBlank(oldAdmin)){
				oldAdmin = applicant;
				topicResource.setAdministrator(oldAdmin);
			}else{
				String[] adminArray = oldAdmin.split(",");
				for(String admin : adminArray){
					if(admin.equals(applicant)){
						return ResponseStatus.SUCCESS;
					}
				}
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(oldAdmin).append(",").append(applicant);
				topicResource.setAdministrator(stringBuilder.toString());
			}
			
		}else{
			topicResource = topicResourceService.buildTopicResource(topic);
			topicResource.setAdministrator(applicant);
		}

		boolean success = topicResourceService.update(topicResource);
		
		if(!success){
			return ResponseStatus.MONGOWRITE;
		}else{
			return ResponseStatus.SUCCESS;
		}
		
	}

	public static void main(String[] args) {
		PerformanceIndexCollector PerformanceIndexCollector = new PerformanceIndexCollector();
		String res = PerformanceIndexCollector.chooseMongoDb().getFirst();
		System.out.println("res is " + res);
		LionConfigBean lionConfigBean = new LionConfigBean();
		lionConfigBean.setConsumerServer("1.1.1.1:1234,1.1.1.2:1235");
		lionConfigBean.setMongo("1.1.1.1:1234,1.1.1.2:1235");
		lionConfigBean.setTopic("example");
		lionConfigBean.setSize(300);
		TopicApplyController topicApplyController = new TopicApplyController();
		topicApplyController.editSwallowLionConfiguration(lionConfigBean);
	}

}
