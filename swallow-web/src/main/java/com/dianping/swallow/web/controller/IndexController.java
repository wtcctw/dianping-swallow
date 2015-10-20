package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.controller.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicBoolean;


@Controller
public class IndexController extends AbstractMenuController {
	
	private AtomicBoolean isAdmin = new AtomicBoolean();

	@Autowired
	private UserUtils userUtils;

	@RequestMapping(value = "/")
	public ModelAndView allApps(HttpServletRequest request) {
		
		String username = userUtils.getUsername(request);
		boolean admin = userUtils.isAdministrator(username, true);
		isAdmin.set(admin);
		
		if(admin){
			return new ModelAndView("server/producer", createViewMap());
		}else{
			return new ModelAndView("topic/index", createViewMap());
		}
	    
	}

	@Override
	protected String getMenu() {
		if(isAdmin.get()){
			return "server";
		}else{
			return "topic";
		}
	}
	
}
