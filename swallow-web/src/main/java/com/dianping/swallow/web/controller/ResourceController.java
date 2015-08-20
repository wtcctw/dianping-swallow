package com.dianping.swallow.web.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.service.ProducerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午11:47:27
 */
@Controller
public class ResourceController extends AbstractSidebarBasedController{
	
	@Resource(name = "consumerServerResourceService")
	private ConsumerServerResourceService consumerServerResourceService;
	
	@Resource(name = "producerServerResourceService")
	private ProducerServerResourceService producerServerResourceService;
	
	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;
	
	@Resource(name = "consumerIdResourceService")
	private ConsumerIdResourceService consumerIdResourceService;
	
	@Resource(name = "ipResourceService")
	private IpResourceService ipResourceService;

	@RequestMapping(value = "/console/resource/producerserver", method = RequestMethod.GET)
	public ModelAndView viewProducerServerResource() {
		
		return new ModelAndView("resource/producerserver", createViewMap());
	}

	@RequestMapping(value = "/console/resource/producerserver/{hostname}", method = RequestMethod.GET)
	public ModelAndView ResourceProducerServer() {

		return new ModelAndView("resource/producerserver", createViewMap());
	}
	
	
	@Override
	protected String getMenu() {

		return "resource";
	}

	@Override
	protected String getSide() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubSide() {
		// TODO Auto-generated method stub
		return null;
	}

}
