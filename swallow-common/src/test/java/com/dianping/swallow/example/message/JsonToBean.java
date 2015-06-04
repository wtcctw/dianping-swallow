package com.dianping.swallow.example.message;

import java.util.HashMap;
import java.util.Map;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.internal.message.SwallowMessage;

public class JsonToBean {

   @SuppressWarnings("unchecked")
public static void main(String[] args) throws Exception {

      //自定义bean
      JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();

      String json = "{\"generatedTime\":null,\"messageId\":null,\"properties\":{\"adasd\":\"dasd\"},\"version\":null,\"sha1\":null,\"content\":\"{\\\"a\\\":1,\\\"b\\\":\\\"b\\\"}\"}";
      SwallowMessage msg = jsonBinder.fromJson(json, SwallowMessage.class);
      System.out.println(msg);
      System.out.println(msg.transferContentToBean(DemoBean.class));
      
     json = "{\"1\" : {\"a\" : 1, \"b\" : \"nihao\"}}";
     
     Map<String, DemoBean> map = new HashMap<String, DemoBean>();
     map = jsonBinder.fromJson(json, map.getClass());
     System.out.println(map);

   }

}
