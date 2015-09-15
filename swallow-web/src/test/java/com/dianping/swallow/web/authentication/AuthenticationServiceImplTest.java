package com.dianping.swallow.web.authentication;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.codehaus.jettison.json.JSONArray;
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

/**
 * @author mingdongli
 *
 *         2015年9月15日下午3:12:04
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class AuthenticationServiceImplTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void test() {

		MvcResult result;
		try {
//			result = this.mockMvc
//					.perform(
//							post("/api/topic/edittopic").param("prop", "dp.wang")
//									.param("topic", "example").param("time", "2015-05-12 09:46").param("exec_user", "hongjun.zhong"))
//					.andDo(MockMvcResultHandlers.print()).andReturn();
//			
//			JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
//			assertEquals(jsonObject.getInt("status"), 0);
			
			String json = "{\"offset\":0,\"limit\":31,\"topic\":\"example\",\"messageId\":\"\",\"basemid\":\"\",\"sort\":false}";
			result = this.mockMvc
					.perform(
							post("/console/message/auth/list", "json").characterEncoding("UTF-8")
							.contentType(MediaType.APPLICATION_JSON).content(json.getBytes()))
							.andDo(MockMvcResultHandlers.print()).andReturn();
			
			String resultString = result.getResponse().getContentAsString();
			JSONObject jsonObject = new JSONObject(resultString);
			JSONArray array = jsonObject.getJSONArray("message");
			assertEquals(array.length(), 31);

			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
