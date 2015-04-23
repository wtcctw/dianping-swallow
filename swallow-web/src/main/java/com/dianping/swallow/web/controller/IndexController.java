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

import com.dianping.swallow.web.dao.MongoManager;

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

	@RequestMapping(value = "/message")
	public ModelAndView message(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("message/index", map);
	}

}