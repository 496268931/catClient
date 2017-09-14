package com.wiseweb.cat.task;

import com.wiseweb.cat.base.*;
import com.wiseweb.cat.client.CatClient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.json.JSONObject;
import com.wiseweb.cat.base.JMessage;
import com.wiseweb.cat.base.JMessageFactory;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 2014-6-12 任务执行控制类
 *
 * @author 贾承斌
 */
public class TaskController extends BaseClass implements TaskClientCallback {
//    private static String[][] taskClientShareGroup = {//用户共享执行端实例
//            {
//                    Constants.WEIBO_SINA_POST,
//                    Constants.WEIBO_SINA_COMMENT,
//                    Constants.WEIBO_SINA_REPOST,
//                    Constants.WEIBO_SINA_COMMENT_AND_REPOST,
//                    Constants.WEIBO_SINA_COMMENT_TO_COMMENT
//            },
//            {
//                    Constants.WEIBO_SINA_SUPPORT,
//                    Constants.WEIBO_SINA_ADD_ATTENTION_FOR_USER,
//                    Constants.WEIBO_SINA_SUPPORT_FOT_COMMENT,
//                    Constants.WEIBO_SINA_MOVIE_SCORE,
//                    Constants.WEIBO_SINA_ADD_ATTENTION_FOR_TOPIC
//            }
//    };

    private Boolean enableSyncMode = false;
    private long syncTime = 5 * 1000;//最短等待更新时间;

    /**
     * 连接器，客户端，用于发送报告
     */
    private CatClient catClient;

    /**
     * 用于保存调度任务*
     */
    private Map<String, List<JSONObject>> schedule_type_map = new HashMap<>();
    /**
     * 用于保存广播任务*
     */
    private Map<String, List<JSONObject>> broadcast_type_map = new HashMap<>();
    /**
     * 保存执行任务具体的执行类*
     */
    private Map<String, TaskClient> task_client_map = new HashMap<>();

    /**
     * 线程池
     */
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * 构造函数
     *
     * @param catClient
     */
    public TaskController(CatClient catClient) {
        this.catClient = catClient;
        setTaskType(ConfigerFactory.getConfiger().getScheduleTask(), ConfigerFactory.getConfiger().getBroadcastTask());
    }


    /**
     * 设置调度类型
     *
     * @param schedule  调度类型
     * @param broadcast 广播类型
     */
    public void setTaskType(String schedule, String broadcast) {
        //调度类型
        if (this.schedule_type_map == null) {
            schedule_type_map = new HashMap<>();
        }
        if (schedule != null) {
            String[] typeList = schedule.split(",");
            for (String type : typeList) {
                schedule_type_map.put(type, new ArrayList<JSONObject>());
            }
        }

        //广播类型
        if (this.broadcast_type_map == null) {
            broadcast_type_map = new HashMap<>();
        }

        if (broadcast != null) {
            String[] typeList = broadcast.split(",");
            for (String type : typeList) {
                List<JSONObject> taskList = new ArrayList<>();
                broadcast_type_map.put(type, taskList);
            }
        }
    }

    //同步模式启动执行端
    public void startSync() {
        info("以同步方式启动");
        enableSyncMode = true;
        long startTime = 0;
        long endTime = 0;

        while (enableSyncMode) {
            try {
                startTime = System.currentTimeMillis();
                catClient.syncCheckBroadCastTask();
                triggerAllClient();
                endTime = System.currentTimeMillis();

                if ((endTime - startTime) < this.syncTime) {
                    Thread.currentThread().sleep(this.syncTime);
                }
            } catch (Exception e) {
                error("同步执行异常：", e);
            }
        }
    }

    public void stopSync() {
        this.enableSyncMode = false;

    }


    /**
     * @param result
     */
    @Override
    public void taskReport(JSONObject result) {
        JMessage message = JMessageFactory.getRequsetMessageByContent(result.toString());
        catClient.sendJMessage(message);

    }

    @Override
    public void taskBroadCastComplete(JSONObject task) {
        removeBroadCastTask(task);
        //        removeBroadCastTaskFromMap(task);
    }

    @Override
    public List<JSONObject> getBroadcastTask(String task) {
        return this.broadcast_type_map.get(task);
    }

    @Override
    public List<JSONObject> getScheduleTask(String task) {
        return this.schedule_type_map.get(task);
    }

    @Override
    public JSONObject getTaskAccountByTaskID(String task_id) {

        return this.catClient.getOneAccount(task_id);

    }

    @Override
    public String getTaskDocByTaskID(String task_id) {
        return this.catClient.getDoc(task_id);
    }

    public void updateTask(Map params) {
        this.catClient.updateTask(params);
    }

