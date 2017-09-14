package com.wiseweb.NewsComment;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClientCallback;
import com.wiseweb.cat.util.Util;
import com.wiseweb.client.util.Httpclient;
import com.wiseweb.json.JSONException;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wiseweb on 2016/2/26.
 */
public class News_toutiao_praiseClient extends Httpclient {
    public static News_toutiao_praiseClient news_toutiao_praiseClient;
    public static News_toutiao_praiseClient getNews_toutiao_praiseClient(){
        news_toutiao_praiseClient = news_toutiao_praiseClient ==null?new News_toutiao_praiseClient(): news_toutiao_praiseClient;
        return news_toutiao_praiseClient;
    }

    public void ToutiaoPraise(String exeurl, JSONObject form, long task_id, String task, JSONObject r, TaskClientCallback callback){
        try {
            r.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
            Map<String, String> map = null;
            Map<String, String> hmap = null;
            if (exeurl != null) {
                //请求表单参数
                map = Util.getForm(form);
                //请求消息头参数
                hmap = Util.getHeader(form);
            } else {
                exeurl = form.getString(Constants.EXE_URL);
                map = form.has(Constants.TASK_TAG_DATA_FORM) ? Util.getMap(form.getJSONObject(Constants.TASK_TAG_DATA_FORM)) : null;
                hmap = form.has(Constants.TASK_TAG_DATA_HEADER) ? Util.getMap(form.getJSONObject(Constants.TASK_TAG_DATA_HEADER)) : new HashMap<String, String>();
            }
            JSONObject account = null;
            //用于判断是否需要登陆,默认需要登陆
            boolean isLogin = true;
            //判断该任务类型是否需要取帐号
            account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
            Map<String, String> cookie = new HashMap<String, String>();
            if (account == null) {//取不到帐号时,删除任务，标识任务失败
                r.put(Constants.CODE_REMARK, "取不到帐号");
                return;
            } else {//登陆
                if (account.has(Constants.ACCOUNT_DISPLAYNAME)) {
                    String displayName = account.getString(Constants.ACCOUNT_DISPLAYNAME);
                    r.put(Constants.ACCOUNT_DISPLAYNAME, displayName);
                }
                r.put(Constants.API_PARAMS_ACCOUNT, new JSONObject(account.toString()));
                r.getJSONObject(Constants.API_PARAMS_ACCOUNT).remove(Constants.ACCOUNT_PASSWORD);
                //用于判断是否登陆
                Login login = new Login();
                String CookieKey = "NewsTouTiaoLoginCookie";
                cookie = login.getCookie(CookieKey, account);
                isLogin = cookie.size() <= 0;
                if (isLogin) {//判断是否需要登陆
                    boolean b = login.TouTiaoNewsApp(map, account, r, callback);
                    if (!b) return;
                }
                //把Cookie信息添加到消息头
                hmap.putAll(cookie);
                if (isLogin) {
                    //更新帐号Cookie信息
                    Map<String, String> info = new HashMap<String, String>();
                    info.put(Constants.STATUS, "1");
                    info.put(Constants.REMARK, task + "登陆成功");
                    JSONObject auth = new JSONObject();
                    auth.put(CookieKey,cookie);
                    auth.getJSONObject(CookieKey).put("time",Util.getCookieValidityTime(CookieKey));//设置一个Cookie有效期时间
                    info.put(Constants.ACCOUNT_AUTHINFO, auth.toString());
                    callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID), info);
                }
                HttpResponse res = this.getHttpResponse(map, hmap, exeurl);
                if (res != null) {
                    String text = Util.getContent(res).trim();
                    int code = Util.getCode(res);
                    if (text != null && text.contains("success")) {
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        return;
                    }
                    r.put(Constants.CODE_REMARK, "Code=" + code);
                }
            }
        } catch (JSONException e) {
            r.put(Constants.CODE_REMARK, "新闻评论支持执行请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("新闻评论支持执行请求时异常—>" + task, e);
        }
    }
}

