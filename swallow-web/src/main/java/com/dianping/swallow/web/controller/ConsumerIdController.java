package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ConsumerIdQueryDto;
import com.dianping.swallow.web.controller.listener.ResourceListener;
import com.dianping.swallow.web.controller.listener.ResourceObserver;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.model.resource.BaseResource;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mingdongli
 *         <p/>
 *         2015年8月27日下午3:33:36
 */
@Controller
public class ConsumerIdController extends AbstractMenuController implements ResourceObserver {

    @Resource(name = "consumerIdResourceService")
    private ConsumerIdResourceService consumerIdResourceService;

    @Autowired
    private UserUtils userUtils;

    private List<ResourceListener> listeners = new ArrayList<ResourceListener>();

    @RequestMapping(value = "/console/consumerid")
    public ModelAndView topicView() {

        return new ModelAndView("consumerid/index", createViewMap());
    }

    @RequestMapping(value = "/console/consumerid/list", method = RequestMethod.POST)
    @ResponseBody
    public Object comsumeridResourceList(@RequestBody ConsumerIdQueryDto consumerIdQueryDto) {

        ConsumerIdParam consumerIdParam = new ConsumerIdParam();
        consumerIdParam.setLimit(consumerIdQueryDto.getLimit());
        consumerIdParam.setOffset(consumerIdQueryDto.getOffset());
        consumerIdParam.setTopic(consumerIdQueryDto.getTopic());
        consumerIdParam.setConsumerId(consumerIdQueryDto.getConsumerId());
        consumerIdParam.setConsumerIp(consumerIdQueryDto.getConsumerIp());
        consumerIdParam.setInactive(consumerIdQueryDto.isInactive());
        return consumerIdResourceService.find(consumerIdParam);
    }

    @RequestMapping(value = "/console/topic/auth/cid", method = RequestMethod.POST)
    @ResponseBody
    public Object queryComsumeridResource(@RequestBody ConsumerIdQueryDto consumerIdQueryDto) {

        String topic = consumerIdQueryDto.getTopic();
        String consumerId = consumerIdQueryDto.getConsumerId();
        return consumerIdResourceService.findByConsumerIdAndTopic(topic, consumerId);
    }

    @RequestMapping(value = "/console/consumerid/update", method = RequestMethod.POST)
    @ResponseBody
    public Boolean updateTopic(@RequestBody ConsumerIdResource consumerIdResource) {

        boolean result = consumerIdResourceService.update(consumerIdResource);
        if (result) {
            doUpdateNotify(consumerIdResource);
        }
        return result;
    }

    @RequestMapping(value = "/console/consumerid/alarm", method = RequestMethod.GET)
    @ResponseBody
    public boolean editProducerAlarmSetting(@RequestParam String topic, @RequestParam boolean alarm,
                                            @RequestParam String consumerId) {

        ConsumerIdParam consumerIdParam = new ConsumerIdParam();
        consumerIdParam.setTopic(topic);
        consumerIdParam.setConsumerId(consumerId);
        Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.find(consumerIdParam);
        List<ConsumerIdResource> consumerIdResourceList = pair.getSecond();
        if (consumerIdResourceList != null && !consumerIdResourceList.isEmpty()) {
            ConsumerIdResource consumerIdResource = consumerIdResourceList.get(0);
            consumerIdResource.setAlarm(alarm);
            boolean result = consumerIdResourceService.update(consumerIdResource);
            if (result) {
                doUpdateNotify(consumerIdResource);
            }
            return result;
        }

        return Boolean.FALSE;
    }

    @RequestMapping(value = "/console/consumerid/remove", method = RequestMethod.GET)
    @ResponseBody
    public int remvoeComsumerid(@RequestParam(value = "consumerId") String consumerId,
                                @RequestParam(value = "topic") String topic) {

        int result = consumerIdResourceService.remove(topic, consumerId);
        if (result > 0) {
            ConsumerIdResource consumerIdResource = new ConsumerIdResource();
            consumerIdResource.setTopic(topic);
            consumerIdResource.setConsumerId(consumerId);
            doDeleteNotify(consumerIdResource);
            return ResponseStatus.SUCCESS.getStatus();
        } else {
            return ResponseStatus.MONGOWRITE.getStatus();
        }
    }

    @RequestMapping(value = "/console/consumerid/allconsumerid", method = RequestMethod.GET)
    @ResponseBody
    public List<String> loadConsumerid(HttpServletRequest request) {

        String username = userUtils.getUsername(request);

        return userUtils.consumerIds(username);

    }

    @RequestMapping(value = "/console/consumerid/ips", method = RequestMethod.GET)
    @ResponseBody
    public List<String> loadConsumerIps(HttpServletRequest request) {

        String username = userUtils.getUsername(request);
        return userUtils.consumerIps(username);

    }

    @RequestMapping(value = "/console/consumerid/ipinfo/{topic}/{cid}", method = RequestMethod.GET)
    public ModelAndView alarmDetail(@PathVariable String topic, @PathVariable String cid) {
        Map<String, Object> map = createViewMap();
        ConsumerIdResource consumerIdResource = consumerIdResourceService.findByConsumerIdAndTopic(topic, cid);
        map.put("topic", topic);
        map.put("cid", cid);
        map.put("entity", consumerIdResource.getConsumerIpInfos());
        return new ModelAndView("consumerid/ipinfo", map);
    }

    @RequestMapping(value = "/console/consumerid/alarm/ipinfo/alarm", method = RequestMethod.GET)
    @ResponseBody
    public boolean setAlarm(String topic, String cid, String ip, boolean alarm) {

        return doSetIpInfo(topic, cid, ip, "alarm", alarm);
    }

    @RequestMapping(value = "/console/consumerid/alarm/ipinfo/active", method = RequestMethod.GET)
    @ResponseBody
    public boolean setActive(String topic, String cid, String ip, boolean active) {

        return doSetIpInfo(topic, cid, ip, "active", active);
    }

    private boolean doSetIpInfo(String topic, String cid, String ip, String type, boolean value) {

        ConsumerIdResource consumerIdResource = consumerIdResourceService.findByConsumerIdAndTopic(topic, cid);
        List<IpInfo> ipInfos = consumerIdResource.getConsumerIpInfos();
        if (ipInfos == null || ip == null || type == null) {
            return false;
        }
        for (IpInfo ipInfo : ipInfos) {
            if (ip.equals(ipInfo.getIp())) {
                if (type.equals("alarm")) {
                    ipInfo.setAlarm(value);
                } else if (type.equals("active")) {
                    ipInfo.setActive(value);
                } else {
                    return false;
                }
                consumerIdResource.setConsumerIpInfos(ipInfos);
                boolean result = consumerIdResourceService.insert(consumerIdResource);
                if (result) {
                    doUpdateNotify(consumerIdResource);
                }
                return result;
            }
        }

        return false;
    }

    @RequestMapping(value = "/console/consumerid/alarm/ipinfo/count/inactive", method = RequestMethod.GET)
    @ResponseBody
    public long countInactive() {

        return consumerIdResourceService.countInactive();
    }

    @Override
    protected String getMenu() {
        return "consumerid";
    }

    @Override
    public void doRegister(ResourceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void doUpdateNotify(BaseResource resource) {
        for (ResourceListener listener : listeners) {
            listener.doUpdateNotify(resource);
        }
    }

    @Override
    public void doDeleteNotify(BaseResource resource) {
        for (ResourceListener listener : listeners) {
            listener.doDeleteNotify(resource);
        }
    }
}
