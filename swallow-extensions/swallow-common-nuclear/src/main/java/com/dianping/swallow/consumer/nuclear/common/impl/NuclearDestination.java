package com.dianping.swallow.consumer.nuclear.common.impl;

import com.dianping.swallow.common.message.Destination;

/**
 * @author qi.yin
 *         2015/12/14  下午7:53.
 */
public final class NuclearDestination extends Destination {

    private NuclearDestination(){

    }

    private NuclearDestination(String name) {
        super(name);
    }

    /***
     * 创建Topic类型的消息目的地<br>
     *
     * @param name Topic名称
     * @return 消息目的地实例
     */
    public static Destination topic(String name) {

        return new NuclearDestination(name);
    }

}
