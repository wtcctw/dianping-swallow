package com.dianping.swallow.web.controller.handler;

import com.dianping.swallow.web.controller.handler.data.Treatable;
import com.dianping.swallow.web.controller.handler.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mingdongli
 *         15/10/23 下午1:47
 */
public abstract class AbstractHandler<T extends Treatable, R extends Result> implements Handler<T, R>{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

}
