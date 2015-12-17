package com.dianping.swallow.web.task;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.*;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.service.*;
import com.dianping.swallow.web.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2015/12/07  上午11:16.
 */
@Component
public class StatsDataClearupTask extends AbstractJobTask {

    private int saveDays = 60;

    private static final String STATSDATA_SAVE_DAYS_KEY = "swallow.web.statsdata.save.days";//天

    private static final long STATS_DATA_TIMESPAN = AbstractRetriever.getKey(6 * 60 * 60 * 1000);

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

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        try {
            ConfigCache configCache = ConfigCache.getInstance();
            saveDays = configCache.getIntProperty(STATSDATA_SAVE_DAYS_KEY);

            configCache.addChange(new ConfigChange() {
                @Override
                public void onChange(String key, String value) {

                    if (STATSDATA_SAVE_DAYS_KEY.equals(key)) {
                        saveDays = Integer.getInteger(value);
                    }
                }
            });
        } catch (LionException e) {
            logger.error("[doInitialize] lion get " + STATSDATA_SAVE_DAYS_KEY + " error.", e);
        }
    }

    @Scheduled(cron = "0 10 0 ? * *")
    public void doClearupTask() {
        if (!isOpened()) {
            return;
        }

        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearupTask");

        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {

                logger.info("[doClearupTask] executor doClearupTask.");

                long timeMillis = DateUtil.getEndPreNDays(saveDays);
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
        ProducerServerStatsData serverStatsData = pServerStatsDataService.findOldestData();
        long oldestKey = serverStatsData.getTimeKey();
        long currentKey = oldestKey;
        while (currentKey < timeKey) {

            long tempKey = oldestKey + STATS_DATA_TIMESPAN;
            currentKey = tempKey > timeKey ? timeKey : tempKey;
            final long removeKey = currentKey;
            SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpProducerServerStatsData");

            catWrapper.doAction(new SwallowAction() {

                @Override
                public void doAction() throws SwallowException {

                    logger.info("[doClearupTask] executor doClearUp ProducerServerStatsData.");
                    pServerStatsDataService.removeLessThanTimeKey(removeKey);
                }
            });

            sleep(1);
        }

    }

    private void doClearUpProducerTopicStatsData(final long timeKey) {
        ProducerTopicStatsData topicStatsData = pTopicStatsDataService.findOldestData();
        long oldestKey = topicStatsData.getTimeKey();
        long currentKey = oldestKey;
        while (currentKey < timeKey) {

            long tempKey = oldestKey + STATS_DATA_TIMESPAN;
            currentKey = tempKey > timeKey ? timeKey : tempKey;
            final long removeKey = currentKey;
            SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpProducerTopicStatsData");

            catWrapper.doAction(new SwallowAction() {

                @Override
                public void doAction() throws SwallowException {
                    logger.info("[doClearupTask] executor doClearUp ProducerTopicStatsData.");
                    pTopicStatsDataService.removeLessThanTimeKey(removeKey);
                }
            });

            sleep(1);
        }
    }

    private void doClearUpConsumerServerStatsData(final long timeKey) {
        ConsumerServerStatsData serverStatsData = cServerStatsDataService.findOldestData();
        long oldestKey = serverStatsData.getTimeKey();
        long currentKey = oldestKey;

        while (currentKey < timeKey) {

            long tempKey = oldestKey + STATS_DATA_TIMESPAN;
            currentKey = tempKey > timeKey ? timeKey : tempKey;
            final long removeKey = currentKey;
            SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpConsumerTopicStatsData");

            catWrapper.doAction(new SwallowAction() {
                @Override
                public void doAction() throws SwallowException {

                    logger.info("[doClearupTask] executor doClearUp ConsumerServerStatsData.");
                    cServerStatsDataService.removeLessThanTimeKey(removeKey);
                }
            });

            sleep(1);
        }
    }

    private void doClearUpConsumerTopicStatsData(final long timeKey) {
        ConsumerTopicStatsData topicStatsData = cTopicStatsDataService.findOldestData();
        long oldestKey = topicStatsData.getTimeKey();
        long currentKey = oldestKey;

        while (currentKey < timeKey) {

            long tempKey = oldestKey + STATS_DATA_TIMESPAN;
            currentKey = tempKey > timeKey ? timeKey : tempKey;
            final long removeKey = currentKey;
            SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearupTask");

            catWrapper.doAction(new SwallowAction() {
                @Override
                public void doAction() throws SwallowException {

                    logger.info("[doClearupTask] executor doClearUp ConsumerTopicStatsData.");
                    cTopicStatsDataService.removeLessThanTimeKey(removeKey);
                }
            });
            sleep(1);
        }
    }

    private void doClearUpConsumerIdStatsData(final long timeKey) {
        ConsumerIdStatsData consumerIdStatsData = cIdStatsDataService.findOldestData();
        long oldestKey = consumerIdStatsData.getTimeKey();
        long currentKey = oldestKey;

        while (currentKey < timeKey) {

            long tempKey = oldestKey + STATS_DATA_TIMESPAN;
            currentKey = tempKey > timeKey ? timeKey : tempKey;
            final long removeKey = currentKey;

            SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearUpConsumerIdStatsData");

            catWrapper.doAction(new SwallowAction() {
                @Override
                public void doAction() throws SwallowException {

                    logger.info("[doClearupTask] executor doClearUp ConusmerIdStatsData.");
                    cIdStatsDataService.removeLessThanTimeKey(removeKey);
                }
            });
            sleep(1);
        }
    }

    private void sleep(int minutes) {
        try {
            TimeUnit.MINUTES.sleep(minutes);
        } catch (InterruptedException e) {
            logger.info("[sleep] interrupted.");
        }
    }

    public static void main(String[] args) {
        StatsDataClearupTask task = new StatsDataClearupTask();
        task.doClearUpConsumerIdStatsData(288190000);
    }
}
