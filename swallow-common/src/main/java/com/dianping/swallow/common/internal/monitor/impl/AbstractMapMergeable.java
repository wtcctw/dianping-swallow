package com.dianping.swallow.common.internal.monitor.impl;

import com.dianping.swallow.common.internal.monitor.MapMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 15/11/17  上午10:09.
 */
public abstract class AbstractMapMergeable<K, V extends Mergeable> implements MapMergeable<K, V>{

    protected NavigableMap<K, V> toMerge = new ConcurrentSkipListMap<K, V>();

    public NavigableMap<K, V> getToMerge() {
        return toMerge;
    }

    public void setToMerge(NavigableMap<K, V> toMerge) {
        this.toMerge = toMerge;
    }

}
