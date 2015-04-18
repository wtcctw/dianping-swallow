package com.dianping.swallow.web.controller;

import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.common.internal.monitor.data.ConsumerMonitorData;
import com.dianping.swallow.common.internal.monitor.data.ProducerMonitorData;
import com.dianping.swallow.web.dao.ConsumerMonitorDataDao;
import com.dianping.swallow.web.dao.ProducerMonitorDataDao;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午9:24:38
 */
@Controller
public class DataCollectorController extends AbstractController implements InitializingBean{
	
	@Autowired
	private ProducerMonitorDataDao  producerMonitorDataDao; 

	@Autowired
	private ConsumerMonitorDataDao  consumerMonitorDataDao; 

	
	@RequestMapping(value = "/api/stats/producer", method = RequestMethod.POST)
	@ResponseBody
	public void addProducerMonitor(@RequestBody ProducerMonitorData  monitorData) throws IOException{
		
		System.out.println(monitorData);
		
	}

	@RequestMapping(value = "/api/stats/consumer", method = RequestMethod.POST)
	@ResponseBody
	public void addConsumerMonitor(@RequestBody ConsumerMonitorData monitorData) throws IOException{
		
		System.out.println(monitorData);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		System.out.println("producerDao:" + producerMonitorDataDao);
		System.out.println("consumerDao:" + consumerMonitorDataDao);
	}
	

}
