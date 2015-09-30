package com.dianping.swallow.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午1:41:02
 */
public class ApplicationController extends AbstractMenuController {
	
	@RequestMapping(value = "/console/application")
	public ModelAndView ipView(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("application/index", createViewMap());
	}

	@Override
	protected String getMenu() {

		return "application";
	}

}
