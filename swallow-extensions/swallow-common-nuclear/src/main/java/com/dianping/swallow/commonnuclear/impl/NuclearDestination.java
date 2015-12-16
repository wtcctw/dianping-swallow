package com.dianping.swallow.commonnuclear.impl;

import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.message.Destination;

/**
 * @author qi.yin
 *         2015/12/14  下午7:53.
 */
public class NuclearDestination extends Destination {

    private static final String DESTINATION_START_PREFIX = "NUCLEARMQ:";

    private NuclearDestination(String name) {
        super(name);
    }

    public static Destination topic(String name) {
        if (!StringUtils.isEmpty(name)) {
            if (name.startsWith(DESTINATION_START_PREFIX)) {
                Destination destination = new NuclearDestination(name.substring(DESTINATION_START_PREFIX.length()));
                return destination;
            }
        }
        throw new IllegalArgumentException("Topic name is illegal");
    }

    public static String getPrefix() {
        return DESTINATION_START_PREFIX;
    }

}
