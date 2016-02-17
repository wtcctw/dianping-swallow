package com.dianping.swallow.common.internal.config.impl.lion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.util.StringUtils;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年12月30日 下午5:42:54
 */
public class LionDynamicConfigTest extends AbstractTest implements ConfigChangeListener {

    public LionDynamicConfig dynamicConfig = new LionDynamicConfig();

    private String testKeyPrefix = "swallow.liondynamic";

    private LionUtil lionUtil = new LionUtilImpl(2L);

    private int testKeyCount = 3;
    private Map<Integer, String> keyValue = new HashMap<Integer, String>();


    @Before
    public void beforeLionDynamicConfigTest() {

        keyValue = setValue();
    }


    private Map<Integer, String> setValue() {

        Map<Integer, String> values = new HashMap<Integer, String>();

        for (int i = 0; i < testKeyCount; i++) {

            String value = UUID.randomUUID().toString();
            lionUtil.createOrSetConfig(key(i), value);
            values.put(i, value);
        }
        return values;

    }


    @Test
    public void testGet() {

        for (Entry<Integer, String> entry : keyValue.entrySet()) {

            Assert.assertEquals(entry.getValue(), dynamicConfig.get(key(entry.getKey())));
        }

        Map<String, String> prefix = dynamicConfig.getProperties(testKeyPrefix);
        Assert.assertEquals(keyValue.size(), prefix.size());

        for (Entry<Integer, String> entry : keyValue.entrySet()) {

            Assert.assertEquals(entry.getValue(), prefix.get(key(entry.getKey())));
        }
    }


    private Set<String> changedKeys = new HashSet<String>();

    @Test
    public void testChange() {

        for (Entry<Integer, String> entry : keyValue.entrySet()) {

            Assert.assertEquals(entry.getValue(), dynamicConfig.get(key(entry.getKey())));
        }

        dynamicConfig.addConfigChangeListener(this);

        Map<Integer, String> newValue = setValue();
        sleep(100);


        for (Entry<Integer, String> entry : newValue.entrySet()) {

            logger.info(key(entry.getKey()) + ":" + entry.getValue());

            //通知到
            Assert.assertTrue(changedKeys.contains(key(entry.getKey())));

            //新旧不等
            Assert.assertNotEquals(entry.getValue(), keyValue.get(entry.getKey()));

            Assert.assertEquals(entry.getValue(), dynamicConfig.get(key(entry.getKey())));
        }


    }


    private String key(int index) {

        return StringUtils.join(".", testKeyPrefix, String.valueOf(index));
    }


    @Override
    public void onConfigChange(String key, String value) throws Exception {
        changedKeys.add(key);
    }

}
