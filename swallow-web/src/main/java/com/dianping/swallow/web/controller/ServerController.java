package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseQueryDto;
import com.dianping.swallow.web.dao.impl.DefaultMongoDao;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.*;
import com.dianping.swallow.web.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * @author mingdongli
 *         <p/>
 *         2015年9月13日下午2:46:30
 */
@Controller
public class ServerController extends AbstractSidebarBasedController {

    private static final String DEFAULT = "default";

    @Resource(name = "producerServerResourceService")
    private ProducerServerResourceService producerServerResourceService;

    @Resource(name = "consumerServerResourceService")
    private ConsumerServerResourceService consumerServerResourceService;

    @Resource(name = "mongoResourceService")
    private MongoResourceService mongoResourceService;

    @Resource(name = "kafkaServerResourceService")
    private KafkaServerResourceService kafkaServerResourceService;

    @Resource(name = "groupResourceService")
    private GroupResourceService groupResourceService;

    @Resource(name = "ipCollectorService")
    private IPCollectorService ipCollectorService;

    @Resource(name = "topicResourceService")
    private TopicResourceService topicResourceService;

    @Autowired
    ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

    @RequestMapping(value = "/console/server")
    public ModelAndView serverSetting() {

        subSide = "producer";
        return new ModelAndView("server/producer", createViewMap());
    }

    @RequestMapping(value = "/console/server/producer")
    public ModelAndView producerServerSetting() {

        subSide = "producer";
        return new ModelAndView("server/producer", createViewMap());
    }

    @RequestMapping(value = "/console/server/producer/list", method = RequestMethod.POST)
    @ResponseBody
    public Object producerServerList(@RequestBody BaseQueryDto baseDto) {

        int offset = baseDto.getOffset();
        int limit = baseDto.getLimit();
        return producerServerResourceService.findProducerServerResourcePage(offset, limit);

    }

    @RequestMapping(value = "/console/server/producer/create", method = RequestMethod.POST)
    @ResponseBody
    public Boolean producerServerResourceCreate(@RequestBody ProducerServerResource producerServerResource) {

        return producerServerResourceService.update(producerServerResource);
    }

    @RequestMapping(value = "/console/server/producer/remove", method = RequestMethod.GET)
    @ResponseBody
    public int removeProducerServerResource(@RequestParam(value = "serverId") String serverId) {

        return producerServerResourceService.remove(serverId);
    }

    @RequestMapping(value = "/console/server/producerserverinfo", method = RequestMethod.GET)
    @ResponseBody
    public Object loadProducerSereverIps() {

        List<String> hostsList;
        List<String> ipsList;
        Map<String, String> hostNames = ipCollectorService.getProducerServerIpsMap();

        if (hostNames != null) {
            Set<String> hosts = hostNames.keySet();
            hostsList = new ArrayList<String>(hosts);
            Collection<String> ipCollection = hostNames.values();
            ipsList = new ArrayList<String>(ipCollection);
        } else {
            hostsList = new ArrayList<String>();
            ipsList = new ArrayList<String>();
        }

        return new Pair<List<String>, List<String>>(hostsList, ipsList);
    }

    @RequestMapping(value = "/console/server/producertopics", method = RequestMethod.GET)
    @ResponseBody
    public List<String> loadProducerSereverTopics(@RequestParam(value = "serverId") String serverId) {

        Set<String> topics = consumerDataRetrieverWrapper.getKey(serverId);

        if (topics != null) {
            return new ArrayList<String>(topics);
        } else {
            return new ArrayList<String>();
        }
    }

    @RequestMapping(value = "/console/server/defaultpresource", method = RequestMethod.GET)
    @ResponseBody
    public Object loadDefaultProducerSereverResource() {

        ProducerServerResource producerServerResource = (ProducerServerResource) producerServerResourceService
                .findDefault();

        return producerServerResource;
    }

    @RequestMapping(value = "/console/server/producer/alarm", method = RequestMethod.GET)
    @ResponseBody
    public boolean editProducerAlarmSetting(@RequestParam String ip, @RequestParam boolean alarm) {

        ProducerServerResource producerServerResource = (ProducerServerResource) producerServerResourceService
                .findByIp(ip);
        producerServerResource.setAlarm(alarm);
        return producerServerResourceService.update(producerServerResource);

    }

