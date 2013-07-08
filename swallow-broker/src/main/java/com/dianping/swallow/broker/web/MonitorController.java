package com.dianping.swallow.broker.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.broker.service.consumer.ConsumerHolder;
import com.dianping.swallow.broker.service.consumer.impl.ConsumerBroker;
import com.dianping.swallow.broker.service.producer.ProducerHolder;
import com.dianping.swallow.producer.Producer;

@Controller
public class MonitorController {

    @Autowired
    private ProducerHolder producerHolder;

    @Autowired
    private ConsumerHolder consumerHolder;

    @RequestMapping(value = "monitor", method = { RequestMethod.POST, RequestMethod.GET })
    public ModelAndView monitor(HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, ConsumerBroker> consumerBrokerMap = consumerHolder.getConsumerBrokerMap();

        Map<String, Producer> producerMap = producerHolder.getProducerMap();

        map.put("consumerBrokerMap", consumerBrokerMap);
        map.put("producerMap", producerMap);

        return new ModelAndView("monitor/detail", map);
    }

}
