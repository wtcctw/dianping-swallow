package com.dianping.swallow.web.controller;

import java.io.IOException;
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

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import com.dianping.swallow.web.service.SaveMessageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class SaveMessageControllerTest {

	private static final String AUTHORIZATION = "Authorization";
	private static final String RANDOMSTRING = "dzpzpndcnwkhgvzfallnelxtaikmxmbb";

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Resource(name = "saveMessageService")
	private SaveMessageService saveMessageService;

	@Before
	public void setUp() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testRetransmitMessage() {

		try {
			this.mockMvc.perform(post("/hotels").param("param",
					"test sendGroupMessage").param("topic", "example"));
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
		saveMessageService.saveNewMessage(topicName, content);
	}

	private static HttpMethod postMethod(String url) throws IOException {
		PostMethod post = new PostMethod(url);
		post.setRequestHeader(AUTHORIZATION, RANDOMSTRING);
		NameValuePair[] param = {
				new NameValuePair("param", "6158666846842126337"),
				new NameValuePair("topic", "example"), };
		post.setRequestBody(param);
		post.releaseConnection();
		return post;
	}
	
	private static HttpMethod postMethod2(String url) throws IOException {
		PostMethod post = new PostMethod(url);
		post.setRequestHeader(AUTHORIZATION, RANDOMSTRING);
		NameValuePair[] param = {
				new NameValuePair("textarea", "test group message api"),
				new NameValuePair("topic", "example"), };
		post.setRequestBody(param);
		post.releaseConnection();
		return post;
	}

	public static void main(String[] args) {
		String url = "http://localhost:8080/console/message/auth/sendmessage";
		HttpClient httpClient = new HttpClient();

		try {
			HttpMethod method = postMethod(url);
			httpClient.executeMethod(method);

			String response = method.getResponseBodyAsString();
			// String(method.getResponseBodyAsString().getBytes("ISO-8859-1"));
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String url2 = "http://localhost:8080/console/message/auth/sendgroupmessage";

		try {
			HttpMethod method = postMethod2(url2);
			httpClient.executeMethod(method);

			String response = method.getResponseBodyAsString();
			// String(method.getResponseBodyAsString().getBytes("ISO-8859-1"));
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
