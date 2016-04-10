package com.dianping.swallow.common.internal.exception;

import java.io.IOException;

/**
 * @author qi.yin
 *         2016/03/17  上午11:34.
 */
public class SwallowIOException extends IOException {

    private static final long serialVersionUID = 1L;

    public SwallowIOException(){

    }

    public SwallowIOException(String message){
        super(message);
    }

    public SwallowIOException(String message, Throwable th){
        super(message, th);
    }

    public SwallowIOException(Throwable th){
        super(th);
    }
}
