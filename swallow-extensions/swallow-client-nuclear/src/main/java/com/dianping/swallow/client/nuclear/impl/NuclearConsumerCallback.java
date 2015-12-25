package com.dianping.swallow.client.nuclear.impl;

import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.message.BytesSwallowMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.internal.task.ConsumerTask;
import com.dianping.swallow.consumer.internal.task.TaskChecker;
import com.meituan.nuclearmq.client.ConsumerCallback;

/**
 * @author qi.yin
 *         2015/12/22  上午10:28.
 */
public class NuclearConsumerCallback implements ConsumerCallback {

    private Consumer consumer;

    private SwallowCatActionWrapper actionWrapper;

    private TaskChecker taskChecker;

    private ConsumerProcessor consumserProcessor;

    public NuclearConsumerCallback(Consumer consumer, SwallowCatActionWrapper actionWrapper, TaskChecker taskChecker, ConsumerProcessor consumserProcessor) {

        this.consumer = consumer;
        this.actionWrapper = actionWrapper;
        this.taskChecker = taskChecker;
        this.consumserProcessor = consumserProcessor;
    }

    @Override
    public void onHandle(long messageId, byte[] bytesContent, Object o) {

        BytesSwallowMessage swallowMessage = new BytesSwallowMessage();
        swallowMessage.setBytesContent(bytesContent);
        swallowMessage.setMessageId(messageId);

        ConsumerTask consumerTask = new NuclearConsumerTask(consumer, actionWrapper, taskChecker,
                swallowMessage, consumserProcessor);

        consumerTask.doConsumerTask();

    }
}