    @Override
    public void updateAccountByID(String account_id, Map info) {
        this.catClient.updateAccount(account_id, info);
    }

    @Override
    public ClientTools getClientTools() {
        return catClient.getClientTools();
    }

    @Override
    public void BroadCastTaskReport(JSONObject result) {
        catClient.BroadCastTaskReport(result.toString());
    }

    /**
     * 添加 调度任务
     *
     * @param task
     */
    public void addRunningScheduleTask(JSONObject task) {
        addRunningTask(task, schedule_type_map);
    }

    /**
     * 添加广播任务
     *
     * @param task
     */
    public void addRunningBroadcastTask(JSONObject task) {
        logger.info("添加广播任务" + task.toString());
        addRunningTask(task, broadcast_type_map);
    }

    /**
     * 将任务添加到指定的队列中
     *
     * @param task
     * @param taskMap
     */
    private void addRunningTask(JSONObject task, Map<String, List<JSONObject>> taskMap) {
        if (task.has(Constants.TASK)) {
            String type = task.getString(Constants.TASK);
            long task_id = task.getLong(Constants.TASK_ID);
            if (task_id != 0) {
                List<JSONObject> list = taskMap.get(type);
                if (!hasTask(list, task_id)) {
                    list.add(task);
                }
              //  triggerClient(type);
            }
        }
    }

    /**
     * 移除广播任务
     *
     * @param task
     */
    public void removeBroadCastTask(JSONObject task) {
        removeRunningTask(task, broadcast_type_map);
    }

    /**
     * 移除调度任务
     *
     * @param task
     */
    public void removeScheduleTask(JSONObject task) {
        removeRunningTask(task, schedule_type_map);
    }

    /**
     * 移除任务
     *
     * @param task
     * @param clientMap
     */
    private void removeRunningTask(JSONObject task, Map<String, List<JSONObject>> clientMap) {
        if (task.has(Constants.TASK)) {
            String type = task.getString(Constants.TASK);
            long task_id = task.getLong(Constants.TASK_ID);
            if (task_id != 0) {
                List<JSONObject> list = clientMap.get(type);
                list.remove(task);
            }
        }
    }

    /**
     * 添加执行端
     * @param task 任务类型
     * @param client  具体的执行端
     */
    public void addTaskClient(String task,TaskClient client){
        task_client_map.put(task,client);
    };

    /**
     * 移除 执行端
     * @param task 任务类型
     */
    public void removeTaskClient(String task){
        task_client_map.remove(task);
    };

    /**
     * 触发执行类型的执行端
     * 首先判断执行端是否初始化，然后通过线程池调用
     *
     * @param task_type 任务类型代码
     */
    private void triggerClient(String task_type) {
        logger.info("触发任务:" + task_type);
        TaskClient client = null;
        client = task_client_map.get(task_type);
        if (client == null) {
            logger.error("此任务无法执行,无可用执行任务的Client：" + task_type);
            return;
        }
        if (!client.isRunning() && (enableSyncMode == false)) {
            threadPool.execute(client);
        } else if (enableSyncMode == true) {
            client.syncRun();
        }
    }

    /**
     * 觸發所有客戶端執行任務
     */
    public void triggerAllClient(){
        List<TaskClient> list =new ArrayList();

        for(String key : task_client_map.keySet()){
            TaskClient a =   task_client_map.get(key);
            if(!list.contains(a)){
                list.add(a);
            }
        }
        for(TaskClient one : list){
            one.syncRun();
        }

    }



    /**
     * 查找类表中是否有指定的id
     *
     * @param list
     * @param task_id
     */
    private boolean hasTask(List<JSONObject> list, long task_id) {
        for (JSONObject task : list) {
            long id = task.getLong(Constants.TASK_ID);
            if (id == task_id) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取支持广播任务 的类型.如果没有设置类型,则返回空
     *
     * @return
     */
    public String getBroadcastType() {
        if (broadcast_type_map.isEmpty()) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (String key : this.broadcast_type_map.keySet()) {
            sb.append(key);
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * 获得正在运行的广播的任务id
     *
     * @return
     */
    public String getRunningBroadcastTaskID() {
        if (broadcast_type_map.isEmpty()) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (String key : this.broadcast_type_map.keySet()) {
            List<JSONObject> mylist = broadcast_type_map.get(key);
            if (mylist != null && (!mylist.isEmpty())) {
                for (JSONObject task : mylist) {
                    if (task.has(Constants.TASK_ID)) {
                        sb.append(task.getLong(Constants.TASK_ID));
                        sb.append(",");
                    }
                }
            }
        }
        return sb.toString();
    }
}
