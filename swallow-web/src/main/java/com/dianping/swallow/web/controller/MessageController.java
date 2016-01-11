package com.dianping.swallow.web.controller;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.controller.dto.MessageQueryDto;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.dao.impl.DefaultMessageDao;
import com.dianping.swallow.web.model.Message;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.LogSearchService;
import com.dianping.swallow.web.service.LogSearchService.SearchResult;
import com.dianping.swallow.web.service.MessageService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author mingdongli
 *         <p/>
 *         2015年4月22日 上午12:04:03
 */
@Controller
public class MessageController extends AbstractMenuController {

    private static final String FACTORY_NAME = "SearchTask";

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

        if (consumerIdResources != null && !consumerIdResources.isEmpty()) {

            SearchTask searchTask = new SearchTask(consumerIdResources.size());

            for (ConsumerIdResource consumerIdResource : consumerIdResources) {
                String consumerId = consumerIdResource.getConsumerId();
                searchTask.submit(new SearchParam(topic, consumerId, messageId), searchResults);
            }
            searchTask.await();
        }
        return searchResults;
    }

    @Override
    protected String getMenu() {

        return "message";
    }

    private class SearchTask {
        private int poolSize = 1;

        private static final int MAX_WAIT_TIME = 30;

        private ExecutorService executorService = null;

        public SearchTask(int size) {
            if (size < 3) {
                poolSize = 1;
            } else if (size < 20) {
                poolSize = size / 3;
            } else {
                poolSize = CommonUtils.DEFAULT_CPU_COUNT * 2;
            }
            executorService = Executors.newFixedThreadPool(poolSize,
                    ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));
        }

        public void submit(final SearchParam searchParam, final List<SearchResult> searchResults) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    searchLog(searchParam, searchResults);
                }
            });
        }

        private void searchLog(SearchParam searchParam, List<SearchResult> searchResults) {
            List<SearchResult> tempResults = logSearchService.search(searchParam.getTopic(), searchParam.getCid(),
                    searchParam.getMid());
            synchronized (searchResults) {
                if (tempResults == null || tempResults.isEmpty()) {
                    searchResults.add(new SearchResult(searchParam.getTopic(), searchParam.getCid(),
                            searchParam.getMid()));
                } else {
                    searchResults.addAll(tempResults);
                }
            }
        }

        public void await() {

            executorService.shutdown();
            try {
                executorService.awaitTermination(MAX_WAIT_TIME, TimeUnit.SECONDS);
                executorService.shutdownNow();
                logger.info("[await] SearchTask is over.");
            } catch (InterruptedException e) {
                logger.info("[await] SearchTask InterruptedException.", e);
            }
        }

    }

    private static class SearchParam {
        private String topic;
        private String cid;
        private long mid;

        public SearchParam(String topic, String cid, long mid) {
            this.topic = topic;
            this.cid = cid;
            this.mid = mid;
        }

        public String getTopic() {
            return topic;
        }

        public String getCid() {
            return cid;
        }

        public long getMid() {
            return mid;
        }

    }

}
