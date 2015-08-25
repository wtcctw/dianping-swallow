package com.dianping.swallow.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IPController extends AbstractMenuController{
	
	@RequestMapping(value = "/console/ip")
	public ModelAndView topicView(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("ip/index", createViewMap());
	}
	

	@Override
	protected String getMenu() {
		return "ip";
	}
}

