package com.dianping.swallow.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.dto.ProducerServerAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.ProducerServerAlarmSettingMapper;
import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 * 
 * @author qiyin
 *
 */
@Controller
public class AlarmSettingController extends AbstractMenuController {

	private static final Logger logger = LoggerFactory.getLogger(AlarmSettingController.class);

	@Resource(name = "producerServerAlarmSettingService")
	private ProducerServerAlarmSettingService producerServerAlarmSettingService;

	@Resource(name = "consumerServerAlarmSettingService")
	private ConsumerServerAlarmSettingService consumerServerAlarmSettingService;

	@Resource(name = "topicAlarmSettingService")
	private TopicAlarmSettingService topicAlarmSettingService;

	@Resource(name = "consumerIdAlarmSettingService")
	private ConsumerIdAlarmSettingService consumerIdAlarmSettingService;

	@Autowired
	ExtractUsernameUtils extractUsernameUtils;

	@RequestMapping(value = "/console/admin/producer/setting", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public void createProducerSetting(String xxxx) {

	}

	@RequestMapping(value = "/console/setting")
	public ModelAndView mainSetting(HttpServletRequest request, HttpServletResponse response) {
		return producerSetting(request, response);
	}

	@RequestMapping(value = "/console/setting/producerserver/default")
	public ModelAndView producerSetting(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("setting/producerserversetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/consumerserver/default")
	public ModelAndView consumerSetting(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("setting/consumerserversetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/topic/default")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("setting/topicsetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/consumerid/default")
	public ModelAndView consumerIdSetting(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("setting/consumeridsetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/producerserver/default/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void producerSettingCreatePost(@RequestBody ProducerServerAlarmSettingDto dto) {
		if (producerServerAlarmSettingService.findOne() != null) {
			ProducerServerAlarmSetting alarmSetting = ProducerServerAlarmSettingMapper
					.toProducerServerAlarmSetting(dto);
			producerServerAlarmSettingService.insert(alarmSetting);
		}
	}
	
	@RequestMapping(value = "/console/setting/producerserver/default/update/", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void producerSettingUpdatePost(@RequestBody ProducerServerAlarmSettingDto dto) {
			ProducerServerAlarmSetting alarmSetting = ProducerServerAlarmSettingMapper
					.toProducerServerAlarmSetting(dto);
			producerServerAlarmSettingService.update(alarmSetting);
	}

	@Override
	protected String getMenu() {
		return "setting";
	}

}
