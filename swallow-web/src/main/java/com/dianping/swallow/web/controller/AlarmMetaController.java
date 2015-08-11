package com.dianping.swallow.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.service.AlarmMetaService;

@Controller
public class AlarmMetaController extends AbstractSidebarBasedController {

	@Autowired
	private AlarmMetaService alarmMetaService;

	@RequestMapping(value = "/console/setting/alarmmeta")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("setting/alarmmetasetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/alarmmeta/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object alarmMetaList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {
		List<AlarmMeta> alarmMetas = alarmMetaService.findByPage(offset, limit);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("size", alarmMetas.size());
		result.put("entitys", alarmMetas);
		return result;
	}
	
	@RequestMapping(value = "/console/setting/alarmmeta/detail/{metaId}", method = RequestMethod.GET)
	public ModelAndView alarmDetail(@PathVariable int metaId) {
		Map<String, Object> paras = super.createViewMap();
		paras.put("entity", alarmMetaService.findByMetaId(metaId));
		return new ModelAndView("setting/alarmmetadetail", paras);
	}

	@Override
	protected String getSide() {
		return "setting";
	}

	@Override
	public String getSubSide() {
		return "alarmmeta";
	}

	@Override
	protected String getMenu() {
		return "setting";
	}

}
