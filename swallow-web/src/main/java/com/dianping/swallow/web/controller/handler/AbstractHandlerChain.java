package com.dianping.swallow.web.controller.handler;

import com.dianping.swallow.web.controller.handler.data.Treatable;
import com.dianping.swallow.web.controller.handler.result.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mingdongli
 *         15/10/23 下午3:41
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractHandlerChain<T extends Treatable, R extends Result> implements HandlerChain<T, R> {

    protected final Logger logger = LogManager.getLogger(getClass());

	protected List<Handler<T, R>> handlers = new LinkedList<Handler<T, R>>();

    @SuppressWarnings("unchecked")
	public AbstractHandlerChain(Handler ... handlers){
        for(Handler<T, R> handler : handlers){
            this.handlers.add(handler);
        }
    }

    @SuppressWarnings("unchecked")
	public void addHandler(Handler handler){
        handlers.add(handler);
    }

    public void removeHandler(Handler handler){
        handlers.remove(handler);
    }

}
