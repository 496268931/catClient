package com.wiseweb.client.util;

import com.wiseweb.cat.base.Logger;

/**
 * Created by Administrator on 2015/11/9.
 */
public class Log4JLogger implements Logger {
    static{
        if(!Config.isConfig){
            Config.read();
        }
    }
    private org.apache.log4j.Logger logger =  org.apache.log4j.Logger.getLogger(this.getClass());
    public void info(String message) {
        logger.info(message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void error(String message, Exception e) {
        logger.error(message,e);
    }

    public void warning(String message) {
        logger.warn(message);
    }
}
