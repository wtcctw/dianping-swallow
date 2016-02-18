package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.controller.dto.BaseQueryDto;
import com.dianping.swallow.web.model.resource.JmxResource;
import com.dianping.swallow.web.service.JmxResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Author   mingdongli
 * 16/2/18  下午6:20.
 */
@Controller
public class JmxController extends AbstractSidebarBasedController{

    @Resource(name = "jmxResourceService")
    private JmxResourceService jmxResourceService;

    @RequestMapping(value = "/console/jmx")
    public ModelAndView topicView() {

        return new ModelAndView("tool/jmx", createViewMap());
    }

    @RequestMapping(value = "/console/jmx/list", method = RequestMethod.POST)
    @ResponseBody
    public Object fetchJmxPage(@RequestBody BaseQueryDto baseDto) {

        return jmxResourceService.findJmxResourcePage(baseDto.getOffset(), baseDto.getLimit());
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
