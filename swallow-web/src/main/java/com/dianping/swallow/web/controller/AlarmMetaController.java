package com.dianping.swallow.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.dto.AlarmMetaBatchDto;
import com.dianping.swallow.web.controller.dto.AlarmMetaDto;
import com.dianping.swallow.web.controller.mapper.AlarmMetaMapper;
import com.dianping.swallow.web.model.alarm.AlarmLevelType;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.service.AlarmMetaService;
import com.dianping.swallow.web.util.ResponseStatus;

@Controller
public class AlarmMetaController extends AbstractSidebarBasedController {

	@Autowired
	private AlarmMetaService alarmMetaService;

	@RequestMapping(value = "/console/setting/alarmmeta")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("tool/alarmmetasetting", createViewMap());
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
		return new ModelAndView("tool/alarmmetadetail", paras);
	}

	@RequestMapping(value = "/console/setting/alarmmeta/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int createAlarmMeta(@RequestBody AlarmMetaDto alarmMetaDto) {
		alarmMetaDto.setMetaId(alarmMetaDto.getType().getNumber());
		boolean result = false;
		if (alarmMetaDto.getIsUpdate()) {
			alarmMetaDto.setUpdateTime(new Date());
			result = alarmMetaService.update(AlarmMetaMapper.convertToAlarmMeta(alarmMetaDto));
		} else {
			AlarmMeta alarmMeta = alarmMetaService.findByMetaId(alarmMetaDto.getType().getNumber());
			if (alarmMeta != null) {
				result = false;
			} else {
				alarmMetaDto.setCreateTime(new Date());
				alarmMetaDto.setUpdateTime(new Date());
				result = alarmMetaService.insert(AlarmMetaMapper.convertToAlarmMeta(alarmMetaDto));
			}
		}
		if (result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/setting/alarmmeta/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int removeAlarmMeta(@RequestParam(value = "metaId") int metaId) {
		int result = alarmMetaService.deleteByMetaId(metaId);
		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/alarmmeta/query/types", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object fingTypes(HttpServletRequest request, HttpServletResponse response) {
		AlarmType[] types = AlarmType.values();
		return types;
	}

	@RequestMapping(value = "/console/alarmmeta/query/leveltypes", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object fingLevelTypes(HttpServletRequest request, HttpServletResponse response) {
		return AlarmLevelType.values();
	}

	@RequestMapping(value = "/console/setting/alarmmeta/batchupdate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public boolean batchUpdate(@RequestBody AlarmMetaBatchDto alarmMetaBatchDto) {
		List<Integer> metaIds = alarmMetaBatchDto.getMetaIds();
		if (metaIds == null) {
			return false;
		}
		for (int metaId : metaIds) {
			AlarmMeta alarmMeta = alarmMetaService.findByMetaId(metaId);
			if (alarmMeta != null) {
				AlarmMetaMapper.update(alarmMetaBatchDto.getUpdateType(),alarmMeta,alarmMetaBatchDto.getIsOpen());
				alarmMetaService.update(alarmMeta);
			}
		}
		return true;
	}

	@Override
	protected String getSide() {
		return "alarm";
	}

	@Override
	public String getSubSide() {
		return "alarmmeta";
	}

	@Override
	protected String getMenu() {
		return "tool";
	}

}
