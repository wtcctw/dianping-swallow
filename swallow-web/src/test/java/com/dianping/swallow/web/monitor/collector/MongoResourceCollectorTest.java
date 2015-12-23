package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.model.alarm.ResultType;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.MongoResourceService;
import org.apache.http.NameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;

/**
 * Author   mingdongli
 * 15/12/22  下午11:20.
 */
public class MongoResourceCollectorTest extends MockTest {

    private static final String MONGO_REPORT = "http://dom.dp/db_daily/message";

    private static final String MONGO_IP_MAPPING = "http://tools.dba.dp/get_mongo_ips.php";

    private static final String POST_RESULT = "{ \"data\": [ { \"load\": 0.055695000000000001, \"disk\": 8.9993999999999996, \"create_date\": \"2015-12-21 00:00:00\", \"dba\": \"\\u82d7\\u53d1\\u5e73\", \"catalog\": \"\\u4ea4\\u6613\\u6d88\\u606f\\u961f\\u521702\", \"threads\": 6, \"io\": null, \"qps\": 1310, \"_id\": 1732, \"dev_cnt\": 2 }, { \"load\": 0.034090000000000002, \"disk\": 35.902200000000001, \"create_date\": \"2015-12-21 00:00:00\", \"dba\": \"\\u82d7\\u53d1\\u5e73\", \"catalog\": \"\\u4e0b\\u5355\\u6d88\\u606f\\u961f\\u5217\", \"threads\": 9, \"io\": null, \"qps\": 31, \"_id\": 1733, \"dev_cnt\": 2 }, { \"load\": 0.058483300000000002, \"disk\": 79.195800000000006, \"create_date\": \"2015-12-21 00:00:00\", \"dba\": \"\\u82d7\\u53d1\\u5e73\", \"catalog\": \"\\u56e2\\u8d2d\\u6d88\\u606f\\u961f\\u5217\", \"threads\": 9, \"io\": null, \"qps\": 913, \"_id\": 1737, \"dev_cnt\": 2 }, { \"load\": 0.028206200000000001, \"disk\": 37.927300000000002, \"create_date\": \"2015-12-21 00:00:00\", \"dba\": \"\\u82d7\\u53d1\\u5e73\", \"catalog\": \"Swallow01\\u6d88\\u606f\\u961f\\u5217\", \"threads\": 10, \"io\": null, \"qps\": 655, \"_id\": 1735, \"dev_cnt\": 2 }, { \"load\": 0.028206200000000001, \"disk\": 37.927300000000002, \"create_date\": \"2015-12-21 00:00:00\", \"dba\": \"\\u82d7\\u53d1\\u5e73\", \"catalog\": \"\\u641c\\u7d22\\u6d88\\u606f\\u961f\\u5217\", \"threads\": 10, \"io\": null, \"qps\": 655, \"_id\": 1736, \"dev_cnt\": 2 }, { \"load\": 0.041549999999999997, \"disk\": 88.604299999999995, \"create_date\": \"2015-12-21 00:00:00\", \"dba\": \"\\u82d7\\u53d1\\u5e73\", \"catalog\": \"\\u4ea4\\u6613\\u6d88\\u606f\\u961f\\u5217\", \"threads\": 22, \"io\": null, \"qps\": 1013, \"_id\": 1738, \"dev_cnt\": 2 }, { \"load\": 0.034090000000000002, \"disk\": 35.902200000000001, \"create_date\": \"2015-12-21 00:00:00\", \"dba\": \"\\u82d7\\u53d1\\u5e73\", \"catalog\": \"\\u7f13\\u5b58\\u6d88\\u606f\\u961f\\u5217\", \"threads\": 68, \"io\": null, \"qps\": 4488, \"_id\": 1734, \"dev_cnt\": 2 } ] }";

    private static final String GET_RESULT = "{\"status\":0,\"message\":{\"\\u4ea4\\u6613\\u6d88\\u606f\\u961f\\u5217\":[\"10.1.6.186:27017\",\"10.1.6.188:27017\"],\"\\u4e0b\\u5355\\u6d88\\u606f\\u961f\\u5217\":[\"10.1.101.155:27018\",\"10.1.101.157:27018\"],\"\\u7f13\\u5b58\\u6d88\\u606f\\u961f\\u5217\":[\"10.1.101.155:21017\",\"10.1.101.157:27017\"],\"\\u56e2\\u8d2d\\u6d88\\u606f\\u961f\\u5217\":[\"10.1.6.31:21018\",\"10.1.6.31:27018\"],\"\\u641c\\u7d22\\u6d88\\u606f\\u961f\\u5217\":[\"10.1.115.11:27017\",\"10.1.115.12:27017\"],\"Swallow01\\u6d88\\u606f\\u961f\\u5217\":[\"10.1.115.11:27018\",\"10.1.115.12:27018\"],\"\\u4ea4\\u6613\\u6d88\\u606f\\u961f\\u521702\":[\"10.3.10.44:27017\",\"10.3.10.48:27017\"]}}";


    @Mock
    private MongoResourceService mongoResourceService;

    @Mock
    private HttpService httpSerivice;

    private MongoResourceCollector mongoResourceCollector;

    @Before
    public void setUp() throws Exception {

        mongoResourceCollector = new MongoResourceCollector();

        HttpService.HttpResult httpResultPost = new HttpService.HttpResult();
        httpResultPost.setResultType(ResultType.SUCCESS);
        httpResultPost.setSuccess(Boolean.TRUE);
        httpResultPost.setResponseBody(POST_RESULT);

        HttpService.HttpResult httpResultGet = new HttpService.HttpResult();
        httpResultGet.setResultType(ResultType.SUCCESS);
        httpResultGet.setSuccess(Boolean.TRUE);
        httpResultGet.setResponseBody(GET_RESULT);

        mongoResourceCollector.setHttpSerivice(httpSerivice);
        mongoResourceCollector.setMongoResourceService(mongoResourceService);

        Mockito.doReturn(httpResultPost).when(httpSerivice).httpPost(MONGO_REPORT, new ArrayList<NameValuePair>());
        Mockito.doReturn(httpResultGet).when(httpSerivice).httpGet(MONGO_IP_MAPPING);

    }

    @Test
    public void testDoCollector(){

        mongoResourceCollector.doCollector();
    }
}