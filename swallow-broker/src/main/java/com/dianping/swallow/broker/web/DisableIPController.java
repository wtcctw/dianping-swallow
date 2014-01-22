package com.dianping.swallow.broker.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.broker.util.GsonUtil;
import com.dianping.swallow.broker.util.HttpClientUtil;
import com.dianping.swallow.common.internal.config.DynamicConfig;

@Controller
@RequestMapping(value = "/api", method = RequestMethod.GET, produces = "application/javascript; charset=utf-8")
public class DisableIPController {
    private static final Logger LOG             = LoggerFactory.getLogger(DisableIPController.class);

    private static final char   SEPARATOR       = ';';
    private static final String DELETE          = "delete";
    private static final String ADD             = "add";
    private static final String PROJECT         = "swallow";
    private static final String LION_KEY        = "disable.ipList";

    private static final String LIONAPI_URL_KEY = "swallow.lionapi.url";

    Set<String>                 validActions    = new HashSet<String>();

    private String              lionApiUrl;

    private String              setUrl;
    private String              getUrl;

    @Autowired
    private DynamicConfig       dynamicConfig;

    @PostConstruct
    public void init() {
        validActions.add(ADD);
        validActions.add(DELETE);

        lionApiUrl = dynamicConfig.get(LIONAPI_URL_KEY);
        setUrl = lionApiUrl + "/setconfig";
        getUrl = lionApiUrl + "/getconfig";

        LOG.info("lionApiUrl is " + lionApiUrl);
    }

    @ResponseBody
    @RequestMapping(value = "/disableIP")
    public Object plget(String callback, String env, String id, String action, String ip) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {

            if (StringUtils.isBlank(env)) {
                env = "dev";
            }

            if (StringUtils.isBlank(id)) {
                id = "2";
            }

            Validate.notEmpty(action, "param(action) is required");
            Validate.isTrue(validActions.contains(action), "param(action) must be one of:" + validActions);

            Validate.notEmpty(ip, "param(ip) is required");

            LOG.info("Receive disableIP request: action=" + action + ", env=" + env + ", ip=" + ip);

            //解析ip为list
            List<String> paramIPs = Arrays.asList(StringUtils.split(ip, SEPARATOR));

            if (StringUtils.equalsIgnoreCase(action, ADD)) {

                //先查询
                HashSet<String> disableIPs = getDisableIPs(env);

                //如果ip不存在，则追加
                for (String paramIP : paramIPs) {
                    disableIPs.add(paramIP);
                }
                map.put("lionApiResult", setDisableIPs(env, id, disableIPs));
                map.put("disableIPs", disableIPs);

            } else if (StringUtils.equalsIgnoreCase(action, DELETE)) {

                //先查询
                HashSet<String> disableIPs = getDisableIPs(env);

                //如果ip存在，则删除
                for (String paramIP : paramIPs) {
                    disableIPs.remove(paramIP);
                }
                map.put("lionApiResult", setDisableIPs(env, id, disableIPs));
                map.put("disableIPs", disableIPs);
            } else {
                throw new IllegalArgumentException("Action(" + action + ") is Error, should be add or delete.");
            }

            map.put("success", true);
        } catch (Exception e) {
            map.put("success", false);
            map.put("errorMsg", e.getMessage());
        }
        if (callback != null) {
            return callback + "(" + GsonUtil.toJson(map) + ");";
        } else {
            return GsonUtil.toJson(map);
        }
    }

    private String setDisableIPs(String env, String id, HashSet<String> disableIPs) throws IOException {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("e", env));
        nvps.add(new BasicNameValuePair("p", PROJECT));
        nvps.add(new BasicNameValuePair("id", id));
        nvps.add(new BasicNameValuePair("k", LION_KEY));
        nvps.add(new BasicNameValuePair("ef", "1"));
        nvps.add(new BasicNameValuePair("v", StringUtils.join(disableIPs, SEPARATOR)));
        String result = HttpClientUtil.get(setUrl, nvps);
        if (StringUtils.startsWithIgnoreCase(result, "1|")) {
            throw new RuntimeException("lionapi result error(" + result + ")");
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private HashSet<String> getDisableIPs(String env) throws IOException {
        HashSet<String> disableIPs;
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("e", env));
        nvps.add(new BasicNameValuePair("k", PROJECT + '.' + LION_KEY));
        String value = HttpClientUtil.get(getUrl, nvps);
        if (StringUtils.startsWithIgnoreCase(value, "1|")) {
            throw new RuntimeException("lionapi result error(" + value + ")");
        }
        if (StringUtils.equalsIgnoreCase("<null>", value)) {
            disableIPs = new HashSet<String>();
        } else {
            disableIPs = new HashSet(Arrays.asList(StringUtils.split(value, SEPARATOR)));
        }
        return disableIPs;
    }

}
