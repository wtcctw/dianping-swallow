package com.dianping.swallow.common.internal.config.impl;

import com.dianping.lion.Environment;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.zip.Deflater;

/**
 * Author   mingdongli
 * 15/12/16  下午7:15.
 */
public class TimeRollingRandomAccessFileAppenderTest {

    private static final String LOG_ROOT = System.getProperty("swallow.log.path", "/data/applogs/swallow/log/");

    private static final String PACKAGE = "com.dianping.swallow.common.internal.config.impl";

    private static final String MESSAGE = "abcdefgijklmnopqistuvwxyabcdefgijklmnopqistuvwxyabcdefgijklmnopqistuvwxyabcdefgijklmnopqistuvwxy";

    private static final String DEFAULT_APP_NAME = "swallow-app";

    private static String APP_NAME = Environment.getAppName();

    private static final int THREAD_SIZE = 5;

    CountDownLatch begSignal = new CountDownLatch(1);

    CountDownLatch endSignal = new CountDownLatch(THREAD_SIZE);

    private Logger logger;

    private TimeRollingRandomAccessFileAppender fileInfoAppender;

    @Before
    public void setUp() throws Exception {

        if (StringUtils.isBlank(APP_NAME)) {
            APP_NAME = DEFAULT_APP_NAME;
        }
        System.setProperty("app.name", APP_NAME);

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Layout<? extends Serializable> layout = PatternLayout.createLayout(
                "[%d{ISO8601}][%-5p][%-25c] %m%n", config, null, null, true, false,
                null, null);

        Filter fileInfoFilter = ThresholdFilter.createFilter(Level.ERROR, Filter.Result.DENY, Filter.Result.ACCEPT);
        fileInfoAppender = TimeRollingRandomAccessFileAppender.createAppender(LOG_ROOT + "/swallow." + APP_NAME + ".log",
                LOG_ROOT + "/swallow." + APP_NAME + ".log.%d{yyyy-MM-dd}.gz", "true", "FileInfo", "false", null,
                TimeBasedTriggeringPolicy.createPolicy("1", "true"),
                DefaultRolloverStrategy.createStrategy("30", "1", null, Deflater.DEFAULT_COMPRESSION + "", config),
                layout, fileInfoFilter, "false", null, null, config, "10000");
        fileInfoAppender.start();
        config.addAppender(fileInfoAppender);
        AppenderRef fileInfoRef = AppenderRef.createAppenderRef("FileInfo", Level.INFO, null);
        AppenderRef[] refs = new AppenderRef[]{fileInfoRef};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.INFO, PACKAGE, "true", refs,
                null, config, null);
        loggerConfig.addAppender(fileInfoAppender, Level.INFO, null);

        config.addLogger(PACKAGE, loggerConfig);
        ctx.updateLoggers();

        logger = LogManager.getLogger(PACKAGE);
    }

    @Test
    public void test() {

        for (int i = 0; i < THREAD_SIZE; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        begSignal.await();
                        int LOOP = 10;
                        while (LOOP > 0) {
                            LOOP--;
                            logger.info(MESSAGE + MESSAGE + MESSAGE + MESSAGE + MESSAGE);
                            Thread.sleep(4000);
                        }

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        endSignal.countDown();
                    }
                }
            }).start();
        }

        try {
            begSignal.countDown();
            endSignal.await();
            System.out.println("运行结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public class Work implements Runnable {

        private CountDownLatch beginSignal;
        private CountDownLatch endSignal;

        public Work(CountDownLatch begin, CountDownLatch end) {
            this.beginSignal = begin;
            this.endSignal = end;
        }

        @Override
        public void run() {
            try {

                beginSignal.await();


            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                endSignal.countDown();
            }
        }
    }
}