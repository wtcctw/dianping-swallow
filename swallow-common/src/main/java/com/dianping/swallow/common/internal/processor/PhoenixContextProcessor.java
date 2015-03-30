package com.dianping.swallow.common.internal.processor;

import java.util.Map;

import com.dianping.phoenix.environment.PhoenixContext;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午2:57:31
 */
public class PhoenixContextProcessor extends AbstractProcessor{

	@Override
	public void beforeSend(SwallowMessage message) throws SwallowException {
		
		Map<String, String> internalProperties = getCreateInternalProperties(message);
		
        try {
            Class.forName("com.dianping.phoenix.environment.PhoenixContext");
            //requestId和referRequestId
            String requestId = PhoenixContext.getInstance().getRequestId();
            String referRequestId = PhoenixContext.getInstance().getReferRequestId();
            String guid = PhoenixContext.getInstance().getGuid();
            if (requestId != null) {
                internalProperties.put(PhoenixContext.REQUEST_ID, requestId);
            }
            if (referRequestId != null) {
                internalProperties.put(PhoenixContext.REFER_REQUEST_ID, referRequestId);
            }
            if (requestId != null) {
                internalProperties.put(PhoenixContext.GUID, guid);
            }
        } catch (ClassNotFoundException e1) {
            logger.debug("Class com.dianping.phoenix.environment.PhoenixContext not found, phoenix env setting is skiped.");
        }
		
	}


	@Override
	public void beforeOnMessage(SwallowMessage message) throws SwallowException {
		Map<String, String> internalProperties = message.getInternalProperties();
        if (internalProperties != null) {
            try {
                Class.forName("com.dianping.phoenix.environment.PhoenixContext");
                String requestId = internalProperties.get(PhoenixContext.REQUEST_ID);
                String referRequestId = internalProperties.get(PhoenixContext.REFER_REQUEST_ID);
                String guid = internalProperties.get(PhoenixContext.GUID);
                if (requestId != null) {
                    PhoenixContext.getInstance().setRequestId(requestId);
                }
                if (referRequestId != null) {
                    PhoenixContext.getInstance().setReferRequestId(referRequestId);
                }
                if (guid != null) {
                    PhoenixContext.getInstance().setGuid(guid);
                }
            } catch (ClassNotFoundException e1) {
            	if(logger.isInfoEnabled()){
            		logger.info("Class com.dianping.phoenix.environment.PhoenixContext not found, phoenix env setting is skiped.");
            	}
            }
        }
	}

	@Override
	public void afterOnMessage(SwallowMessage message) throws SwallowException {
        try {
            Class.forName("com.dianping.phoenix.environment.PhoenixContext");
            PhoenixContext.getInstance().clear();
        } catch (Exception e1) {
        	if(logger.isInfoEnabled()){
        		logger.info("Class com.dianping.phoenix.environment.PhoenixContext not found, phoenix env setting is skiped.", e1);
        	}
        }
	}

}
