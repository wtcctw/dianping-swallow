package com.dianping.swallow.producer.impl.internal;

import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import com.dianping.pigeon.remoting.invoker.route.balance.RandomLoadBalance;
import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.*;
import com.dianping.swallow.common.internal.config.impl.DefaultDynamicConfig;
import com.dianping.swallow.common.internal.config.impl.SwallowClientConfigImpl;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.message.Destination;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qi.yin
 *         2016/01/19  下午4:32.
 */
public class SwallowPigeonLoadBalance extends RandomLoadBalance {

    private SwallowClientConfig clientConfig = new SwallowClientConfigImpl();

    public SwallowPigeonLoadBalance() {
    }

    @Override
    public Client doSelect(List<Client> clients, InvokerConfig<?> invokerConfig, InvocationRequest request, int[] weights) {

        Object[] parameters = request.getParameters();
        List<Client> selectedClients = clients;

        if (clients != null && !clients.isEmpty()) {

            if (parameters != null && parameters.length > 0) {
                for (Object parameter : parameters) {
                    if (parameter instanceof PktMessage) {

                        PktMessage pktMessage = (PktMessage) parameter;
                        Destination destination = pktMessage.getDestination();
                        selectedClients = selectClients(clients, destination.getName());
                        break;
                    }

                }
            }
        }
        return super.doSelect(selectedClients, invokerConfig, request, weights);
    }

    private List<Client> selectClients(List<Client> clients, String topicName) {

        TopicConfig topicCfg = getTopicCfgByKeyOrDefault(topicName);
        if (!isTopicCfgValid(topicCfg)) {
            return clients;
        }

        GroupConfig groupCfg = getGroupCfgByKeyOrDefault(topicCfg.getGroup());
        if (!isGroupCfgValid(groupCfg)) {
            return clients;
        }

        List<Client> copyClients = new ArrayList<Client>(clients.size());
        copyClients.addAll(clients);
        String[] producerIps = groupCfg.getProducerIps();

        if (copyClients != null) {
            Iterator<Client> it = copyClients.iterator();

            while (it.hasNext()) {

                Client client = it.next();
                boolean isExist = false;
                for (String ip : producerIps) {
                    if (client.getHost().equals(ip)) {
                        isExist = true;
                        break;
                    }
                }

                if (!isExist) {
                    it.remove();
                }
            }

            if (!copyClients.isEmpty()) {
                return copyClients;
            }

        }

        return clients;
    }

    private TopicConfig getTopicCfgByKeyOrDefault(String topicName) {
        TopicConfig topicCfg = null;

        if (!StringUtils.isEmpty(topicName)) {
            topicCfg = clientConfig.getTopicCfg(topicName);
        }

        if (!isTopicCfgValid(topicCfg)) {
            topicCfg = clientConfig.defaultTopicCfg();
        }
        return topicCfg;
    }

    private GroupConfig getGroupCfgByKeyOrDefault(String groupName) {
        GroupConfig groupCfg = null;

        if (!StringUtils.isEmpty(groupName)) {
            groupCfg = clientConfig.getGroupCfg(groupName);
        }

        if (!isGroupCfgValid(groupCfg)) {
            groupCfg = clientConfig.defaultGroupCfg();
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

        if (groupCfg == null || groupCfg.getProducerIps() == null) {

            return false;
        }
        return true;
    }

}
