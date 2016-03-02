package com.dianping.swallow.web.controller.filter.validator;

import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.service.GroupResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;


/**
 * @author mingdongli
 *
 * 2015年9月18日下午4:47:44
 */
public class ValidatorTest extends MockTest{
	
	@Mock
	private UserUtils userUtils;
	
	@Mock
	private TopicResourceService topicResourceService;

	@Mock
	private GroupResourceService groupResourceService;
	
	private TopicApplyDto topicApplyDto;

	private SwitchValidatorFilter switchValidatorFilter = new SwitchValidatorFilter();
	
	private ValidatorFilterChain validatorFilterChain = new ValidatorFilterChain();

	@Before
	public void setUp() throws Exception {
		 
		Map<String, Set<String>> topicToWhiltelist = new HashMap<String, Set<String>>();
		List<String> groupNames = new ArrayList<String>();
		groupNames.add("default");
		groupNames.add("pay");
		groupNames.add("search");
		groupNames.add("kafka-default");

		Set<String> topics = new HashSet<String>();
		topicToWhiltelist.put("example", topics);
		topicToWhiltelist.put("example1", topics);
		
		topicApplyDto = new TopicApplyDto();
		topicApplyDto.setAmount(50);
		topicApplyDto.setSize(1);
		topicApplyDto.setTopic("swallow-test");
		topicApplyDto.setApprover("hongjun.zhong");
		topicApplyDto.setType("default");
		topicApplyDto.setApplicant("mingdong.li");


		TypeValidatorFilter typeValidator = new TypeValidatorFilter();
		QuoteValidatorFilter quoteValidator = new QuoteValidatorFilter();
		NameValidatorFilter nameValidator = new NameValidatorFilter();
		ApplicantValidatorFilter applicantValidatorFilter = new ApplicantValidatorFilter();
		validatorFilterChain.addFilter(switchValidatorFilter);
		validatorFilterChain.addFilter(applicantValidatorFilter);
		validatorFilterChain.addFilter(nameValidator);
		validatorFilterChain.addFilter(quoteValidator);
		validatorFilterChain.addFilter(typeValidator);

		switchValidatorFilter.setApplyTopicSwitch("true");
		nameValidator.setTopicResourceService(topicResourceService);

		typeValidator.setGroupResourceService(groupResourceService);

		Mockito.doReturn(Boolean.TRUE).when(userUtils).isAdministrator(topicApplyDto.getApprover(), true);
		Mockito.doReturn(topicToWhiltelist).when(topicResourceService).loadCachedTopicToAdministrator();
		Mockito.doReturn(groupNames).when(groupResourceService).findAllGroupName();
	}

	@Test
	public void test() {
		
		ValidatorFilterResult validatorFilterResult = new ValidatorFilterResult();
		/*-----------------------通过测试--------------------------*/
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == 0);

		/*-----------------------域名称写成EMAIL--------------------------*/
		validatorFilterChain.resetFilterChain();
		topicApplyDto.setApplicant(" mingdong.li@dianping.com ");
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == 0);

		/*-----------------------开关关闭--------------------------*/
		validatorFilterChain.resetFilterChain();
		topicApplyDto.setApplicant("mingdong.li");
		switchValidatorFilter.setApplyTopicSwitch("false");
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == -24);
		switchValidatorFilter.setApplyTopicSwitch("true");

		/*-----------------------配额太大--------------------------*/
		validatorFilterChain.resetFilterChain();
		topicApplyDto.setSize(20);
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == -12);
		
		/*-----------------------topic名字不合法--------------------------*/
		validatorFilterChain.resetFilterChain();
		topicApplyDto.setSize(1);
		topicApplyDto.setTopic("swallow.test");
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == -11);

		/*-----------------------topic已经申请--------------------------*/
		validatorFilterChain.resetFilterChain();
		topicApplyDto.setApprover("hongjun.zhong");
		topicApplyDto.setTopic("example");
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == -11);

		/*-----------------------类型不正确--------------------------*/
		validatorFilterChain.resetFilterChain();
		topicApplyDto.setTopic("swallow-test");
		topicApplyDto.setType("general1");
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == -20);

		/*-----------------------Kafka类型不正确--------------------------*/
		validatorFilterChain.resetFilterChain();
		topicApplyDto.setType("kafka-default");
		topicApplyDto.setTopic("swallow-test");
//		topicApplyDto.setKafkaTopicType(TOPIC_TYPE.DURABLE_FIRST.toString().toLowerCase());
		topicApplyDto.setKafkaTopicType(TOPIC_TYPE.DURABLE_FIRST.toString().toLowerCase() + "-");
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
		Assert.assertTrue(validatorFilterResult.getStatus() == -25);

	}

}
