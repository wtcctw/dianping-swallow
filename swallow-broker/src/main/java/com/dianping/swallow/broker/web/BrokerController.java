package com.dianping.swallow.broker.web;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.broker.util.GsonUtil;

@Controller
public class BrokerController {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerController.class);

    @RequestMapping(value = "/sendMsg", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Object sendMsg(String topic, String oType) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            //根据topic找到Producer，进行发送
            
            
            map.put("success", true);
        } catch (IllegalArgumentException e) {
            map.put("success", false);
            map.put("errorMsg", e.getMessage());
        } catch (Exception e) {
            map.put("success", false);
            map.put("errorMsg", e.getMessage());
            LOG.error(e.getMessage(), e);
        }
        return GsonUtil.toJson(map);
    }

}
