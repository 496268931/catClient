package com.wiseweb.client;

import com.wiseweb.NewsComment.NewsCommentSupportHttpClient;
import com.wiseweb.NewsComment.News_toutiao_praiseClient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClient;
import com.wiseweb.json.JSONArray;
import com.wiseweb.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsCommentSupportClient extends TaskClient {
    private int sleeptime = 1000 * 60;
    public NewsCommentSupportClient(String task, int sleeptime) {
        super(task, sleeptime);
    }

    @Override
    protected void runTask(JSONObject taskObject1,String task) {
        this.running = true;
        info("新闻评论支持启动");
        List<JSONObject> broadcastlist = this.callback.getBroadcastTask(task);
        NewsCommentSupportHttpClient newsClient = NewsCommentSupportHttpClient.getNewsCommentSupportHttpClient();
        String exeurl = null;
        JSONObject result = null;
        List<JSONObject> temp = null;
        while (!broadcastlist.isEmpty()){
            temp = new ArrayList<>();
            temp.addAll(broadcastlist);
            try {
                result = getBroadcastResultJson();
                JSONArray taskresultList = new JSONArray();
                for (JSONObject taskObject : temp) {
                    int count=0;
                    try {
                        //准备返回信息
                        JSONObject r = getResultJson(taskObject);
                        if (r == null) {
                            info("封装返回信息异常，清空任务—>"+task);
                            broadcastlist.remove(taskObject);
                        } else {
                            long task_id = taskObject.getLong(Constants.TASK_ID);
                            count = taskObject.getInt(Constants.COUNT);
                            info("------------------------"+task_id+"   "+count+"     "+taskObject);
                            JSONObject tag = taskObject.getJSONObject(Constants.TASK_TAG);
                            //获取执行地址
                            JSONObject form = null;
                            if(tag.has(Constants.EXE_URL)){
                                exeurl = tag.getString("exeurl");
                                //获取请求表单与请求消息头
                                if(tag.has(Constants.FORM)){
                                    form = tag.getJSONObject(Constants.FORM);
                                }
                            }else if(tag.has(Constants.TASK_TAG_DATA)){
                                form = tag.getJSONObject(Constants.TASK_TAG_DATA);
                            }
                            //开始执行
                            if(task.equals(Constants.NEW_TOUTIAO_APP_SUPPORT)){
                                News_toutiao_praiseClient client = new News_toutiao_praiseClient();
                                client.ToutiaoPraise(exeurl, form, task_id, task, r, callback);
                            }else{
                                newsClient.NewsCommentSupportAction(form, exeurl, task, r);
                            }
                            taskresultList.put(r);

                            debug("执行数量" + count);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        error("新闻评论支持执行时异常，清空任务—>"+task,e);
                    }finally {
                        count--;
                        if (count < 1) {
                            broadcastlist.remove(taskObject);
                        }
                        taskObject.put(Constants.COUNT,count);
                    }
                }
                result.put(Constants.RESULT, taskresultList);
                this.callback.BroadCastTaskReport(result);
                Thread.sleep(sleeptime);
            } catch (Exception e) {
                error("新闻评论支持while循环时异常—>"+task,e);
            }
        }
        //*************当 任务为空 则技术 执行逻辑结束*****************
        this.running = false;
        info("新闻评论支持停止");
    }


//    @Override
//    protected void runTask(JSONObject taskObject,String task) {
//
//    }

}
