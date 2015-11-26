package com.dianping.swallow.common.internal.monitor;

import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 15/11/5  下午6:16.
 */
public interface MapMergeable<K, V extends Mergeable>{

    void merge(NavigableMap<K, V> fromMerge);
}
