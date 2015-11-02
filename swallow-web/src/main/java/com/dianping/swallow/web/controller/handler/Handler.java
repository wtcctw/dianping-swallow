package com.dianping.swallow.web.controller.handler;

import com.dianping.swallow.web.controller.handler.data.Treatable;
import com.dianping.swallow.web.controller.handler.result.Result;

/**
 * @author mingdongli
 *         15/10/23 上午11:37
 */
public interface Handler<T extends Treatable, R extends Result> {

    Object handle(T value, R result);
}
