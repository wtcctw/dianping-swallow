package com.dianping.swallow.broker.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.broker.util.GsonUtil;

@Controller
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @SuppressWarnings({ "rawtypes" })
    @RequestMapping(value = "testReceiver", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Object testReceiver(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {

            //解析请球中的参数
            Map paramMap = request.getParameterMap();
            logger.info("[testReceiver]request param is " + paramMap);

        } catch (IllegalArgumentException e) {
            map.put("result", "fail");
            map.put("errorMsg", e.getMessage());
        } catch (Exception e) {
            map.put("result", "fail");
            map.put("errorMsg", e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return GsonUtil.toJson(map);
    }

}
