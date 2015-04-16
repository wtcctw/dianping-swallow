package com.dianping.swallow.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.common.internal.monitor.data.ProducerMonitorData;
import com.dianping.swallow.common.internal.util.IOUtilsWrapper;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午9:24:38
 */
@Controller
public class DataCollectorController extends AbstractController{
	
	@RequestMapping(value = "/api/stats/producer", method = RequestMethod.POST)
	public void addProducerMonitor(HttpServletRequest request) throws IOException{
		
		String jsonString = IOUtilsWrapper.convetStringFromRequest(request.getInputStream());
		ProducerMonitorData  monitorData =  jsonBinder.fromJson(jsonString, ProducerMonitorData.class);
		System.out.println(monitorData);
		
	}

	
	@RequestMapping(value = "/api/stats/consumer", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public People addConsumerMonitor(@RequestBody People people){
		
		System.out.println(people);
		
		return new People("xiaoming");
	}


}
