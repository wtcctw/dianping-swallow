package com.dianping.swallow.web.controller.validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.chain.validator.AuthenticationValidator;
import com.dianping.swallow.web.controller.chain.validator.NameValidator;
import com.dianping.swallow.web.controller.chain.validator.QuoteValidator;
import com.dianping.swallow.web.controller.chain.validator.TypeValidator;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


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
	
	private AuthenticationValidator validator;
	
	private TopicApplyDto topicApplyDto;

	@Before
	public void setUp() throws Exception {
		 
		Map<String, Set<String>> topicToWhiltelist = new HashMap<String, Set<String>>();
		Set<String> topics = new HashSet<String>();
		topicToWhiltelist.put("example", topics);
		topicToWhiltelist.put("example1", topics);
		
		topicApplyDto = new TopicApplyDto();
		topicApplyDto.setAmount(50);
		topicApplyDto.setSize(1);
		topicApplyDto.setTopic("swallow-test");
		topicApplyDto.setApprover("hongjun.zhong");
		topicApplyDto.setType(MongoType.GENERAL.toString());
		
		
		TypeValidator typeValidator = new TypeValidator();
		QuoteValidator quoteValidator = new QuoteValidator(typeValidator);
		NameValidator nameValidator = new NameValidator(quoteValidator);
		AuthenticationValidator authenticationValidator = new AuthenticationValidator(nameValidator);
		authenticationValidator.setUserUtils(userUtils);
		nameValidator.setTopicResourceService(topicResourceService);
		validator = authenticationValidator;
		Mockito.doReturn(Boolean.TRUE).when(userUtils).isTrueAdministrator(topicApplyDto.getApprover());
		Mockito.doReturn(topicToWhiltelist).when(topicResourceService).loadCachedTopicToAdministrator();
	}

	@Test
	public void test() {
		/*-----------------------通过测试--------------------------*/
		Assert.assertTrue(validator.ValidateTopicApplyDto(topicApplyDto) == ResponseStatus.SUCCESS);
		
		/*-----------------------配额太大--------------------------*/
		topicApplyDto.setSize(20);
		Assert.assertTrue(validator.ValidateTopicApplyDto(topicApplyDto) == ResponseStatus.TOOLARGEQUOTA);
		
		/*-----------------------topic名字不合法--------------------------*/
		topicApplyDto.setSize(1);
		topicApplyDto.setTopic("swallow.test");
		Assert.assertTrue(validator.ValidateTopicApplyDto(topicApplyDto) == ResponseStatus.INVALIDTOPICNAME);

		/*-----------------------审批人没有权限--------------------------*/
		topicApplyDto.setTopic("swallow-test");
		topicApplyDto.setApprover("dp.wang");
		Assert.assertTrue(validator.ValidateTopicApplyDto(topicApplyDto) == ResponseStatus.UNAUTHENTICATION);

		/*-----------------------topic已经申请--------------------------*/
		topicApplyDto.setApprover("hongjun.zhong");
		topicApplyDto.setTopic("example");
		Assert.assertTrue(validator.ValidateTopicApplyDto(topicApplyDto) == ResponseStatus.INVALIDTOPICNAME);

		/*-----------------------类型不正确--------------------------*/
		topicApplyDto.setTopic("swallow-test");
		topicApplyDto.setType("general1");
		Assert.assertTrue(validator.ValidateTopicApplyDto(topicApplyDto) == ResponseStatus.INVALIDTYPE);
		
		
	}

}
