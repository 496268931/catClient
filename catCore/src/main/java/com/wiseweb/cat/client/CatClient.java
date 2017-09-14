package com.wiseweb.cat.client;

import com.wiseweb.cat.base.*;
import com.wiseweb.cat.task.TaskClient;
import com.wiseweb.json.JSONObject;
import com.wiseweb.cat.base.JMessage;
import com.wiseweb.cat.task.TaskController;

import java.util.Map;

/**
 * Created by 贾承斌 on 15/11/7.
 */
public class CatClient extends BaseClass {
    private TaskController taskController;// = new TaskController(this);
    private TcpTaskClient tcpClient;
    private HttpTaskClient httpClient;

    public void setClientTools(ClientTools clientTools) {
        this.clientTools = clientTools;
    }

    private ClientTools clientTools = null;
    private boolean httpEnable = false;
    private boolean tcpEnable = false;
    private boolean syncMode = false;

    public CatClient() {
        taskController = new TaskController(this);
        this.httpClient = new HttpTaskClient(taskController);
    }

    public void enableTcpConnect() {
        tcpEnable = true;
        if (tcpClient == null) {
            this.tcpClient = new TcpTaskClient(taskController);
        }

    }

    public void enableHttpConnect() {
        httpEnable = true;
        if (httpClient == null) {
            this.httpClient = new HttpTaskClient(taskController);
        }
    }

    /**
     * 同步模式
     */
    public void enableSyncMode() {

        syncMode = true;
        httpEnable = false;
        tcpEnable = false;
        if (httpClient == null) {
            this.httpClient = new HttpTaskClient(taskController);
        }
    }

    public void start() {
        info("当前版本"+ConfigerFactory.getConfiger().getVersion());
        if(httpEnable||syncMode){
            if(!httpClient.reg()){
                return;
            }
        }
        if (syncMode) {
            info("启动同步模式。");
            taskController.startSync();
            return;//互斥
        }

        if (httpEnable) {
            info("启动Http 任务接口。");
            httpClient.start();
        }
        if (tcpEnable) {
            info("启动TCP 任务接口。");
            tcpClient.start();
        }

    }


    public void stop() {

        if (httpClient != null) {
            httpClient.stop();
            info("停止Http 任务接口。");
        }
        if (tcpClient != null) {
            tcpClient.stop();
            info("停止TCP 任务接口。");
        }
        if (syncMode) {
            info("停止同步模式。");
            taskController.stopSync();
        }
    }
    //--------------------------设置任务执行端-----------------------------------------//

    /**
     * 向控制添加 任务执行端
     * @param task 类型
     * @param client 任务执行端
     */
    public void addTaskClient (String task, TaskClient client){
        if(task!=null&&!task.equals("")&&client!=null) {
            client.setCallback(this.taskController);
            taskController.addTaskClient(task, client);
        }
    }

    /**
     * 同时为多种 任务类型添加相同的执行端
     * @param tasks
     * @param client
     */
    public void addTaskClient (String[] tasks, TaskClient client){
        if(tasks!=null&&tasks.length>0){
            for (int i=0;i<tasks.length;i++){
                addTaskClient(tasks[i],client);
            }
        }
    }



    //-----------------------数据交互方法------------------------------//
    public JSONObject getOneAccount(String task_id) {
        return httpClient.getOneAccount(task_id);
    }

    public void updateAccount(final String _id, final Map<String, String> params) {
        if (syncMode) {
            httpClient.syncUpdateAccount(_id, params);
        } else {
            httpClient.asyncUpdateAccount(_id, params);
        }

    }

    public String getDoc(String task_id) {
        return httpClient.getDoc(task_id);
    }

    public void asyncCheckBroadCastTask() {
        httpClient.asyncCheckBroadCastTask();

    }
    public void syncCheckBroadCastTask() {
        httpClient.syncCheckBroadCastTask();

    }

    public void sendJMessage(JMessage message) {
        tcpClient.sendJMessage(message);
    }

    public void updateTask(final Map<String, String> params) {
        if (syncMode) {
            httpClient.syncUpdateTask(params);
        } else {
            httpClient.asyncUpdateTask(params);
        }

    }

    public void BroadCastTaskReport(String s) {
        httpClient.BroadCastTaskReport(s);
    }
    public ClientTools getClientTools() {
        return this.clientTools;
    }
}
