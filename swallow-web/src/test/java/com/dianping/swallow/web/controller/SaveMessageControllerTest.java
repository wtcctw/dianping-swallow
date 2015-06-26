package com.dianping.swallow.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.service.MessageRetransmitService;


/**
 * @author mingdongli
 *
 * 2015年6月12日下午2:28:49
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SaveMessageControllerTest {

	private static final String RANDOMSTRING = "lfimuqqxjlgvniueuiqooorkkyxdmwrm";

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Resource(name = "saveMessageService")
	private MessageRetransmitService saveMessageService;

	@Before
	public void setUp() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testRetransmitMessage() {

		try {
			this.mockMvc.perform(post("/console/message/sendgroupmessage")
					.param("param", "test sendGroupMessage").param("topic",
							"example"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRetransmit() {
		String topicName = "example";
		long mid = 6156810622106337423L;
		boolean res = saveMessageService.doRetransmit(topicName, mid);
		assertEquals(res, Boolean.TRUE);
	}

	@Test
	public void testSendGroupMessages() {
		String topicName = "example";
		String content = "test sendGroupMessages API";
		String type = "";
		String property = "";
		String delimitor = ":";
		saveMessageService.saveNewMessage(topicName, content, type, delimitor, property);
	}

	private static HttpMethod postMethod(String url) throws IOException {
		PostMethod post = new PostMethod(url);
		NameValuePair[] param = {
				new NameValuePair("mid", "6161611639629021185,6161611631039086593"),
				new NameValuePair("authentication", RANDOMSTRING),
				new NameValuePair("topic", "example") };
		post.setRequestBody(param);
		post.releaseConnection();
		return post;
	}

	private static HttpMethod postMethod2(String url) throws IOException {
		
		PostMethod post = new PostMethod(url);
		String textarea[]={"test group message api with type and property, No 1", "test group message api with type and property, No 2"};
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new NameValuePair("content",textarea[0]));
		nameValuePairs.add(new NameValuePair("topic", "example"));
		nameValuePairs.add(new NameValuePair("type", "jiagou"));
		nameValuePairs.add(new NameValuePair("authentication", RANDOMSTRING));
		nameValuePairs.add(new NameValuePair("property", "test:true::work:on"));
		
		NameValuePair[] array = new NameValuePair[nameValuePairs.size()];
		nameValuePairs.toArray(array); // fill the array
		post.setRequestBody(array);
		post.releaseConnection();
		return post;
	}

	public static void main(String[] args) {
		String host = null;
		try {
			ConfigCache configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
			host = configCache.getProperty("swallow.web.sso.url");
			host = "http://localhost:8080";  //本机测试使用，真实环境时注释之
		} catch (LionException e1) {
			e1.printStackTrace();
		}
		String url = host + "/api/message/sendmessageid";
		HttpClient httpClient = new HttpClient();

		try {
			HttpMethod method = postMethod(url);
			httpClient.executeMethod(method);

			String response = method.getResponseBodyAsString();
			try {
				JSONObject json = new JSONObject(response);
				System.out.println(response);
				System.out.println(json.getInt("status"));
				System.out.println(json.getString("message"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String url2 = host + "/api/message/sendmessage";

		try {
			HttpMethod method = postMethod2(url2);
			httpClient.executeMethod(method);

			String response = method.getResponseBodyAsString();
			try {
				JSONObject json = new JSONObject(response);
				
				System.out.println(response);
				System.out.println(json.getInt("status"));
				System.out.println(json.getString("message"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// String(method.getResponseBodyAsString().getBytes("ISO-8859-1"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
