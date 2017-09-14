package com.wiseweb.bbs;

import com.wiseweb.NewsComment.Login;
import com.wiseweb.client.util.Httpclient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClientCallback;
import com.wiseweb.cat.util.Util;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BbsHttpClient extends Httpclient {
    public static BbsHttpClient bbsHttpClient;
    public static BbsHttpClient getNewsCommentSupportHttpClient(){
        bbsHttpClient = bbsHttpClient ==null?new BbsHttpClient(): bbsHttpClient;
        return bbsHttpClient;
    }
    private String isPost = Constants.BBS_163_POST+","
            +Constants.BBS_PEOPLE_POST+","
            +Constants.BBS_SOHU_POST+",";
//    public static void main(String[] age) {
//        JSONObject r = new JSONObject();
//        new BBSPostAction("http://bbs1.people.com.cn/post/80/1/1/148250362.html", 0, Constants.BBS_PEOPLE_COMMENT, r, null);
//        System.out.println(r);
//    }

    /**
     * 新闻评论
     * @param url
     * @param task_id  任务id
     * @param task     任务类型
     * @param callback
     */
    public void BBSPostAction(String url, long task_id, String task, JSONObject r, TaskClientCallback callback) {
        try {
            r.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
            //取语料
            String doc = callback.getTaskDocByTaskID(String.valueOf(task_id));
            String title = null;
            String content = null;
            if (doc == null) {//取不到语料时,标识任务失败
                r.put(Constants.CODE_REMARK, "取不到语料");
                return;
            } else {
                String[] sp = null;
                if(isPost.contains(task)){
                    sp = doc.split(Constants.DOC_SPLIT);
                }
                title = sp==null?null:sp[0];
                content = sp==null?doc.trim():sp[1];
            }
            r.put(Constants.API_PARAMS_DOC,title==null?content:title);
            //获取请求数据
            JSONObject Data = getHttpData(task, url, title, content);
            if (Data == null) {
                r.put(Constants.CODE_REMARK, "获取请求数据为空");
                return;
            }
            String exeurl = Data.getString(Constants.EXE_URL);
            Map<String, String> map = Data.has(Constants.TASK_TAG_DATA_FORM) ? Util.getMap(Data.getJSONObject(Constants.TASK_TAG_DATA_FORM)) : null;
            Map<String, String> hmap = Data.has(Constants.TASK_TAG_DATA_HEADER) ? Util.getMap(Data.getJSONObject(Constants.TASK_TAG_DATA_HEADER)) : new HashMap<String, String>();

            //用于判断是否需要登陆,默认需要登陆
            boolean isLogin = true;
            //判断该任务类型是否需要取帐号
            JSONObject account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
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
                String key = Util.getCookieKey(task);
                cookie = login.getCookie(key, account);
                isLogin = cookie.size() <= 0;
                if (isLogin) {//判断是否需要登陆
                    boolean b = login(cookie, account, r, task, callback);
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
                    auth.put(key, cookie);
                    auth.getJSONObject(key).put("time", Util.getCookieValidityTime(task));//设置一个Cookie有效期时间
                    info.put(Constants.ACCOUNT_AUTHINFO, auth.toString());
                    callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID), info);
                }

            }


            HttpResponse res = this.getHttpResponse(map, hmap, exeurl);
            if (res == null) {
                r.put(Constants.CODE_REMARK, "论坛发帖请求失败");
                return;
            }
            String text = Util.getContent(res);
            if (text == null) {
                r.put(Constants.CODE_REMARK, "论坛发帖返回值为空");
            }
            int code = Util.getCode(res);
            switch (task) {
                case Constants.BBS_163_POST://网易发帖
                    if (code == 302) {
                        r.put(Constants.RESULT,Util.getLocation(res));
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    } else {
                        r.put(Constants.CODE_REMARK, "发帖失败，Code=" + code);
                    }
                    break;
                case Constants.BBS_163_COMMENT://网易评论
                    if (code == 302) {
                        r.put(Constants.RESULT,Util.getLocation(res));
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    } else {
                        r.put(Constants.CODE_REMARK, "评论失败，Code=" + code);
                    }
                    break;
                case Constants.BBS_PEOPLE_POST://强国发帖
                    if (code == 200 && text != null && text.contains("true") && !text.contains("系统遇到错误，请重试")) {
                        String bid = text.split("\\&")[0].split(":")[1].trim();
                        String id = text.split("=")[1].trim();
                        r.put(Constants.RESULT,"http://bbs1.people.com.cn/post/" + bid + "/1/2/" + id + ".html");
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    } else {
                        if(text!=null && text.contains("系统遇到错误，请重试")){
                            r.put(Constants.CODE_REMARK,"强国论坛系统出错");
                        }else {
                            r.put(Constants.CODE_REMARK, "发帖失败，Code=" + code);
                        }
                    }
                    break;
                case Constants.BBS_PEOPLE_COMMENT://强国评论
                    if (code == 200 && text != null && text.contains("true") && !text.contains("系统遇到错误，请重试")) {
                        String bid = text.split("\\&")[0].split(":")[1].trim();
                        String id = text.split("=")[1].trim();
                        r.put(Constants.RESULT,"http://bbs1.people.com.cn/post/" + bid + "/1/2/" + id + ".html");
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    } else {
                        if(text!=null && text.contains("系统遇到错误，请重试")){
                            r.put(Constants.CODE_REMARK,"强国论坛系统出错");
                        }else {
                            r.put(Constants.CODE_REMARK, "发帖失败，Code=" + code);
                        }
                    }
                    break;
                case Constants.BBS_SOHU_POST://搜狐发帖
                    if (code == 302) {
                        r.put(Constants.RESULT,"http://" + new URL(url).getHost() + Util.getLocation(res));
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    } else {
                        r.put(Constants.CODE_REMARK, "发帖失败，Code=" + code);
                    }
                    break;
                case Constants.BBS_SOHU_COMMENT://搜狐评论
                    if (code == 302) {
                        r.put(Constants.RESULT,"http://" + new URL(url).getHost() + Util.getLocation(res).replace("?pagect=","/p"));
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    } else {
                        r.put(Constants.CODE_REMARK, "评论失败，Code=" + code);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private JSONObject getHttpData(String task, String url, String title, String content) {
        try {
            JSONObject data = new JSONObject();
            JSONObject form = new JSONObject();
            form.put("content", content);//语料
            JSONObject header = new JSONObject();
            URL u = new URL(url);
            String host = u.getHost();
            String path = u.getPath();
            switch (task) {
                case Constants.BBS_163_POST://网易发帖请求参数
                    String boardId = (path.split("/")[path.split("/").length - 1]).replace(".html", "");
                    String Referer = "http://" + host + "/post/" + boardId + "/publish/common-";
                    String exeurl = "http://" + host + "/v2/post/doPublish";

                    form.put("title", title);//标题
                    form.put("titleColor", "1");
                    form.put("type", "-1");
                    form.put("postType", "common");
                    form.put("boardId", boardId);
                    form.put("threadId", "-1");
                    form.put("photoSetImgUrls", "");
                    form.put("checkcode", "");

                    header.put("Referer", Referer);
                    header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");

                    data.put(Constants.EXE_URL, exeurl);
                    data.put(Constants.TASK_TAG_DATA_FORM, form);
                    data.put(Constants.TASK_TAG_DATA_HEADER, header);
                    return data;
                case Constants.BBS_163_COMMENT:
                    HttpResponse res = this.getHttpResponse(null, null, url);
                    if (res == null) return null;
                    String text = Util.getContent(res);
                    if (text == null) return null;
                    Document doc = Jsoup.parse(text);
                    form.put("title", doc.title());
                    form.put("replyPostId", "");
                    String[] sp = url.split("/");
                    form.put("boardId", sp[sp.length - 2].trim());
                    form.put("threadId", sp[sp.length - 1].substring(0, sp[sp.length - 1].indexOf(".html")).trim());
                    form.put("photoSetImgUrls", "");
                    form.put("checkcode", "");

                    header.put("Referer", url.trim());
                    header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");

                    data.put(Constants.EXE_URL, "http://bbs.news.163.com/v2/post/doReply");
                    data.put(Constants.TASK_TAG_DATA_FORM, form);
                    data.put(Constants.TASK_TAG_DATA_HEADER, header);
                    return data;
                case Constants.BBS_PEOPLE_POST:
                    String bid = (path.split("/")[path.split("/").length - 1]).replace(".html", "");
                    form.put("treeView", "");
                    form.put("treeValue", "");
                    form.put("returnUrl", "");
                    form.put("bid", bid);
                    form.put("parentId", "0");
                    form.put("respUrl", "");
                    form.put("titleText", title);
                    form.put("imageShowState", "");
                    form.put("message", content);
                    form.put("imglocalid[]", "1");
                    form.put("keywords", "");
                    form.put("username", "");
                    form.put("password", "");

                    header.put("Referer", "http://bbs1.people.com.cn/postDisplayAction.do?bid=" + bid);
                    header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");

                    data.put(Constants.EXE_URL, "http://bbs1.people.com.cn/postAction.do");
                    data.put(Constants.TASK_TAG_DATA_FORM, form);
                    data.put(Constants.TASK_TAG_DATA_HEADER, header);
                    return data;
                case Constants.BBS_PEOPLE_COMMENT:
                    String[] pesp = url.replace(".html", "").split("/");
                    form.put("parentId", pesp[4]);
                    form.put("bid", pesp[pesp.length - 1]);
                    form.put("titleText", content);
                    form.put("pageNo", "0");
                    form.put("view", "true");
                    form.put("message", content);

                    header.put("Referer", url.trim());
                    header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");

                    data.put(Constants.EXE_URL, "http://bbs1.people.com.cn/postAction.do");
                    data.put(Constants.TASK_TAG_DATA_FORM, form);
                    data.put(Constants.TASK_TAG_DATA_HEADER, header);
                    return data;
                case Constants.BBS_SOHU_POST:
                    url = url.replace(new URL(url).getHost(),"m.club.sohu.com").replace("threads","post?");
                    form.put("title", title);//标题
                    form.put("token", "");
                    form.put("sub", "ok");
                    form.put("submit", "发表");

                    header.put("Referer", url.trim()+"?status=");
                    header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");

                    data.put(Constants.EXE_URL,url.trim());
                    data.put(Constants.TASK_TAG_DATA_FORM, form);
                    data.put(Constants.TASK_TAG_DATA_HEADER, header);
                    return data;
                case Constants.BBS_SOHU_COMMENT:
                    url = url.replace(new URL(url).getHost(), "m.club.sohu.com");
                    form.put("title", title);//标题
                    form.put("token", "");
                    form.put("sub", "ok");
                    form.put("submit", "发表");
                    form.put("islock", "");

                    header.put("Referer", url.trim());
                    header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");

                    data.put(Constants.EXE_URL, url.trim());
                    data.put(Constants.TASK_TAG_DATA_FORM, form);
                    data.put(Constants.TASK_TAG_DATA_HEADER, header);
                    return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean login(Map<String, String> cookie, JSONObject account, JSONObject r, String task, TaskClientCallback callback) {
        try {
            Login login = new Login();
            switch (task) {
                case Constants.BBS_163_POST://网易论坛登陆
                    return login.WyNewsWeb(cookie, account, r, callback);
                case Constants.BBS_163_COMMENT://网易论坛登陆
                    return login.WyNewsWeb(cookie, account, r, callback);
                case Constants.BBS_PEOPLE_POST://强国论坛登陆
                    return login.BBSPeople(cookie, account, r, callback);
                case Constants.BBS_PEOPLE_COMMENT://强国论坛登陆
                    return login.BBSPeople(cookie, account, r, callback);
                case Constants.BBS_SOHU_POST://搜狐论坛登录
                    return login.SohuNewsWeb(cookie, account, r, callback);
                case Constants.BBS_SOHU_COMMENT://搜狐论坛登录
                    return login.SohuNewsWeb(cookie, account, r, callback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
