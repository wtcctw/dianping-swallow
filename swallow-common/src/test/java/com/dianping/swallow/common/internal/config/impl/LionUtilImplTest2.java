package com.dianping.swallow.common.internal.config.impl;

import com.dianping.lion.Constants;
import com.dianping.lion.client.ConfigCache;
import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.impl.lion.LionUtilImpl;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.util.http.HttpMethod;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年6月15日 下午2:45:25
 */
public class LionUtilImplTest2 extends AbstractTest {


    private LionUtilImpl lionUtil = new LionUtilImpl(2L);

    private String TEST_KEY = "unittest2";

    @Before
    public void beforeLionUtilImplTest() {

    }

    @Test
    public void testGetKey() {

        String env = EnvUtil.getEnv();
        if ("alpha".equalsIgnoreCase(env) || "dev".equalsIgnoreCase(env)) {

            lionUtil.createOrSetConfig("swallow.test.lion.api", "true");
            String result = lionUtil.getValue("swallow.test.lion.api");
            Assert.assertEquals(result, "true");

            lionUtil.createOrSetConfig("swallow.test.lion.api", "alphapost", HttpMethod.POST, env);
            result = lionUtil.getValue("swallow.test.lion.api");
            Assert.assertEquals(result, "alphapost");

            lionUtil.createOrSetConfig("swallow.test.lion.api", "alphaget", HttpMethod.GET, env);
            result = lionUtil.getValue("swallow.test.lion.api");
            Assert.assertEquals(result, "alphaget");

            lionUtil.createOrSetConfig("swallow.test.lion.api", "testcreate", HttpMethod.POST, null);
            result = lionUtil.getValue("swallow.test.lion.api");
            Assert.assertEquals(result, "testcreate");

        }


    }

    //@Test
    public void testGetConfigs() {

        String value = "swallow" + UUID.randomUUID().toString();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 20; ++i) {
            stringBuilder.append(value);
        }
        value = stringBuilder.toString();
        String newKeys[] = new String[]{"", "a", "a.b", ".a", ".a.b"};

        for (String addKey : newKeys) {

            lionUtil.createOrSetConfig(TEST_KEY + addKey, value + addKey, HttpMethod.GET, null);
        }

        Map<String, String> cfgs = lionUtil.getCfgs(TEST_KEY);

        Assert.assertEquals(newKeys.length, cfgs.size());

        for (String addKey : newKeys) {

            Assert.assertEquals(value + addKey, cfgs.get(LionUtilImpl.getRealKey(TEST_KEY + addKey)));
        }


    }

    @Test
    public void testLion() {

        ConfigCache cc = ConfigCache.getInstance();
        String group = cc.getAppenv(Constants.KEY_SWIMLANE);

        System.out.println(group);


    }
}
