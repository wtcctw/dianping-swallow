package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseQueryDto;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.JmxResource;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.service.JmxResourceService;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author   mingdongli
 * 16/2/18  下午6:20.
 */
@Controller
public class JmxController extends AbstractSidebarBasedController{

    @Resource(name = "jmxResourceService")
    private JmxResourceService jmxResourceService;

    @Resource(name = "kafkaServerResourceService")
    private KafkaServerResourceService kafkaServerResourceService;

    @RequestMapping(value = "/console/jmx")
    public ModelAndView topicView() {

        return new ModelAndView("tool/jmx", createViewMap());
    }

    @RequestMapping(value = "/console/jmx/list", method = RequestMethod.POST)
    @ResponseBody
    public Object fetchJmxPage(@RequestBody BaseQueryDto baseDto) {

        Pair<Long, List<JmxResource>> pair = jmxResourceService.findJmxResourcePage(baseDto.getOffset(), baseDto.getLimit());
        List<JmxResource> jmxResources = pair.getSecond();
        if(jmxResources != null){
            Set<String> ips = kafkaServerIps();
            for(JmxResource jmxResource : jmxResources){
                List<IpInfo> ipInfos = jmxResource.getBrokerIpInfos();
                if(ipInfos == null && !ips.isEmpty()){
                    ipInfos = new ArrayList<IpInfo>();
                    for(String ip : ips){
                        ipInfos.add(new IpInfo(ip, true, true));
                    }
                    jmxResource.setBrokerIpInfos(ipInfos);
                    jmxResourceService.update(jmxResource);
                }
            }
        }

        return pair;
    }

    private Set<String> kafkaServerIps(){
        Set<String> ips = new HashSet<String>();
        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findAll();
        for(KafkaServerResource kafkaServerResource : kafkaServerResources){
            String brokerIp = kafkaServerResource.getIp();
            if(StringUtils.isNotBlank(brokerIp)){
                ips.add(brokerIp);
            }
        }
        return ips;
    }

    @RequestMapping(value = "/console/jmx/create", method = RequestMethod.POST)
    @ResponseBody
    public Boolean kafkaServerResourceCreate(@RequestBody JmxResource jmxResource) {

        return jmxResourceService.update(jmxResource);
    }

    @RequestMapping(value = "/console/jmx/remove", method = RequestMethod.POST)
    @ResponseBody
    public int remvoeKafkaServerResource(@RequestBody JmxResource jmxResource) {

        return jmxResourceService.remove(jmxResource);
    }

    @RequestMapping(value = "/console/jmx/ipinfo/alarm", method = RequestMethod.GET)
    @ResponseBody
    public boolean setActive(String name, String ip, boolean alarm) {

        List<JmxResource> jmxResources = jmxResourceService.findByName(name);
        if(jmxResources != null){
            for(JmxResource jmxResource : jmxResources){
                List<IpInfo> ipInfos = jmxResource.getBrokerIpInfos();
                if (ipInfos == null || ip == null) {
                    return false;
                }
                for (IpInfo ipInfo : ipInfos) {
                    if (ip.equals(ipInfo.getIp())) {
                        ipInfo.setAlarm(alarm);
                        jmxResource.setBrokerIpInfos(ipInfos);
                        return jmxResourceService.update(jmxResource);
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected String getSide() {
        return "jmx";
    }

    @Override
    public String getSubSide() {
        return subSide;
    }

    private String subSide = "mbean";

    @Override
    protected String getMenu() {
        return "tool";
    }
}
