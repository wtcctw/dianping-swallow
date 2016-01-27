package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.common.WebComponentConfig;
import com.dianping.swallow.web.container.ServerReportStatsDataContainer;
import com.dianping.swallow.web.dao.ServerReportDao;
import com.dianping.swallow.web.model.report.ServerReport;
import com.dianping.swallow.web.monitor.DailyReportRetriever;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ServerReportService;
import com.dianping.swallow.web.service.ServiceLifecycle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.concurrent.*;

/**
 * Author   mingdongli
 * 16/1/21  下午11:31.
 */
public abstract class AbstractServerReportService extends AbstractSwallowService implements ServerReportService, ApplicationContextAware, ServiceLifecycle {

    protected static final int SAMPLE_INTERVAL = 30;

    public static final int MILLISECOND_TO_SCEOND = 1000;

    protected static final String DEFAULT = "default";

    protected static final String CAT_TYPE = "BuildReport";

    public static final String TOTAL = "total";

    protected ScheduledExecutorService serverReportExecutorForPast = Executors.newScheduledThreadPool(1);

    protected ServerReport firstServerReport;

    private ApplicationContext applicationContext;

    protected ConcurrentMap<String, ServerReportStatsDataContainer> serverReportStatsDataMap = new ConcurrentHashMap<String, ServerReportStatsDataContainer>();

    public static int monthSize = 6;

    @Autowired
    WebComponentConfig webComponentConfig;

    @Value("${swallow.web.monitor.report.monthsize}")
    public void setMonthSize(int monthSize) {
        AbstractServerReportService.monthSize = monthSize;
    }

