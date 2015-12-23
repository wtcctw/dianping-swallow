package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.web.controller.handler.AbstractHandlerChain;
import com.dianping.swallow.web.controller.handler.Handler;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *         15/10/23 下午4:32
 */
public class LionHandlerChain extends AbstractHandlerChain<LionEditorEntity, EmptyObject>{

    public LionHandlerChain(Handler<LionEditorEntity, EmptyObject>... handlers) {
        super(handlers);
    }

    @Override
    public ResponseStatus handle(LionEditorEntity value, EmptyObject result) {

        ResponseStatus status;
        for (Handler<LionEditorEntity, EmptyObject> handler : handlers) {
            status = (ResponseStatus) handler.handle(value, result);
            if (!ResponseStatus.SUCCESS.equals(status)) {
                return status;
            }
        }
        return ResponseStatus.SUCCESS;
    }
}
