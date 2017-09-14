package com.wiseweb.client.util;

import com.wiseweb.cat.base.Configer;
import com.wiseweb.cat.base.ConfigerFactory;
import com.wiseweb.cat.util.IPUtil;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2016/1/15.
 */
public class Config implements Configer {
    String schedule_task;
    String broadcast_task;
    String version;
    String key;
    String client_id;
    String apiurl;
    String mac;
    String area;
    String schedule_host;
    int schedule_port;
    int defaultSleepTime;
    Map<String,Integer> taskSleepTime = new HashMap<String,Integer>();
    public static boolean isConfig = true;
    public static boolean isVpn = false;
    public static String vpnTask = "201,202,203,204,205,210,211,212,213,214,215,216,217,218,219,220,221,222";
    public static void read(){
        try{
            String path = "";

            //path = Config.class.getResource("/").getPath();

            /*WY
            String path = "D:/Program Files/IDEA/IDEAWorkspace/catClient-master-c98e05448962d836e9e1b91f5e884e6dfebd7988/CatClientJava/src/main/resources/";
            String path = Config.class.getResource("/").getPath();
            System.out.println(path);*/

            //String log4j = Config.class.getClassLoader().getResource("log4j.properties").getPath();
            String log4j = path + "config/log4j.properties";


            PropertyConfigurator.configure(log4j);//加载log4j配置文件


            //String config = Config.class.getClassLoader().getResource("config.properties").getPath();
            String config = path + "config/config.properties";


            Properties pr = new Properties();
            InputStreamReader fi = new InputStreamReader(new FileInputStream(config),"utf-8");
            pr.load(fi);
            fi.close();
            Config configs = new Config();
            Iterator i = (Iterator) pr.keys();
            Map<String,Integer> taskSleepTime = new HashMap<String,Integer>();
            while(i.hasNext()){
                String key = (String) i.next();
                switch(key){
                    case "host":
                        configs.setSchedule_host(pr.getProperty("host"));
                        break;
                    case "port":
                        configs.setSchedule_port(Integer.parseInt(pr.getProperty("port")));
                        break;
                    case "version":
                        configs.setVersion(pr.getProperty("version"));
                        break;
                    case "schedule":
                        configs.setSchedule_task(pr.getProperty("schedule"));
                        break;
                    case "broadcast":
                        configs.setBroadcast_task(pr.getProperty("broadcast"));
                        break;
                    case "key":
                        configs.setKey(pr.getProperty("key"));
                        break;
                    case "client_id":
                        configs.setClient_id(pr.getProperty("client_id"));
                        break;
                    case "apiurl":
                        configs.setApiurl(pr.getProperty("apiurl"));
                        break;
                    case "vpn":
                        configs.setVpn(pr.getProperty("vpn"));
                        break;
                    default:
                        if(pr.getProperty(key).length()<4){
                            taskSleepTime.put(key,Integer.parseInt(pr.getProperty(key)));
                        }
                        break;
                }
            }
            configs.setTaskSleepTime(taskSleepTime);
            configs.setMac(Util.getMAC());
            //地区;
            String area = IPUtil.getAreaByIP(null);
            if(area!=null){
                configs.setArea(area);
                ConfigerFactory.initConfiger(configs);
                isConfig = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setSchedule_host(String schedule_host) {
        this.schedule_host = schedule_host;
    }

    public String getScheduleHost() {
        return schedule_host;
    }

    public void setSchedule_port(int schedule_port) {
        this.schedule_port = schedule_port;
    }

    public int getSchedulePort() {
        return schedule_port;
    }

    public void setSchedule_task(String schedule_task) {
        this.schedule_task = schedule_task;
    }

    public String getScheduleTask() {
        return schedule_task;
    }

    public void setBroadcast_task(String broadcast_task) {
        this.broadcast_task = broadcast_task;
    }

    public String getBroadcastTask() {
        return broadcast_task;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClientId() {
        return client_id;
    }

    public void setApiurl(String apiurl) {
        this.apiurl = apiurl;
    }

    public String getApiurl() {
        return apiurl;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMAC() {
        return mac;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setTaskSleepTime(Map<String, Integer> taskSleepTime) {
        this.taskSleepTime = taskSleepTime;
    }

    public int getTaskSleepTime(String task) {
        int sleepTime = 10;
        if(taskSleepTime.get(task)!=null){
            sleepTime = isVpn?0:taskSleepTime.get(task);
        }
        return sleepTime;
    }

    public String getOauthUrl(String task) {
        return null;
    }

    public void setVpn(String vpn){
        String[] arr = vpn.split(",");
        if(arr.length==3){
            VPN.vpnName = arr[0];
            VPN.vpnUsername = arr[1];
            VPN.vpnPassword = arr[2];
            isVpn = true;
        }
    }
}
