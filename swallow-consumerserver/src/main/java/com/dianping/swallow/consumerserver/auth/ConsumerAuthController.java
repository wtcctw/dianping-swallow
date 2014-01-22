package com.dianping.swallow.consumerserver.auth;

import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

/**
 * 消费者权限控制
 * 
 */
public interface ConsumerAuthController {

    /**
     * 判断该消费者是否合法（如果临时限制了，则不能让其消费消息）
     */
    boolean isValid(ConsumerInfo consumerInfo, String ip);

}
