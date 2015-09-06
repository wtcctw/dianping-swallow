package com.dianping.swallow.consumerserver.util;


import io.netty.channel.Channel;

import com.dianping.swallow.common.internal.consumer.ConsumerInfo;


public class ConsumerUtil {

    public static String getPrettyConsumerInfo(ConsumerInfo consumerInfo, Channel channel) {
        String remoteAddress = (channel != null) ? channel.remoteAddress().toString() : null;
        if (consumerInfo != null) {
            return "[topic:" + consumerInfo.getDest().getName() + ", " + "cid:" + consumerInfo.getConsumerId() + ", type:" + consumerInfo.getConsumerType() + ", remoteAddress:" + remoteAddress + "]";
        }
        return "[consumerInfo:null, remoteAddress:" + remoteAddress + "]";
    }

}
