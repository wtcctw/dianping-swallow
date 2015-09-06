package com.dianping.swallow.common.internal.codec.impl;

import java.util.Date;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.example.message.DemoBean;

public class JsonCoderTest {

   @Test
   public void testDecode() throws Exception {
      //构造序列化后的json
      DemoBean demoBean = new DemoBean();
      demoBean.setA(1);
      demoBean.setB("b");
      SwallowMessage message = new SwallowMessage();
      message.setContent(demoBean);
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("property-key", "property-value");
      message.setProperties(map);
      JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
      String json = jsonBinder.toJson(message);
      //使用HessianDecoder解码
      JsonCodec jsonDecoder = new JsonCodec(SwallowMessage.class, SwallowMessage.class);
      Message actualMessage = (Message) jsonDecoder.decode(json);
      //assert
      Assert.assertEquals(message, actualMessage);
   }

   @Test
   public void testDecode2() throws Exception {
	   
      Object o = new Object();
      JsonCodec jsonDecoder = new JsonCodec(SwallowMessage.class, SwallowMessage.class);
      Assert.assertEquals(o, jsonDecoder.decode(o));
      
   }

   @Test
   public void testEncode1() throws Exception {
      //构造序列化后的json
      SwallowMessage msg = new SwallowMessage();
      msg.setGeneratedTime(new Date());
      msg.setMessageId(123L);
      HashMap<String, String> map = new HashMap<String, String>();
      map.put("property-key", "property-value");
      msg.setContent("content");
      JsonCodec jsonEncoder = new JsonCodec(SwallowMessage.class, SwallowMessage.class);
      String result = (String) jsonEncoder.encode(msg);
      //解码
      JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
      SwallowMessage actualMsg = jsonBinder.fromJson(result, SwallowMessage.class);

      //assert
      Assert.assertEquals(msg, actualMsg);
   }

   @Test
   public void testEncode2() throws Exception {
      Object o = new Object();
      JsonCodec jsonEncoder = new JsonCodec(SwallowMessage.class, SwallowMessage.class);
      Assert.assertEquals(o, jsonEncoder.encode(o));
   }
   
   
}
