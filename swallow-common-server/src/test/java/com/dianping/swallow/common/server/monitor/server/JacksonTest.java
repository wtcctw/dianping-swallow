package com.dianping.swallow.common.server.monitor.server;

import java.io.IOException;
import java.net.UnknownHostException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoStatus;
import com.mongodb.MongoClient;

/**
 * @author mengwenchao
 *
 * 2015年7月1日 下午5:03:09
 */
public class JacksonTest {

	@Test
	public void testJackson() throws JsonGenerationException, JsonMappingException, IOException{
		
		try{
			ObjectMapper mapper = new ObjectMapper();
			// 设置输出时包含属性的风格
//			mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
			
			MongoStatus status = createMongoStatus();
			
			String result = mapper.writeValueAsString(status);
			System.out.println(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private MongoStatus createMongoStatus() throws UnknownHostException {
		
		return new MongoStatus(new MongoClient("192.168.213.143", 27018));
	}
}
