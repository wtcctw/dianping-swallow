package com.dianping.swallow.common.internal.config;

import java.util.Map;

public interface DynamicConfig {

   String get(String key);
   
   Map<String, String> getProperties(String prefix);

   void addConfigChangeListener(ConfigChangeListener listener);
   
   void removeConfigChangeListener(ConfigChangeListener listener);
}
