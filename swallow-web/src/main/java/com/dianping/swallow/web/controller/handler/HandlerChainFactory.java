package com.dianping.swallow.web.controller.handler;

import com.dianping.swallow.web.controller.handler.config.ConfigureHandlerChain;
import com.dianping.swallow.web.controller.handler.lion.LionHandlerChain;

import org.springframework.stereotype.Component;

/**
 * @author mingdongli
 *         15/10/26 上午9:41
 */
@Component
public class HandlerChainFactory {

    @SuppressWarnings("unchecked")
	public LionHandlerChain createLionHandlerChain() {
        return new LionHandlerChain();
    }

    public ConfigureHandlerChain createConfigureHandlerChain() {
        return new ConfigureHandlerChain();
    }

}
