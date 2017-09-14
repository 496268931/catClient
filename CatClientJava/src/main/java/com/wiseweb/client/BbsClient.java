package com.wiseweb.client;

import com.wiseweb.bbs.BbsHttpClient;
import com.wiseweb.cat.task.TaskClient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.json.JSONArray;
import com.wiseweb.json.JSONObject;

/**
 * Created by 贾承斌 on 15/2/2.
 * 执行网页网页任务
 */
class BbsClient extends TaskClient {
    private int sleeptime = 1000 * 60;

    public BbsClient(String task, int sleeptime) {
        super(task, sleeptime);
    }

    protected void runTask(JSONObject taskObject, String task) {
        BbsHttpClient bbsClient = BbsHttpClient.getNewsCommentSupportHttpClient();
        int count=0;
        try {
            JSONObject result = getBroadcastResultJson();
            JSONArray taskresultList = new JSONArray();
            JSONObject r = getResultJson(taskObject);
            if (r == null) {
                info("执行异常清空任务");
                this.callback.getBroadcastTask(task).remove(taskObject);
            } else {
                long task_id = taskObject.getLong(Constants.TASK_ID);
                count = taskObject.getInt(Constants.COUNT);
                info("------------------------" + task_id + "   " + count + "     " + taskObject);
                if (!taskObject.has(Constants.TASK_TAG)) {
                    debug("TAG信息不存在");
                    this.callback.getBroadcastTask(task).remove(taskObject);
                    return;
                }
                JSONObject tag = taskObject.getJSONObject(Constants.TASK_TAG);
                String exeurl = tag.getString(Constants.EXE_URL);
                //执行程序
                bbsClient.BBSPostAction(exeurl, task_id, task, r, callback);
                taskresultList.put(r);
                debug("执行数量" + count);


                result.put(Constants.RESULT, taskresultList);
                this.callback.BroadCastTaskReport(result);
                Thread.currentThread().sleep(sleeptime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("论坛执行时异常，清空任务—>" + task, e);
        }finally {
            count--;
            if (count < 1) {
                this.callback.getBroadcastTask(task).remove(taskObject);
            }
            taskObject.put(Constants.COUNT, count);
        }
    }
}
