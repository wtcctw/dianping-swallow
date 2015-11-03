package com.dianping.swallow.web.controller.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.dianping.swallow.web.model.Administrator;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.TopicController;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.dao.impl.DefaultConsumerIdResourceDao;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.ApplicationResourceService;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.UserService;

/**
 * @author mingdongli
 *         <p/>
 *         2015年9月1日下午7:04:53
 */
@Component
public class UserUtils {

    private static final String LOGINDELIMITOR = "\\|";

    private static final String ALL = "all";

    private static final String IP = "ip";

    private static final String APPLICATION = "application";

    @Resource(name = "ipResourceService")
    private IpResourceService ipResourceService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "topicResourceService")
    private TopicResourceService topicResourceService;

    @Resource(name = "consumerIdResourceService")
    private ConsumerIdResourceService consumerIdResourceService;

    @Resource(name = "applicationResourceService")
    private ApplicationResourceService applicationResourceService;

    public String getUsername(HttpServletRequest request) {
        String tmpusername = request.getRemoteUser();

        if (tmpusername == null) {
            return "";
        } else {
            String[] userinfo = tmpusername.split(LOGINDELIMITOR);
            return userinfo[0];
        }
    }

    public boolean isAdministrator(String username) {
        return isAdministrator(username, false);
    }

    public boolean isAdministrator(String username, boolean real) {

        Set<String> adminSet = userService.loadCachedAdministratorSet();

        if (!real) {
            return adminSet.contains(username) || adminSet.contains(ALL);
        } else {
            return adminSet.contains(username);
        }
    }

    public List<String> topicNames(String username) {

        List<String> topics;
        Map<String, Set<String>> topicToWhiteList = topicResourceService.loadCachedTopicToAdministrator();
        boolean findAll = isAdministrator(username);

        if (findAll) {
            topics = new ArrayList<String>(topicToWhiteList.keySet());
            if (isAdministrator(username, true)) {
                topics.add(TopicController.DEFAULT);
            }
        } else {
            topics = new ArrayList<String>();
            for (Map.Entry<String, Set<String>> entry : topicToWhiteList.entrySet()) {
                if (entry.getValue().contains(username)) {
                    String topic = entry.getKey();
                    if (!topics.contains(topic)) {
                        topics.add(topic);
                    }
                }
            }
        }

        return topics;
    }

    public List<String> consumerIds(String username) {

        List<String> consumerIds = new ArrayList<String>();

        if (!isAdministrator(username)) {
            List<String> topics = topicNames(username);
            String topicString = StringUtils.join(topics, ",");

            ConsumerIdParam consumerIdParam = new ConsumerIdParam();
            consumerIdParam.setTopic(topicString);
            consumerIdParam.setConsumerId("");
            consumerIdParam.setConsumerIp("");
            consumerIdParam.setLimit(Integer.MAX_VALUE);
            consumerIdParam.setOffset(0);

            Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
            if (pair.getFirst() > 0) {
                for (ConsumerIdResource consumerIdResource : pair.getSecond()) {
                    String id = consumerIdResource.getConsumerId();
                    if (StringUtils.isNotBlank(id) && !consumerIds.contains(id)) {
                        consumerIds.add(id);
                    }
                }
            }
        } else {
            List<ConsumerIdResource> consumerIdResources = consumerIdResourceService
                    .findAll(DefaultConsumerIdResourceDao.CONSUMERID);

            for (ConsumerIdResource consumerIdResource : consumerIdResources) {
                String cid = consumerIdResource.getConsumerId();
                if (!consumerIds.contains(cid)) {
                    consumerIds.add(cid);
                }
            }
        }

        if (isAdministrator(username, true)) {
            consumerIds.remove(TopicController.DEFAULT);
        }

        return consumerIds;

    }

    public List<String> consumerIps(String username) {

        Set<String> consumerIps = new HashSet<String>();

        if (!isAdministrator(username)) {
            List<String> topics = topicNames(username);
            String topicString = StringUtils.join(topics, ",");

            ConsumerIdParam consumerIdParam = new ConsumerIdParam();
            consumerIdParam.setTopic(topicString);
            consumerIdParam.setConsumerId("");
            consumerIdParam.setConsumerIp("");
            consumerIdParam.setLimit(Integer.MAX_VALUE);
            consumerIdParam.setOffset(0);

            Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.findByTopic(consumerIdParam);
            if (pair.getFirst() > 0) {
                for (ConsumerIdResource consumerIdResource : pair.getSecond()) {
                    List<IpInfo> ipInfos = consumerIdResource.getConsumerIpInfos();
                    Set<String> ips = IpInfoUtils.extractIps(ipInfos);
                    if (!ips.isEmpty()) {
                        consumerIps.addAll(ips);
                    }
                }
            }
        } else {
            List<ConsumerIdResource> consumerIdResources = consumerIdResourceService
                    .findAll(DefaultConsumerIdResourceDao.CONSUMERIPS);

            for (ConsumerIdResource consumerIdResource : consumerIdResources) {
                List<IpInfo> ipInfos = consumerIdResource.getConsumerIpInfos();
                Set<String> ips = IpInfoUtils.extractIps(ipInfos);
                if (!ips.isEmpty()) {
                    consumerIps.addAll(ips);
                }
            }
        }

        return new ArrayList<String>(consumerIps);

    }

    public List<String> producerIps(String username) {

        Set<String> producerIp = new HashSet<String>();

        List<TopicResource> topicResources = topicResourceService.findAll();
        Set<String> tmpips;
        if (!isAdministrator(username)) {
            for (TopicResource topicResource : topicResources) {
                String admin = topicResource.getAdministrator();
                if (StringUtils.isNotBlank(admin)) {
                    String[] adminArray = admin.split(",");
                    if (Arrays.asList(adminArray).contains(username)) {
                        List<IpInfo> tmpIpInfos = topicResource.getProducerIpInfos();
                        tmpips = IpInfoUtils.extractIps(tmpIpInfos);
                        if (tmpips != null) {
                            producerIp.addAll(tmpips);
                        }
                    }
                }
            }
        } else {
            for (TopicResource topicResource : topicResources) {
                List<IpInfo> tmpIpInfos = topicResource.getProducerIpInfos();
                tmpips = IpInfoUtils.extractIps(tmpIpInfos);
                if (tmpips != null) {
                    producerIp.addAll(tmpips);
                }
            }
        }

        return new ArrayList<String>(producerIp);
    }

    public List<String> ips(String username) {

        Set<String> ips = new HashSet<String>();

        if (isAdministrator(username)) {
            List<IpResource> ipResources = ipResourceService.findAll(IP);

            for (IpResource ipResource : ipResources) {
                String ip = ipResource.getIp();
                if (StringUtils.isNotBlank(ip) && !ips.contains(ip)) {
                    ips.add(ip);
                }
            }
        } else {
            List<String> producerIps = producerIps(username);
            List<String> consumerIps = consumerIps(username);
            ips.addAll(consumerIps);
            ips.addAll(producerIps);
        }

        return new ArrayList<String>(ips);
    }

    public List<String> allApplications() {

        Set<String> apps = new HashSet<String>();

        List<ApplicationResource> applicationResources = applicationResourceService.findAll(APPLICATION);
        if (applicationResources != null) {
            for (ApplicationResource applicationResource : applicationResources) {
                apps.add(applicationResource.getApplication());
            }
        }

        return new ArrayList<String>(apps);
    }

    public List<String> applications(String username) {
        Set<String> apps = new HashSet<String>();

        if (isAdministrator(username)) {
            return allApplications();
        } else {
            List<String> ips = this.ips(username);
            List<IpResource> ipResources = ipResourceService.findByIps(ips.toArray(new String[ips.size()]));
            for (IpResource ir : ipResources) {
                String app = ir.getApplication();
                if (StringUtils.isNotBlank(app)) {
                    apps.add(app);
                }
            }
        }

        return new ArrayList<String>(apps);
    }

    public List<String> administrator(String username) {

        Set<String> administrators = new HashSet<String>();

        if (!isAdministrator(username)) {
            Pair<Long, List<TopicResource>> pair = topicResourceService.findByAdministrator(0, Integer.MAX_VALUE, username);
            if (pair.getFirst() > 0) {
                List<TopicResource> topicResources = pair.getSecond();
                for (TopicResource tr : topicResources) {
                    String admin = tr.getAdministrator();
                    if (StringUtils.isNotBlank(admin)) {
                        String[] adminArray = admin.split(",");
                        administrators.addAll(Arrays.asList(adminArray));
                    }
                }
            }
        } else {
            List<Administrator> adminList = userService.findAll();

            for (Administrator administrator : adminList) {
                administrators.add(administrator.getName());
            }

            List<TopicResource> topicResources = topicResourceService.findAll();
            for (TopicResource topicResource : topicResources) {
                String whiteListString = topicResource.getAdministrator();
                String[] whiteList = whiteListString.split(",");
                for (String wl : whiteList) {
                    administrators.add(wl);
                }
            }
        }

        return new ArrayList<String>(administrators);
    }

}
