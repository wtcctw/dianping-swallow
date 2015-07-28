package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ConsumerServerAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.ConsumerServerAlarmSettingMapper;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.monitor.wapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日上午10:40:07
 */
@Controller
public class ConsumerServerAlarmSettingController extends AbstractSidebarBasedController {

	@Resource(name = "consumerServerAlarmSettingService")
	private ConsumerServerAlarmSettingService consumerServerAlarmSettingService;
	
	@Resource(name = "ipCollectorService")
	private IPCollectorService ipCollectorService;
	
	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;


	@RequestMapping(value = "/console/setting/consumerserver")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("setting/consumerserversetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/consumerserver/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object consumerserverSettingList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {
		
		List<ConsumerServerAlarmSetting> consumerAlarmSettingList = consumerServerAlarmSettingService.findByPage(offset, limit);
		List<ConsumerServerAlarmSettingDto> consumerAlarmSettingListDto = new ArrayList<ConsumerServerAlarmSettingDto>();
		for(ConsumerServerAlarmSetting consumerAlarmSetting : consumerAlarmSettingList){
			consumerAlarmSettingListDto.add(ConsumerServerAlarmSettingMapper.toConsumerServerAlarmSettingDto(consumerAlarmSetting));
		}
		return new Pair<Integer, List<ConsumerServerAlarmSettingDto>>(consumerAlarmSettingListDto.size(), consumerAlarmSettingListDto);
		
	}

	@RequestMapping(value = "/console/setting/consumerserver/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int consumerserverSettingCreate(@RequestBody ConsumerServerAlarmSettingDto dto) {

		ConsumerServerAlarmSetting consumerServerAlarmSetting = ConsumerServerAlarmSettingMapper.toConsumerServerAlarmSetting(dto);
		boolean result = consumerServerAlarmSettingService.update(consumerServerAlarmSetting);
		if(!result){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/setting/consumerserver/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int remvoeConsumerserverSettingCreate(@RequestParam(value = "serverId") String serverId) {
		
		int result = consumerServerAlarmSettingService.deleteByServerId(serverId);
		if(result > 0){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/console/setting/consumerserver/serverids", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Collection<String> loadProducerSereverIds() {
		
		Set<String> masterIps =  ipCollectorService.getConsumerServerMasterIpsMap().keySet();
		Set<String> slaveIps =  ipCollectorService.getConsumerServerMasterIpsMap().keySet();
		return CollectionUtils.union(masterIps, slaveIps);
		
	}

	@RequestMapping(value = "/console/setting/consumerserver/topics", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> loadProducerSereverTopics(@RequestParam(value = "serverId") String serverId) {
		
		Set<String> topics = consumerDataRetrieverWrapper.getKey(serverId);
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

	private String subSide = "consumer";

	@Override
	public String getSubSide() {
		return subSide;
	}

}
