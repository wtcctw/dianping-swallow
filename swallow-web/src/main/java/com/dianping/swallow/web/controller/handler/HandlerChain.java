package com.dianping.swallow.web.controller.handler;

import com.dianping.swallow.web.controller.handler.data.Treatable;
import com.dianping.swallow.web.controller.handler.result.Result;

/**
 * @author mingdongli
 *         15/10/23 下午3:23
 */
public interface HandlerChain<T extends Treatable, R extends Result> extends Handler<T, R>{

     void addHandler(Handler<T, R> handler);

     void removeHandler(Handler<T, R> handler);
}
