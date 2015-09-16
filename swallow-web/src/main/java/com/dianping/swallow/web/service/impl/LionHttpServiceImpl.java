package com.dianping.swallow.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.LionHttpService;


/**
 * @author mingdongli
 *
 * 2015年9月9日下午6:54:50
 */
@Service("lionHttpService")
public class LionHttpServiceImpl extends AbstractSwallowService implements LionHttpService {

	private static final String CREATE_URL = "http://lionapi.dp:8080/config2/create";

	private static final String SET_URL = "http://lionapi.dp:8080/config2/set";

	private static final String GET_URL = "http://lionapi.dp:8080/config2/get";

	@Autowired
	private HttpService httpSerivice;

	@Override
	public LionHttpResponse setUsingGet(int id, String env, String key, String value) {
		
		//HttpServiceImpl httpSerivice = new HttpServiceImpl();
		LionHttpResponse lionHttpResponse = new LionHttpResponse();
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("?id=").append(id).append("&env=").append(env).append("&key=").append(key).append("&value=").append(value);
		String url = stringBuilder.toString();
		String encodeUrl;
		try {
			encodeUrl = SET_URL + URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			encodeUrl = SET_URL + url;
		}
		HttpResult httpResult = httpSerivice.httpGet(encodeUrl);
		
		if(httpResult != null && httpResult.isSuccess()){
			String response = httpResult.getResponseBody();
			try {
				ObjectMapper mapper = new ObjectMapper();
				lionHttpResponse = mapper.readValue(response, LionHttpResponse.class);
				if(logger.isInfoEnabled()){
					logger.info(String.format("set request %s successfully", url));
				}
			}catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error(String.format("Error when execute set request %s", url));
				}
			}
			
		}
		return lionHttpResponse;
	}

	@Override
	public LionHttpResponse setUsingPost(int id, String env, String key, String value) {
		
		//HttpServiceImpl httpSerivice = new HttpServiceImpl();
		LionHttpResponse lionHttpResponse = new LionHttpResponse();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(id)));
		params.add(new BasicNameValuePair("env", env));
		params.add(new BasicNameValuePair("key", key));
		params.add(new BasicNameValuePair("value", value));
		
		HttpResult httpResult = httpSerivice.httpPost(SET_URL, params);
		
		if(httpResult != null && httpResult.isSuccess()){
			String response = httpResult.getResponseBody();
			try {
				ObjectMapper mapper = new ObjectMapper();
				lionHttpResponse = mapper.readValue(response, LionHttpResponse.class);
				if(logger.isInfoEnabled()){
					logger.info(String.format("set request %s with post method successfully", SET_URL));
				}
			}catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error(String.format("Error when execute set request %s with post method", SET_URL));
				}
			}
			
		}
		return lionHttpResponse;
	}

	@Override
	public LionHttpResponse create(int id, String project, String key, String desc) {

		LionHttpResponse lionHttpResponse = new LionHttpResponse();
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(CREATE_URL).append("?id=").append(id).append("&project=").append(project).append("&key=").append(key).append("&desc=").append(desc);
		String url = stringBuilder.toString();
		HttpResult httpResult = httpSerivice.httpGet(url);
		
		if(httpResult != null && httpResult.isSuccess()){
			String response = httpResult.getResponseBody();
			try {
				ObjectMapper mapper = new ObjectMapper();
				lionHttpResponse = mapper.readValue(response, LionHttpResponse.class);
				if(logger.isInfoEnabled()){
					logger.info(String.format("create request %s successfully", url));
				}
			}catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error(String.format("Error when execute create request %s", url));
				}
			}
			
		}
		return lionHttpResponse;
	}
	
	@Override
	public LionHttpResponse get(int id, String env, String key) {
		
		//HttpServiceImpl httpSerivice = new HttpServiceImpl();
		LionHttpResponse lionHttpResponse = new LionHttpResponse();
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(GET_URL).append("?id=").append(id).append("&env=").append(env).append("&key=").append(key);
		String url = stringBuilder.toString();
		HttpResult httpResult = httpSerivice.httpGet(url);
		
		if(httpResult != null && httpResult.isSuccess()){
			String response = httpResult.getResponseBody();
			try {
				ObjectMapper mapper = new ObjectMapper();
				lionHttpResponse = mapper.readValue(response, LionHttpResponse.class);
				if(logger.isInfoEnabled()){
					logger.info(String.format("get request %s successfully", url));
				}
			}catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error(String.format("Error when execute get request %s", url));
				}
			}
			
		}
		return lionHttpResponse;
	}

	public static void main(String[] args) {
		
		LionHttpServiceImpl lionHttpServiceImpl = new LionHttpServiceImpl();
		//lionHttpServiceImpl.create(2, "lion-test", "lion-test.lion", "lion-test");
		//lionHttpServiceImpl.get(2, "dev", "swallow.topic.whitelist");
		String url = "http://lionapi.dp:8080/config2/create?id=2&project=swallow&key=swallow.topiccfg.lmdyyh&desc=topic config from LionUtilImpl";
		int i = 0;
		for (char r : url.toCharArray()){
			//System.out.println("i " + i + " " + r);
			++i;
		}
		
	}

}
