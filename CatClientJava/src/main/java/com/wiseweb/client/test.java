package com.wiseweb.client;

import com.wiseweb.NewsComment.NewsCommentHttpClient;
import com.wiseweb.json.JSONObject;

/**
 * Created by wiseweb on 2016/4/29.
 */
public class test {
    public static void main(String []args){
        test test = new test();
//        test.wangyiCommentWeb();
//        test.sinaCommentWeb();
        test.souhuCommentWeb();
//        test.ifengComment();
    }



    public void wangyiCommentWeb(){
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

    public void sinaCommentWeb(){
        NewsCommentHttpClient ht  = new NewsCommentHttpClient();
        String url = "http://comment5.news.sina.com.cn/comment/skin/default.html?channel=sh&newsid=comos-fxrtvtp1611163";
        String []strs = url.split("&");
        String channel = strs[0].split("=")[1];
        String newsId = strs[1].split("=")[1];
        JSONObject task_tag_data = new JSONObject();
        JSONObject task_tag_data_header = new JSONObject();
        JSONObject form = new JSONObject();
        task_tag_data.put("exeurl","http://comment5.news.sina.com.cn/cmnt/submit");
        form.put("parent", "B");
        form.put("channel",channel);
        form.put("newsid",newsId);
        form.put("format","json");
        form.put("ie","utf-8");
        form.put("oe","utf-8");
        form.put("ispost",0);
        form.put("share_url",url);
        form.put("video_url","");
        form.put("img_url","");
        form.put("iframe","1");
        form.put("callback","iJax1426140507000");
        task_tag_data_header.put("Referer",url);
        task_tag_data.put("task_tag_data_form",form);
        task_tag_data.put("task_tag_data_header",task_tag_data_header);
        long task_id = 0;
        JSONObject r= new JSONObject();
        ht.NewsCommentAction(null,task_tag_data,task_id,"206",r,null);
    }

    public void souhuCommentWeb(){
        NewsCommentHttpClient ht  = new NewsCommentHttpClient();
        String url = "http://quan.sohu.com/pinglun/cyqemw6s1/446752109";
        String []strs = url.split("/");
        String client_id = url.split("/")[4].trim();
        String topicsid = url.split("/")[5].trim();
        JSONObject task_tag_data = new JSONObject();
        JSONObject task_tag_data_header = new JSONObject();
        JSONObject form = new JSONObject();
        task_tag_data.put("exeurl","http://changyan.sohu.com/api/2/comment/submit");
        form.put("client_id", client_id);
        form.put("topic_id",topicsid);
        task_tag_data_header.put("Referer",url);
        task_tag_data.put("task_tag_data_form",form);
        task_tag_data.put("task_tag_data_header",task_tag_data_header);
        long task_id = 0;
        JSONObject r= new JSONObject();
        ht.NewsCommentAction(null,task_tag_data,task_id,"403",r,null);
    }

    public void ifengComment(){
        NewsCommentHttpClient ht  = new NewsCommentHttpClient();
        String url = "http://gentie.ifeng.com/view.html?docUrl=http%3A%2F%2Fnews.ifeng.com%2Fa%2F20160504%2F48670318_0.shtml&docName=%E6%B5%B7%E5%8F%A3%EF%BC%9A%E7%BD%91%E7%BA%A6%E8%BD%A6%E5%8F%B8%E6%9C%BA%E8%A2%AB%E6%9B%9D%E5%9C%A84%E5%90%8D%E5%A5%B3%E9%AB%98%E4%B8%AD%E7%94%9F%E5%89%8D%E8%87%AA%E6%85%B0(%E5%9B%BE)&skey=f1bc42&speUrl=";
        String []strs = url.split("&");
        String docUrl = strs[0].split("=")[1];
        String docName = strs[1].split("=")[1];
        String commentUrl =strs[0].substring(0,strs[0].indexOf("?"))+"?docUrl="+docUrl+"&docName="+docName;
        String exeurl = "http://comment.ifeng.com/post.php?callback=postCmt&docUrl="+docUrl+"&docName="+docName+"&speUrl=&format=js&content=弄死&callback=postCmt";
        JSONObject task_tag = new JSONObject();
        JSONObject form = new JSONObject();
        form.put("docUrl",docUrl);
        form.put("docName",docName);
        form.put("speUrl","");
        form.put("Referer",commentUrl);
        long task_id = 0;
        JSONObject r= new JSONObject();
        ht.NewsCommentAction(exeurl,form,task_id,"501",r,null);
    }
}
