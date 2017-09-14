package com.wiseweb.cat.base;

/**
 * Created by 贾承斌 on 15/11/7.
 */
public interface Configer {
    public String getScheduleHost();
    public int getSchedulePort();
    public String getScheduleTask();
    public String getBroadcastTask();
    public String getVersion();
    public String getKey();
    public String getClientId();
    public String getApiurl();
    public String getMAC();
    public String getArea();
    public int getTaskSleepTime(String task);
    public String getOauthUrl(String oauthMsg);
}
