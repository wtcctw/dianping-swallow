package com.dianping.swallow.common.internal.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qi.yin
 *         2016/02/17  上午10:43.
 */
public class AtomicPositiveIntegerTest {

    @Test
    public void getAndIncrementTest() {
        AtomicPositiveInteger value = new AtomicPositiveInteger(Integer.MAX_VALUE);

        value.getAndIncrement();
        Assert.assertTrue(value.get() == 0);

        value.getAndIncrement();
        Assert.assertTrue(value.get() == 1);
    }

}