    @RequestMapping(value = "/console/server/producer/active", method = RequestMethod.GET)
    @ResponseBody
    public boolean editProducerActiveSetting(@RequestParam String ip, @RequestParam boolean active) {

        ProducerServerResource producerServerResource = (ProducerServerResource) producerServerResourceService
                .findByIp(ip);
        producerServerResource.setActive(active);
        return producerServerResourceService.update(producerServerResource);

    }

    /**
     * Consumer server controller
     */

    @RequestMapping(value = "/console/server/consumer")
    public ModelAndView topicSetting() {

        subSide = "consumer";
        return new ModelAndView("server/consumer", createViewMap());
    }

    @RequestMapping(value = "/console/server/consumer/list", method = RequestMethod.POST)
    @ResponseBody
    public Object consumerServerList(@RequestBody BaseQueryDto baseDto) {

        int offset = baseDto.getOffset();
        int limit = baseDto.getLimit();
        return consumerServerResourceService.findConsumerServerResourcePage(offset, limit);

    }

    @RequestMapping(value = "/console/server/consumer/create", method = RequestMethod.POST)
    @ResponseBody
    public Boolean ConsumerServerResourceCreate(@RequestBody ConsumerServerResource consumerServerResource) {

        return consumerServerResourceService.update(consumerServerResource);
    }

    @RequestMapping(value = "/console/server/consumer/remove", method = RequestMethod.GET)
    @ResponseBody
    public int remvoeConsumerserverResource(@RequestParam(value = "serverId") String serverId) {

        return consumerServerResourceService.remove(serverId);
    }

    @RequestMapping(value = "/console/server/consumerserverinfo", method = RequestMethod.GET)
    @ResponseBody
    public Object loadConsumerSereverInfo() {

        Set<String> hostsSet = new HashSet<String>();
        Set<String> ipsSet = new HashSet<String>();

        Map<String, String> master = ipCollectorService.getConsumerServerMasterIpsMap();
        if (master != null) {
            hostsSet.addAll(master.keySet());
            ipsSet.addAll(master.values());
        }

        Map<String, String> slave = ipCollectorService.getConsumerServerSlaveIpsMap();
        if (slave != null) {
            hostsSet.addAll(slave.keySet());
            ipsSet.addAll(slave.values());
        }

        return new Pair<List<String>, List<String>>(new ArrayList<String>(hostsSet), new ArrayList<String>(ipsSet));

    }

    @RequestMapping(value = "/console/server/consumer/get/topics", method = RequestMethod.GET)
    @ResponseBody
    public List<String> loadConsumerSereverTopics(@RequestParam String ip) {

        Set<String> result = new HashSet<String>();
        String consumerServerLionConfig = consumerServerResourceService.loadConsumerServerLionConfig();
        if (consumerServerLionConfig == null) {
            return new ArrayList<String>(result);
        }

        Map<String, Set<String>> topicToConsumerServer = parseServerURIString(consumerServerLionConfig);

        for (Map.Entry<String, Set<String>> entry : topicToConsumerServer.entrySet()) {
            Set<String> servers = entry.getValue();
            String topic = entry.getKey();
            if (servers != null && servers.contains(ip) && !result.contains(topic)) {
                result.add(topic);
            }
        }

        if (result.contains(DEFAULT)) {
            result.remove(DEFAULT);
            Set<String> allTopics = topicResourceService.loadCachedTopicToAdministrator().keySet();
            Set<String> allTopicsClone = new HashSet<String>(allTopics);
            Set<String> excludeTopics = topicToConsumerServer.keySet();
            allTopicsClone.removeAll(excludeTopics);
            result.addAll(allTopicsClone);

        }

        return new ArrayList<String>(result);
    }

    @RequestMapping(value = "/console/server/defaultcresource", method = RequestMethod.GET)
    @ResponseBody
    public Object loadDefaultConsumerSereverResource() {

        ConsumerServerResource consumerServerResource = (ConsumerServerResource) consumerServerResourceService
                .findDefault();

        return consumerServerResource;
    }

