package com.wiseweb.cat.base;

import com.wiseweb.cat.util.Util;

/**
 * Created by 贾承斌 on 15/11/7.
 */
public abstract class BaseClass {
    protected Logger logger = LoggerFactory.getLogger();

    protected void info(String message){
        logger.info("[info][" + Util.getDateFormat() + "]["+this.getClass().getName()+"]----->"+ message);
    };
    protected void debug(String message){
        logger.debug("[info][" + Util.getDateFormat() + "][" + this.getClass().getName() + "]----->" + message);
    };
    protected void error(String message){
        logger.error("[info][" + Util.getDateFormat() + "][" + this.getClass().getName() + "]----->" + message);
    };
    protected void error(String message,Exception e){
        logger.error("[info][" + Util.getDateFormat() + "][" +this.getClass().getName()+"]----->"+message,e);
    };
    protected void warning(String message){
        logger.warning("[info][" + Util.getDateFormat() + "][" + this.getClass().getName() + "]----->" + message);
    };



}
