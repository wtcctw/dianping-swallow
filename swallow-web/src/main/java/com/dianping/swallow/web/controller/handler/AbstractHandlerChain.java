package com.dianping.swallow.web.controller.handler;

import com.dianping.swallow.web.controller.handler.data.Treatable;
import com.dianping.swallow.web.controller.handler.result.Result;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mingdongli
 *         15/10/23 下午3:41
 */
public abstract class AbstractHandlerChain<T extends Treatable, R extends Result> implements HandlerChain<T, R> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected List<Handler> handlers = new LinkedList<Handler>();

    public AbstractHandlerChain(Handler ... handlers){
        for(Handler handler : handlers){
            this.handlers.add(handler);
        }
    }

    public void addHandler(Handler handler){
        handlers.add(handler);
    }

    public void removeHandler(Handler handler){
        handlers.remove(handler);
    }

}
