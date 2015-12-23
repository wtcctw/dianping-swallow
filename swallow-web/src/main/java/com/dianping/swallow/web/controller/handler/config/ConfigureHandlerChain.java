package com.dianping.swallow.web.controller.handler.config;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.AbstractHandlerChain;
import com.dianping.swallow.web.controller.handler.Handler;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *         15/10/23 下午2:42
 */
@SuppressWarnings("rawtypes")
public class ConfigureHandlerChain extends AbstractHandlerChain<TopicApplyDto, LionConfigureResult> {

    public ConfigureHandlerChain(Handler... handlers) {
        super(handlers);
    }

    @SuppressWarnings("unchecked")
	@Override
    public ResponseStatus handle(TopicApplyDto value, LionConfigureResult result) {

        ResponseStatus status;
        for (Handler handler : handlers) {
            status = (ResponseStatus) handler.handle(value, result);
            if (!ResponseStatus.SUCCESS.equals(status)) {
                return status;
            }
        }
        return ResponseStatus.SUCCESS;
    }

}