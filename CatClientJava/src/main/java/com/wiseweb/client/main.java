package com.wiseweb.client;

import com.wiseweb.cat.base.ConfigerFactory;
import com.wiseweb.cat.client.CatClient;
import com.wiseweb.client.util.Config;
import com.wiseweb.client.util.VPN;

/**
 * Created by Administrator on 2016/1/15.
 */
public class main {
    public static void main(String[] ages){
//    public void start(){
        try{
            //读取配置文件并且初始化Config
            Config.read();
            if(!Config.isConfig)return;
            CatClient main = new CatClient();
            String[] tasks = ConfigerFactory.getConfiger().getBroadcastTask().split(",");
            System.out.println(tasks);
            for(int i=0;i<tasks.length;i++){
                String task = tasks[i];
                if (task.length()>1){
                    //微博
                    if(task .equals ("201") || task .equals("202") || task .equals("203") || task .equals("204") || task .equals("205") || task .equals("210") || task .equals("211") || task .equals("212")|| task .equals("213") || task .equals("214") || task .equals("215") || task .equals("216") || task .equals("217")|| task .equals("218")){
                        main.addTaskClient(task,new WeiboClient(task,ConfigerFactory.getConfiger().getTaskSleepTime(task)));
                    }
                    //格瓦拉
                    if(task .equals("1201") || task .equals("1202") || task .equals("1203") || task.equals("1204") || task .equals("1205")){
                        main.addTaskClient(task,new GewaraClient(task,ConfigerFactory.getConfiger().getTaskSleepTime(task)));
                    }
                    //新闻评论
//                    if(task .equals("206") || task .equals("208") || task.equals("303") || task.equals("305") || task .equals("403") || task .equals("405") ||task.equals("501") || task .equals("503") || task .equals("802") || task.equals("1002")){
//                        main.addTaskClient(task,new NewsCommentClient(task,ConfigerFactory.getConfiger().getTaskSleepTime(task)));
//                    }
//                    新闻支持
                    if(task.equals("207") ||task.equals("209") || task.equals("304") || task.equals("306") || task.equals("404") || task.equals("406") || task.equals("502") || task .equals("504") || task.equals("801") || task.equals("1001")){
                        main.addTaskClient(task,new NewsCommentSupportClient(task,ConfigerFactory.getConfiger().getTaskSleepTime(task)));
                    }
                    //论坛发帖,顶贴
//                    if(task.equals("301") || task .equals("302") || task.equals("401") || task.equals("402") || task.equals("701") ||task.equals("702")){
//                        main.addTaskClient(task,new BbsClient(task,ConfigerFactory.getConfiger().getTaskSleepTime(task)));

//                    }
                    if(task.equals("220")){
                        main.addTaskClient(task,new WeiboClient(task,0));
                    }
                }
            }
            if(Config.isVpn){
                main.setClientTools(VPN.getVPN());
            }
            main.enableSyncMode();
            main.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
