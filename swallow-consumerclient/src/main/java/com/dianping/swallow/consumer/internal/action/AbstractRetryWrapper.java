package com.dianping.swallow.consumer.internal.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.threadfactory.PullStrategy;
import com.dianping.swallow.common.internal.util.CatUtil;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午4:27:57
 */
public abstract class AbstractRetryWrapper implements SwallowCatActionWrapper{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

	private int totalRetryCount;

	private PullStrategy pullStrategy;
	
	public AbstractRetryWrapper(PullStrategy pullStrategy, int totalRetryCount){
		
		this.totalRetryCount = totalRetryCount;
		this.pullStrategy = pullStrategy;
	}
	

	@Override
	public void doAction(Transaction transaction, SwallowAction action) {
		
        int retryCount = 0;
        boolean success = false;
        while (!success && retryCount <= totalRetryCount) {
            try {
            	action.doAction();
                success = true;
            } catch (Throwable e) {
            	
                CatUtil.logException(e);
            	
            	Class<?> exceptionClass = getExceptionRetryClass();
            	if(exceptionClass.isInstance(e)){
            		
                    retryCount++;
                    transaction.addData("retry", retryCount);
                    
                    if (retryCount <= totalRetryCount) {
                        logger.error("BackoutMessageException occur on onMessage(), onMessage() will be retryed soon [retryCount=" + retryCount + "]. ", e);
                        pullStrategy.fail(true);
                    } else {
                        transaction.setStatus(e);
                        logger.error("BackoutMessageException occur on onMessage(), onMessage() failed.", e);
                    }
            	}else{
            		transaction.setStatus(e);
            		break;
            	}
            } 
        }
	}

	protected abstract Class<?> getExceptionRetryClass();
}
