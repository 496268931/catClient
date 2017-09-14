package com.wiseweb.NewsComment;

import com.wiseweb.client.util.Httpclient;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.util.Util;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class NewsCommentSupportHttpClient extends Httpclient {
    public static NewsCommentSupportHttpClient newsCommentSupportHttpClient;
    public static NewsCommentSupportHttpClient getNewsCommentSupportHttpClient(){
        newsCommentSupportHttpClient = newsCommentSupportHttpClient ==null?new NewsCommentSupportHttpClient(): newsCommentSupportHttpClient;
        return newsCommentSupportHttpClient;
    }

    /**
     * 新闻评论支持
     *
     * @param form   请求表单
     * @param exeurl 请求Url
     * @param task
     * @param r
     * @return 执行结果
     * @throws Exception
     */
    public void NewsCommentSupportAction(JSONObject form, String exeurl, String task, JSONObject r) {
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
            //开始请求
            HttpResponse res = this.getHttpResponse(map, hmap, exeurl);
            logger.info("URL：" + exeurl + "\nMAP：" + map + "\nHMAP：" + hmap);
            //开始处理执行结果
            if (res != null) {
                String text = Util.getContent(res).trim();
                int code = Util.getCode(res);
                switch (task) {
                    case Constants.NEW_163_SUPPORT://网易新闻Web评论支持
                        if (code == 200 && text.length()==0) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_SINA_SUPPORT://新浪新闻Web评论支持
                        if (code == 200 && text != null && text.contains("var data=")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_SOHU_SUPPORT://搜狐新闻Web评论支持
                        if (code == 200 && text != null && text.contains("count")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_IFENG_SUPPORT://凤凰新闻Web评论支持
                        if (code == 200 && text != null && text.equals("1")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_163_APP_SUPPORT://网易新闻App评论支持
                        if (code == 404) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_SINA_APP_SUPPORT://网易新闻App评论支持
                        if (code == 200 && text != null && text.contains("var data=")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_SOHU_APP_SUPPORT://搜狐新闻App评论支持
                        if (text != null && text.contains("成功")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_IFENG_APP_SUPPORT://凤凰新闻App评论支持
                        if (code == 200 && text != null && text.equals("1")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.NEW_PENGPAI_APP_SUPPORT://澎湃新闻App评论支持
                        if (code == 200 && text != null && text.contains("点赞成功")) {
                            r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            return;
                        }
                        break;
                    case Constants.GENERAL_ROBOT://httpPost请求
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        break;
                    case Constants.GENERAL_HTTP://httpGet请求
                        r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                        break;
                }
                r.put(Constants.CODE_REMARK, "Code=" + code);
            } else {
                r.put(Constants.CODE_REMARK, "请求失败");
            }
        } catch (Exception e) {
            r.put(Constants.CODE_REMARK, "新闻评论支持执行请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("新闻评论支持执行请求时异常—>" + task, e);
        }
    }

//    public void test(){
//        String url ="http://isub.snssdk.com/2/data/comment_action/";
//        Map<String, String> map = new HashMap<>();
//        Map<String, String> hmap = new HashMap<>();
//        map.put("comment_id","6031094270");
//        map.put("action","digg");
//        map.put("group_id","6254326489878200577");
////        hmap.put("Cookie","sessionid=619dfe278ed31d513c4d39f5703da725");
//        HttpResponse res = this.getHttpResponse(map, hmap, url);
//        String text = Util.getContent(res).trim();
//        int code = Util.getCode(res);
//        info(text);
//        info(code+"");
//    }
//    public static void main(String []args){
//        NewsCommentSupportHttpClient client = new NewsCommentSupportHttpClient();
////            client.test();
//        String url = "http://toutiao.com/a6254326489878200577/?tt_from=mobile_qq&utm_campaign=client_share&app=news_article&utm_source=mobile_qq&iid=0&utm_medium=toutiao_android";
//        System.out.print(url.split("/")[3].substring(1));
//    }
}

