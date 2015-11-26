package com.dianping.swallow.web.controller;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author mingdongli
 *
 *         2015年9月9日下午8:04:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class TopicApplyControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private boolean autotest;

	private String json = "{\"topic\":\"lmdyyh_swallow_test\",\"approver\":\"hongjun.zhong\",\"size\":1,\"amount\":50,\"applicant\":\"yapu.wang\",\"test\":true,\"type\":\"一般消息队列\"}";

	@Before
	public void setUp() throws Exception {

		this.autotest = true;
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void test() {
		try {

			if (autotest) {

				/*------------case 1 新建topic：lmdyyh_swallow_test-----------------*/
				MvcResult result = this.mockMvc
						.perform(
								post("/api/topic/apply", "json").characterEncoding("UTF-8")
										.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
				assertEquals(jsonObject.getInt("status"), 0);

				Thread.sleep(1000);

				/*------------case 2 批复人无权限-----------------*/
				json = "{\"topic\":\"lmdyyh_swallow_test1\",\"approver\":\"dp.wang\",\"size\":1,\"amount\":50,\"applicant\":\"yapu.wang\",\"test\":false}";

				result = this.mockMvc
						.perform(
								post("/api/topic/apply", "json").characterEncoding("UTF-8")
										.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				jsonObject = new JSONObject(result.getResponse().getContentAsString());
				assertEquals(jsonObject.getInt("status"), -2);

				Thread.sleep(1000);

				/*------------case 3 配额大于700-----------------*/
				json = "{\"topic\":\"lmdyyh_swallow_test1\",\"approver\":\"hongjun.zhong\",\"size\":10,\"amount\":100,\"applicant\":\"yapu.wang\",\"test\":false}";

				result = this.mockMvc
						.perform(
								post("/api/topic/apply", "json").characterEncoding("UTF-8")
										.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				jsonObject = new JSONObject(result.getResponse().getContentAsString());
				assertEquals(jsonObject.getInt("status"), -12);

				Thread.sleep(1000);

				/*------------case 4 名称不合法-----------------*/
				json = "{\"topic\":\"lmdyyh.swallow.test\",\"approver\":\"hongjun.zhong\",\"size\":1,\"amount\":50,\"applicant\":\"yapu.wang\",\"test\":false}";

				result = this.mockMvc
						.perform(
								post("/api/topic/apply", "json").characterEncoding("UTF-8")
										.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				jsonObject = new JSONObject(result.getResponse().getContentAsString());
				assertEquals(jsonObject.getInt("status"), -11);

				Thread.sleep(1000);

				/*------------case 5 新建重复topic：lmdyyh_swallow_test-----------------*/
				json = "{\"topic\":\"lmdyyh_swallow_test\",\"approver\":\"hongjun.zhong\",\"size\":1,\"amount\":50,\"applicant\":\"yapu.wang\",\"test\":false}";

				result = this.mockMvc
						.perform(
								post("/api/topic/apply", "json").characterEncoding("UTF-8")
										.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				jsonObject = new JSONObject(result.getResponse().getContentAsString());
				assertEquals(jsonObject.getInt("status"), -11);
			} else {
				/*-------------测试修改lion配置失败----------------*/
				/*-------------case 1 修改whitelist成功后，修改consumerServer失败----------------*/
				/*-------------写成功后断网----------------*/

				/*-------------case 2 修改consumerServer成功后，创建topiccfg失败----------------*/
				/*-------------写成功后断网----------------*/

				/*-------------case 3 创建topiccfg成功后，修改tpiccfg失败----------------*/
				/*-------------写成功后断网----------------*/
				MvcResult result = this.mockMvc
						.perform(
								post("/api/topic/apply", "json").characterEncoding("UTF-8")
										.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
				assertEquals(jsonObject.getInt("status"), -17);

				result = this.mockMvc
						.perform(
								post("/api/topic/apply", "json").characterEncoding("UTF-8")
										.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
						.andDo(MockMvcResultHandlers.print()).andReturn();
				jsonObject = new JSONObject(result.getResponse().getContentAsString());
				assertEquals(jsonObject.getInt("status"), 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpMethod postMethod(String url) throws IOException {
		PostMethod post = new PostMethod(url);
		NameValuePair[] param = { new NameValuePair("topic", "lmdyyh_swallow_test"),
				new NameValuePair("approver", "hongjun.zhong"), new NameValuePair("applicant", "yapu.wang"),
				new NameValuePair("size", "1"), new NameValuePair("test", "true"), new NameValuePair("amount", "50"),
				new NameValuePair("group", "test") };
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
		String url = host + "/api/topic/apply";
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
