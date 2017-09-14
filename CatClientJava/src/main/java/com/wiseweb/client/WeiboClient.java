package com.wiseweb.client;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClient;
import com.wiseweb.json.JSONArray;
import com.wiseweb.json.JSONObject;
import com.wiseweb.weibo.WeiboWebHttpClient;
import com.wiseweb.weibo.WeiboWapLogin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/15.
 */
public class WeiboClient extends TaskClient {
    public WeiboClient(String task, int sleeptime) {
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
                if(task.equals(Constants.WEIBO_SINA_LOGIN)){//帐号登陆
                    WeiboWapLogin.getWeiboWapLogin().getUserLogin();
                }else{//执行微博其它任务
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
                }
                info(task+"间隔时间："+sleeptime+"秒");
                Thread.currentThread().sleep(sleeptime*1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("新浪微博执行时异常，清空任务—>" + task, e);
        }finally {
            count--;
            if (count < 1) {
                this.callback.getBroadcastTask(task).remove(taskObject);
            }else{
                taskObject.put(Constants.COUNT, count);
            }
        }
    }

    public void implementLogin(){
        try{
            WeiboWapLogin weiboWapLogin = WeiboWapLogin.getWeiboWapLogin();
            JSONObject account = callback.getTaskAccountByTaskID(task_id);
            if(account!=null){
                weiboWapLogin.WeiboWapLogin(account);
            }
        }catch (Exception e){
            error("implementLogin()异常",e);
        }
    }

    public JSONObject implementTask(JSONObject form){
        JSONObject result = new JSONObject();
        result.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
        WeiboWebHttpClient weiboWebHttpClient = WeiboWebHttpClient.getWeiboWebHttpClient();
        WeiboWapLogin weiboWapLogin = WeiboWapLogin.getWeiboWapLogin();
        String id = form.isNull("id")?null:form.getString("id");
        String pic_id = form.isNull("pic_id")?null:form.getString("pic_id");
        String doc = getDoc(result);
        String weiboCookieType = "weibo_com";
        JSONObject Cookie = getWeiboCookie(result,weiboCookieType);
//        JSONObject Cookie =new JSONObject("_T_WM=47820bec55769e9c82aa8b825ca8a417; SUB=_2A256JGsXDeRxGeRI7lEU9y_PzzuIHXVZ53VfrDV6PUJbrdBeLRPFkW1LHes0SEGst6NHivyjP1AX428V9XTcRA..; SUHB=0ftoK-6mSRE5la; SSOLoginState=1461721927; M_WEIBOCN_PARAMS=uicode%3D20000174; gsid_CTandWM=4uagCpOz5bTlV5JrHbLvpb8jx5B");
        JSONObject res = new JSONObject();
        if(result.isNull(Constants.CODE_REMARK)){
            try{
                switch(task){
                    case Constants.WEIBO_SINA_POST://新浪微博直发
                        res = weiboWebHttpClient.weiboAdd(Cookie,doc,pic_id);
                        break;
                    case Constants.WEIBO_SINA_COMMENT://新浪微博评论
                        res = weiboWebHttpClient.weiboComment(Cookie,id,doc);
                        break;
                    case Constants.WEIBO_SINA_REPOST://新浪转发
                        res = weiboWebHttpClient.weiboForward(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_SUPPORT://新浪微博赞
                        res = weiboWebHttpClient.weiboSupport(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_COMMENT_AND_REPOST://微博转评
                        res = weiboWebHttpClient.weiboForwardComment(Cookie,id,doc);
                        break;
                    case Constants.WEIBO_SINA_ADD_ATTENTION_FOR_USER://微博关注
                        res = weiboWebHttpClient.weiboFollow(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_SUPPORT_FOT_COMMENT://评论赞
                        res = weiboWebHttpClient.commentSupport(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_MOVIE_SCORE://电影点评
                        int score = form.getInt("score");
                        res = weiboWebHttpClient.movieScore(Cookie,doc,id,score,pic_id);
                        break;
                    case Constants.WEIBO_SINA_ADD_ATTENTION_FOR_TOPIC://话题加粉
                        res = weiboWebHttpClient.topicFollow(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_TOPIC_POST://话题直发
                        res = weiboWebHttpClient.topicAdd(Cookie,doc,id,pic_id);
                        break;
                    case Constants.WEIBO_SINA_MOVIE_SUPPORT://电影点赞
                        res = weiboWebHttpClient.movieSupport(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_MOVIE_SEE://电影想看
                        res = weiboWebHttpClient.movieSee(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_MOVIE_FOLLOW://电影关注
                        res = weiboWebHttpClient.movieFollow(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_VOTE://微博投票
                        String items = form.getString("items");
                        res = weiboWebHttpClient.weiboVote(Cookie,id,items);
                        break;
//                    case Constants.WEIBO_SINA_LOGIN://微博登录
//                        weiboWapLogin.getUserLogin();
//                        break;
                    case Constants.WEIBO_SINA_VOIDE_SUPPORT://视频点赞
                        res = weiboWebHttpClient.voideSupport(Cookie,id);
                        break;
                    case Constants.WEIBO_SINA_AUDIO_SUPPORT://音乐点赞
                        res = weiboWebHttpClient.audioSupport(Cookie,id);
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
            switch(code){
                case "100006"://帐号异常
                    info.put(Constants.STATUS, "0");
                    info.put(Constants.REMARK, task + result.getString(Constants.REMARK));
                    callback.updateAccountByID(result.getString(Constants.ACCOUNT_ID), info);
                    break;
                case "100003"://您的帐号存在高危风险
                    info.put(Constants.STATUS, "1");
                    info.put(Constants.REMARK, task + result.getString(Constants.REMARK));
                    info.put(Constants.TASK,task);
                    callback.updateAccountByID(result.getString(Constants.ACCOUNT_ID), info);
                    break;
            }
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
            String isDoc = Constants.WEIBO_SINA_POST+","+
                    Constants.WEIBO_SINA_COMMENT+","+
                    Constants.WEIBO_SINA_COMMENT_AND_REPOST+","+
                    Constants.WEIBO_SINA_MOVIE_SCORE+","+
                    Constants.WEIBO_SINA_TOPIC_POST;
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
    public JSONObject getWeiboCookie(JSONObject result,String type) {
        JSONObject account = null;
        try{
            account = callback.getTaskAccountByTaskID(task_id);
            if(account!=null){
                result.put(Constants.ACCOUNT_DISPLAYNAME,account.has(Constants.ACCOUNT_DISPLAYNAME)?account.getString(Constants.ACCOUNT_DISPLAYNAME):"无昵称");
                result.put(Constants.ACCOUNT_ID, account.getString(Constants.ACCOUNT_ID));
                if(account.toString().contains(type)){
                    return account.getJSONObject(Constants.ACCOUNT_AUTHINFO).getJSONObject(type);
                }else{
                    result.put(Constants.CODE_REMARK,"帐号auth_info字段无"+type+"类型Cookie");
                }
            }else{
                result.put(Constants.CODE_REMARK,"取帐号失败");
            }
        }catch (Exception e){
            result.put(Constants.CODE_REMARK,"取帐号异常");
            error("取帐号异常\n帐号信息："+account,e);
        }
        return null;
    }
}
