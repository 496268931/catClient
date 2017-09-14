package com.wiseweb.cat.base;


import java.util.*;

/**
 * Created by 贾承斌 on 15/11/7.
 */
public class DefaultConfiger implements Configer {
    private String host = "114.215.170.176";
    private int port = 9001;
    private int defaultSleepTime=60;//默认事件

    private String schedule ="";// "201,202,203,204,205,210,211,212,213,101,102,103,104,105,301,302,303,304,401,402,403,404,501,502,601,602,701,702,G01,G02";
    private String broadcast = "";//"201,202,203,204,205,214,G01,G02,301,302,401,402,701,702,201,202,203,204,205,210,211,212,213,304,207,404,502,306,209,406,504,801,1001,303,206,403,501,305,208,405,503,802,1002,406,504";
    private String version = "V3.2";
    private String key = "wise";
    private String client_id = UUID.randomUUID().toString();
    private String apiurl = "http://114.215.170.176:3000";
    private String mac = "未知MAC";
    private String area = "未知地区";


    protected Map<String,Integer> taskSleepTime = new HashMap<String,Integer>();
    protected Map<String,String> oauthUrl = new HashMap<String,String>();


    public DefaultConfiger(){
        oauthUrl.put(Constants.SINA_WEIBO_APP_OAUTH_MSG,"https://api.weibo.com/oauth2/authorize?client_id=1073104718&redirect_uri=http://id.ifeng.com/callback/sina&response_type=code");
        oauthUrl.put(Constants.NEWS_IFENG_SINA_OAUTH_MSG,"https://open.weibo.cn/oauth2/authorize?client_id=1967296247&response_type=token&redirect_uri=http://t.pp.cc&display=mobile,https://open.weibo.cn/2/oauth2/authorize?client_id=966056985&response_type=code&display=mobile&redirect_uri=http://www.sina.com&from=&with_cookie=&packagename=com.sina.news&key_hash=18da2bf10352443a00a5e046d9fca6bd,https://open.weibo.cn/2/oauth2/authorize?client_id=1038008035&response_type=token&display=mobile&redirect_uri=http://movie.douban.com/app/android,https://open.weibo.cn/2/oauth2/authorize?client_id=3651065292&response_type=code&display=mobile&redirect_uri=http://api.k.sohu.com&from=&with_cookie=&packagename=com.sohu.newsclient&key_hash=35c162871bf3051bddbf5d4eeb9decdb");
    }


    @Override
    public String getScheduleHost() {
        return host;
    }

    @Override
    public int getSchedulePort() {
        return port;
    }

    @Override
    public String getScheduleTask() {
        return schedule;
    }

    @Override
    public String getBroadcastTask() {
        return broadcast;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getClientId() {
        return client_id;
    }

    @Override
    public String getApiurl() {
        return apiurl;
    }



    public void setScheduleHost(String host) {
        this.host = host;
    }

    public void setSchedulePort(int port) {
        this.port = port;
    }

    public void setScheduleTask(String schedule) {
        this.schedule = schedule;
    }

    public void setBroadcastTask(String broadcast) {
        this.broadcast = broadcast;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setClientId(String client_id) {
        this.client_id = client_id;
    }

    public void setApiurl(String apiurl) {
        this.apiurl = apiurl;
    }

    public void setMAC(String mac) {
        this.mac = mac;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void addTaskSleepTime(String task,int sleepTime){
        taskSleepTime.put(task,sleepTime);
    }


    @Override
    public String getMAC() {
        return this.mac;
    }

    @Override
    public String getArea() {
        return this.area;
    }

    @Override
    public int getTaskSleepTime(String task) {
        if(taskSleepTime.containsKey(task)){
            return this.taskSleepTime.get(task);
        }else{
            return defaultSleepTime;
        }

    }

    @Override
    public String getOauthUrl(String task) {
        if(oauthUrl.containsKey(task)){
            return this.oauthUrl.get(task);
        }else{
            return null;
        }
    }
    /**
     * 该方法可调用可不调用
     */




}
