package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * 
 * @author qiyin
 *
 *         2015年9月23日 下午11:52:59
 */
public class HttpServiceTest {

	HttpService httpService = null;

	@Before
	public void before() {
		httpService = new HttpServiceImpl();
	}

	@Test
	public void httpGetTest() {
		for (int i = 0; i < 1; i++) {
			HttpResult result = httpService.httpGet("http://www.baidu.com");
			Assert.assertTrue(result.isSuccess());
		}
	}

	@Test
	public void httpPostTest() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("wd", "mobile"));
		for (int i = 0; i < 1; i++) {
			HttpResult result = httpService.httpPost("http://www.baidu.com", params);
			Assert.assertFalse(result.isSuccess());
		}
	}

}
