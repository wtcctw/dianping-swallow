package com.dianping.swallow.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
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

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.service.TopicResourceService;

/**
 * @author mingdongli
 *
 *         2015年8月31日下午6:39:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class TopicControllerTest {

	private static final String AUTHORIZATION = "Authorization";
	private static final String USERNAME = "yapu.wang";

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Before
	public void setUp() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testEditTopic() {

		try {
			this.mockMvc.perform(post("/api/topic/edittopic").param("prop", "qi.yin")
					.param("topic", "example").param("time", "2015-05-12 09:46").param("exec_user", "hongjun.zhong"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpMethod postMethod(String url) throws IOException {
		PostMethod post = new PostMethod(url);
		NameValuePair[] param = { new NameValuePair("prop", "leon.li,mingdong.li"),
				new NameValuePair("time", "2015-06-12 35:35"), new NameValuePair("topic", "example3"),
				new NameValuePair("exec_user", "hongjun.zhong") };
		post.setRequestBody(param);
		post.releaseConnection();
		return post;
	}

	public static void main(String[] args) {

		String host = null;
		try {
			ConfigCache configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
			host = configCache.getProperty("swallow.web.sso.url");
			host = "http://localhost:8080"; // 本机测试使用，真实环境时注释之
		} catch (LionException e1) {
			e1.printStackTrace();
		}
		String url = host + "/api/topic/edittopic";
		HttpClient httpClient = new HttpClient();

		try {
			HttpMethod method = postMethod(url);
			httpClient.executeMethod(method);

			String response = method.getResponseBodyAsString();
			try {
				// JSONObject json = new JSONObject(response);
				// assertEquals(json.getInt("status"), 0);
				System.out.println(response);
				// System.out.println(json.getInt("status"));
				// System.out.println(json.getString("message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
