package com.dianping.swallow.broker.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.broker.service.ProducerHolder;
import com.dianping.swallow.broker.util.GsonUtil;
import com.dianping.swallow.broker.vo.ParamVO;
import com.dianping.swallow.producer.Producer;

@Controller
public class BrokerController {

    private static final Logger LOG     = LoggerFactory.getLogger(BrokerController.class);

    private static final String CONTENT = "content";

    private static final Object TOPIC   = "topic";

    @Autowired
    private ProducerHolder      producerHolder;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/sendMsg", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=utf-8")
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

            Map paramMap = request.getParameterMap();
            LOG.info("request param is " + paramMap);

            if (paramMap != null && paramMap.size() > 0) {
                Set<Map.Entry> set = paramMap.entrySet();
                for (Map.Entry paramEntry : set) {
                    String name = (String) paramEntry.getKey();
                    String value = (String) paramEntry.getValue();
                    if (CONTENT.equals(name)) {
                        content = value;
                    } else if (TOPIC.equals(name)) {
                        topic = value;
                    } else {
                        properties.put(name, value);
                    }
                }
            }

            //根据topic找到Producer，进行发送
            Producer producer = producerHolder.getProducer(topic);

            //            String messageContent = "o" + URLEncoder.encode(oType, "UTF-8") + "&";
            producer.sendMessage(content, properties);

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

    private List<ParamVO> parse(String queryString) {
        queryString = queryString.trim();
        String[] pairs = StringUtils.split(queryString, '&');
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                StringUtils.split(pair, '=');
            }
        }

        return null;
    }

}
