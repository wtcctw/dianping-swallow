package com.dianping.swallow.common.internal.config;


import com.dianping.lion.Environment;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.zip.Deflater;


/**
 * @author mingdongli
 *         <p/>
 *         2015年10月27日下午3:41:33
 */
public class LoggerLoader {

    private static final String PACKAGE = "com.dianping.swallow";

    private static final String APP_NAME = Environment.getAppName();

    private static final String LOG_ROOT = System.getProperty("swallow.log.path", "/data/applogs/swallow/log/");

    public static synchronized void init() {

        System.setProperty("app.name", APP_NAME);

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Layout<? extends Serializable> layout = PatternLayout.createLayout(
                "%d[%-5p][%t][%c] %L %m%n", config, null, null, true, false,
                null, null);

        // file info,  filter-same or more specific than
        Filter fileInfoFilter = ThresholdFilter.createFilter(Level.ERROR, Result.DENY, Result.ACCEPT);
        Appender fileInfoAppender = RollingFileAppender.createAppender(LOG_ROOT + "/" + APP_NAME + "/" + APP_NAME + ".log",
                LOG_ROOT + "/" + APP_NAME + "/" + APP_NAME + ".log.%d{yyyy-MM-dd}.gz", "true", "FileInfo", "true", "4000",
                "false", TimeBasedTriggeringPolicy.createPolicy("1", "true"),
                DefaultRolloverStrategy.createStrategy("30", "1", null, Deflater.DEFAULT_COMPRESSION + "", config),
                layout, fileInfoFilter, "false", null, null, config);
        fileInfoAppender.start();
        config.addAppender(fileInfoAppender);
        AppenderRef fileInfoRef = AppenderRef.createAppenderRef("FileInfo", null, fileInfoFilter);

        // console error
        Appender consoleErrorAppender = ConsoleAppender.createAppender(layout, null, "SYSTEM_ERR", "ConsoleError",
                "false", "false");
        config.addAppender(consoleErrorAppender);
        consoleErrorAppender.start();
        AppenderRef consoleErrorAppenderRef = AppenderRef.createAppenderRef("ConsoleError", Level.ERROR, null);
        AsyncAppender asyncConsoleErrorAppender = AsyncAppender.createAppender(
                new AppenderRef[]{consoleErrorAppenderRef}, null, true, 128, "AsyncConsoleError", false, null,
                config, false);
        asyncConsoleErrorAppender.start();
        config.addAppender(asyncConsoleErrorAppender);
        AppenderRef asyncConsoleErrorRef = AppenderRef.createAppenderRef("AsyncConsoleError", Level.ERROR, null);

        // console warn
        Filter consoleWarnFilter = ThresholdFilter.createFilter(Level.WARN, Result.DENY, Result.NEUTRAL);
        Appender consoleWarnAppender = ConsoleAppender.createAppender(layout, consoleWarnFilter, "SYSTEM_OUT",
                "ConsoleWarn", "false", "false");
        config.addAppender(consoleWarnAppender);
        consoleWarnAppender.start();
        AppenderRef consoleWarnAppenderRef = AppenderRef
                .createAppenderRef("ConsoleWarn", Level.WARN, consoleWarnFilter);

        // console info
        Filter consoleInfoFilter = ThresholdFilter.createFilter(Level.INFO, Result.DENY, Result.ACCEPT);
        Appender consoleInfoAppender = ConsoleAppender.createAppender(layout, consoleInfoFilter, "SYSTEM_OUT",
                "ConsoleInfo", "false", "false");
        config.addAppender(consoleInfoAppender);
        consoleInfoAppender.start();
        AppenderRef consoleInfoAppenderRef = AppenderRef
                .createAppenderRef("ConsoleInfo", Level.INFO, consoleInfoFilter);

        AppenderRef[] refs = new AppenderRef[]{asyncConsoleErrorRef, consoleWarnAppenderRef, consoleInfoAppenderRef, fileInfoRef};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.INFO, PACKAGE, "true", refs,
                null, config, null);
        loggerConfig.addAppender(asyncConsoleErrorAppender, Level.ERROR, null);
        loggerConfig.addAppender(consoleWarnAppender, Level.WARN, null);
        loggerConfig.addAppender(consoleInfoAppender, Level.INFO, null);
        loggerConfig.addAppender(fileInfoAppender, Level.INFO, null);

        config.addLogger(PACKAGE, loggerConfig);

        ctx.updateLoggers();
    }
}

