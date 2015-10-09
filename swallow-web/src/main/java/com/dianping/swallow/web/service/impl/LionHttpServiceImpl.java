package com.dianping.swallow.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.LionHttpService;

/**
 * @author mingdongli
 *
 *         2015年9月9日下午6:54:50
 */
@Service("lionHttpService")
public class LionHttpServiceImpl extends AbstractSwallowService implements LionHttpService {

	private static final String CREATE_URL = "http://lionapi.dp:8080/config2/create";

	private static final String SET_URL = "http://lionapi.dp:8080/config2/set";

	private static final String GET_URL = "http://lionapi.dp:8080/config2/get";

	@Autowired
	private HttpService httpSerivice;

	@Override
	public LionHttpResponse setUsingGet(int id, String env, String key, String value, String group) {

		// HttpServiceImpl httpSerivice = new HttpServiceImpl();
		LionHttpResponse lionHttpResponse = new LionHttpResponse();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("?id=").append(id).append("&env=").append(env).append("&key=").append(key)
				.append("&value=").append(value).append("&group=").append(group);
		String url = stringBuilder.toString();
		String encodeUrl;
		try {
			encodeUrl = SET_URL + URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			encodeUrl = SET_URL + url;
		}
		HttpResult httpResult = httpSerivice.httpGet(encodeUrl);

		if (httpResult != null && httpResult.isSuccess()) {
			String response = httpResult.getResponseBody();
			try {
				JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
				lionHttpResponse = jsonBinder.fromJson(response, LionHttpResponse.class);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("request /set lion api %s successfully", url));
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Error when execute set request %s", url));
				}
			}

		}
		return lionHttpResponse;
	}

	@Override
	public LionHttpResponse setUsingPost(int id, String env, String key, String value, String group) {

		LionHttpResponse lionHttpResponse = new LionHttpResponse();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(id)));
		params.add(new BasicNameValuePair("env", env));
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("value", value));
		params.add(new BasicNameValuePair("group", group));

		HttpResult httpResult = httpSerivice.httpPost(SET_URL, params);

		if (httpResult != null && httpResult.isSuccess()) {
			String response = httpResult.getResponseBody();
			try {
				JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
				lionHttpResponse = jsonBinder.fromJson(response, LionHttpResponse.class);
				if (logger.isInfoEnabled() && params != null) {
					logger.info(String.format("request /set lion api %s with post body %s successfully", SET_URL, params.toString()));
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Error when execute set request %s with post body %s failed", SET_URL, params.toString()));
				}
			}

		}
		return lionHttpResponse;
	}

	@Override
	public LionHttpResponse create(int id, String project, String key, String desc) {

		LionHttpResponse lionHttpResponse = new LionHttpResponse();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CREATE_URL).append("?id=").append(id).append("&project=").append(project).append("&key=")
				.append(key).append("&desc=").append(desc);
		String url = stringBuilder.toString();
		HttpResult httpResult = httpSerivice.httpGet(url);

		if (httpResult != null && httpResult.isSuccess()) {
			String response = httpResult.getResponseBody();
			try {
				JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
				lionHttpResponse = jsonBinder.fromJson(response, LionHttpResponse.class);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("request /create lion api %s successfully", url));
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Error when execute create request %s", url));
				}
			}

		}
		return lionHttpResponse;
	}

	@Override
	public LionHttpResponse get(int id, String env, String key, String group) {

		LionHttpResponse lionHttpResponse = new LionHttpResponse();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(GET_URL).append("?id=").append(id).append("&env=").append(env).append("&key=").append(key)
				.append("&group=").append(group);
		String url = stringBuilder.toString();
		HttpResult httpResult = httpSerivice.httpGet(url);

		if (httpResult != null && httpResult.isSuccess()) {
			String response = httpResult.getResponseBody();
			try {
				JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
				lionHttpResponse = jsonBinder.fromJson(response, LionHttpResponse.class);
				if (logger.isInfoEnabled() && lionHttpResponse != null) {
					logger.info(String.format("request /get lion api %s with response %s successfully", url, lionHttpResponse.getResult()));
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(String.format("Error when execute get request %s", url));
				}
			}

		}
		return lionHttpResponse;
	}

}
