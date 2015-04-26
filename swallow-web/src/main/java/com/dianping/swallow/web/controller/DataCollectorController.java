package com.dianping.swallow.web.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.common.server.monitor.data.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.ProducerMonitorData;
import com.dianping.swallow.web.dao.ConsumerMonitorDao;
import com.dianping.swallow.web.dao.ProducerMonitorDao;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;


/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午9:24:38
 */
@Controller
public class DataCollectorController extends AbstractController{
	
	@Autowired
	private ProducerMonitorDao  producerMonitorDataDao; 

	@Autowired
	private ConsumerMonitorDao  consumerMonitorDataDao; 

	@Autowired
	private ProducerDataRetriever producerDataRetriever;
	
	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;
	
	private static final Logger logger = LoggerFactory.getLogger(DataCollectorController.class);  
	
	@RequestMapping(value = "/api/stats/producer", method = RequestMethod.POST)
	@ResponseBody
	public void addProducerMonitor(@RequestBody ProducerMonitorData  producerMonitorData) throws IOException{
		
		if(logger.isDebugEnabled()){
			logger.debug("[addProducerMonitor]" + producerMonitorData);
		}
		producerDataRetriever.add(producerMonitorData);
		producerMonitorDataDao.saveProducerMonotorData(producerMonitorData);
		
	}

	@RequestMapping(value = "/api/stats/consumer", method = RequestMethod.POST)
	@ResponseBody
	public void addConsumerMonitor(@RequestBody ConsumerMonitorData consumerMonitorData) throws IOException{

		if(logger.isDebugEnabled()){
			logger.debug("[addConsumerMonitor]" + consumerMonitorData);
		}
		consumerDataRetriever.add(consumerMonitorData);
		consumerMonitorDataDao.saveConsumerMonotorData(consumerMonitorData);

	}

}
