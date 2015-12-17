package com.dianping.swallow.common.internal.config.impl;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.rolling.*;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

/**
 * Author   mingdongli
 * 15/12/16  下午6:35.
 */
@Plugin(name = "TimeRollingRandomAccessFile", category = "Core", elementType = "appender", printObject = true)
public class TimeRollingRandomAccessFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_FLUSH_INTERVAL = 10;

    private final String fileName;
    private final String filePattern;
    private Object advertisement;
    private final Advertiser advertiser;
    private volatile long timeOfLastFlush = System.currentTimeMillis();
    private Long flushInterval;

    private TimeRollingRandomAccessFileAppender(final String name, final Layout<? extends Serializable> layout,
                                            final Filter filter, final RollingFileManager manager, final String fileName,
                                            final String filePattern, final boolean ignoreExceptions,
                                            final boolean immediateFlush, final int bufferSize, final Advertiser advertiser, final String flushInterval) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
        if (advertiser != null) {
            final Map<String, String> configuration = new HashMap<String, String>(layout.getContentFormat());
            configuration.put("contentType", layout.getContentType());
            configuration.put("name", name);
            advertisement = advertiser.advertise(configuration);
        }
        this.fileName = fileName;
        this.filePattern = filePattern;
        this.advertiser = advertiser;
        this.flushInterval = Long.parseLong(flushInterval, DEFAULT_FLUSH_INTERVAL);
    }

    @Override
    public void stop() {
        super.stop();
        if (advertiser != null) {
            advertiser.unadvertise(advertisement);
        }
    }

    private synchronized boolean checkLastFlushInterval(final LogEvent event){

        long current = System.currentTimeMillis();
        boolean isEndOfBatch = event.isEndOfBatch();

        if(isEndOfBatch){
            timeOfLastFlush = current;
            return Boolean.TRUE;
        }else{
            if(current - this.timeOfLastFlush >= this.flushInterval){
                this.timeOfLastFlush = current;
                System.out.println("Time is up");
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    }

    @Override
    public void append(final LogEvent event) {
        final RollingRandomAccessFileManager manager = (RollingRandomAccessFileManager) getManager();
        manager.checkRollover(event);
        boolean isEndOfBatch = checkLastFlushInterval(event);

        // Leverage the nice batching behaviour of async Loggers/Appenders:
        // we can signal the file manager that it needs to flush the buffer
        // to disk at the end of a batch.
        // From a user's point of view, this means that all log events are
        // _always_ available in the log file, without incurring the overhead
        // of immediateFlush=true.

        manager.setEndOfBatch(isEndOfBatch);
        super.append(event);
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public int getBufferSize() {
        return ((RollingRandomAccessFileManager) getManager()).getBufferSize();
    }

    /**
     * Create a RollingRandomAccessFileAppender.
     *
     * @param fileName The name of the file that is actively written to.
     *            (required).
     * @param filePattern The pattern of the file name to use on rollover.
     *            (required).
     * @param append If true, events are appended to the file. If false, the
     *            file is overwritten when opened. Defaults to "true"
     * @param name The name of the Appender (required).
     * @param immediateFlush When true, events are immediately flushed. Defaults
     *            to "true".
     * @param bufferSizeStr The buffer size, defaults to {@value RollingRandomAccessFileManager#DEFAULT_BUFFER_SIZE}.
     * @param policy The triggering policy. (required).
     * @param strategy The rollover strategy. Defaults to
     *            DefaultRolloverStrategy.
     * @param layout The layout to use (defaults to the default PatternLayout).
     * @param filter The Filter or null.
     * @param ignore If {@code "true"} (default) exceptions encountered when appending events are logged; otherwise
     *               they are propagated to the caller.
     * @param advertise "true" if the appender configuration should be
     *            advertised, "false" otherwise.
     * @param advertiseURI The advertised URI which can be used to retrieve the
     *            file contents.
     * @param config The Configuration.
     * @param flushInterval The pattern of the file name to use on rollover.
     *           (required).
     * @return A RollingRandomAccessFileAppender.
     */
    @PluginFactory
    public static TimeRollingRandomAccessFileAppender createAppender(
            @PluginAttribute("fileName") final String fileName,
            @PluginAttribute("filePattern") final String filePattern,
            @PluginAttribute("append") final String append,
            @PluginAttribute("name") final String name,
            @PluginAttribute("immediateFlush") final String immediateFlush,
            @PluginAttribute("bufferSize") final String bufferSizeStr,
            @PluginElement("Policy") final TriggeringPolicy policy,
            @PluginElement("Strategy") RolloverStrategy strategy,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("ignoreExceptions") final String ignore,
            @PluginAttribute("advertise") final String advertise,
            @PluginAttribute("advertiseURI") final String advertiseURI,
            @PluginConfiguration final Configuration config,
            @PluginAttribute("flushInterval") final String flushInterval) {

        final boolean isAppend = Booleans.parseBoolean(append, true);
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
        final boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
        final boolean isAdvertise = Boolean.parseBoolean(advertise);
        final int bufferSize = Integers.parseInt(bufferSizeStr, RollingRandomAccessFileManager.DEFAULT_BUFFER_SIZE);

        if (flushInterval == null) {
            LOGGER.error("No flushInterval provided for FileAppender");
            return null;
        }

        if (name == null) {
            LOGGER.error("No name provided for FileAppender");
            return null;
        }

        if (fileName == null) {
            LOGGER.error("No filename was provided for FileAppender with name " + name);
            return null;
        }

        if (filePattern == null) {
            LOGGER.error("No filename pattern provided for FileAppender with name " + name);
            return null;
        }

        if (policy == null) {
            LOGGER.error("A TriggeringPolicy must be provided");
            return null;
        }

        if (strategy == null) {
            strategy = DefaultRolloverStrategy.createStrategy(null, null, null,
                    String.valueOf(Deflater.DEFAULT_COMPRESSION), config);
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        final RollingRandomAccessFileManager manager = RollingRandomAccessFileManager.getRollingRandomAccessFileManager(
                fileName, filePattern, isAppend, isFlush, bufferSize, policy, strategy, advertiseURI, layout);
        if (manager == null) {
            return null;
        }

        return new TimeRollingRandomAccessFileAppender(name, layout, filter, manager,
                fileName, filePattern, ignoreExceptions, isFlush, bufferSize,
                isAdvertise ? config.getAdvertiser() : null, flushInterval);
    }
}
