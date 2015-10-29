package com.dianping.swallow.web.controller.listener;

import com.dianping.swallow.web.model.resource.BaseResource;

/**
 * @author qi.yin
 *         2015/10/29  下午7:04.
 */
public interface ResourceObserver {

    void doRegister(ResourceListener listener);

    void doUpdateNotify(BaseResource resource);

    void doDeleteNotify(BaseResource resource);
}
