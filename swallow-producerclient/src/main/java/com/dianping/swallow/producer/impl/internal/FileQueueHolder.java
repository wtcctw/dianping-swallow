package com.dianping.swallow.producer.impl.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.packet.Packet;
import com.geekhua.filequeue.Config;
import com.geekhua.filequeue.FileQueue;
import com.geekhua.filequeue.FileQueueImpl;

public class FileQueueHolder {
	
    private static final Logger                   logger                 = LogManager
                                                                                 .getLogger(FileQueueHolder.class);

    private static  long                     	  DEFAULT_FILEQUEUE_SIZE = 100 * 1024 * 1024;
    
    private static final int                      MSG_AVG_LEN            = 512;                                      //默认的filequeue切片大小，100MB

    private static Map<String, FileQueue<Packet>> queues                 = new HashMap<String, FileQueue<Packet>>(); //当前TopicName与Filequeue对应关系的集合

    /**
     * 获取指定fileName及选项的FileQueue，如果已经存在则返回引用，如果不存在就创建新的FileQueue
     * 
     * @param fileName 消息目的地名称
     * @param sendMsgLeftLastSessions 是否重启续传
     * @return 指定参数的FileQueue
     */
    public synchronized static FileQueue<Packet> getQueue(String fileName, boolean sendMsgLeftLastSessions,
                                                          String filequeueBaseDir) {
        //如果Map里已经存在该filequeue，则重复利用
        if (queues.containsKey(fileName)) {
            return queues.get(fileName);
        }

        Config fileQueueConfig = new Config();
        fileQueueConfig.setName(fileName);
        fileQueueConfig.setFileSiz(DEFAULT_FILEQUEUE_SIZE);
        fileQueueConfig.setMsgAvgLen(MSG_AVG_LEN);
        if (filequeueBaseDir != null) {
            fileQueueConfig.setBaseDir(filequeueBaseDir);
        }
        
        //如果Map里不存在该filequeue，此handler又要求将之前的文件删除，则删除
        if (!sendMsgLeftLastSessions) {//如果不续传，则需要把/data/appdatas/filequeue/<topicName> 目录删除掉
            File file = new File(fileQueueConfig.getBaseDir(), fileName);
            if (file.exists()) {
                try {
                	if(logger.isInfoEnabled()){
                		logger.info("[getQueue][delete file]" + file);
                	}
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }

        FileQueue<Packet> newQueue = new FileQueueImpl<Packet>(fileQueueConfig);

        queues.put(fileName, newQueue);

        return queues.get(fileName);
    }
    
    
    public synchronized static void removeQueue(String fileName){
    	
    	queues.remove(fileName);
    }
}
