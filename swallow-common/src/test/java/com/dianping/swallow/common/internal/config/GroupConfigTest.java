package com.dianping.swallow.common.internal.config;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.message.JsonDeserializedException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author qi.yin
 *         2016/01/26  上午10:26.
 */
public class GroupConfigTest {

    @Test
    public void testGroupCfg() {
        String[] producerIps = new String[]{"192.168.8.171", "192.168.8.172"};
        String[] consumerIps = new String[]{"192.168.8.171"};
        String strGroupCfg = "{\"producerIps\": [\"192.168.8.171\", \"192.168.8.172\"],\"consumerIps\":[\"192.168.8.171\"]}";

        GroupConfig groupCfg = JsonBinder.getNonEmptyBinder().fromJson(strGroupCfg, GroupConfig.class);
        System.out.println(groupCfg);
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(producerIps[i], groupCfg.getProducerIps()[i]);
        }
        Assert.assertEquals(consumerIps[0], groupCfg.getConsumerIps()[0]);
    }

    @Test(expected = JsonDeserializedException.class)
    public void testBadJson() {
        String strGroupCfg = "{\"producerIps\": ,\"consumerIps\":[\"192.168.8.171\"]}";

        GroupConfig groupCfg = JsonBinder.getNonEmptyBinder().fromJson(strGroupCfg, GroupConfig.class);

    }

    @Test//(expected = JsonDeserializedException.class)
    public void testBadJson0() {
        String strGroupCfg = "{\"producerIps\": }";

        GroupConfig groupCfg = JsonBinder.getNonEmptyBinder().fromJson(strGroupCfg, GroupConfig.class);

    }


    @Test
    public void testGroupCfg0() {
        String[] consumerIps = new String[]{"192.168.8.171"};
        String strGroupCfg = "{\"consumerIps\":[\"192.168.8.171\"]}";

        GroupConfig groupCfg = JsonBinder.getNonEmptyBinder().fromJson(strGroupCfg, GroupConfig.class);
        System.out.println(groupCfg);

        Assert.assertEquals(consumerIps[0], groupCfg.getConsumerIps()[0]);
    }


    @Test
    public void testGroupCfgEqual(){
        String strGroupCfg0 = "{\"producerIps\": [\"192.168.8.171\", \"192.168.8.172\"],\"consumerIps\":[\"192.168.8.171\"]}";
        GroupConfig groupCfg0 = JsonBinder.getNonEmptyBinder().fromJson(strGroupCfg0, GroupConfig.class);

        String strGroupCfg1 = "{\"producerIps\": [\"192.168.8.171\",\"192.168.8.172\"],\"consumerIps\":[\"192.168.8.171\"]}";
        GroupConfig groupCfg1 = JsonBinder.getNonEmptyBinder().fromJson(strGroupCfg1, GroupConfig.class);

        System.out.println(groupCfg0);
        System.out.println(groupCfg1);

        Assert.assertTrue(groupCfg0.equals(groupCfg1));
    }
}
