package com.dianping.swallow.common.internal.monitor.impl;

import com.dianping.swallow.common.internal.monitor.MapMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;

import java.util.Map;
import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 15/11/5  下午6:20.
 */
public class MapMergeableImpl<K, V extends Mergeable> extends AbstractMapMergeable<K, V> implements MapMergeable<K, V> {

    @SuppressWarnings("unchecked")
    @Override
    public void merge(NavigableMap<K, V> fromMerge) {
        if (fromMerge == null || fromMerge.isEmpty()) {
            return;
        }
        for (Map.Entry<K, V> entry : fromMerge.entrySet()) {
            K fromKey = entry.getKey();
            V fromValue = entry.getValue();

            V toValue = MapUtil.getOrCreate(toMerge, fromKey, (Class<? extends V>) fromValue.getClass());
            toValue.merge(fromValue);
        }
    }
}
