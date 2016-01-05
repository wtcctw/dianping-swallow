package com.dianping.swallow.web.controller.filter.validator;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.service.GroupResourceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * Author   mingdongli
 * 15/12/23  上午10:06.
 */
public class TypeValidatorFilterTest extends MockTest{

    @Mock
    private GroupResourceService groupResourceService;

    private TopicApplyDto topicApplyDto;

    private ValidatorFilterChain validatorFilterChain = new ValidatorFilterChain();

    @Before
    public void setUp() throws Exception {

        List<String> groupNames = new ArrayList<String>();
        groupNames.add("default");
        groupNames.add("pay");
        groupNames.add("search");


        topicApplyDto = new TopicApplyDto();
        topicApplyDto.setAmount(50);
        topicApplyDto.setSize(1);
        topicApplyDto.setTopic("swallow-test");
        topicApplyDto.setApprover("hongjun.zhong");
        topicApplyDto.setType("default");
        topicApplyDto.setApplicant("mingdong.li");


        TypeValidatorFilter typeValidator = new TypeValidatorFilter();
        validatorFilterChain.addFilter(typeValidator);

        typeValidator.setGroupResourceService(groupResourceService);
        Mockito.doReturn(groupNames).when(groupResourceService).findAllGroupName();
    }

    @Test
    public void test() {

        ValidatorFilterResult validatorFilterResult = new ValidatorFilterResult();
		/*-----------------------通过测试--------------------------*/
        validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
        Assert.assertTrue(validatorFilterResult.getStatus() == 0);

        /*-----------------------通过测试--------------------------*/
        topicApplyDto.setType("pay ");
        validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
        Assert.assertTrue(validatorFilterResult.getStatus() == 0);

        /*-----------------------通过测试--------------------------*/
        topicApplyDto.setType(" search");
        validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
        Assert.assertTrue(validatorFilterResult.getStatus() == 0);


		/*-----------------------类型不正确--------------------------*/
        validatorFilterChain.resetFilterChain();
        topicApplyDto.setType("general1");
        validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
        Assert.assertTrue(validatorFilterResult.getStatus() == -20);

        /*-----------------------类型不正确--------------------------*/
        validatorFilterChain.resetFilterChain();
        topicApplyDto.setType("");
        validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);
        Assert.assertTrue(validatorFilterResult.getStatus() == -20);

    }
}