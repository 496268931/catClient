package com.wiseweb.cat.task;

import com.wiseweb.json.JSONObject;

import java.io.Serializable;

/**
 * 2014-5-14
 *
 * @author 贾承斌
 */
public class Task implements Serializable {

    /**
     * 任务编号
     */
    private long task_id;//
    /**
     * 任务类型 广播任务还是调度任务
     */
    private int task_type;//
    /**
     * 执行动作
     */
    private int task;//
    //   private int task;//
    /**
     * 任务名称
     */
    private String name;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 用户显示名称
     */
    private String user_displayname;
    /**
     * 前台id
     */
    private String foregroundID;

    /**
     * 任务标签
     */
    private JSONObject task_tag;//执行标签
    /**
     * 任务起始时间
     */
    private String start_time;//
    /**
     * 任务结束时间
     */
    private String stop_time;//
    //任务执行基间隔 单位统一为秒

    /**
     * 调度最小时间间隔
     */
    private int schedule_min = 1;//  
    /**
     * 调度最大时间间隔
     */
    private int schedule_max = 30;//最大值

//    private int[] task_cycle;//执行周期
    /**
     * 放置对ip的要求
     */
    private String IPSet;//
    /**
     * 放置对找那个号的要求
     */
    private String userSet;//
    /**
     * 执行版本号，区分不同功能的执行,
     */
    private int exeVersion;//
    /**
     * 任务执行次数 用户记录父类执行的次数
     */
    private int scheduled_times = 0;//
    /**
     * 调度次数 默认三次 ，任务被重复执行的次数
     */
    private int need_schedule_times = 3;//
    /**
     * 需要执行 子任务量
     */
    private int sub_count = 0;//
    /**
     * 已经执行 子任务执行量
     */
    private int sub_schedule_count = 0;//
    /**
     * 任务状态
     */
    private int task_status = 0;//
    
      private String create_time;//
    

    public Task() {
    }

    public String getCreat_time() {
        return create_time;
    }

    public void setCreat_time(String creat_time) {
        this.create_time = creat_time;
    }

    public int getSub_count() {
        return sub_count;
    }

    /**
     * 设置需要执行的量
     *
     * @param sub_count
     */
    public void setSub_count(int sub_count) {
        this.sub_count = sub_count;
    }

    /**
     * 获取已经执行的量
     *
     * @return
     */
    public int getSub_schedule_count() {
        return sub_schedule_count;
    }

    /**
     * 设置已经执行的量
     *
     * @param sub_schedule_count
     */
    public void setSub_schedule_count(int sub_schedule_count) {
        this.sub_schedule_count = sub_schedule_count;
    }

   // private JSONObject json;
    public Task(long id) {
        this.task_id = id;
    }

    public int getTask_type() {
        return task_type;
    }

    public void setTask_type(int task_type) {
        this.task_type = task_type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public String getiIPSet() {
        return IPSet;
    }

    public void setIPSet(String IPSet) {
        this.IPSet = IPSet;
    }

    public String getUserSet() {
        return userSet;
    }

    public void setUserSet(String userSet) {
        this.userSet = userSet;
    }

    public int getScheduled_times() {
        return scheduled_times;
    }

    public void setScheduled_times(int scheduled_times) {
        System.out.println("=================>" + scheduled_times);
        this.scheduled_times = scheduled_times;
    }

    public int getNeed_schedule_times() {
        return need_schedule_times;
    }

    public void setNeed_schedule_times(int need_schedule_times) {
        this.need_schedule_times = need_schedule_times;
    }

    /*  public JSONObject getJson() {
     return json;
     }

     public void setJson(JSONObject json) {
     this.json = json;
     }
     */
    public int getSchedule_min() {
        return schedule_min;
    }

    public void setSchedule_min(int schedule_min) {
        this.schedule_min = schedule_min;
    }

    public int getSchedule_max() {
        return schedule_max;
    }

    public void setSchedule_max(int schedule_max) {
        this.schedule_max = schedule_max;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public int getExeVersion() {
        return exeVersion;
    }

    public void setExeVersion(int exeVersion) {
        this.exeVersion = exeVersion;
    }

    public int getTask_status() {
        return task_status;
    }

    public void setTask_status(int task_status) {
        this.task_status = task_status;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public JSONObject getTask_tag() {
        return task_tag;
    }

    public void setTask_tag(JSONObject task_tag) {
        this.task_tag = task_tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_displayname() {
        return user_displayname;
    }

    public void setUser_displayname(String user_displayname) {
        this.user_displayname = user_displayname;
    }

    public String getForegroundID() {
        return foregroundID;
    }

    public void setForegroundID(String foregroundID) {
        this.foregroundID = foregroundID;
    }
    

}
