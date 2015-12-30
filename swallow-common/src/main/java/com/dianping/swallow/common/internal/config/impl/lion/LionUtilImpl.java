package com.dianping.swallow.common.internal.config.impl.lion;

import com.dianping.swallow.common.internal.codec.Codec;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.PropertiesUtils;
import com.dianping.swallow.common.internal.util.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.dianping.swallow.common.internal.util.http.ContentTypeDesc;
import com.dianping.swallow.common.internal.util.http.HttpManager;
import com.dianping.swallow.common.internal.util.http.HttpMethod;
import com.dianping.swallow.common.internal.util.http.SimpleHttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author mengwenchao
 *
 * 2015年6月10日 下午4:25:16
 */
public class LionUtilImpl implements LionUtil{

	protected final Logger logger     = LogManager.getLogger(getClass());

	private Long userId = PropertiesUtils.getLongProperty("lion.id", 76L);
	
	private final String BASIC_LION_CONFIG_URL = "http://lionapi.dp:8080/config2";
		
	private static final String PROJECT = "swallow";
	
	private HttpManager httpManager = new HttpManager(5000, 5000, CommonUtils.getCpuCount());
	
	private Charset charset = Codec.DEFAULT_CHARSET;
	
	private int retryCount = 3;
	
	public LionUtilImpl(){
		
	}
	
	public LionUtilImpl(Long userId) {
		
		this.userId = userId;
	}
	
	public boolean createConfig(String key, String env) {
		
		key = key.trim();
		String args = getBasicArgs("topic config from LionUtilImpl", true, env);
		String url = BASIC_LION_CONFIG_URL + "/create?" + args;
		url += "&" + keyValue("key", getRealKey(key));
		
		LionRetResultString ret = executeRequest(url, LionRetResultString.class);
		return ret.isSuccess() || ret.getMessage().contains("exists");
	
	}
	
	@Override
	public Map<String, String> getCfgs(String prefix) {

		String args = getBasicArgs("get topic with prefix " + prefix, false, null);
		String url = BASIC_LION_CONFIG_URL + "/get?" + args;
		url += "&" + keyValue("prefix", getRealKey(prefix));

		LionRetResultMap ret = executeRequest(url, LionRetResultMap.class);
		return ret.getResult();
	}

	@Override
	public String getValue(String key) {

		String args = getBasicArgs("get topic with key " + key, false, null);
		String url = BASIC_LION_CONFIG_URL + "/get?" + args;
		url += "&" + keyValue("key", getRealKey(key));

		LionRetResultString ret = executeRequest(url, LionRetResultString.class);
		return ret.getResult();
	}

	private String getBasicArgs(String desc, boolean withProject, String env) {

		String result = StringUtils.join("&", keyValue("id", String.valueOf(userId)),
				keyValue("desc", desc),
				keyValue("env", org.apache.commons.lang.StringUtils.isBlank(env) ? EnvUtil.getEnv() : env.trim()));

		
		if(withProject){
			
			result = StringUtils.join("&", result, keyValue("project", PROJECT));
		}
		
		String group = EnvUtil.getGroup();
		if(group != null){
			result = StringUtils.join("&", result, keyValue("group", group));
		}

		return result;
	}

	private <T extends LionRet> T executeRequest(String urlAddress, Class<T> clazz) {
		return executeRequest(urlAddress, clazz, HttpMethod.GET);
	}
	
	private <T extends LionRet> T executeRequest(String urlAddress, Class<T> clazz, HttpMethod httpMethod) {
		
		if(logger.isDebugEnabled()){
			logger.debug("[executeGet]" + urlAddress);
		}
		
		IOException exception = null;
		
		for(int i=0; i <= retryCount; i++){
			
			HttpUriRequest request = null;
			try {
				switch(httpMethod){
					case GET:
						request = new HttpGet(urlAddress);
						break;
					case POST:
						String[] urlParam = urlAddress.split("\\?");
						if (urlParam.length != 2) {
							throw new IllegalArgumentException("illegal urlAddress :" + urlAddress);
						}
						HttpPost post = new HttpPost(urlParam[0]);
						StringEntity entity = new StringEntity(urlParam[1], ContentType.create(ContentTypeDesc.FORM_URLENCODED, charset));
						post.setEntity(entity);
						
						request  = post;
						break;
					default:
						throw new IllegalArgumentException("illegal httpMethod : " + httpMethod);
				}
				
				SimpleHttpResponse<String> response = httpManager.executeReturnString(request);
				return JsonBinder.getNonEmptyBinder().fromJson(response.getContent(), clazz);
			} catch (IOException e) {
				logger.error("[executeRequest]" + urlAddress, e);
				exception = e;
			}
		}
		
		throw new IllegalStateException("io exception", exception);
	}

	public static String getRealKey(String key) {
		
		key = key.trim();
		if(key.startsWith(PROJECT)){
			return key;
		}
		return StringUtils.join(".", PROJECT, key);
	}

	private String getBasicArgs(String desc, String env) {
		return getBasicArgs(desc, true, env);
	}

	private String keyValue(String key, String value){
		
		if(key.equals("key")){
			value = getRealKey(value);
		}
		return key + "=" + encode(value);
	}
	private String encode(String value) {
		try {
			return URLEncoder.encode(value, charset.displayName());
		} catch (UnsupportedEncodingException e) {
		}
		return value;
	}

	public void setValue(String key, String value, HttpMethod httpMethod, String env) {
		
		String args = getBasicArgs("topic配置信息", env);
		String url = BASIC_LION_CONFIG_URL + "/set?" + args;
		url += "&" + keyValue("key", key)
				+ "&" + keyValue("value", value);
		
		LionRetResultString ret = executeRequest(url, LionRetResultString.class, httpMethod);
		if(ret ==null || !ret.isSuccess()){
			throw new IllegalStateException("[setValue][set value failed][" + key + ":" + value + "]" + ret);
		}
	}

	@Override
	public void createOrSetConfig(String key, String value) {

		createOrSetConfig(key, value, HttpMethod.GET, null);
	}

	@Override
	public void createOrSetConfig(String key, String value, HttpMethod httpMethod) {
		createOrSetConfig(key, value, httpMethod, null);
	}

	
	@Override
	public void createOrSetConfig(String key, String value, HttpMethod httpMethod, String env) {
		
		if(StringUtils.isEmpty(key)){
			throw new IllegalArgumentException("key null:" + key);
		}
		if(value == null){
			throw new IllegalArgumentException("value null:" + key);
		}
		
		key = key.trim();
		value = value.trim();
		
		createConfig(key, env);
		setValue(key, value, httpMethod, env);
		
	}

	public static class LionRet{
		
		private String status;
		private String message;
		
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
		public boolean isSuccess(){
			return status != null && status.equals("success");
		}
		
		
		@Override
		public String toString() {
			
			return "status:" + status + ",message:" + message;
		}
	}

	public static class LionRetResultString extends LionRet{

		private String result;

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
	}

	public static class LionRetResultMap extends LionRet{
		
		private Map<String, String> result;

		
		public Map<String, String> getResult() {
			return result;
		}
		
		public void setResult(Map<String, String> result) {
			this.result = result;
		}
	}

}