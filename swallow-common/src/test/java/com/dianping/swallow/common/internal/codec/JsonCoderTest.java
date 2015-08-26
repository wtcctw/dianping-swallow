package com.dianping.swallow.common.internal.codec;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.codec.JsonBinder;
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
      JsonCoder jsonDecoder = new JsonCoder(SwallowMessage.class, SwallowMessage.class);
      Message actualMessage = (Message) jsonDecoder.decode(null, ChannelBuffers.wrappedBuffer(json.getBytes("UTF-8")));
      //assert
      Assert.assertEquals(message, actualMessage);
   }

   @Test
   public void testDecode2() throws Exception {
	   
      Object o = new Object();
      JsonCoder jsonDecoder = new JsonCoder(SwallowMessage.class, SwallowMessage.class);
      Assert.assertEquals(o, jsonDecoder.decode(null, o));
      
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
      JsonCoder jsonEncoder = new JsonCoder(SwallowMessage.class, SwallowMessage.class);
      ChannelBuffer channelBuffer = (ChannelBuffer) jsonEncoder.encode(null, msg);
      //解码
      JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
      SwallowMessage actualMsg = jsonBinder.fromJson(channelBuffer.toString(Charset.forName("UTF-8")),
            SwallowMessage.class);

      //assert
      Assert.assertEquals(msg, actualMsg);
   }

   @Test
   public void testEncode2() throws Exception {
      Object o = new Object();
      JsonCoder jsonEncoder = new JsonCoder(SwallowMessage.class, SwallowMessage.class);
      Assert.assertEquals(o, jsonEncoder.encode(null, o));
   }
   
   
}
