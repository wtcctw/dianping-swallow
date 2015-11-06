package com.dianping.swallow.common.internal.monitor.impl;

import com.dianping.swallow.common.internal.monitor.MapMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;

import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 15/11/5  下午6:20.
 */
public class MapMergeableImpl<K, V> implements MapMergeable<K, V> {

    private NavigableMap<K, V> toMerge = new ConcurrentSkipListMap<K, V>();

    @Override
    public void merge(Map<K, V> fromMerge) {
        if (fromMerge == null) {
            return;
        }
        for (Map.Entry<K, V> entry : fromMerge.entrySet()) {
            K fromKey = entry.getKey();
            V fromValue = entry.getValue();
            V toValue = toMerge.get(fromKey);

            if (toValue == null) {
                if (fromValue instanceof Mergeable || fromValue instanceof Long) {
                    toMerge.put(fromKey, fromValue);
                }else {
                    throw new IllegalArgumentException("Unsupport value type");
                }
            } else {
                if (toValue instanceof Mergeable) {
                    ((Mergeable) toValue).merge((Mergeable) fromValue);
                    toMerge.put(fromKey, toValue);
                } else if (toValue instanceof Long) {
                    Long fromLongValue = (Long) fromValue;
                    Long toLongValue = (Long) toValue;
                    Long newValue = fromLongValue + toLongValue;
                    toMerge.put(fromKey, (V) newValue);
                } else {
                    throw new IllegalArgumentException("Unsupport value type");
                }
            }
        }

    }

    @Override
    public void merge(Mergeable fromMerge) {
        throw new UnsupportedOperationException("Not support method");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        MapMergeableImpl mapMergeableImpl = (MapMergeableImpl) super.clone();
        for (Map.Entry<K, V> entry : this.toMerge.entrySet()) {

            K key = entry.getKey();
            V value = entry.getValue();
            mapMergeableImpl.toMerge.put(key, value);
        }

        return mapMergeableImpl;
    }

    public NavigableMap<K, V> getToMerge() {
        return toMerge;
    }

    public void setToMerge(NavigableMap<K, V> toMerge) {
        this.toMerge = toMerge;
    }
}