    @RequestMapping(value = "/console/server/consumer/alarm", method = RequestMethod.GET)
    @ResponseBody
    public boolean editConsumerAlarmSetting(@RequestParam String ip, @RequestParam boolean alarm) {

        ConsumerServerResource consumerServerResource = (ConsumerServerResource) consumerServerResourceService
                .findByIp(ip);
        consumerServerResource.setAlarm(alarm);
        return consumerServerResourceService.update(consumerServerResource);

    }

    @RequestMapping(value = "/console/server/consumer/active", method = RequestMethod.GET)
    @ResponseBody
    public boolean editConsumerActiveSetting(@RequestParam String ip, @RequestParam boolean active) {

        ConsumerServerResource consumerServerResource = (ConsumerServerResource) consumerServerResourceService
                .findByIp(ip);
        consumerServerResource.setActive(active);
        return consumerServerResourceService.update(consumerServerResource);

    }

    @RequestMapping(value = "/console/server/mongo")
    public ModelAndView mongoServerSetting(HttpServletRequest request, HttpServletResponse response) {

        subSide = "mongo";
        return new ModelAndView("server/mongo", createViewMap());
    }

    @RequestMapping(value = "/console/server/mongo/list", method = RequestMethod.POST)
    @ResponseBody
    public Object mongoServerList(@RequestBody BaseQueryDto dto) {

        int offset = dto.getOffset();
        int limit = dto.getLimit();
        return mongoResourceService.findMongoResourcePage(offset, limit);

    }

    @RequestMapping(value = "/console/server/mongo/create", method = RequestMethod.POST)
    @ResponseBody
    public Boolean mongoResourceCreate(@RequestBody MongoResource mongoResource) {

        return mongoResourceService.update(mongoResource);
    }

    @RequestMapping(value = "/console/server/mongo/remove", method = RequestMethod.GET)
    @ResponseBody
    public int remvoeMongoResource(@RequestParam(value = "catalog") String catalog) {

        return mongoResourceService.remove(catalog);
    }

    @RequestMapping(value = "/console/server/mongoip", method = RequestMethod.GET)
    @ResponseBody
    public Object loadMongoSereverIp() {

        List<String> list = new ArrayList<String>();

        List<MongoResource> mongoResources = mongoResourceService.findAll(DefaultMongoDao.IP);
        for (MongoResource mongoResource : mongoResources) {
            String ip = mongoResource.getIp();
            if (!list.contains(ip)) {
                list.add(ip);
            }
        }

        return list;

    }

    @RequestMapping(value = "/console/server/grouptype", method = RequestMethod.GET)
    @ResponseBody
    public Object loadGroupType() {

        return groupResourceService.findAllGroupName();

    }

    @RequestMapping(value = "/console/server/kafka")
    public ModelAndView kafkaServer() {

        subSide = "kafka";
        return new ModelAndView("server/kafka", createViewMap());
    }

    @RequestMapping(value = "/console/server/kafka/list", method = RequestMethod.POST)
    @ResponseBody
    public Object kafkaServerList(@RequestBody BaseQueryDto baseDto) {

        int offset = baseDto.getOffset();
        int limit = baseDto.getLimit();
        return kafkaServerResourceService.findKafkaServerResourcePage(offset, limit);

    }

    @RequestMapping(value = "/console/server/kafka/create", method = RequestMethod.POST)
    @ResponseBody
    public Boolean kafkaServerResourceCreate(@RequestBody KafkaServerResource kafkaServerResource) {

        return kafkaServerResourceService.update(kafkaServerResource);
    }

    @RequestMapping(value = "/console/server/kafka/remove", method = RequestMethod.GET)
    @ResponseBody
    public int remvoeKafkaServerResource(@RequestParam(value = "serverId") String serverId) {

        return kafkaServerResourceService.remove(serverId);
    }

