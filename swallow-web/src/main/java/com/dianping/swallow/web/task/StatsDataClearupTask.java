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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qi.yin
 *         2015/12/07  上午11:16.
 */
@Component
public class StatsDataClearupTask extends AbstractTask {

    private volatile AtomicBoolean isTasking = new AtomicBoolean(false);

    private volatile int saveDays = 90;

    private static final String STATSDATA_SAVE_DAYS_KEY = "swallow.web.statsdata.save.days";//天

    private static final long CLEARUP_TIMEUNIT = AbstractRetriever.getKey(20 * 60 * 1000);

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

    private ConfigCache configCache = null;

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        try {
            configCache = ConfigCache.getInstance();
            saveDays = configCache.getIntProperty(STATSDATA_SAVE_DAYS_KEY);

            configCache.addChange(new ConfigChange() {
                @Override
                public void onChange(String key, String value) {

                    if (STATSDATA_SAVE_DAYS_KEY.equals(key)) {
                        saveDays = Integer.parseInt(value);
                        if (logger.isInfoEnabled()) {
                            logger.info("[onChange] " + STATSDATA_SAVE_DAYS_KEY + " newValue:" + saveDays);
                        }
                    }

                }
            });
        } catch (LionException e) {
            logger.error("[doInitialize] lion get " + STATSDATA_SAVE_DAYS_KEY + " error.", e);
        }
    }

    @Scheduled(cron = "0 40 0 ? * *")
    public void doClearupTask() {
        if (!isOpened()) {
            return;
        }

        if (!isTasking.compareAndSet(false, true)) {
            return;
        }

        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doClearupTask");

        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {

                logger.info("[doClearupTask] executor doClearupTask.");

                long timeMillis = DateUtil.getEndPreNDays(saveDays);
                long timeKey = AbstractRetriever.getKey(timeMillis);

                doClearUpStatsData(timeKey, CLEARUP_TIMEUNIT * 6, pServerStatsDataService);
                doClearUpStatsData(timeKey, CLEARUP_TIMEUNIT * 2, pTopicStatsDataService);
                doClearUpStatsData(timeKey, CLEARUP_TIMEUNIT * 6, cServerStatsDataService);
                doClearUpStatsData(timeKey, CLEARUP_TIMEUNIT * 6, cTopicStatsDataService);
                doClearUpStatsData(timeKey, CLEARUP_TIMEUNIT, cIdStatsDataService);
            }
        });

        isTasking.compareAndSet(true, false);
    }

    private void doClearUpStatsData(long timeKey, long timeUnit, final StatsDataService statsDataService) {
        StatsData statsData = statsDataService.findOldestData();
        long oldestKey = statsData.getTimeKey();
        long currentKey = oldestKey;
        while (currentKey < timeKey) {

            long tempKey = currentKey + timeUnit;
            currentKey = tempKey > timeKey ? timeKey : tempKey;
            final long removeKey = currentKey;

            SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "doCleanUp"
                    + statsDataService.getStatsDataClass().getSimpleName());

            catWrapper.doAction(new SwallowAction() {
                @Override
                public void doAction() throws SwallowException {
                    logger.info("[doClearupTask] executor doClearUp "
                            + statsDataService.getStatsDataClass().getSimpleName() + " less than timeKey: " + removeKey);
                    statsDataService.removeLessThanTimeKey(removeKey);
                }
            });

            sleep(5);
        }

    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            logger.info("[sleep] interrupted.");
        }
    }

}
