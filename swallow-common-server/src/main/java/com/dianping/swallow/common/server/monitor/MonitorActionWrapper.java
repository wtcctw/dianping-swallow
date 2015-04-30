package com.dianping.swallow.common.server.monitor;

import com.dianping.swallow.common.internal.action.AbstractSwallowActionWrapper;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.server.monitor.collector.AbstractMonitorDataAction;
import com.dianping.swallow.common.server.monitor.data.TimeException;

/**
 * @author mengwenchao
 *
 * 2015年4月30日 下午2:37:21
 */
public class MonitorActionWrapper extends AbstractSwallowActionWrapper implements SwallowActionWrapper{

	@Override
	public void doAction(SwallowAction action) {
		
		try {
			action.doAction();
		} catch(TimeException e){
			
			String msg = "";
			if(action instanceof AbstractMonitorDataAction){
				AbstractMonitorDataAction ama = (AbstractMonitorDataAction) action;
				msg = ama.toString();
			}
			logger.warn("[doAction]" + msg +"," +  e.getMessage());
		}catch (SwallowException e) {
			logger.error("[doAction]", e);
		}catch(Throwable t){
			logger.error("[doAction]", t);
		}
		
	}

}
