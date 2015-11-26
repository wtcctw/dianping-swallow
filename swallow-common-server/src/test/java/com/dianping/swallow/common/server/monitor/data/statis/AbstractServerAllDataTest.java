package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.AbstractTest;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingdongli
 *         15/11/2 下午2:19
 */
public class AbstractServerAllDataTest extends AbstractTest {

    protected List<String> topics = new ArrayList<String>();

    protected List<String> topics2 = new ArrayList<String>();

    protected List<String> consumerIds = new ArrayList<String>();

    protected List<String> ips = new ArrayList<String>();

    protected Long startKey = 100L, endKey = 140L;

    protected final long avergeDelay = 50;
    protected final long qpsPerUnit = 10;
    protected final int intervalCount = 6;

    @Before
    public void test(){

    }
}
