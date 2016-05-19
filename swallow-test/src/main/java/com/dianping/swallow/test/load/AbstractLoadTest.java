package com.dianping.swallow.test.load;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年1月28日 下午7:10:54
 */
public abstract class AbstractLoadTest {

    @JsonIgnore
    protected Logger logger = LogManager.getLogger(getClass());

    protected String topicName = "LoadTestTopic";

    protected String type = "type";

    protected long messageStartIndex = Long.parseLong(System.getProperty("messageStartIndex", "0"));
    protected long totalMessageCount = Long.parseLong(System.getProperty("totalMessageCount", String.valueOf(Long.MAX_VALUE)));
    protected int concurrentCount = Integer.parseInt(System.getProperty("concurrentCount", "1"));
    protected int topicCount = Integer.parseInt(System.getProperty("topicCount", "1"));

    protected int topicStartIndex = Integer.parseInt(System.getProperty("topicStartIndex", "0"));


    protected static int maxRunMinutes = Integer.parseInt(System.getProperty("maxRunMinutes", "10080"));

    protected static int threadSizes = Integer.parseInt(System.getProperty("threadSizes", "100"));

    protected AtomicLong count = new AtomicLong();
    protected AtomicLong preCount = new AtomicLong();
    protected long preTime;
    protected long startTime;

    protected int zeroCount = 0;
    protected int zeroExit = 10;

    @JsonIgnore
    protected ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(4);

    @JsonIgnore
    protected ExecutorService executors = Executors.newFixedThreadPool(threadSizes, new MQThreadFactory("LOAD-TEST-POOL"));

    public static int messageSize = Integer.parseInt(System.getProperty("messageSize", "10240"));

    public static String message;


    protected String createMessage() {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageSize; i++) {

            int random = (int) (Math.random() * ('a' - 'A'));
            sb.append((char) ('A' + random));
        }
        message = sb.toString();
        return message;
    }


    protected String getTopicName(String name, int count) {


        return name + "-" + count;
    }

    protected boolean isLastTopic(String currentTopicName) {

        int index = Integer.parseInt(currentTopicName.substring(topicName.length() + 1));

        return index == topicCount + topicStartIndex - 1;
    }


    protected void getArgs() {
        if (totalMessageCount <= 0) {
            totalMessageCount = Long.MAX_VALUE;
        }

    }

    protected void start() throws Exception {

        getArgs();

        createMessage();

        startFrequencyCounter();

        startTime = System.currentTimeMillis();

        logger.info("[doStart][args]" + this);
        doStart();


        if (maxRunMinutes <= 0) {
            return;
        }

        scheduled.schedule(new Runnable() {

            @Override
            public void run() {

                if (logger.isInfoEnabled()) {
                    logger.info("[start][time exceed return]");
                }
                if (isExitOnExecutorsReturn()) {
                    exit();
                }
            }

        }, maxRunMinutes, TimeUnit.MINUTES);
    }

    protected void doStart() throws InterruptedException, IOException, Exception {


        for (int i = 0; i < topicCount; i++) {

            String currentTopic = getTopicName(topicName, topicStartIndex + i);

            for (int j = 0; j < concurrentCount; j++) {

                if (logger.isInfoEnabled()) {
                    logger.info("[doStart]" + currentTopic + "," + j);
                }

                Runnable task = createLoadTask(currentTopic, j);
                if (task != null) {
                    executors.execute(task);
                } else {
                    logger.warn("task null" + currentTopic);
                }
            }
        }
    }

    protected Runnable createLoadTask(String topicName, int concurrentIndex) {
        return null;
    }


    protected void doRun() {

    }

    protected void exit() {


        doOnExit();
        logger.info("[exit]" + "Total Message count:" + count.get());
        logger.info("[exit]" + "Total Message Frequency:" + count.get() / ((System.currentTimeMillis() - startTime) / 1000));
        scheduled.shutdown();
        System.exit(0);

    }


    protected void doOnExit() {

    }


    protected boolean isExitOnExecutorsReturn() {
        return true;
    }

    private void startFrequencyCounter() {

        preTime = System.currentTimeMillis();
        preCount.set(0);

        final ScheduledFuture<?> future = scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                try {

                    long currentTime = System.currentTimeMillis();
                    long currentCount = count.get();

                    logger.info("[run]" + "current rate:" + (currentCount - preCount.get()) / ((currentTime - preTime) / 1000));
                    logger.info("[run]" + "total rate:" + (currentCount) / ((currentTime - startTime) / 1000));
                    logger.info("[run]" + "message count:" + currentCount);


                    if (currentCount > totalMessageCount) {
                        logger.info("[run][currentCount > totalMessageCount][exit]" + currentCount + "," + totalMessageCount);
                        exit();
                    }

                    if (currentCount - preCount.get() == 0) {
                        zeroCount++;
                    } else {
                        zeroCount = 0;
                    }
                    if (zeroCount >= zeroExit || (isExit())) {
                        logger.info("[run][zero size exceed maximum count, exit]" + zeroExit);
                        exit();
                    }
                    preTime = currentTime;
                    preCount.set(currentCount);
                } catch (Throwable th) {
                    logger.error("[run]", th);
                }
            }

        }, 5, 5, TimeUnit.SECONDS);


        executors.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    logger.info("[scheduled future]" + future.get());
                } catch (Exception e) {
                    logger.error("[run]", e);
                }
            }

        });

    }

    protected boolean isExit() {
        return false;
    }

    protected void sleep(int timeMili) {

        try {
            TimeUnit.MILLISECONDS.sleep(timeMili);
        } catch (InterruptedException e) {
            logger.error("[sleep]", e);
        }
    }

    protected long increaseAndGetCurrentCount() {
        long current = count.incrementAndGet();
        if (current > totalMessageCount) {
            count.decrementAndGet();
            throw new CountExceedException("current:" + current);
        }
        return base(current);
    }

    private long base(long current) {

        return messageStartIndex + current;
    }


    protected long getCurrentCount() {
        return base(count.get());
    }

    protected long addAndGetCurrentCount(long delta) {
        long current = count.addAndGet(delta);
        if (current > totalMessageCount) {
            count.addAndGet(-delta);
            throw new CountExceedException("currentCount:" + current);
        }
        return base(current);
    }

    @Override
    public String toString() {
        return JsonBinder.getNonEmptyBinder().toJson(this);
    }
}
