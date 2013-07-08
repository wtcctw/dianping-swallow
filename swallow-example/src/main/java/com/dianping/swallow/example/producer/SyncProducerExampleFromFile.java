package com.dianping.swallow.example.producer;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @rundemo_name 生产者例子(同步,本地文件)
 */
public class SyncProducerExampleFromFile {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        ProducerConfig config = new ProducerConfig();
        config.setMode(ProducerMode.SYNC_MODE);
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("example"), config);

        InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("messages.txt");
        List<String> lines = IOUtils.readLines(ins, "UTF-8");
        if (lines != null) {
            for (String line : lines) {
                System.out.println("正在发送：" + line);
                try {
                    p.sendMessage(line);
                    System.out.println("发送完毕。");
                } catch (Exception e) {
                    System.out.println("发送失败: " + e.getMessage());
                }
            }
        }
    }

}
