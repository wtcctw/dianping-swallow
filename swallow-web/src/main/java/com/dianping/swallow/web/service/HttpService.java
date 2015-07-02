package com.dianping.swallow.web.service;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * 
 * http 访问接口
 * @author qiyin
 *
 */
public interface HttpService {

	/**
	 * http post请求
	 * @param url
	 * @param params请求参数
	 * @return
	 */
	public boolean httpPost(String url, List<NameValuePair> params);
	/**
	 * http get请求
	 * @param url
	 * @return
	 */
	public boolean httpGet(String url);
	
}
