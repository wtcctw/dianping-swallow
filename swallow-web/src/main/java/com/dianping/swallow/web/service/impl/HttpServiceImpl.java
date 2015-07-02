package com.dianping.swallow.web.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mortbay.jetty.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.service.HttpService;


/**
 * 
 * @author qiyin
 *
 */
@Service("httpService")
public class HttpServiceImpl implements HttpService {

	private static final Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);
	
	private static final String UTF_8 = "UTF-8";

	private static final HttpClient httpClient = new DefaultHttpClient();

	@Override
	public boolean httpPost(String url, List<NameValuePair> params) {
		HttpPost httpPost = new HttpPost(url);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, UTF_8));
		} catch (UnsupportedEncodingException e) {
			logger.error("http post param encoded failed", e);
		}
		try {
			HttpResponse result = httpClient.execute(httpPost);
			if (result.getStatusLine().getStatusCode() == HttpStatus.ORDINAL_200_OK) {
				return true;
			}
		} catch (IOException e) {
			logger.error("http post request failed .", e);
		}
		return false;
	}

	@Override
	public boolean httpGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse result = httpClient.execute(httpGet);
			if (result.getStatusLine().getStatusCode() == HttpStatus.ORDINAL_200_OK) {
				return true;
			}
		} catch (IOException e) {
			logger.error("http get request failed .", e);
		}
		return false;
	}

}
