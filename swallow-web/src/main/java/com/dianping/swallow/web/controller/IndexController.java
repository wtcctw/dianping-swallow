//package com.dianping.swallow.web.controller;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//@Controller
//public class IndexController extends AbstractController{
//
//	@SuppressWarnings("unused")
//	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
//
//	@RequestMapping(value = "/")
//	public ModelAndView allApps(HttpServletRequest request, HttpServletResponse response) {
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("menu", "help");
//		map.put("contextPath", "");
//		return new ModelAndView("help/help", map);
//	}
//
//}

package com.dianping.swallow.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class IndexController extends AbstractController {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(IndexController.class);
	

	@RequestMapping(value = "/")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("topic/index", map);
	}
}
