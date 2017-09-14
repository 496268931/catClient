package com.wiseweb.client;

import com.wiseweb.cat.task.TaskClient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.json.JSONArray;
import com.wiseweb.json.JSONObject;
import com.wiseweb.NewsComment.NewsCommentHttpClient;

/**
 * Created by 贾承斌 on 15/2/2.
 * 执行网页网页任务
 */
public class NewsCommentClient extends TaskClient {
    private int sleeptime = 1000 * 60;
    /**
     * 需要登陆的任务类型,拼接起来用于判断
     */
    String tasks = Constants.NEW_163_COMMENTS + ","
            + Constants.NEW_SINA_COMMENTS + ","
            + Constants.NEW_SOHU_COMMENTS + ","
            + Constants.NEW_IFENG_COMMENTS + ","
            + Constants.NEW_163_APP_COMMENTS + ","
            + Constants.NEW_SINA_APP_COMMENTS + ","
            + Constants.NEW_PENGPAI_APP_COMMENTS + ","
            + Constants.NEW_TOUTIAO_APP_COMMENTS;


    public NewsCommentClient(String task, int sleeptime) {
        super(task, sleeptime);
    }

    protected void runTask(JSONObject taskObject, String task) {
        int count=0;
        try {
            JSONObject result = getBroadcastResultJson();
            JSONArray taskresultList = new JSONArray();
            JSONObject r = getResultJson(taskObject);
            NewsCommentHttpClient newsClient = NewsCommentHttpClient.getNewsCommentHttpClient();
            if (r == null) {
                info("执行异常清空任务");
                this.callback.getBroadcastTask(task).remove(taskObject);
            } else {
                long task_id = taskObject.getLong(Constants.TASK_ID);
                count = taskObject.getInt(Constants.COUNT);
                info("------------------------" + task_id + "   " + count + "     " + taskObject);
                if (!taskObject.has(Constants.TASK_TAG)) {
                    debug("TAG信息不存在");
                    return;
                }
                JSONObject tag = taskObject.getJSONObject(Constants.TASK_TAG);
                String exeurl = null;
                JSONObject form = null;
                if (tag.has(Constants.EXE_URL)) {
                    exeurl = tag.getString(Constants.EXE_URL);
                    if (tag.has(Constants.FORM)) {
                        form = tag.getJSONObject(Constants.FORM);
                    }
                } else {
                    form = tag.getJSONObject(Constants.TASK_TAG_DATA);
                }
                //执行程序
                newsClient.NewsCommentAction(exeurl, form, task_id, task, r, callback);
                taskresultList.put(r);
                debug("执行数量" + count);
                result.put(Constants.RESULT, taskresultList);
                this.callback.BroadCastTaskReport(result);
                Thread.currentThread().sleep(sleeptime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("新闻评论执行时异常，清空任务—>" + task, e);
        }finally {
            count--;
            if (count < 1) {
                this.callback.getBroadcastTask(task).remove(taskObject);
            }
            taskObject.put(Constants.COUNT, count);
        }
    }
}
