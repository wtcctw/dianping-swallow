package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

public class HttpServiceTest {

	public static void main(String[] args) {
		HttpService httpService = new HttpServiceImpl();
		for (int i = 0; i < 10; i++) {
			HttpResult result= httpService.httpGet("http://www.baidu.com");
			System.out.println(result);
			
			HttpResult result1= httpService.httpGet("http://www.sohu.com");
			System.out.println(result1);
		}

	}

}
