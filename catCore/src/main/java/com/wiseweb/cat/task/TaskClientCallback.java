/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wiseweb.cat.task;

import com.wiseweb.cat.base.ClientTools;
import com.wiseweb.json.JSONObject;


import java.util.List;
import java.util.Map;

/**
 *回掉类 用户任务执行完成 回掉
 * @author apple
 */
public interface TaskClientCallback {

    /**
     * 任务完成汇报，用于上报调度任务 和 广播任务的任务
     * @param result 
     */
    public void taskReport(JSONObject result);
    /**
     * 
     * @param result 
     */
    public void BroadCastTaskReport(JSONObject result);
    /**
     * 广播完成
     * @param
     */
    public void taskBroadCastComplete(JSONObject task);

    /**
     * 获取待执行的 广播任务
     * @param task
     * @return
     */
    public List<JSONObject> getBroadcastTask(String task);

    /**
     * 获取
     * @param task
     * @return
     */
    public List<JSONObject> getScheduleTask(String task);

    /**
     * 通过任务id获取任务账号
     * @return 账号信息，用户名、密码、cookie,token
     */
    public JSONObject getTaskAccountByTaskID(String task_id);
    
    /**
     * 标识任务失败
     * @param params
     */
    public void updateTask(Map params);

    /**
     * 通过任务id获取预料
     * @return
     */
    public  String getTaskDocByTaskID(String task_id);

    /**
     * 通过id跟新账号
     * @param account_id
     * @param info
     */
    public  void  updateAccountByID(String account_id,Map info);

    public ClientTools getClientTools();
}
