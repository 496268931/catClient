package com.wiseweb.cat.task;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.json.JSONObject;

import java.util.Timer;


/**
 * 2014-6-12
 *
 * @author 贾承斌
 */
public abstract class TaskClientForBroadCast implements Runnable {

    /**
     * 定时器
     */
    Timer timer;
    /**
     * 执行标签
     */
    protected JSONObject json;
    /**
     * 批量上报数量 默认数量10
     */
    protected int reportCount = 10;// 
    /**
     * 执行次数
     */
    protected int count = 0;// 
    /**
     * 默认间隔时间,单位毫秒
     */
    protected int sleepTime = -1;
    /**
     * 回掉函数
     */
    protected TaskClientCallback callback;

    public TaskClientForBroadCast(JSONObject json, TaskClientCallback callback) {

        this.callback = callback;
        this.json = json;
        timer = new Timer();
    }
    
    
     public TaskClientForBroadCast(JSONObject json, TaskClientCallback callback,int reportCount) {
        this(json, callback);
        this.reportCount = reportCount;
    }
    
    
    /**
     * 数据内容
     * @param json
     * @param callback 回传
     * @param sleeptime 睡眠时间
     * @param reportCount  网站数量
     */
     public TaskClientForBroadCast(JSONObject json, TaskClientCallback callback,int sleeptime,int reportCount) {
        this(json,callback);
        this.reportCount = reportCount;
        this.sleepTime = sleeptime;
       
    }
     
    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    /**
     * 根据任务json创建返回结果json对象
     * @return
     */
    protected JSONObject getBroadCastResultJson() {
        JSONObject result = new JSONObject();
        result.put(Constants.ACTION, Constants.ACTION_BROADCAST_REPORT);
        result.put(Constants.TASK, json.getString(Constants.TASK));
        result.put(Constants.TASK_ID, json.getLong(Constants.TASK_ID));
        result.put(Constants.SUB_ID, System.currentTimeMillis());
        result.put(Constants.AREA, json.getString(Constants.AREA));
        return result;
    }
    /**
     * 获取任务完成结果
     * @param task_id
     * @return 
     */
 protected JSONObject getBroadCastCompleteJson(long task_id) {
        JSONObject result = new JSONObject();
        result.put(Constants.ACTION, Constants.ACTION_BROADCAST_COMPLETE);
        result.put(Constants.TASK_ID,task_id);
        return result;
    }

}
