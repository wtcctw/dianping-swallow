package com.dianping.swallow.web.util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author mingdongli
 *
 * 2015年7月6日下午1:33:13
 */
public class MapUtils {
	
	 public static <K, V extends Comparable<V>> Map<K, V> 
	    sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator = 
	             new Comparator<K>() {
	      public int compare(K k1, K k2) {
	        int compare = 
	              map.get(k2).compareTo(map.get(k1));
	        if (compare == 0) 
	          return 1;
	        else 
	          return compare;
	      }
	    };
	 
	    Map<K, V> sortedByValues = 
	      new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	  }

}
