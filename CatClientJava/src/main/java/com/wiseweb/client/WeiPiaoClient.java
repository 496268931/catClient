package com.wiseweb.client;

import com.wiseweb.WeiPiao.WeiPiaoHttpClient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClient;
import com.wiseweb.json.JSONArray;
import com.wiseweb.json.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wiseweb on 2016/3/4.
 */
public class WeiPiaoClient extends TaskClient {
    public WeiPiaoClient(String task, int sleeptime) {
        super(task, sleeptime);
    }

    @Override
    protected void runTask(JSONObject jsonObject, String s) {
        int count=0;
        try {
            JSONObject result = getBroadcastResultJson();
            JSONArray taskresultList = new JSONArray();
            JSONObject r = getResultJson(jsonObject);
            if (r == null) {
                logger.info("执行异常清空任务");
                this.callback.getBroadcastTask(task).remove(jsonObject);
            } else {
                task_id = jsonObject.getLong(Constants.TASK_ID)+"";
                count = jsonObject.getInt(Constants.COUNT);
                info("执行数量："+count);
                JSONObject task_tag = jsonObject.getJSONObject(Constants.TASK_TAG);
                info("执行任务：" + task_tag);
                //执行程序
                JSONObject res = implementTask(task_tag.getJSONObject(Constants.TASK_TAG_DATA).getJSONObject(Constants.TASK_TAG_DATA_FORM),task_id,r);
                if(res!=null && res.length()>0){
                    Iterator i = res.keys();
                    while(i.hasNext()){
                        String key = (String)i.next();
                        r.put(key,res.get(key));
                    }
                }
                taskresultList.put(r);
                result.put(Constants.RESULT, taskresultList);
                this.callback.BroadCastTaskReport(result);
                info(task+"间隔时间："+sleeptime+"秒");
                Thread.currentThread().sleep(sleeptime*1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("新浪微博执行时异常，清空任务—>" + task, e);
        }finally {
            count--;
            if (count < 1) {
                this.callback.getBroadcastTask(task).remove(jsonObject);
            }
            jsonObject.put(Constants.COUNT, count);
        }
    }

    public JSONObject implementTask(JSONObject form,String task_id,JSONObject r){
        JSONObject result = new JSONObject();
        result.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
        WeiPiaoHttpClient weiPiaoHttpClient = new WeiPiaoHttpClient();
        String id = form.isNull("id")?null:form.getString("id");
        String commentId = form.isNull("commentId")?null:form.getString("commentId");
        String score = form.isNull("score")?null:form.getString("score");
        String nickName = form.isNull("nickname")?null:form.getString("nickname");
        String doc = getDoc(result);
        JSONObject res = new JSONObject();
        if(result.isNull(Constants.CODE_REMARK)){
            try{
                switch(task){
                    case "1301"://微票儿电影想看
                        res = weiPiaoHttpClient.weiPiaoWantSee(id,task_id,r,callback);
                        break;
                    case "1302"://微票儿电影评论
                        res = weiPiaoHttpClient.weiPiaoWantComment(id,doc,score,task_id,r,callback);
                        break;
                    case "1303"://微票评论支持
                        res = weiPiaoHttpClient.weiPiaoCommentPraise(id,commentId,task_id,r,callback);
                        break;
                    case "1304"://微票评论回复
                        res = weiPiaoHttpClient.weiPiaoWantReply(id,commentId,doc,task_id,r,callback);
                        break;
                }
            }catch (Exception e){
                error("implementTask(JSONObject form)异常",e);
            }
        }
        if(res!=null && res.length()>0){
            Iterator i = res.keys();
            while(i.hasNext()){
                String key = (String)i.next();
                result.put(key,res.get(key));
            }
        }
        if(!result.isNull(Constants.CODE)){
            String code = result.getString(Constants.CODE);
            Map<String, String> info = new HashMap<String, String>();
            if(code !="200"){
                info.put(Constants.STATUS,"0");
                info.put(Constants.REMARK,result.getString(Constants.REMARK));
            }
            info.put("lastUsedTime",new Date()+"");
            callback.updateAccountByID(result.getString(Constants.ACCOUNT_ID), info);
        }
        return result;
    }

    /**
     * 取语料
     * @param result
     * @return
     */
    private String getDoc(JSONObject result) {
        String doc= null;
        try{
            doc = callback.getTaskDocByTaskID(task_id);
            if(doc==null){
                result.put(Constants.CODE_REMARK,"取语料失败");
            }
        }catch (Exception e){
            result.put(Constants.CODE_REMARK,"取语料异常");
            error("取语料异常",e);
        }
        return doc;
    }
}
