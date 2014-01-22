package com.dianping.swallow.consumerserver.util;

import org.jboss.netty.channel.Channel;

import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

public class ConsumerUtil {

    public static String getPrettyConsumerInfo(ConsumerInfo consumerInfo, Channel channel) {
        String remoteAddress = (channel != null) ? channel.getRemoteAddress().toString() : null;
        if (consumerInfo != null) {
            return "[topic:" + consumerInfo.getDest().getName() + ", " + "cid:" + consumerInfo.getConsumerId() + ", type:" + consumerInfo.getConsumerType() + ", remoteAddress:" + remoteAddress + "]";
        }
        return "[consumerInfo:null, remoteAddress:" + remoteAddress + "]";
    }

}
