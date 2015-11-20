package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.controller.dto.MessageQueryDto;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.dao.impl.DefaultMessageDao;
import com.dianping.swallow.web.model.Message;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.LogSearchService;
import com.dianping.swallow.web.service.LogSearchService.SearchResult;
import com.dianping.swallow.web.service.MessageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mingdongli
 *         <p/>
 *         2015年4月22日 上午12:04:03
 */
@Controller
public class MessageController extends AbstractMenuController {

    @Resource(name = "messageService")
    private MessageService messageService;

    @Autowired
    private ConsumerIdResourceService cIdResourceService;

    @Autowired
    private LogSearchService logSearchService;

    @Autowired
    UserUtils extractUsernameUtils;

    @RequestMapping(value = "/console/message")
    public ModelAndView message() {

        return new ModelAndView("message/index", createViewMap());
    }

    @RequestMapping(value = "/console/message/auth/list", method = RequestMethod.POST)
    @ResponseBody
    public Object messageDefault(@RequestBody MessageQueryDto messageQueryDto) {

        return messageService.getMessageFromTopic(messageQueryDto);
    }

    @RequestMapping(value = "/console/message/timespan", method = RequestMethod.GET)
    @ResponseBody
    public String getMinAndMaxTime(String topic) {

        long millions = messageService.loadTimeOfFirstMessage(topic);
        if (millions < 0) {
            return StringUtils.EMPTY;
        }
        return new SimpleDateFormat(DefaultMessageDao.TIMEFORMAT).format(new Date(millions));
    }

    @RequestMapping(value = "/console/message/auth/content", method = RequestMethod.GET)
    @ResponseBody
    public Message showMessageContent(String topic, String mid)
            throws UnknownHostException {

        return messageService.getMessageContent(topic, mid);
    }

    @RequestMapping(value = "/console/message/consumer/detail", method = RequestMethod.GET)
    @ResponseBody
    public List<SearchResult> showConsumerDetail(String topic, String mid) {
        long messageId = Long.valueOf(mid).longValue();
        List<ConsumerIdResource> consumerIdResources = cIdResourceService.findByTopic(topic);
        List<SearchResult> searchResults = new ArrayList<SearchResult>();
        for (ConsumerIdResource consumerIdResource : consumerIdResources) {
            String consumerId = consumerIdResource.getConsumerId();
            List<SearchResult> tempResults = logSearchService.search(topic, consumerId, messageId);
            if (tempResults == null || tempResults.isEmpty()) {
                searchResults.add(new SearchResult(topic, consumerId, messageId));
            } else {
                searchResults.addAll(tempResults);
            }
        }
        return searchResults;
    }

    @Override
    protected String getMenu() {

        return "message";
    }

}
