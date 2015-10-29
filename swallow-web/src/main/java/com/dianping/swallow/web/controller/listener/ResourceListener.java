package com.dianping.swallow.web.controller.listener;

import com.dianping.swallow.web.model.resource.BaseResource;

/**
 * @author qi.yin
 *         2015/10/29  下午7:01.
 */
public interface ResourceListener {

    void doUpdateNotify(BaseResource resource);

    void doDeleteNotify(BaseResource resource);
}
