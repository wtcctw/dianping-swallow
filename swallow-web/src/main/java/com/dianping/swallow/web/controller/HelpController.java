package com.dianping.swallow.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
	
public class HelpController extends AbstractMenuController{

	@RequestMapping(value = "/help")
	public ModelAndView allApps(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = createViewMap();
		return new ModelAndView("help/help", map);
	}

	@Override
	protected String getMenu() {
		return "help";
	}

}
