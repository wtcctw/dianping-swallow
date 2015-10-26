package com.dianping.swallow.web.controller.handler.config;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.AbstractHandler;
import com.dianping.swallow.web.controller.handler.Handler;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.util.ResponseStatus;
import org.springframework.stereotype.Component;

/**
 * @author mingdongli
 *         15/10/23 下午2:37
 */
@Component
public class QuoteHandler extends AbstractHandler<TopicApplyDto,LionConfigureResult> implements Handler<TopicApplyDto,LionConfigureResult> {

    @Override
    public Object handle(TopicApplyDto value, LionConfigureResult result) {

        float amount = value.getAmount();
        int size = value.getSize();
        int size4SevenDay = (int) (amount * size * 7 * 10);

        // size4sevenday取500的倍数
        int mod = size4SevenDay % 500;
        size4SevenDay = (mod != 0) ? (size4SevenDay / 500 + 1) * 500 : size4SevenDay;
        if(result == null){
            result = new LionConfigureResult();
        }
        result.setSize4SevenDay(size4SevenDay);

        return ResponseStatus.SUCCESS;
    }
}
