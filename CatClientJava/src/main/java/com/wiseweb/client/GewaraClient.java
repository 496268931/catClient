package com.wiseweb.client;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClient;
import com.wiseweb.gewara.GewaraHttpClient;
import com.wiseweb.json.JSONArray;
import com.wiseweb.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Administrator on 2016/1/19.
 */
public class GewaraClient extends TaskClient{
    public GewaraClient(String task, int sleeptime) {
        super(task, sleeptime);
    }

    @Override
    protected void runTask(JSONObject taskObject, String s) {
        int count=0;
        try {
            JSONObject result = getBroadcastResultJson();
            JSONArray taskresultList = new JSONArray();
            JSONObject r = getResultJson(taskObject);
            if (r == null) {
                logger.info("执行异常清空任务");
                this.callback.getBroadcastTask(task).remove(taskObject);
            } else {
                task_id = taskObject.getLong(Constants.TASK_ID)+"";
                count = taskObject.getInt(Constants.COUNT);
                info("执行数量："+count);
                JSONObject task_tag = taskObject.getJSONObject(Constants.TASK_TAG);
                info("执行任务：" + task_tag);
                //执行程序
                JSONObject res = implementTask(task_tag.getJSONObject(Constants.TASK_TAG_DATA).getJSONObject(Constants.TASK_TAG_DATA_FORM));
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
                Thread.sleep(sleeptime * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("格瓦拉行时异常，清空任务—>" + task, e);
        }finally {
            count--;
            if (count < 1) {
                this.callback.getBroadcastTask(task).remove(taskObject);
            }
            taskObject.put(Constants.COUNT, count);
        }
    }

    private JSONObject implementTask(JSONObject form) {
        JSONObject result = new JSONObject();
        try{
            result.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
            GewaraHttpClient gewaraHttpClient = GewaraHttpClient.getGewaraHttpClient();
            String id = form.isNull("id")?null:form.getString("id");
            String doc = getDoc(result);
            String weiboCookieType = "sina_com_cn";
            JSONObject account = getWeiboAccount(result,weiboCookieType);
            if(result.isNull(Constants.CODE_REMARK) && account!=null){
                JSONObject Cookie = account.getJSONObject("auth_info").getJSONObject(weiboCookieType);
                gewaraHttpClient.gewaraWeiboOauth(Cookie,account,result);
                if(!result.isNull("Cookie")){
                    JSONObject res = null;
                    Cookie = result.getJSONObject("Cookie");
                    result.remove("Cookie");
                    switch(task){
                        case Constants.GEWARA_COMMENT_REPLY:
                            res = gewaraHttpClient.commentReply(Cookie,doc,id);
                            break;
                        case Constants.GEWARA_COMMENT_SUPPORT:
                            res = gewaraHttpClient.commentSupport(Cookie,id);
                            break;
                        case Constants.GEWARA_MOVIE_COMMENT:
                            res = gewaraHttpClient.comment(Cookie,doc,id,(String)form.get("score"));
                            break;
                        case Constants.GEWARA_MOVIE_EXPECT:
                            res = gewaraHttpClient.addCollection(Cookie,id);
                            break;
                        case Constants.GEWARA_USER_FOLLOW:
                            res = gewaraHttpClient.addAttention(Cookie,id);
                            break;
                    }
                    if(res!=null && res.length()>0){
                        Iterator i = res.keys();
                        while(i.hasNext()){
                            String key = (String)i.next();
                            result.put(key,res.get(key));
                        }
                    }
                }
            }
        }catch (Exception e){
            error("implementTask(JSONObject jsonObject)异常",e);
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
            String isDoc = Constants.GEWARA_COMMENT_REPLY+","+
                    Constants.GEWARA_MOVIE_COMMENT;
            if(isDoc.contains(task)){
                doc = callback.getTaskDocByTaskID(task_id);
                if(doc==null){
                    result.put(Constants.CODE_REMARK,"取语料失败");
                }
            }
        }catch (Exception e){
            result.put(Constants.CODE_REMARK,"取语料异常");
            error("取语料异常",e);
        }
        return doc;
    }

    /**
     * 获取帐号信息取微博Cookie
     * @return
     */
    public JSONObject getWeiboAccount(JSONObject result,String type) {
        try{
            JSONObject account = callback.getTaskAccountByTaskID(task_id);
            if(account!=null){
                result.put(Constants.ACCOUNT_DISPLAYNAME,account.has(Constants.ACCOUNT_DISPLAYNAME)?account.getString(Constants.ACCOUNT_DISPLAYNAME):"无昵称");
                result.put(Constants.ACCOUNT_ID, account.getString(Constants.ACCOUNT_ID));
                if(account.toString().contains(type)){
                    return account;
                }else{
                    result.put(Constants.CODE_REMARK,"帐号auth_info字段无"+type+"类型Cookie");
                }
            }else{
                result.put(Constants.CODE_REMARK,"取帐号失败");
            }
        }catch (Exception e){
            result.put(Constants.CODE_REMARK,"取帐号异常");
            error("取帐号异常\n帐号信息：",e);
        }
        return null;
    }
}
