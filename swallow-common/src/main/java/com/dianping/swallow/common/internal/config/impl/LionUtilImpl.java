package com.dianping.swallow.common.internal.config.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.PropertiesUtils;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *
 * 2015年6月10日 下午4:25:16
 */
public class LionUtilImpl implements LionUtil{

	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	private Long userId = PropertiesUtils.getLongProperty("lion.id", 76L);
	
	private final String BASIC_LION_CONFIG_URL = "http://lionapi.dp:8080/config2";
		
	private final String PROJECT = "swallow";
	
	public LionUtilImpl(){
		
	}
	
	public LionUtilImpl(Long userId) {
		
		this.userId = userId;
	}
	
	public boolean createConfig(String key) {
		
		key = key.trim();
		String args = getBasicArgs("topic config from LionUtilImpl");
		String url = BASIC_LION_CONFIG_URL + "/create?" + args;
		url += "&" + keyValue("key", getRealKey(key));
		
		LionRet ret = executeGet(url);
		return ret.isSuccess() || ret.getMessage().contains("exists");
	
	}

	@Override
	public Map<String, String> getCfgs(String prefix) {
		
		String args = getBasicArgs("get topic with prefix " + prefix, false);
		String url = BASIC_LION_CONFIG_URL + "/get?" + args;
		url += "&" + keyValue("prefix", getRealKey(prefix));
		
		LionRet ret = executeGet(url);
		return ret.getResult();
	}

	private String getBasicArgs(String desc, boolean withProject) {

		String result = StringUtils.join("&", keyValue("id", String.valueOf(userId)),
				keyValue("desc", desc),
				keyValue("env", EnvUtil.getEnv()));

		if(withProject){
			
			result += StringUtils.join("&", keyValue("project", PROJECT));
		}

		return result;
	}

	private LionRet executeGet(String urlAddress) {
		
		if(logger.isInfoEnabled()){
			logger.info("[executeGet]" + urlAddress);
		}
		
		URL url;
		StringBuffer result = new StringBuffer();
		HttpURLConnection connection = null;
		try {
			url = new URL(urlAddress);
			connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line = null;
			while((line = reader.readLine()) != null){
				result.append(line);
			}
		} catch (IOException e) {
			logger.error("[executeGet]", e);
			throw new IllegalStateException("io exception", e);
		}finally{
			if(connection != null){
				connection.disconnect();
			}
		}
		
		return JsonBinder.getNonEmptyBinder().fromJson(result.toString(), LionRet.class);
		
	}

	private String getRealKey(String key) {
		if(key.startsWith(PROJECT)){
			return key;
		}
		return StringUtils.join(".", PROJECT, key);
	}

	private String getBasicArgs(String desc) {
		return getBasicArgs(desc, true);
	}

	private String keyValue(String key, String value){
		
		if(key.equals("key")){
			value = getRealKey(value);
		}
		return key + "=" + encode(value);
	}
	private String encode(String value) {
		try {
			return URLEncoder.encode(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return value;
	}

	public void setValue(String key, String value) {
		
		String args = getBasicArgs("topic配置信息");
		String url = BASIC_LION_CONFIG_URL + "/set?" + args;
		url += "&" + keyValue("key", key)
				+ "&" + keyValue("value", value);
		
		LionRet ret = executeGet(url);
		if(ret ==null || !ret.isSuccess()){
			throw new IllegalStateException("[setValue][set value failed][" + key + ":" + value + "]" + ret);
		}

	}

	@Override
	public void createOrSetConfig(String key, String value) {
		
		key = key.trim();
		value = value.trim();
		
		createConfig(key);
		setValue(key, value);
	}

	public static class LionRet{
		
		private String status;
		private String message;
		private Map<String, String> result;
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public Map<String, String> getResult() {
			return result;
		}
		
		public boolean isSuccess(){
			return status != null && status.equals("success");
		}
		
		public void setResult(Map<String, String> result) {
			this.result = result;
		}
		
		@Override
		public String toString() {
			
			return "status:" + status + ",message:" + message;
		}
	}

}
