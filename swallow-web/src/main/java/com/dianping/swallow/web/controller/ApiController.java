package com.dianping.swallow.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController extends AbstractController{
	
	
	@RequestMapping(value = "/api/createTopic", consumes = "application/json")
	@ResponseBody
	public ResponseMessage createTopic(CreateTopicInfo createTopicInfo){
		
		return null;
	}

}
