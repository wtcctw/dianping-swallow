package com.dianping.swallow.web.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.mortbay.jetty.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.alarm.ResultType;
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

	@Override
	public HttpResult httpPost(String url, List<NameValuePair> params) {
		HttpPost httpPost = new HttpPost(url);
		HttpResult result = new HttpResult();
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, UTF_8));
		} catch (UnsupportedEncodingException e) {
			logger.error("http post param encoded failed. ", e);
		}
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.ORDINAL_200_OK) {
				result.setResponseBody(EntityUtils.toString(response.getEntity()));
				result.setSuccess(true);
			}
		} catch (UnknownHostException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_HOST_UNKNOWN);
			logger.error("http post request failed. url = {}", url, e);
		} catch (ConnectTimeoutException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_CONNECTION_TIMEOUT);
			logger.error("http post request failed. url = {}", url, e);
		} catch (ConnectException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_CONNECT);
			logger.error("http post request failed. url = {}", url, e);
		} catch (SocketTimeoutException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_SOCKET_TIMEOUT);
			logger.error("http post request failed. url = {}", url, e);
		} catch (IOException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED);
			logger.error("http post request failed. url = {}", url, e);
		}
		return result;
	}

	@Override
	public HttpResult httpGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		HttpResult result = new HttpResult();
		try {
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.ORDINAL_200_OK) {
				result.setResponseBody(EntityUtils.toString(response.getEntity()));
				result.setSuccess(true);
			}
		} catch (UnknownHostException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_HOST_UNKNOWN);
			logger.error("http get request failed. url = {}", url, e);
		} catch (ConnectTimeoutException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_CONNECTION_TIMEOUT);
			logger.error("http get request failed. url = {}", url, e);
		} catch (ConnectException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_CONNECT);
			logger.error("http get request failed. url = {}", url, e);
		} catch (SocketTimeoutException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED_SOCKET_TIMEOUT);
			logger.error("http get request failed. url = {}", url, e);
		} catch (IOException e) {
			result.setSuccess(false);
			result.setResultType(ResultType.FAILED);
			logger.error("http get request failed. url = {}", url, e);
		}
		return result;
	}
}
