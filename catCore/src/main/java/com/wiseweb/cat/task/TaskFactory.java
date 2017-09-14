package com.wiseweb.cat.task;

import com.wiseweb.cat.base.BaseClass;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.util.Util;
import com.wiseweb.json.JSONException;
import com.wiseweb.json.JSONObject;

/**
 * 2014-5-26
 *
 * @author 贾承斌
 */
public class TaskFactory extends BaseClass {
    public static Task getTaskByJson(JSONObject taskjson) {
        Task task = null;
        if (!taskjson.has(Constants.TASK_ID)) {
            return null;
        }
        task = new Task(taskjson.getLong(Constants.TASK_ID));
        task.setTask_status(Constants.TASK_STATUS_WAIT);
        if (taskjson.has(Constants.TASK_TYPE)) {
            task.setTask_type(taskjson.getInt(Constants.TASK_TYPE));
            if (!(task.getTask_type() == Constants.TASK_TYPE_SCHEDULE || task.getTask_type() == Constants.TASK_TYPE_BROADCAST)) {
                return null;
            }
        }

        if (taskjson.has(Constants.TASK)) {
            task.setTask(taskjson.getInt(Constants.TASK));
        } else {
            return null;
        }

        if (taskjson.has(Constants.TASK_TAG)) {
            task.setTask_tag(taskjson.getJSONObject(Constants.TASK_TAG));
        } else {
            return null;
        }
        /*
         if (taskjson.has(TASK_PLATFROM)) {
         task.setTask(taskjson.getInt(TASK_PLATFROM));
         }
         */
        if (taskjson.has(Constants.TASK_START_TIME)) {
            task.setStart_time(taskjson.getString(Constants.TASK_START_TIME));
        }
        
        if (taskjson.has(Constants.TASK_CREATE_TIME)) {
            task.setCreat_time(taskjson.getString(Constants.TASK_CREATE_TIME));
        }else{
            task.setCreat_time(Util.getFromatDateString());
        }

        if (taskjson.has(Constants.TASK_STOP_TIME)) {
            task.setStop_time(taskjson.getString(Constants.TASK_STOP_TIME));
        }

        if (taskjson.has(Constants.TASK_SCHEDULE_MIN)) {
            task.setSchedule_min(taskjson.getInt(Constants.TASK_SCHEDULE_MIN));
        }

        if (taskjson.has(Constants.TASK_SCHEDULE_MAX)) {
            task.setSchedule_max(taskjson.getInt(Constants.TASK_SCHEDULE_MAX));
        }

        if (taskjson.has(Constants.TASK_IP_SET)) {
            task.setIPSet(taskjson.getString(Constants.TASK_IP_SET));
        }

        if (taskjson.has(Constants.TASK_USER_SET)) {
            task.setUserSet(taskjson.getString(Constants.TASK_USER_SET));
        }

        if (taskjson.has(Constants.TASK_NAME)) {
            task.setName(taskjson.getString(Constants.TASK_NAME));
        }
        if (taskjson.has(Constants.TASK_USERNAME)) {
            task.setUsername(taskjson.getString(Constants.TASK_USERNAME));
        }
        if (taskjson.has(Constants.TASK_USER_DISPLAYNAME)) {
            task.setUser_displayname(taskjson.getString(Constants.TASK_USER_DISPLAYNAME));
        }
        if (taskjson.has(Constants.TASK_FOREGROUNDID)) {
            task.setForegroundID(taskjson.getString(Constants.TASK_FOREGROUNDID));
        }

        if (taskjson.has(Constants.SUB_COUNT)) {
            task.setSub_count(taskjson.getInt(Constants.SUB_COUNT));
        } else {
            return null;
        }

        if (taskjson.has(Constants.TASK_NEED_SCHEDULE_TIMES)) {
            task.setNeed_schedule_times(taskjson.getInt(Constants.TASK_NEED_SCHEDULE_TIMES));
        } else {
            task.setNeed_schedule_times(Constants.DEFAULT_TASK_SCHEDULE_TIMES);
        }

        return task;

    }

    public static Task getTaskByString(String jsonString) {
        Task task = null;
        try {
            JSONObject taskjson = new JSONObject(jsonString);
            task = getTaskByJson(taskjson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return task;
    }

    /**
     * 根据父类和ID子类任务记录
     *
     * @param task
     * @param id
     * @return
     */
    public static SubTask getSubByTask(Task task, long id) {
        SubTask subtask = new SubTask(task.getTask_id(), id);
        subtask.setTask(task.getTask());
        subtask.setTag(task.getTask_tag());
        subtask.setStatus(Constants.TASK_STATUS_WAIT);
        return subtask;
    }

    public static SubTask getSubByTask(Task task) {
        return getSubByTask(task, System.currentTimeMillis());
    }

    public static SubTask getSubByJson(JSONObject task) {
        SubTask subtask = null;
        if (task == null) {
            return null;
        }
        try {
            long taskid = task.getLong(Constants.TASK_ID);
            long subid = task.getLong(Constants.SUB_ID);
            subtask = new SubTask(taskid, subid);
            if (!task.isNull(Constants.RESULT)) {
                subtask.setResult(task.getString(Constants.RESULT));
            }
            if (!task.isNull(Constants.STATUS)) {
                subtask.setStatus(task.getInt(Constants.STATUS));
            }

            if (!task.isNull(Constants.TIME)) {
                subtask.setTime(task.getString(Constants.TIME));
            }

            if (!task.isNull(Constants.TASK)) {
                subtask.setTask(task.getInt(Constants.TASK));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return subtask;
    }

    /**
     * 获取一个广播子任务 json
     *
     * @param task
     * @return
     */
    public static JSONObject getBroadCastSubByTask(Task task) {
        SubTask subtask = getSubByTask(task);
        JSONObject json = subtask.toJSONObject();
        json.put(Constants.ACTION, Constants.ACTION_BROADCAST);
        return json;
    }

    /**
     * 获得一个调度任务 json
     *
     * @param task
     * @return
     */
    public static JSONObject getNewScheduleSubByTask(Task task) {
        SubTask subtask = getSubByTask(task);
        JSONObject json = subtask.toJSONObject();
        json.put(Constants.ACTION, Constants.ACTION_SCHEDULE);
        return json;

    }

}