    @RequestMapping(value = "/console/server/kafkaserverinfo", method = RequestMethod.GET)
    @ResponseBody
    public Object loadKafkaSereverInfo() {

        Set<String> hostsSet = new HashSet<String>();
        Set<String> ipsSet = new HashSet<String>();

        List<KafkaServerResource> kafkaServerResources = kafkaServerResourceService.findAll();

        if(kafkaServerResources != null){
            for(KafkaServerResource kafkaServerResource : kafkaServerResources){
                hostsSet.add(kafkaServerResource.getHostname());
                ipsSet.add(kafkaServerResource.getIp());
            }
        }

        return new Pair<List<String>, List<String>>(new ArrayList<String>(hostsSet), new ArrayList<String>(ipsSet));

    }

    @RequestMapping(value = "/console/server/defaultkresource", method = RequestMethod.GET)
    @ResponseBody
    public Object loadDefaultKafkaSereverResource() {

        KafkaServerResource kafkaServerResource = kafkaServerResourceService.findByIp(DEFAULT);
        return kafkaServerResource;
    }

    @RequestMapping(value = "/console/server/kafka/alarm", method = RequestMethod.GET)
    @ResponseBody
    public boolean editKafkaAlarmSetting(@RequestParam String ip, @RequestParam boolean alarm) {

        KafkaServerResource kafkaServerResource =  kafkaServerResourceService.findByIp(ip);
        kafkaServerResource.setAlarm(alarm);
        return kafkaServerResourceService.update(kafkaServerResource);

    }

    @RequestMapping(value = "/console/server/kafka/active", method = RequestMethod.GET)
    @ResponseBody
    public boolean editKafkaActiveSetting(@RequestParam String ip, @RequestParam boolean active) {

        KafkaServerResource kafkaServerResource =  kafkaServerResourceService.findByIp(ip);
        kafkaServerResource.setAlarm(active);
        return kafkaServerResourceService.update(kafkaServerResource);

    }

    @RequestMapping(value = "/console/server/group")
    public ModelAndView groupSetting(HttpServletRequest request, HttpServletResponse response) {

        subSide = "group";
        return new ModelAndView("server/group", createViewMap());
    }

    @RequestMapping(value = "/console/server/group/list", method = RequestMethod.POST)
    @ResponseBody
    public Object groupList(@RequestBody BaseQueryDto dto) {

        int offset = dto.getOffset();
        int limit = dto.getLimit();
        return groupResourceService.findGroupResourcePage(offset, limit);

    }

    @RequestMapping(value = "/console/server/group/create", method = RequestMethod.POST)
    @ResponseBody
    public Boolean groupResourceCreate(@RequestBody GroupResource groupResource) {

        return groupResourceService.update(groupResource);
    }

    @RequestMapping(value = "/console/server/group/remove", method = RequestMethod.GET)
    @ResponseBody
    public int remvoeGroupResource(@RequestParam(value = "groupName") String groupName) {

        return groupResourceService.remove(groupName);
    }

    public static Map<String, Set<String>> parseServerURIString(String value) {

        Map<String, Set<String>> result = new HashMap<String, Set<String>>();

        for (String topicNamesToURI : value.split("\\s*;\\s*")) {

            if (StringUtils.isEmpty(topicNamesToURI)) {
                continue;
            }

            String[] splits = topicNamesToURI.split("=");
            if (splits.length != 2) {
                continue;
            }
            String consumerServerURI = splits[1].trim();
            String[] ipAddrs = consumerServerURI.split(",");
            Set<String> ips = new HashSet<String>();
            if (ipAddrs.length == 2) {
                for (int i = 0; i < 2; ++i) {
                    String[] ipPort = ipAddrs[i].split(":");
                    if (ipPort.length != 2) {
                        continue;
                    }
                    ips.add(ipPort[0]);
                }
            } else {
                continue;
            }

            String topicNameStr = splits[0].trim();
            result.put(topicNameStr, ips);
        }

        return result;
    }

    @Override
    protected String getMenu() {
        return "server";
    }

    @Override
    protected String getSide() {
        return "serverwarn";
    }

    private String subSide = "producer";

    @Override
    public String getSubSide() {
        return subSide;
    }

}
