package com.dianping.swallow.broker.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.broker.service.producer.ProducerHolder;
import com.dianping.swallow.broker.util.GsonUtil;
import com.dianping.swallow.producer.Producer;

@Controller
public class ProducerController {

    private static final Logger LOG     = LoggerFactory.getLogger(ProducerController.class);

    private static final String CONTENT = "content";

    private static final Object TOPIC   = "topic";

    @Autowired
    private ProducerHolder      producerHolder;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "sendMsg", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Object sendMsg(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {

            //topic
            String topic = null;
            //消息内容
            String content = null;
            //自定义的参数
            Map<String, String> properties = new HashMap<String, String>();

            //解析请球中的参数
            Map paramMap = request.getParameterMap();
            LOG.info("[sendMsg]request param is " + paramMap);

            if (paramMap != null && paramMap.size() > 0) {
                Set<Map.Entry> set = paramMap.entrySet();
                for (Map.Entry paramEntry : set) {
                    String name = (String) paramEntry.getKey();
                    String[] values = (String[]) paramEntry.getValue();
                    if (values != null && values.length > 0) {
                        String value = values[0].toString();
                        if (CONTENT.equals(name)) {
                            content = value;
                        } else if (TOPIC.equals(name)) {
                            topic = value;
                        } else {
                            properties.put(name, value);
                        }
                    }
                }
            }
            //验证参数
            if (StringUtils.isBlank(topic)) {
                throw new IllegalArgumentException("Topic (" + topic + ") can not be blank.");
            } else if (StringUtils.isEmpty(content)) {
                throw new IllegalArgumentException("Content (" + content + ") can not be empty.");
            }

            //根据topic找到Producer
            LOG.info("[sendMsg]Sending message: topic=" + topic + ", content=" + content + ", properties=" + properties);
            Producer producer = producerHolder.getProducer(topic);
            if (producer != null) {
                //发送消息
                producer.sendMessage(content, properties);
            } else {
                throw new IllegalArgumentException("Topic '" + topic + "' not allowed.");
            }

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
