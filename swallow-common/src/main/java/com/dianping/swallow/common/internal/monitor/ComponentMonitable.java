package com.dianping.swallow.common.internal.monitor;

import com.dianping.swallow.common.internal.lifecycle.Namable;

/**
 * @author mengwenchao
 *
 * 2015年6月26日 下午4:31:01
 */
public interface ComponentMonitable extends Namable{

	Object getStatus();
}