    @Override
    protected void doInitialize() throws Exception {

        serverReportExecutorForPast.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (needBuild()) {
                    SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClazz());
                    catWrapper.doAction(new SwallowAction() {
                        @Override
                        public void doAction() throws SwallowException {
                            Pair<Long, Long> timePair = timePair();
                            long endTimeKey = timePair.getSecond();
                            long startTimeKey = timePair.getFirst();
                            doBuild(startTimeKey, endTimeKey);
                            if (logger.isInfoEnabled()) {
                                logger.info(String.format("Build daily report for %s on %tD", getClazz(), new Date(endTimeKey)));
                            }
                        }
                    });
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("shut down serverReportExecutorForPast");
                    }
                    serverReportExecutorForPast.shutdownNow();
                }
            }
        }, 0, 15, TimeUnit.SECONDS);

        initServerReportMap();
    }

    @Override
    public boolean insert(ServerReport serverReport) {
        return getDao().insert(serverReport);
    }

    @Override
    public List<ServerReport> find(String ip) {

        long startKey = getEndKeyForPastXMonth(monthSize);
        return find(ip, startKey, System.currentTimeMillis());
    }

    @Override
    public List<ServerReport> find(String ip, long startKey, long endKey) {
        return getDao().find(ip, startKey, endKey);
    }

    @Override
    public Map<String, NavigableMap<Long, Long>> find(long startKey, long endKey) {

        List<ServerReport> serverReportList = getDao().find(startKey, endKey);

        Map<String, NavigableMap<Long, Long>> serverReportStatsDataMaps = null;

        if (serverReportList != null) {

            serverReportStatsDataMaps = new HashMap<String, NavigableMap<Long, Long>>();
            for (ServerReport sr : serverReportList) {

                String ip = sr.getIp();
                if (serverReportStatsDataMaps.containsKey(ip)) {

                    NavigableMap<Long, Long> serverReportStatsDataMap = serverReportStatsDataMaps.get(ip);
                    serverReportStatsDataMap.put(sr.getTimeKey(), sr.getCount());
                    serverReportStatsDataMaps.put(ip, serverReportStatsDataMap);

                } else {

                    NavigableMap<Long, Long> serverReportStatsDataMap = new TreeMap<Long, Long>();
                    serverReportStatsDataMap.put(sr.getTimeKey(), sr.getCount());
                    serverReportStatsDataMaps.put(ip, serverReportStatsDataMap);
                }
            }
        }
        return serverReportStatsDataMaps;
    }

    @Override
    public long firstServerReportTimeInMemory() {
        if (checkNull(serverReportStatsDataMap)) {
            return Long.MAX_VALUE;
        }

        Set<String> ipSet = serverReportStatsDataMap.keySet();
        if (ipSet.isEmpty()) {
            return Long.MAX_VALUE;
        }

        for (String ip : ipSet) {
            ServerReportStatsDataContainer srsdc = serverReportStatsDataMap.get(ip);
            if (srsdc != null) {
                NavigableMap<Long, Long> record = srsdc.retrieve();
                if (record.size() > 0) {
                    return record.firstKey();
                }
            }
        }

        return Long.MAX_VALUE;

    }

    private boolean checkNull(Object obj) {
        if (obj == null) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, NavigableMap<Long, Long>> retrieve() {
        if (checkNull(serverReportStatsDataMap)) {
            return null;
        }

        Map<String, NavigableMap<Long, Long>> result = new HashMap<String, NavigableMap<Long, Long>>();
        for (Map.Entry<String, ServerReportStatsDataContainer> entry : serverReportStatsDataMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().retrieve());
        }

        return result;
    }

    protected Pair<Long, Long> timePair() {

        long endTimeKey;
        long startTimeKey;

        if (firstServerReport == null) {
            endTimeKey = getYesterdayEndKey();
            startTimeKey = endTimeKey - milliSecondOfOneDay() + MILLISECOND_TO_SCEOND;
        } else {
            endTimeKey = firstServerReport.getTimeKey() - milliSecondOfOneDay();
            startTimeKey = endTimeKey - milliSecondOfOneDay() + MILLISECOND_TO_SCEOND;
        }

        return new Pair<Long, Long>(startTimeKey, endTimeKey);
    }

    protected ServerReport buildServerReport(String ip, long count, long timeKey) {

        ServerReport serverReport = new ServerReport();
        serverReport.setCount(count);
        serverReport.setIp(ip);
        serverReport.setTimeKey(timeKey);
        return serverReport;
    }

    public static long milliSecondOfOneDay() {
        return DailyReportRetriever.DAILY_INTERVAL_SECOND * MILLISECOND_TO_SCEOND;
    }

    public static Calendar getYesterdayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static long getYesterdayEndKey() {
        Calendar calendar = getYesterdayCalendar();
        return calendar.getTimeInMillis() - MILLISECOND_TO_SCEOND;
    }

    public static long getEndKeyForPastXMonth(int monthSize) {

        Calendar calendar = getYesterdayCalendar();
        calendar.add(Calendar.MONTH, -monthSize);
        return calendar.getTimeInMillis() - MILLISECOND_TO_SCEOND;
    }

    public static long normalizeStartTime(long start) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(start);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void initServerReportMap() {
        long endKey = getYesterdayEndKey();
        long startKey = getEndKeyForPastXMonth(monthSize);
        List<ServerReport> serverReportList = getDao().find(startKey, endKey);
        if (serverReportList == null) {
            return;
        }
        Collections.reverse(serverReportList); //防止查询时由于先放最早的，导致从内存查询
        for (ServerReport sr : serverReportList) {
            String ip = sr.getIp();
            ServerReportStatsDataContainer srsdc = getServerReportStatsDataContaine(ip);
            srsdc.add(sr);
            serverReportStatsDataMap.put(ip, srsdc);
        }
    }

    protected void updateServerReportMap(ServerReport serverReport) {

        String ip = serverReport.getIp();
        ServerReportStatsDataContainer serverReportStatsDataContainer = getServerReportStatsDataContaine(ip);
        serverReportStatsDataContainer.add(serverReport);
        serverReportStatsDataMap.put(ip, serverReportStatsDataContainer);
    }

    protected void storeAndCache(String ip, long totalCount, long end) {
        ServerReport serverReport = buildServerReport(ip, totalCount, end);
        getDao().insert(serverReport);
        updateServerReportMap(serverReport);
    }

    protected long narrowTime(long time) {
        return time / 5 / MILLISECOND_TO_SCEOND;
    }

    private ServerReportStatsDataContainer getServerReportStatsDataContaine(String ip) {

        ServerReportStatsDataContainer serverReportStatsDataContainer = serverReportStatsDataMap.get(ip);
        if (serverReportStatsDataContainer == null) {
            serverReportStatsDataContainer = applicationContext.getBean(ServerReportStatsDataContainer.class);
        }
        return serverReportStatsDataContainer;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void buildReportForYesterday() {

        if (!webComponentConfig.isJobTask()) {
            return;
        }
        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doBuildReport");

        catWrapper.doAction(new SwallowAction() {
            @Override
            public void doAction() throws SwallowException {

                long endKey = getYesterdayEndKey();
                long startKey = endKey - milliSecondOfOneDay() + MILLISECOND_TO_SCEOND;
                if (getDao().find(endKey) == null) {

                    doBuild(startKey, endKey);
                }
            }
        });

    }

    abstract protected boolean needBuild();

    abstract protected void doBuild(long startTimeKey, long endTimeKey);

    abstract protected ServerReportDao getDao();

    abstract protected String getClazz();

}
