package com.dianping.swallow.consumer.impl;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author mengwenchao
 *
 * 2015年7月7日 下午5:33:21
 */
public class ConsumerFactoryImplTest {

    private ConsumerFactoryImpl consumerFactoryImpl = (ConsumerFactoryImpl) ConsumerFactoryImpl.getInstance();
    
    @Test
    public void testcompatibility(){

        String lionValue= "default=127.0.0.1:1,127.0.0.1:11;topic1=127.0.0.2:2,127.0.0.2:22;;topic2,topic3=127.0.0.1:3,127.0.0.2:33;;";
        Map<String, List<InetSocketAddress>>  map = consumerFactoryImpl.lionValue2Map(lionValue);
        checkConfig(map);
        

    	
    }
    

    @Test
    public void testlionValue2Map(){

        String lionValue= "default=127.0.0.1:1,127.0.0.1:11;topic1=127.0.0.2:2,127.0.0.2:22;topic2,topic3=127.0.0.1:3,127.0.0.2:33;";
        Map<String, List<InetSocketAddress>>  map = consumerFactoryImpl.lionValue2Map(lionValue);
        checkConfig(map);

        lionValue= "default=127.0.0.1:1,127.0.0.1:11 ; \n"
                +  "topic1=127.0.0.2:2 , 127.0.0.2:22 ; \n\r "
                +  "topic2 , topic3=127.0.0.1 : 3,127.0.0.2:33;";
        map = consumerFactoryImpl.lionValue2Map(lionValue);
        checkConfig(map);

    }

    private void checkConfig(Map<String, List<InetSocketAddress>> map) {

        String []topics = new String[]{"default", "topic1", "topic2", "topic3"};
        int []resultPort =  new int[]{1, 2 , 3, 3};
        int index = 0;

        for(String topic : topics){

            List<InetSocketAddress> addresses = map.get(topic);
            Assert.assertEquals(resultPort[index], addresses.get(0).getPort());
            index++;
        }
    }
}
