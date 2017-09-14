package com.wiseweb.NewsComment;

import com.wiseweb.client.util.Httpclient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClientCallback;
import com.wiseweb.cat.util.Util;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class NewsCommentHttpClient extends Httpclient {
    public static NewsCommentHttpClient newsCommentHttpClient;
    public static NewsCommentHttpClient getNewsCommentHttpClient(){
        newsCommentHttpClient = newsCommentHttpClient ==null?new NewsCommentHttpClient(): newsCommentHttpClient;
        return newsCommentHttpClient;
    }

    public static void main(String []args){
        NewsCommentHttpClient ht  = new NewsCommentHttpClient();
        String url = "http://comment.news.163.com/news_guonei8_bbs/BLO24MST0001124J.html";
        String []strs = url.split("/");
        String board = strs[strs.length-2];
        String threadid = strs[strs.length - 1].substring(0, strs[strs.length - 1].indexOf("."));
        JSONObject task_tag_data = new JSONObject();
        JSONObject task_tag_data_header = new JSONObject();
        JSONObject form = new JSONObject();
        task_tag_data.put("exeurl","http://comment.news.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/BLO24MST0001124J/comments?ibc=newspc");
        form.put("parentId", "");
        form.put("board",board);
        form.put("threadid",threadid);
        task_tag_data_header.put("Referer",url);
        task_tag_data.put("task_tag_data_form",form);
        task_tag_data.put("task_tag_data_header",task_tag_data_header);
        long task_id = 0;
        JSONObject r= new JSONObject();
        ht.NewsCommentAction(null,task_tag_data,task_id,"303",r,null);
    }

    /*     * 需要登陆的任务类型,拼接起来用于判断
         */
    String tasks = Constants.NEW_163_COMMENTS + ","
            + Constants.NEW_SINA_COMMENTS + ","
            + Constants.NEW_SOHU_COMMENTS + ","
            + Constants.NEW_IFENG_COMMENTS + ","
            + Constants.NEW_163_APP_COMMENTS + ","
            + Constants.NEW_SINA_APP_COMMENTS + ","
            + Constants.NEW_PENGPAI_APP_COMMENTS + ","
            + Constants.WEIBO_SINA_SUPPORT+","
            + Constants.WEIBO_SINA_ADD_ATTENTION_FOR_USER+","
            + Constants.WEIBO_SINA_SUPPORT_FOT_COMMENT+","
            + Constants.WEIBO_SINA_ADD_ATTENTION_FOR_TOPIC+","
            + Constants.WEIBO_SINA_MOVIE_SUPPORT +","
            + Constants.NEW_TOUTIAO_APP_COMMENTS;


    /**
     * 用于判断是否需要获取语料
     */
    String is_getdoc = Constants.WEIBO_SINA_SUPPORT + ","
            + Constants.WEIBO_SINA_ADD_ATTENTION_FOR_USER + ","
            + Constants.WEIBO_SINA_SUPPORT_FOT_COMMENT + ","
            + Constants.WEIBO_SINA_ADD_ATTENTION_FOR_TOPIC + ",";


    /**
     * 新闻评论
     *
     * @param exeurl
     * @param task_id  任务id
     * @param task     任务类型
     * @param callback
     */
    public void NewsCommentAction(String exeurl, JSONObject form, long task_id, String task, JSONObject r, TaskClientCallback callback) {
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

            //取语料
//            String doc = callback.getTaskDocByTaskID(String.valueOf(task_id));
            String doc = "呵呵";
            if (is_getdoc.contains(task)) doc = "";
            if (doc == null) {//取不到语料时,标识任务失败
                r.put(Constants.CODE_REMARK, "取不到语料");
                return;
            } else {
                r.put(Constants.API_PARAMS_DOC, doc);
                if (task.equals(Constants.NEW_TOUTIAO_APP_COMMENTS)) {
                    map.put("text",doc);
                    map.put("is_comment","0");
                } else {
                    if(!is_getdoc.contains(task)) {
                        map.put(getDocKey(task), doc.trim());
                    }
                }
            }
            JSONObject account = new JSONObject();
            //用于判断是否需要登陆,默认需要登陆
            boolean isLogin = true;
            //判断该任务类型是否需要取帐号
            if (tasks.contains(task.trim())) {
                account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
                account.put("username","agn0q3ybs@163.com");
                account.put("password","jiaocai4c");
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
                    String CookieKey = Util.getCookieKey(task);
                    cookie = login.getCookie(CookieKey, account);
                    isLogin = cookie.size() <= 0;
                    if (isLogin) {//判断是否需要登陆
                        boolean b = login(map, cookie, account, r, callback, task);
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
                }
            }

            //开始请求
            HttpResponse res = this.getHttpResponse(map, hmap, exeurl);
            info("URL：" + exeurl + "\nMAP：" + map + "\nHMAP：" + hmap);
            //开始处理请求结果
            if (res != null) {
                String text = Util.getContent(res);
                if (text == null || text.length()==0) {
                    r.put(Constants.CODE_REMARK, "请求返回结果为NULL");
                    return;
                }
                int code = Util.getCode(res);
                JSONObject j = null;
                switch (task) {
                    case Constants.NEW_163_COMMENTS://网易新闻Web评论
                        if (hmap.get("my_comment_" + map.get("threadid")) != null) {//评论成功
                            String url = hmap.get("Referer").replace(map.get("threadid"), hmap.get("my_comment_" + map.get("threadid")).replace("_", "/"));
                            r.put(Constants.RESULT, url);
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        } else {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code);
                        }
                        break;
                    case Constants.NEW_SINA_COMMENTS://新浪新闻Web评论
                        text = text.substring(text.indexOf("({") + 1, text.indexOf("})") + 1);
                        j = new JSONObject(text).getJSONObject("result");
                        if (j.has("id")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        } else {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code + "，返回结果=" + j.getJSONObject("status").getString("msg").trim());
                        }
                        break;
                    case Constants.NEW_SOHU_COMMENTS://搜狐新闻Web评论
                        j = new JSONObject(text);
                        r.put(Constants.STATUS, j.getInt("id") == 0 ? Constants.TASK_STATUS_FAILED : Constants.TASK_STATUS_COMPLETE);
                        if(j.getInt("id") == 0) {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code);
                        }
                        break;
                    case Constants.NEW_IFENG_COMMENTS://凤凰新闻Web评论
                        if (code == 200) {
                            text.split("=")[1].split(";")[0].trim();
                            j = new JSONObject(text.split("=")[1].split(";")[0].trim());
                            if (j.getInt("code") == 1) {
                                r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            } else if (j.has("message")) {
                                r.put(Constants.CODE_REMARK, "评论失败，返回结果" + j.getString("message"));
                            }
                        } else {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code);
                        }
                        break;
                    case Constants.NEW_163_APP_COMMENTS://网易新闻APP评论
                        if (hmap.get("my_comment_" + map.get("threadid")) != null) {//评论成功
                            String url = hmap.get("Referer").replace(map.get("threadid"), hmap.get("my_comment_" + map.get("threadid").replace("_", "\\/")));
                            r.put(Constants.RESULT, url);
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        } else {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code);
                        }
                        break;
                    case Constants.NEW_SINA_APP_COMMENTS://新浪新闻APP评论
                        text = text.substring(text.indexOf("({") + 1, text.indexOf("})") + 1);
                        j = new JSONObject(text).getJSONObject("result");
                        if (j.has("id")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        } else {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code + "，返回结果=" + j.getJSONObject("status").getString("msg").trim());
                        }
                        break;
                    case Constants.NEW_SOHU_APP_COMMENTS://搜狐新闻APP评论
                        if (code == 200) {
                            j = new JSONObject(text);
                            if (j != null && j.has("isSuccess") && j.getString("isSuccess").equals("S")) {
                                r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                                break;
                            }
                        }
                        r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code);
                        break;
                    case Constants.NEW_IFENG_APP_COMMENTS://凤凰新闻APP评论
                        if (code == 200) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        } else {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code + (isLogin ? "" : "，Cookie执行"));
                        }
                        break;
                    case Constants.NEW_PENGPAI_APP_COMMENTS://澎湃新闻APP评论
                        if (text.indexOf("发表成功") != -1) {//评论成功
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        } else {
                            r.put(Constants.CODE_REMARK, "新闻评论失败,Code=" + code);
                        }
                        break;
                    case Constants.NEW_TOUTIAO_APP_COMMENTS:
                        if(text.indexOf("success") != -1){
                            r.put(Constants.STATUS,Constants.TASK_STATUS_COMPLETE);
                        }else{
                            r.put(Constants.STATUS,"新闻评论失败，code="+code);
                        }
                        break;
                    default:
                        if(text.trim().equals("")){
                            r.put(Constants.CODE_REMARK,"返回结果为空，帐号可能存在异常!");

                            Map<String, String> info = new HashMap<String, String>();
                            info.put(Constants.STATUS, "0");
                            info.put(Constants.REMARK, task + "登陆成功");
                            callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID), info);
                        }else{
                            j = new JSONObject(text);
                            if (j.getInt("ok") == 1) {
                                r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            } else if (j.has("msg")) {
                                r.put(Constants.CODE_REMARK,j.getString("msg").trim().equals("")?"执行成功":j.getString("msg"));
                            } else {
                                r.put(Constants.CODE_REMARK, "执行失败，原因："+j.toString());
                            }
                        }
                        break;
                }
                if (r.has(Constants.CODE_REMARK)) {
                    r.put(Constants.CODE_REMARK, r.getString(Constants.CODE_REMARK) + (isLogin ? "" : ",Cookie执行"));
                } else {
                    r.put(Constants.CODE_REMARK, isLogin ? "" : ",Cookie执行");
                }
            } else {
                r.put(Constants.CODE_REMARK, "新闻评论请求失败");
            }
        } catch (Exception e) {
            r.put(Constants.CODE_REMARK, "新闻评论请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("新闻评论执行请求时异常—>" + task, e);
        }
    }

    /**
     * 获取Cookie信息的key值
     */
    private String getCookieKey(String task) {
        switch (task) {
            case Constants.NEW_163_COMMENTS://网易新闻Web评论
                return Constants.NEWS_163_LOGIN_COOKIE;
            case Constants.NEW_163_APP_COMMENTS://网易新闻App评论
                return Constants.NEWS_163_LOGIN_COOKIE;
            case Constants.NEW_SINA_COMMENTS://新浪新闻Web评论
                return Constants.SINA_COM_CN_COOKIE;
            case Constants.NEW_SINA_APP_COMMENTS://新浪新闻App评论
                return Constants.SINA_COM_CN_COOKIE;
            case Constants.NEW_SOHU_COMMENTS://搜狐新闻Web评论
                return Constants.NEWS_SOHU_LOGIN_COOKIE;
            case Constants.NEW_IFENG_COMMENTS://凤凰新闻Web评论
                return Constants.NEWS_IFENG_SINA_OAUTH_COOKIE;
            case Constants.NEW_PENGPAI_APP_COMMENTS://澎湃新闻App评论
                return Constants.NEWS_PENGPAI_LOGIN_COOKIE;
            case Constants.NEW_TOUTIAO_APP_COMMENTS://头条新闻App评论
                return Constants.NEWS_TOUTIAO_LOGIN_COOKIE;
            default://新浪微博手机触屏版登陆
                return Constants.SINA_WEIBO_PHONE_LOGIN_COOKIE;
        }
    }

    private static boolean isCookieExpirationTime(long etime) {
        return false;
    }

    /**
     * 登陆
     *
     * @param map
     * @param hmap
     * @param account
     * @param r
     * @param callback
     * @param task
     * @return
     */
    public boolean login(Map<String, String> map, Map<String, String> hmap, JSONObject account, JSONObject r, TaskClientCallback callback, String task) {
        try {
            Login login = new Login();
            switch (task) {
                case Constants.NEW_163_COMMENTS:
                    return login.WyNewsWeb(hmap, account, r, callback);
                case Constants.NEW_SINA_COMMENTS:
                    return login.SinaWeiboPhoneLogin(hmap, account, r, null, 2, callback);
                case Constants.NEW_SOHU_COMMENTS:
                    return login.SohuNewsWeb(hmap, account, r, callback);
                case Constants.NEW_IFENG_COMMENTS:
                    return login.IfengNewsWeb(hmap, account, r, callback);
                case Constants.NEW_163_APP_COMMENTS:
                    return login.WyNewsWeb(hmap, account, r, callback);
                case Constants.NEW_SINA_APP_COMMENTS:
                    return login.SinaWeiboPhoneLogin(hmap, account, r, null, 2, callback);
                case Constants.NEW_PENGPAI_APP_COMMENTS:
                    return login.PengPaiNewsApp(hmap, map.get("WD-UUID").trim(), account, r, callback);
                case Constants.NEW_TOUTIAO_APP_COMMENTS:
                    return login.TouTiaoNewsApp(hmap, account, r, callback);
                default://新浪微博手机登陆
                    return login.SinaWeiboPhoneLogin(hmap, account, r, null, 1, callback);
            }
        } catch (Exception e) {
            error("异常—>", e);
        }
        return false;
    }

    /**
     * 标识任务失败
     *
     * @param callback
     * @param task
     * @param task_id
     */
    private void updateTask(TaskClientCallback callback, String task, long task_id, String type) {
        try {
            Map<String, String> map = new HashMap<String, String>();
            callback.getBroadcastTask(task).remove(task);
            map.put(Constants.TASK_ID, String.valueOf(task_id));
            map.put(Constants.STATUS, String.valueOf(Constants.TASK_STATUS_FAILED));
            map.put(Constants.REMARK, type.equals("doc") ? "取语料失败" : "取帐号失败");
            callback.updateTask(map);
        } catch (Exception e) {
            error("异常—>", e);
        }
    }

    /**
     * 获取请求数据中的评论内容的key
     *
     * @param task
     * @return
     */
    private String getDocKey(String task) {
        try {
            switch (task) {
                case Constants.NEW_163_COMMENTS://网易新闻Web评论
                    return "content";
                case Constants.NEW_SINA_COMMENTS://新浪新闻Web评论
                    return "content";
                case Constants.NEW_SOHU_COMMENTS://搜狐新闻Web评论
                    return "content";
                case Constants.NEW_IFENG_COMMENTS://凤凰新闻Web评论
                    return "content";
                case Constants.NEW_163_APP_COMMENTS://网易新闻APP评论
                    return "body";
                case Constants.NEW_SINA_APP_COMMENTS://新浪新闻APP评论
                    return "content";
                case Constants.NEW_SOHU_APP_COMMENTS://搜狐新闻APP评论
                    return "cont";
                case Constants.NEW_IFENG_APP_COMMENTS://凤凰新闻APP评论
                    return "content";
                case Constants.NEW_PENGPAI_APP_COMMENTS://澎湃新闻APP评论
                    return "content";
                case Constants.WEIBO_SINA_MOVIE_SUPPORT://新浪微博电影点评
                    return "content";
            }
        } catch (Exception e) {
            error("异常—>", e);
        }
        return "content";
    }
}
