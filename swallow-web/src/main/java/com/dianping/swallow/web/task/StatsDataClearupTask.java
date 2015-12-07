package com.dianping.swallow.web.task;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.service.*;
import com.dianping.swallow.web.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qi.yin
 *         2015/12/07  上午11:16.
 */
@Component
public class StatsDataClearupTask extends AbstractJobTask {

    private static final int SAVE_MONTH_COUNT = 4;

    @Autowired
    private ProducerServerStatsDataService pServerStatsDataService;
    @Autowired
    private ProducerTopicStatsDataService pTopicStatsDataService;
    @Autowired
    private ConsumerServerStatsDataService cServerStatsDataService;
    @Autowired
    private ConsumerTopicStatsDataService cTopicStatsDataService;
    @Autowired
    private ConsumerIdStatsDataService cIdStatsDataService;

    public void doClearupTask() {
        if (!isOpened()) {
            return;
        }

        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearupTask");
        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {

                logger.info("[doClearupTask] executor doClearupTask.");
                long timeMillis = DateUtil.getEndPreNMonth(SAVE_MONTH_COUNT);
                long timeKey = AbstractRetriever.getKey(timeMillis);

                doClearUpProducerServerStatsData(timeKey);
                doClearUpProducerTopicStatsData(timeKey);
                doClearUpConsumerServerStatsData(timeKey);
                doClearUpConsumerTopicStatsData(timeKey);
                doClearUpConsumerIdStatsData(timeKey);

            }
        });
    }

    private void doClearUpProducerServerStatsData(final long timeKey) {
        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpProducerServerStatsData");
        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {
                logger.info("[doClearupTask] executor doClearUp ProducerServerStatsData.");
                pServerStatsDataService.removeLessThanTimeKey(timeKey);
            }
        });

    }

    private void doClearUpProducerTopicStatsData(final long timeKey) {
        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpProducerTopicStatsData");
        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {
                logger.info("[doClearupTask] executor doClearUp ProducerTopicStatsData.");
                pTopicStatsDataService.removeLessThanTimeKey(timeKey);
            }
        });
    }

    private void doClearUpConsumerServerStatsData(final long timeKey) {
        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpConsumerTopicStatsData");
        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {
                logger.info("[doClearupTask] executor doClearUp ConsumerServerStatsData.");
                cServerStatsDataService.removeLessThanTimeKey(timeKey);
            }
        });
    }

    private void doClearUpConsumerTopicStatsData(final long timeKey) {
        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearupTask");
        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {
                logger.info("[doClearupTask] executor doClearUp ConsumerTopicStatsData.");
                cTopicStatsDataService.removeLessThanTimeKey(timeKey);
            }
        });
    }

    private void doClearUpConsumerIdStatsData(final long timeKey) {
        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpConsumerIdStatsData");
        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {
                logger.info("[doClearupTask] executor doClearUp ConusmerIdStatsData.");
                cIdStatsDataService.removeLessThanTimeKey(timeKey);
            }
        });
    }
}
