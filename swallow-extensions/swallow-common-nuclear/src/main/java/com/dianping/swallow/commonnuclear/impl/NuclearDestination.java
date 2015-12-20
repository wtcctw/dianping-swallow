package com.dianping.swallow.commonnuclear.impl;

import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.message.Destination;

/**
 * @author qi.yin
 *         2015/12/14  下午7:53.
 */
public final class NuclearDestination extends Destination {

    private static final String NAME_PREFIX = "NUCLEARMQ:";

    private NuclearDestination(String name) {
        super(name);
    }

    public static Destination topic(String name) {
        return createDestination(name);
    }

    public static Destination destination(Destination dest) {
        if (!(dest instanceof NuclearDestination)) {
            return createDestination(dest.getName());
        } else {
            return dest;
        }

    }

    private static Destination createDestination(String name) {

        if (validateName(name)) {
            Destination destination = new NuclearDestination(name.substring(NAME_PREFIX.length()));

            return destination;
        }
        throw new IllegalArgumentException("Topic name is illegal,Nuclear Topic name should be start with NUCLEARMQ:");

    }

    private static boolean validateName(String name) {
        if (!StringUtils.isEmpty(name)) {
            if (name.startsWith(NAME_PREFIX)) {
                return true;
            }
        }
        return false;
    }

    public static boolean supportedDestination(Destination dest) {
        if (dest != null) {
            if (dest instanceof NuclearDestination) {
                return true;
            } else {
                return validateName(dest.getName());
            }
        }
        return false;
    }

}
