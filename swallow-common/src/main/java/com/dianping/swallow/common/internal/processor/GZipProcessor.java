package com.dianping.swallow.common.internal.processor;

import java.io.IOException;
import java.util.Map;

import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.ZipUtil;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午2:35:45
 */
public class GZipProcessor extends AbstractProcessor implements Processor{

	boolean gzipBeforeSend;

	public GZipProcessor() {
		
	}

	public GZipProcessor(boolean gzipBeforeSend) {
		
		this.gzipBeforeSend = gzipBeforeSend;
	}

	@Override
	public void beforeOnMessage(SwallowMessage message) throws SwallowException {
		
        try {
	        Map<String, String> internalProperties = message.getInternalProperties();
	        if (internalProperties != null) {
	            if ("gzip".equals(message.getInternalProperties().get("compress"))) {
						message.setContent(ZipUtil.unzip(message.getContent()));
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void afterOnMessage(SwallowMessage message) {
		
	}

	@Override
	public void beforeSend(SwallowMessage message) {
		
		if(!gzipBeforeSend){
			return;
		}
		Map<String, String> internalProperties = getCreateInternalProperties(message);
        try {
            message.setContent(ZipUtil.zip(message.getContent()));
            internalProperties.put("compress", "gzip");
        } catch (Exception e) {
            logger.warn("Compress message failed.Content=" + message.getContent(), e);
            internalProperties.put("compress", "failed");
        }
	}

}
