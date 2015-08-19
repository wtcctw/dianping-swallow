package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ProducerServerAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.ProducerServerAlarmSettingMapper;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日上午10:40:07
 */
@Controller
public class ProducerServerAlarmSettingController extends AbstractSidebarBasedController {

	@Resource(name = "producerServerAlarmSettingService")
	private ProducerServerAlarmSettingService producerServerAlarmSettingService;

	@Resource(name = "ipCollectorService")
	private IPCollectorService ipCollectorService;
	
	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	@RequestMapping(value = "/console/setting/producerserver")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("setting/producerserversetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/producerserver/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object producerserverSettingList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {
		
		List<ProducerServerAlarmSetting> producerAlarmSettingList = producerServerAlarmSettingService.findByPage(offset, limit);
		List<ProducerServerAlarmSettingDto> producerAlarmSettingListDto = new ArrayList<ProducerServerAlarmSettingDto>();
		for(ProducerServerAlarmSetting producerAlarmSetting : producerAlarmSettingList){
			producerAlarmSettingListDto.add(ProducerServerAlarmSettingMapper.toProducerServerAlarmSettingDto(producerAlarmSetting));
		}
		return new Pair<Integer, List<ProducerServerAlarmSettingDto>>(producerAlarmSettingListDto.size(), producerAlarmSettingListDto);
		
	}

	@RequestMapping(value = "/console/setting/producerserver/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int producerserverSettingCreate(@RequestBody ProducerServerAlarmSettingDto dto) {

		ProducerServerAlarmSetting consumerServerAlarmSetting = ProducerServerAlarmSettingMapper.toProducerServerAlarmSetting(dto);
		boolean result = producerServerAlarmSettingService.update(consumerServerAlarmSetting);
		
		if(!result){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/setting/producerserver/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int remvoeProducerserverSettingCreate(@RequestParam(value = "serverId") String serverId) {
		
		int result = producerServerAlarmSettingService.deleteByServerId(serverId);
		
		if(result > 0){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}
	
	@RequestMapping(value = "/console/setting/producerserver/serverids", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> loadProducerSereverIds() {
		
		Set<String> hostNames = ipCollectorService.getProducerServerIpsMap().keySet();
		return new ArrayList<String>(hostNames);
	}

	@RequestMapping(value = "/console/setting/producerserver/topics", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> loadProducerSereverTopics(@RequestParam(value = "serverId") String serverId) {
		
		Set<String> topics =  consumerDataRetrieverWrapper.getKey(serverId);
		
		if(topics != null){
			return new ArrayList<String>(topics);
		}else{
			return new ArrayList<String>();
		}
	}

	@Override
	protected String getMenu() {
		return "setting";
	}

	@Override
	protected String getSide() {
		return "warn";
	}

	private String subSide = "producer";

	@Override
	public String getSubSide() {
		return subSide;
	}

}
