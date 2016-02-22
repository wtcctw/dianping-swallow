package com.dianping.swallow.producer.impl.internal;

import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.route.balance.RandomLoadBalance;
import com.dianping.swallow.common.internal.config.*;
import com.dianping.swallow.common.internal.config.impl.SwallowClientConfigImpl;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.util.AtomicPositiveInteger;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.message.Destination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qi.yin
 *         2016/01/19  下午4:32.
 */
public class SwallowPigeonLoadBalance extends RandomLoadBalance {

    private static final Logger logger = LogManager.getLogger(SwallowPigeonLoadBalance.class);

    private SwallowClientConfig clientConfig = new SwallowClientConfigImpl();

    private ConcurrentHashMap<String, AtomicPositiveInteger> selecteds = new ConcurrentHashMap<String, AtomicPositiveInteger>();

    public SwallowPigeonLoadBalance() {
    }

    @Override
    public Client doSelect(List<Client> clients, InvokerConfig<?> invokerConfig, InvocationRequest request, int[] weights) {

        Object[] parameters = request.getParameters();

        Client client = null;
        if (clients != null && !clients.isEmpty()) {
            if (parameters != null && parameters.length > 0) {
                for (Object parameter : parameters) {
                    if (parameter instanceof PktMessage) {

                        PktMessage pktMessage = (PktMessage) parameter;
                        Destination destination = pktMessage.getDestination();
                        client = selectClient(clients, destination.getName());
                        break;
                    }
                }
            }
        }

        if (client == null) {
            return super.doSelect(clients, invokerConfig, request, weights);
        }
        return client;
    }

    public Client selectClient(List<Client> clients, String topicName) {

        TopicConfig topicCfg = getTopicCfgByKeyOrDefault(topicName);
        if (!isTopicCfgValid(topicCfg)) {
            logger.error("[selectClient] cannot find valid default topic config, topicName: {}, defaultTopic: default , defaultTopicConfig: {}.", topicName, topicCfg);
            return null;
        }

        GroupConfig groupCfg = getGroupCfg(topicCfg.getGroup());
        if (!isGroupCfgValid(groupCfg)) {
            logger.error("[selectClient] cannot find valid group config, group: {}, groupCfg: {}.", topicCfg.getGroup(), groupCfg);
            return null;
        }

        return selectClient0(clients, topicCfg.getGroup(), groupCfg.getProducerIps());
    }


    public Client selectClient0(List<Client> clients, String group, String[] producerIps) {
        Client selectedClient = null;
        int count = 0;

        if (!selecteds.containsKey(group)) {
            selecteds.putIfAbsent(group, new AtomicPositiveInteger(0));
        }

        while (selectedClient == null && count < producerIps.length) {

            AtomicPositiveInteger currentSelected = selecteds.get(group);
            String selectedIp = producerIps[currentSelected.getAndIncrement() % producerIps.length];

            for (Client client : clients) {

                if (client.getHost().equals(selectedIp)) {
                    selectedClient = client;
                    break;
                }
            }
            count++;
        }

        if (selectedClient == null) {
            selectedClient = clients.get(random.nextInt(clients.size()));
            logger.warn("[selectClient0] cannot select client, then random selected all clients, producerIps: {}.", (Object[]) producerIps);
        }

        return selectedClient;
    }


    private TopicConfig getTopicCfgByKeyOrDefault(String topicName) {
        TopicConfig topicCfg = null;

        if (!StringUtils.isEmpty(topicName)) {
            topicCfg = clientConfig.getTopicConfig(topicName);
        }

        if (!isTopicCfgValid(topicCfg)) {
            topicCfg = clientConfig.defaultTopicConfig();
        }
        return topicCfg;
    }

    private GroupConfig getGroupCfg(String groupName) {
        GroupConfig groupCfg = null;

        if (!StringUtils.isEmpty(groupName)) {
            groupCfg = clientConfig.getGroupConfig(groupName);
        }
        return groupCfg;
    }


    private boolean isTopicCfgValid(TopicConfig topicCfg) {

        if (topicCfg == null || StringUtils.isEmpty(topicCfg.getGroup())) {

            return false;
        }
        return true;
    }

    private boolean isGroupCfgValid(GroupConfig groupCfg) {

        if (groupCfg == null || groupCfg.getProducerIps() == null
                || groupCfg.getProducerIps().length == 0) {

            return false;
        }
        return true;
    }

}