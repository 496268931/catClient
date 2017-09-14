package com.wiseweb.WeiPiao;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClientCallback;
import com.wiseweb.cat.util.Util;
import com.wiseweb.client.util.Httpclient;
import com.wiseweb.json.JSONException;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.security.MessageDigest;
import java.util.*;

/**
 * Created by wiseweb on 2016/3/3.
 */
public class WeiPiaoHttpClient extends Httpclient {
    //求情地址
    private static final String URL_WANT_SEE = "http://androidcgi.wepiao.com/movie/want";
    private static final String URL_PUBLISH_MY_COMMENT = "http://androidcgi.wepiao.com/comment/save";
    private static final String URL_COMMENT_REPLY = "http://androidcgi.wepiao.com/comment/add-reply";
    public static final String URL_USER_EDIT = "http://androidcgi.wepiao.com/user/user-edit";

    public static WeiPiaoHttpClient weiPiaoHttpClient;
    public static WeiPiaoHttpClient getWeiPiaoHttpClient(){
        weiPiaoHttpClient = weiPiaoHttpClient ==null?new WeiPiaoHttpClient(): weiPiaoHttpClient;
        return weiPiaoHttpClient;
    }

    /**
     * 微票儿电影想看
     * @param movieId
     * @return
     */
    public JSONObject weiPiaoWantSee(String movieId,String task_id,JSONObject r,TaskClientCallback callback){
        r.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
        try {
            Map map = new HashMap();
            JSONObject form = new JSONObject();
            if(movieId != null){
                JSONObject account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
                String token = account.getJSONObject("auth_info").getString("token");
                String uid = account.getString("uid");
                form.put("movieId",movieId);
                form.put("uid",uid);
                form.put("want","1");
                map = getFrom(form);
                HttpResponse res = getResponse(map,token,URL_WANT_SEE);
                if(res == null){
                    r.put(Constants.CODE_REMARK, "微票儿想看请求失败");
                    return r;
                }
                String text = Util.getContent(res);
                if (text == null) {
                    r.put(Constants.CODE_REMARK, "微票儿想看返回值为空");
                    return r;
                }
                int code = Util.getCode(res);
                if(code == 200 && text.contains("success")){
                    r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    return r;
                }
                r.put(Constants.CODE_REMARK, "Code=" + code);
            }
        } catch (JSONException e) {
            r.put(Constants.CODE_REMARK, "微票想看请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("微票想看执行请求时异常—>", e);
        }
        return r;
    }

    /**
     * 微票儿电影评论，
     * @param movieId
     * @param score
     * @param doc
     * @return
     */
    public JSONObject weiPiaoWantComment(String movieId,String doc,String score,String task_id,JSONObject r,TaskClientCallback callback){
        r.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
        try {
            Map map = new HashMap();
            JSONObject form = new JSONObject();
            if(movieId != null){
                JSONObject account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
                String token = account.getJSONObject("auth_info").getString("token");
                String uid = account.getString("uid");
                form.put("movieId",movieId);
                form.put("uid",uid);
                form.put("content",doc);
                if(score!=null && score.trim().length()>0){
                    form.put("score",score);
                }
                map = getFrom(form);
                HttpResponse res = getResponse(map,token,URL_PUBLISH_MY_COMMENT);
                if(res == null){
                    r.put(Constants.CODE_REMARK, "微票儿评论请求失败");
                    return r;
                }
                String text = Util.getContent(res);
                if (text == null) {
                    r.put(Constants.CODE_REMARK, "微票儿评论返回值为空");
                    return r;
                }
                int code = Util.getCode(res);
                if(code == 200 && text.contains("success")){
                    r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    return r;
                }
                r.put(Constants.CODE_REMARK, "Code=" + code);
            }
        } catch (JSONException e) {
            r.put(Constants.CODE_REMARK, "微票儿评论请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("微票儿评论执行请求时异常—>", e);
        }
        return r;
    }

    /**
     * 微票儿电影评论支持
     * @param movieId
     * @return
     */
    public JSONObject weiPiaoCommentPraise(String movieId,String commentId,String task_id,JSONObject r,TaskClientCallback callback){
        r.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
        try {
            Map map = new HashMap();
            JSONObject form = new JSONObject();
            if(movieId != null){
                JSONObject account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
                String token = account.getJSONObject("auth_info").getString("token");
                String uid = account.getString("uid");
                form.put("movieId",movieId);
                form.put("uid",uid);
                form.put("favor","1");
                form.put("commentId",commentId);
                map = getFrom(form);
                HttpResponse res = getResponse(map,token,URL_WANT_SEE);
                if(res == null){
                    r.put(Constants.CODE_REMARK, "微票儿评论支持请求失败");
                    return r;
                }
                String text = Util.getContent(res);
                if (text == null) {
                    r.put(Constants.CODE_REMARK, "微票儿评论支持返回值为空");
                    return r;
                }
                int code = Util.getCode(res);
                if(code == 200 && text.contains("success")){
                    r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    return r;
                }
                r.put(Constants.CODE_REMARK, "Code=" + code);
            }
        } catch (JSONException e) {
            r.put(Constants.CODE_REMARK, "微票儿评论支持请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("微票儿评论支持执行请求时异常—>", e);
        }
        return r;
    }
    /**
     * 微票儿电影评论回复
     * @param movieId
     * @param commentId
     * @param doc
     * @return
     */
    public JSONObject weiPiaoWantReply(String movieId,String commentId,String doc,String task_id,JSONObject r,TaskClientCallback callback){
        r.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
        try {
            Map map = new HashMap();
            JSONObject form = new JSONObject();
            if(movieId != null){
                JSONObject account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
                String token = account.getJSONObject("auth_info").getString("token");
                String uid = account.getString("uid");
                form.put("movieId",movieId);
                form.put("uid",uid);
                form.put("commentId",commentId);
                form.put("content",doc);
                map = getFrom(form);
                HttpResponse res = getResponse(map,token,URL_COMMENT_REPLY);
                if(res == null){
                    r.put(Constants.CODE_REMARK, "微票儿评论回复请求失败");
                    return r;
                }
                String text = Util.getContent(res);
                if (text == null) {
                    r.put(Constants.CODE_REMARK, "微票儿评论回复返回值为空");
                    return r;
                }
                int code = Util.getCode(res);
                if(code == 200 && text.contains("success")){
                    r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    return r;
                }
                r.put(Constants.CODE_REMARK, "Code=" + code);
            }
        } catch (JSONException e) {
            r.put(Constants.CODE_REMARK, "微票儿评论回复请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("微票儿评论执行请求时异常—>", e);
        }
        return r;
    }

    /**
     * 微票儿修改昵称
     * @param
     * @return
     */
    public void weiPiaoUserEdit(String nickName,String task_id,JSONObject r,TaskClientCallback callback){
        try {
            r.put(Constants.STATUS, Constants.TASK_STATUS_FAILED);
            Map map = new HashMap();
            JSONObject form = new JSONObject();
            if(nickName != null){
                JSONObject account = callback.getTaskAccountByTaskID(String.valueOf(task_id));
                String token = account.getJSONObject("auth_info").getString("token");
                String uid = account.getString("uid");
                form.put("uid",uid);
                form.put("t",System.currentTimeMillis()/1000+"");
                form.put("nickName",nickName);
                map = getFrom(form);
                HttpResponse res = getResponse(map,token,URL_USER_EDIT);
                if(res == null){
                    r.put(Constants.CODE_REMARK, "微票儿想看请求失败");
                    return;
                }
                String text = Util.getContent(res);
                if (text == null) {
                    r.put(Constants.CODE_REMARK, "微票儿想看返回值为空");
                }
                int code = Util.getCode(res);
                if(code == 200 && text.contains("success")){
                    r.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                    return;
                }
                r.put(Constants.CODE_REMARK, "Code=" + code);
            }
        } catch (JSONException e) {
            r.put(Constants.CODE_REMARK, "微票想看请求时异常");
            r.put(Constants.STATUS, Constants.TASK_STATUS_ERROR);
            error("微票想看执行请求时异常—>", e);
        }
    }
    /**
     * Http请求
     */
    public HttpResponse getResponse(Map map,String token,String exeurl) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            //请求超时
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            //读取超时
            //httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
            HttpPost post = null;
            HttpGet get = null;
            if (map != null) {//post请求
                post = new HttpPost(exeurl);
                List<NameValuePair> nvps = addDate(map);
                post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            } else {//get请求
                get = new HttpGet(exeurl);
            }
            //添加消息头信息
            post.addHeader("token",token);
            //请求
            HttpResponse res = httpclient.execute(post == null ? get : post);
            return res;
        } catch (Exception e) {
            error("无代理IP请求异常—>" + exeurl, e);
        }
        return null;
    }

    /**添加参数到集合
     * @param map
     * @return
     */
    private List<NameValuePair> addDate(Map<String, String> map) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        try {
            Iterator i= map.entrySet().iterator();
            while(i.hasNext()){
                Map.Entry e=(Map.Entry)i.next();
                nvps.add(new BasicNameValuePair(e.getKey().toString().trim(),e.getValue().toString().trim()));
            }
        } catch (Exception e) {
            logger.error("异常", e);
        }
        return nvps;
    }

    /**添加消息头信息
     * @param http
     * @param hmap
     * @param type
     */
    private void addHeader(Object http, Map<String, String> hmap,String type) {
        if(hmap!=null && hmap.size()>0){
            try {
                HttpPost post = (HttpPost) (type.equals("post")?http:null);
                HttpGet get = (HttpGet) (type.equals("get")?http:null);
                Iterator i= hmap.entrySet().iterator();
                String Cookie = "";
                while(i.hasNext()){
                    Map.Entry e=(Map.Entry)i.next();
                    switch(e.getKey().toString().trim()){
                        case "token":
                            (type.equals("get")?get:post).addHeader(e.getKey().toString().trim(),e.getValue().toString().trim());
                            break;
                        default:
                            Cookie = Cookie;
                            break;
                    }
                }
            } catch (Exception e) {
                logger.error("异常",e);
            }
        }
    }

    /**
     * 拼装组合form
     * @param from
     * @return
     */
    public static Map<String,Object> getFrom(JSONObject from) {
        from.put("appver","5.5.2");
        from.put("v","2015121801");
        from.put("appkey","9");
        from.put("from","0123456789");
        Map<String,Object> p = new HashMap<>();
        StringBuffer sb = new StringBuffer();
        List<String> keylist = new ArrayList();
        for (String key : from.keySet()) {
            keylist.add(key);
        }
        Collections.sort(keylist);
        for (int i = 0; i < keylist.size(); i++) {
            sb.append("&").append(keylist.get(i)).append("=").append(from.get(keylist.get(i)));
            p.put(keylist.get(i),from.get(keylist.get(i)));
        }
        String s = sb.toString().substring(1);
        String s1 = "zJwaQBQ553lHr6DfnX02WcJtZF" + s;
        String md5 =  MD5(s1);
        p.put("sign", md5);
        return p;

    }

    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
