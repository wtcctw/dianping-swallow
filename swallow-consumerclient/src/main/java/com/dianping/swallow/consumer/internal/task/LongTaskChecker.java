package com.dianping.swallow.consumer.internal.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年3月30日 下午5:19:21
 */
public class LongTaskChecker implements TaskChecker, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LongTaskChecker.class);

    private static final String DEFAULT_CAT_TYPE = "SwallowLongTask";

    private String catType;

    private volatile boolean closed = false;

    /**
     * 运行时间超过此时间，报警，单位毫秒
     */
    private int alertTime;

    private AtomicInteger alertCount = new AtomicInteger();

    private Map<ConsumerTask, Long> tasks = new ConcurrentHashMap<ConsumerTask, Long>();

    public LongTaskChecker(int alertTime) {
        this(alertTime, DEFAULT_CAT_TYPE);
    }

    public LongTaskChecker(int alertTime, String catType) {
        this.alertTime = alertTime;
        this.catType = catType;
    }

    @Override
    public void addTask(ConsumerTask task) {

        if (logger.isDebugEnabled()) {
            logger.debug("[addTask]" + task);
        }
        tasks.put(task, System.currentTimeMillis());
    }

    @Override
    public void removeTask(ConsumerTask task) {

        if (logger.isDebugEnabled()) {
            logger.debug("[removeTask]" + task);
        }
        tasks.remove(task);
    }

    @Override
    public void run() {

        if (alertTime <= 0) {
            if (logger.isInfoEnabled()) {
                logger.info("[run][exit alertTime <= 0]" + alertTime);
            }
            return;
        }

        while (!closed) {
            try {
                TimeUnit.MILLISECONDS.sleep(alertTime);
            } catch (InterruptedException e) {
            }

            try {
                check();
            } catch (Throwable th) {
                logger.error("[run]", th);
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("[run][exit]");
        }
    }

    private void check() {
        Long current = System.currentTimeMillis();

        for (Entry<ConsumerTask, Long> taskEntry : tasks.entrySet()) {
            ConsumerTask task = taskEntry.getKey();
            Long startTime = taskEntry.getValue();

            if ((current - startTime) > alertTime) {
                alert(task);
            }
        }
    }

    private void alert(ConsumerTask task) {

        if (logger.isInfoEnabled()) {
            logger.info("[alert]" + task);
        }

        Transaction transaction = Cat.newTransaction(catType, task.toString() + "," + alertTime);
        transaction.setStatus(Transaction.SUCCESS);
        transaction.complete();

        alertCount.incrementAndGet();
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public int getAlertCount() {
        return alertCount.get();
    }
}
