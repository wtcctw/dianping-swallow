package com.dianping.swallow.web.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author mingdongli
 * 2015年5月12日 下午2:42:37
 */

@Configuration
@EnableScheduling
public class ScanWriteDaoConfig {
	
	  @Bean
	  public ScanWriteDaoScheduler createScanWriteDaoScheduler() {
	        return new ScanWriteDaoScheduler();
	  }
}
