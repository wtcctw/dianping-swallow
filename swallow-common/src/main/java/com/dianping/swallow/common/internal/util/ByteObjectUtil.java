package com.dianping.swallow.common.internal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author qi.yin
 *         2015/12/15  下午3:28.
 */
public class ByteObjectUtil {

    private static final Logger logger = LoggerFactory.getLogger(ByteObjectUtil.class);

    public static byte[] transformToByte(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] results = null;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            results = bos.toByteArray();
        } catch (IOException e) {
            logger.error("[transformToByte] error.", e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                logger.error("[transformToByte] close error.", e);
            }
        }
        return results;
    }

    public static Object transformToObj(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        Object result = null;
        try {
            ois = new ObjectInputStream(bis);
            result = ois.readObject();
        } catch (Exception e) {
            logger.error("[transformToObj] error.", e);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                logger.error("[transformToObj] close error.", e);
            }
        }
        return result;
    }

}
