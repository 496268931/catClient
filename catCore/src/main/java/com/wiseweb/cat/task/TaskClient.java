package com.wiseweb.cat.task;

import com.wiseweb.cat.base.BaseClass;
import com.wiseweb.cat.base.ClientTools;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.json.JSONObject;
import com.wiseweb.cat.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 2014-6-12
 *
 * @author 贾承斌
 */
public abstract class TaskClient extends BaseClass implements Runnable {
    /**
     * 执行标签
     */
    protected List<String> tasks;

    protected int sleeptime = 15000;
    protected String RobotIP = "127.0.0.1";
    protected int RobotPort = 50000;
    protected String task;
    protected String task_id;
    /**
     * 回掉函数
     */
    protected TaskClientCallback callback=null;

    public TaskClient(TaskClientCallback callback, String task, int sleeptime, String RobotIP, int RobotPort) {
        this.callback = callback;
        this.task = task;
        this.sleeptime = sleeptime;
        this.RobotIP = RobotIP;
        this.RobotPort = RobotPort;
        tasks = new ArrayList<>();
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

//    [info][2016-07-13 14:01:42][com.wiseweb.datatask.impl.BbsTask]----->上报信息：{"date":"2016-07-12","data":"{\"totle\":0,\"protal\":[{\"name\":\"else\",\"count\":0},{\"name\":\"tieba\",\"count\":0}]}","movieId":"56c1b0efd04c83dc61ed0c1c"}
//    上报结果：200—>{"

    public TaskClient(TaskClientCallback callback, String task, int sleeptime) {
        this.callback = callback;
        this.sleeptime = sleeptime;
        this.task = task;
        tasks = new ArrayList<>();
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    public TaskClient(String task, int sleeptime) {
        this.sleeptime = sleeptime;
        this.task = task;
        tasks = new ArrayList<>();
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    public void addTask(String task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    public void setCallback(TaskClientCallback callback) {
        this.callback = callback;
    }

    /**
     * 标记执行是否在运行
     */
    protected boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public TaskClient(TaskClientCallback callback, String task) {
        this.callback = callback;
        this.task = task;
        tasks = new ArrayList<>();
        if (!tasks.contains(task)) {
            tasks.add(task);
        }
    }

    /**
     * 根据任务json创建返回结果json对象
     *
     * @return
     */
    protected JSONObject getResultJson(JSONObject json) {
        try {
            JSONObject result = new JSONObject();

            result.put(Constants.TASK, json.getString(Constants.TASK));
            result.put(Constants.TASK_ID, json.getLong(Constants.TASK_ID));
            result.put(Constants.SUB_ID, json.has(Constants.SUB_ID) ? json.getLong(Constants.SUB_ID) : System.currentTimeMillis());
            result.put(Constants.TIME, Util.getDateFormat());
            if (json.has(Constants.TASK_FOREGROUNDID)) {
                result.put(Constants.TASK_FOREGROUNDID, json.get(Constants.TASK_FOREGROUNDID));
            }
            if (json.has(Constants.USER_ID)) {
                result.put(Constants.USER_ID, json.get(Constants.USER_ID).toString());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            error("统一封装返回信息异常", e);
        }
        return null;
    }

    /**
     * 初始化一个广播上报类
     *
     * @param
     * @return
     */
    protected JSONObject getBroadcastResultJson() {
        JSONObject result = new JSONObject();
        result.put(Constants.ACTION, Constants.ACTION_BROADCAST_REPORT);
        return result;
    }


    /**
     * 原则：调度优先，所有调度任务执行完再执行广播
     * 广播任务是按照类型走的，先执行一类热舞执行一遍，在执行另外类型的任务。调度执行穿插执行
     */
    @Override
    public void run() {
        this.running = true;
        info("==================执行端启动===============");
        while (running) {

            boolean needContinue = false;
//            for (String task : tasks) {
            for (int i = 0; i < tasks.size(); i++) {
                String task = tasks.get(i);
                List<JSONObject> broadcastlist = this.callback.getBroadcastTask(task);
                if ((broadcastlist != null && !broadcastlist.isEmpty())) {
                    debug("======》执行广播任务:" + task);
                    List<JSONObject> broadcastTempList = null;
                    broadcastTempList = new ArrayList<>();
                    broadcastTempList.addAll(broadcastlist);
                    while ((broadcastTempList != null && !broadcastTempList.isEmpty())) {
                        runScheduleTask();
                        runOneBroadcastTask(broadcastTempList, task);
                    }
                    needContinue = needContinue || true;
                }
            }
            running = needContinue;
        }
        info("==================任务执行完成，执行端停止===============");
    }

    public void syncRun() {
        this.running = true;
        info("==================执行端启动:同步模式===============");
//        while (running) {

//            boolean needContinue = false;

            for (int i = 0; i < tasks.size(); i++) {
                String task = tasks.get(i);
                List<JSONObject> broadcastlist = this.callback.getBroadcastTask(task);
                if ((broadcastlist != null && !broadcastlist.isEmpty())) {
                    debug("======》执行广播任务:" + task);
                    List<JSONObject> broadcastTempList = null;
                    broadcastTempList = new ArrayList<>();
                    broadcastTempList.addAll(broadcastlist);
                    while ((broadcastTempList != null && !broadcastTempList.isEmpty())) {
                        runOneBroadcastTask(broadcastTempList, task);
                        debug("同步停顿");
                    }
//                    needContinue = needContinue || true;
                }
            }
//            running = needContinue;
//        }
        info("==================任务执行完成，执行端停止：同步模式===============");
    }


    /**
     * 执行调度方法
     *
     * @return
     */
    protected boolean runScheduleTask() {
        boolean doTask = false;
        boolean hasTask = true;
        while (hasTask) {
            boolean needContinue = false;
            for (String task : tasks) {
                List<JSONObject> schedulelist = this.callback.getScheduleTask(task);
                if (schedulelist != null && !schedulelist.isEmpty()) {
                    debug("有需要执行调度任务：task" + task);
                    runOneScheduleTask(schedulelist);
                    doTask = true;
                    needContinue = needContinue || true;//
                } else {
                    needContinue = needContinue || false;//
                }
            }
            hasTask = needContinue;
        }
        debug("调度任务执行完成");
        return doTask;
    }


    /**
     * 执行一个广播任务
     *
     * @param broadcastTemplist 从广播任务临时对垒汇总出去一个 执行完并删除
     * @throws Exception json获取数据异常
     */
    protected void runOneBroadcastTask(List<JSONObject> broadcastTemplist, String task) {
        JSONObject oneTask = broadcastTemplist.get(0);
        if (oneTask.has(Constants.TASK_ID)) {
            debug("执行广播任务===>" + oneTask.get(Constants.TASK_ID));
            runTask(oneTask, task);
        } else {
            debug("执行广播任务===>" + "未知ID");
        }
        broadcastTemplist.remove(oneTask);
        ClientTools clientTools=  callback.getClientTools();
        if(clientTools!=null){
            clientTools.run();
        }
    }

    ;

    /**
     * 执行一个调度任务
     * 从调度任务队列取出第一个，然后执行并删除
     *
     * @param schedulelist 调度任务队列
     */
    protected void runOneScheduleTask(List<JSONObject> schedulelist) {
        if (schedulelist != null && !schedulelist.isEmpty()) {
            JSONObject oneTask = schedulelist.get(0);
            if (oneTask.has(Constants.TASK_ID)) {
                debug("执行调度任务===>" + oneTask.get(Constants.TASK_ID));
            } else {
                debug("执行调度任务===>" + "未知ID");
            }
            runTask(oneTask, oneTask.getString(Constants.TASK));
            schedulelist.remove(oneTask);
        }
    }

    /**
     * 具体的方法执行 执行的是 广播任务
     * 需要执行的内容
     * 1.组装结果对象
     * 2.获取参数
     * 3.更新结果
     *
     * @param task
     */
    protected abstract void runTask(JSONObject taskObject, String task);
}
