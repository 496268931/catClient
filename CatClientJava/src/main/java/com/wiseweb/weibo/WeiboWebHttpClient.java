package com.wiseweb.weibo;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.client.util.HttpClien;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;

import java.net.URLDecoder;

/**
 * Created by Administrator on 2015/12/10.
 */
public class WeiboWebHttpClient extends HttpClien {
    public final String WEIBO_ADD = "微博直发";
    public final String WEIBO_FORWARD = "微博转发";
    public final String WEIBO_FORWARD_COMMENT = "微博转评";
    public final String WEIBO_SUPPORT = "微博点赞";
    public final String WEIBO_COMMENT = "微博评论";
    public final String COMMENT_SUPPORT = "评论点赞";
    public final String WEIBO_FOLLOW = "微博关注";

    public final String TOPIC_ADD = "话题直发";
    public final String TOPIC_FOLLOW = "话题关注";

    public final String MOVIE_SCORE = "电影评分";
    public final String MOVIE_SUPPORT = "电影点赞";
    public final String MOVIE_SEE = "电影想看";
    public final String MOVIE_FOLLOW = "电影关注";

    public final String VOIDE_SUPPORT = "视频点赞";
    public final String AUDIO_SUPPORT = "音乐点赞";

    public final String WEIBO_VOTE = "微博投票";
    public static WeiboWebHttpClient weiboWebHttpClient;
    public static WeiboWebHttpClient getWeiboWebHttpClient(){
        weiboWebHttpClient = weiboWebHttpClient ==null?new WeiboWebHttpClient(): weiboWebHttpClient;
        return weiboWebHttpClient;
    }

    public static void main(String[] ages){
        WeiboWebHttpClient weiboWebHttpClient = getWeiboWebHttpClient();
        JSONObject Cookie = new JSONObject();
        Cookie.put("SUE","es%3Da1cab5c86c7d1cff06181c87adf482a5%26ev%3Dv1%26es2%3Dff115c21cfb67c0460e1d4be2db3dba7%26rs0%3DfptrT%252FApOHhBYupND%252FLbw1svRkR1epSDA%252FK8nYnHgV02EmxvzR2hPDTvaqWb3oV58hs5uQKC1VAVCsRscqCtbBFrF32JsuzTH%252BZEfGZpHJFue11t3H1CUdbW1uFkLcZB%252BOgbcxtIRz9JlKjNM%252BYhPGm8QE85UoXRYSSnfeOK86k%253D%26rv%3D0");
        Cookie.put("SUB","_2A257mIWVDeTxGeNP6VAV8i_Kzj-IHXVZYivdrDV8PEJb6dNZWSGXkm5JdS457dCYw6PG39pf5CSc9b-uii4.");
        Cookie.put("SUS","SID-5122421653-1453127109-GZ-bqd6j-bbc8228d1b21f1c93ea41d523bce5267");
        Cookie.put("SUBP","0033WrSXqPxfM725Ws9jqgMF55529P9D9WW6qReI56DMUvikzFS3fGx1");
        Cookie.put("SUP","cv%3D1%26bt%3D1453127109%26et%3D1453213509%26d%3Dc909%26i%3D5267%26us%3D1%26vf%3D%26vt%3D%26ac%3D%26st%3D0%26uid%3D5122421653%26name%3Dkqlocqq426%2540sina.cn%26nick%3D%25E5%258F%259B%25E9%2580%2586%25E7%259A%2584%25E6%259C%25B1%25E5%258F%25A4%25E5%258A%259B%26fmp%3D%26lcp%3D2014-09-13%252013%253A43%253A22");
        JSONObject res = weiboWebHttpClient.weiboSupport(Cookie, "3932811931102150");
        weiboWebHttpClient.info(res.toString());
    }


    /**
     *  微博统一网络请求
     * @param form
     * @param headers
     * @param url
     * @param type
     * @return
     */
    public JSONObject weiboHttpRequest(JSONObject form,JSONObject headers,String url,String type){
        try{
            HttpResponse response = Request(form, headers, url);
            System.out.println(response);
            return handleResponse(response, type);
        } catch (Exception e) {
            error("gewaraHttpRequest(JSONObject form,JSONObject headers,String url,String type)异常", e);
        }
        return null;
    }

    public JSONObject handleResponse(HttpResponse response,String type){
        JSONObject result = new JSONObject();
        try{
            if(response!=null){
                String content = getEntity(response);
                if(content!=null && content.trim().length()>0){
                    JSONObject j = new JSONObject(content);
                    String code = null;
                    result.put("data",j);
                    code = j.getString("code");
                    result.put(Constants.CODE,code);
                    switch(code){
                        case "100000"://执行成功
                            result.put(Constants.CODE_REMARK,"成功");
                            result.put(Constants.STATUS, Constants.TASK_STATUS_COMPLETE);
                            break;
                        case "100006"://帐号异常
                            result.put(Constants.CODE_REMARK,"帐号异常");
                            result.put(Constants.CODE,"100006");
                            break;
                        case "100003"://您的帐号存在高危风险
                            result.put(Constants.CODE_REMARK,j.getString("msg").split("。")[0]);
                            break;
                        case "100001":
                            if(j.getString("msg").contains("提交失败")){
                                result.put(Constants.CODE,"100003");
                            }
                            result.put(Constants.CODE_REMARK,j.get("msg"));
                            break;
                        case "100002":
                            result.put(Constants.REMARK,"账号异常");
                            break;
                        default://第三种情况
                            result.put(Constants.CODE_REMARK,j.isNull("msg")?j.toString():j.get("msg"));
                            break;
                    }
                }else{
                    result.put(Constants.CODE_REMARK,"返回错误结果："+content);
                }
            }else{
                info(type + "请求异常");
                result.put(Constants.CODE_REMARK, "请求异常");
            }
        }catch (Exception e) {
            error("handleResponse(HttpResponse response,String type)异常", e);
            result.put(Constants.CODE_REMARK, "处理返回结果时异常");
        }
        return result;
    }

    /**
     * 微博投票
     * @param Cookie
     * @param poll_id
     * @param items
     * @return
     */
    public JSONObject weiboVote(JSONObject Cookie,String poll_id,String items){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://vote.weibo.com/poll/"+poll_id);
            headers.put("X-Requested-With","XMLHttpRequest");
            headers.put("Content-Type","application/x-www-form-urlencoded");
            headers.put("Cookie",Cookie);
            JSONObject form = new JSONObject();
            form.put("poll_id",poll_id);
            form.put("items",items);
            form.put("anonymous","0");
            form.put("share","0");
            form.put("_t","0");
            return weiboHttpRequest(form,headers,"http://vote.weibo.com/aj/joinpoll?ajwvr=6&__rnd="+getTime(),WEIBO_VOTE);
        }catch (Exception e){
            error("weiboVote(JSONObject Cookie,String poll_id)异常", e);
        }
        return null;
    }

    /**
     * 微博电影想看
     * @param Cookie
     * @param uid
     * @return
     */
    public JSONObject movieSee(JSONObject Cookie,String uid){
        JSONObject result = new JSONObject();
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://movie.weibo.com/");
            headers.put("X-Requested-With","XMLHttpRequest");
            headers.put("Content-Type","application/x-www-form-urlencoded");
            headers.put("Cookie",Cookie);
            JSONObject form = new JSONObject();
            uid = uid.trim();
            if(uid.length()==12){
                uid = uid.substring(uid.length() - 6, uid.length());
            }
            form.put("uid",uid);
            form.put("film_id", uid);
            HttpResponse response = Request(form,headers,"http://movie.weibo.com/movie/webajax/want");
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                String content = getEntity(response);
                if(content!=null && content.contains("retcode")){
                    JSONObject j = new JSONObject(content);
                    int code = j.getInt("retcode");
                    result.put(Constants.STATUS,code==1?Constants.TASK_STATUS_COMPLETE:result.get(Constants.STATUS));
                    if(!j.isNull("retval")){
                        result.put(Constants.CODE_REMARK,j.get("retval"));
                    }
                }else{
                    result.put(Constants.CODE_REMARK,"返回错误结果："+content);
                }
            }
        }catch (Exception e){
            error("movieSee(JSONObject Cookie,String uid)异常", e);
            result.put(Constants.CODE_REMARK, "方法异常"+e);
        }
        return result;
    }

    /**
     * 视频点赞
     * @param Cookie
     * @param object_id
     * @return
     */
    public JSONObject voideSupport(JSONObject Cookie,String object_id){
        return objectLik(Cookie,object_id,VOIDE_SUPPORT);
    }

    /**
     * 音乐点赞
     * @param Cookie
     * @param object_id  电影ID
     * @return
     */
    public JSONObject audioSupport(JSONObject Cookie, String object_id){
        return objectLik(Cookie,"1022:"+object_id,AUDIO_SUPPORT);
    }

    /**
     * 电影点赞
     * @param Cookie
     * @param object_id  电影ID
     * @return
     */
    public JSONObject movieSupport(JSONObject Cookie, String object_id){
        return objectLik(Cookie,"1022:"+object_id,MOVIE_SUPPORT);
    }

    /**
     * 评论点赞
     * @param Cookie
     * @param mid  评论ID
     * @return
     */
    public JSONObject commentSupport(JSONObject Cookie,String mid){
        return objectLik(Cookie,mid,COMMENT_SUPPORT);
    }

    public JSONObject objectLik(JSONObject Cookie, String id,String Object_type){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://weibo.com");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("Content-Type","application/x-www-form-urlencoded");
            headers.put("Cookie",Cookie);
            JSONObject form = new JSONObject();
            form.put("object_id",id);
            form.put("object_type",Object_type.equals(COMMENT_SUPPORT)?"comment":"movie");
            return weiboHttpRequest(form, headers, "http://weibo.com/aj/v6/like/objectlike?ajwvr=6", Object_type);
        }catch (Exception e){
            error("objectLik(JSONObject Cookie, String id,String Object_type)异常", e);
        }
        return null;
    }

    /**
     * 微博关注
     * @param Cookie
     * @param uid  用户ID
     * @return
     */
    public JSONObject weiboFollow(JSONObject Cookie, String uid){
        try{
            JSONObject headers = getHeader();
            headers.put("Cookie",Cookie);
            headers.put("X-Requested-With","XMLHttpRequest");
            headers.put("Referer","http://weibo.com/p/230672?from=interest");
            headers.put("Content-Type","application/x-www-form-urlencoded");
            JSONObject form = new JSONObject();
            form.put("uid",uid);
            form.put("f","0");
            form.put("refer_sort","friends");
            form.put("refer_flag","dynamic");
            form.put("location","friends_dynamic");
            form.put("wforce","1");
            form.put("nogroup","false");
            form.put("fnick","");
            form.put("template","2");
            form.put("third_appkey_alias","attfeed");
            form.put("_t","0");
            return weiboHttpRequest(form, headers,"http://weibo.com/aj/f/followed?ajwvr=6&__rnd="+getTime(), WEIBO_FOLLOW);
        }catch (Exception e){
            error("weiboFollow(JSONObject Cookie,String uid)异常", e);
        }
        return null;
    }

    /**
     * 话题关注
     * @param Cookie
     * @param uid
     * @return
     */
    public JSONObject topicFollow(JSONObject Cookie, String uid){
        return follow(Cookie,uid,TOPIC_FOLLOW);
    }

    /**
     * 电影关注
     * @param Cookie
     * @param uid
     * @return
     */
    public JSONObject movieFollow(JSONObject Cookie, String uid){
        return follow(Cookie,uid,MOVIE_FOLLOW);
    }


    /**
     * 话题或电影关注
     * @param Cookie
     * @param uid  话题ID
     * @return
     */
    public JSONObject follow(JSONObject Cookie, String uid,String type){
        try{
            JSONObject headers = getHeader();
            headers.put("Cookie",Cookie);
            headers.put("X-Requested-With","XMLHttpRequest");
            headers.put("Referer","http://weibo.com/p/"+uid);
            headers.put("Content-Type","application/x-www-form-urlencoded");
            JSONObject form = new JSONObject();
            String domain = uid.substring(0,6);
            form.put("uid","1022:"+uid);
            form.put("objectid","1022:"+uid);
            form.put("f","1");
            form.put("location","page_"+domain+"_home");
            form.put("oid",uid.replace(domain,"").trim());
            form.put("refer_flag","dynamic");
            form.put("location","friends_dynamic");
            form.put("wforce","1");
            form.put("nogroup","1");
            form.put("template","4");
            form.put("isinterest","true");
            form.put("_t","0");
            return weiboHttpRequest(form, headers, "http://weibo.com/p/aj/relation/follow?ajwvr=6&__rnd=" + getTime(), type);
        }catch (Exception e){
            error("follow(JSONObject Cookie,String uid)异常", e);
        }
        return null;
    }


    /**
     * 微博评论
     * @param Cookie
     * @return
     */
    public JSONObject weiboComment(JSONObject Cookie, String mid, String text){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://weibo.com");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("Cookie",Cookie);
            JSONObject form = new JSONObject();
            form.put("act","post");
            form.put("mid",mid);
            form.put("forward","0");
            form.put("isroot","1");
            form.put("content",text);
            form.put("module","bcommlist");
            form.put("_t","0");
            return weiboHttpRequest(form, headers, "http://weibo.com/aj/v6/comment/add?ajwvr=6&__rnd=" + getTime(), WEIBO_COMMENT);
        }catch (Exception e){
            error("weiboComment(JSONObject Cookie, String mid,String text)异常", e);
        }
        return null;
    }



    /**
     * 微博点赞
     * @param Cookie
     * @return
     */
    public JSONObject weiboSupport(JSONObject Cookie, String mid){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://weibo.com");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("Cookie",Cookie);
            info(headers.toString());
            JSONObject form = new JSONObject();
            form.put("version","mini");
            form.put("qid","heart");
            form.put("mid",mid);
            form.put("loc","profile");
            return weiboHttpRequest(form, headers, "http://weibo.com/aj/v6/like/add?ajwvr=6", WEIBO_SUPPORT);
        }catch (Exception e){
            error("weiboSupport(JSONObject Cookie, String mid)异常", e);
        }
        return null;
    }

    /**
     * 微博转发
     * @param Cookie
     * @return
     */
    public JSONObject weiboForward(JSONObject Cookie, String mid){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://www.weibo.com");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("Cookie",Cookie);
            JSONObject form = new JSONObject();
            form.put("mid",mid);
            form.put("style_type","1");
            form.put("rank","0");
            form.put("_t","0");
            return weiboHttpRequest(form, headers,"http://weibo.com/aj/v6/mblog/forward?ajwvr=6&__rnd=" + getTime(),WEIBO_FORWARD);
        }catch (Exception e){
            error("weiboForwardComment(JSONObject Cookie,String mid,String text)异常", e);
        }
        return null;
    }


    /**
     * 微博转发或转评
     * @param Cookie
     * @return
     */
    public JSONObject weiboForwardComment(JSONObject Cookie, String mid, String text){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://www.weibo.com");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("Cookie",Cookie);
            JSONObject form = new JSONObject();
            form.put("mid",mid);
            form.put("style_type","2");
            form.put("reason",text);
            form.put("rank","0");
            form.put("_t","0");
            form.put("is_comment_base","1");
            form.put("is_comment","1");
            return weiboHttpRequest(form, headers,"http://weibo.com/aj/v6/mblog/forward?ajwvr=6&__rnd=" + getTime(), WEIBO_FORWARD_COMMENT);
        }catch (Exception e){
            error("weiboForwardComment(JSONObject Cookie,String mid,String text)异常", e);
        }
        return null;
    }


    /**
     * 微博直发
     * @param Cookie
     * @param text
     * @return
     */
    public JSONObject weiboAdd(JSONObject Cookie,String text,String pic_id){
        return mblogAdd(Cookie,text, WEIBO_ADD,null,pic_id,0);
    }


    /**
     * 电影评分
     * @param Cookie
     * @param text
     * @param id
     * @param score
     * @return
     */
    public JSONObject   movieScore(JSONObject Cookie,String text,String id,int score,String pic_id){
        return mblogAdd(Cookie, text, MOVIE_SCORE, id, pic_id,score);
    }

    /**
     * 话题直发或话题分享
     * @param Cookie
     * @param text
     * @param id
     * @return
     */
    public JSONObject topicAdd(JSONObject Cookie,String text,String id,String pic_id){
        return mblogAdd(Cookie, text, TOPIC_ADD, id,pic_id, 0);
    }

    /**
     * 微博直发  电影评分  话题直发
     * @param Cookie
     * @param text
     * @return
     */
    public JSONObject mblogAdd(JSONObject Cookie,String text,String type,String id,String pic_id,int score){
        try{
            JSONObject headers = getHeader();
            headers.put("Referer","http://www.weibo.com");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("Cookie", Cookie);
            JSONObject form = new JSONObject();
            form.put("style_type","1");
            form.put("text",text);
            form.put("rank","0");
            form.put("pub_type","dialog");
            form.put("_t","0");
            if(pic_id!=null){
                form.put("pic_id",pic_id);
            }
            String url = "";
            switch(type){
                case WEIBO_ADD://微博直发
                    url = "http://weibo.com/aj/mblog/add?ajwvr=6&__rnd=";
                    form.put("location","v6_content_home");
                    form.put("module","stissue");
                    form.put("pub_source","main_");
                    break;
                case MOVIE_SCORE://电影评分
                    headers.put("Referer","http://weibo.com/p/"+id);
                    url = "http://weibo.com/p/aj/v6/mblog/add?ajwvr=6&domain="+id.substring(0, 6)+"&__rnd=";
                    headers.put("Referer","http://weibo.com/p/"+id);
                    form.put("location","page_"+id.substring(0,6)+"_home");
                    form.put("pdetail",id);
                    form.put("pub_source","page_1");
                    form.put("object_id","1022:"+id);
                    form.put("module","publish_913");
                    form.put("page_module_id","913");
                    form.put("show_review","true");
                    form.put("score",score+"");
                    String[] scores = new String[]{"[星星]","[星星][星星]","[星星][星星][星星]","[星星][星星][星星][星星]","[星星][星星][星星][星星][星星]"};
                    form.put("prefixtext","http://weibo.com/p/"+id+" "+scores[score-1]+"，");
                    break;
                case TOPIC_ADD://话题直发
                    headers.put("Referer","http://weibo.com/p/"+id);
                    url = "http://weibo.com/p/aj/proxy?ajwvr=6&__rnd=";
                    String domain = id.substring(0,6);
                    form.put("id",id);
                    form.put("domain",domain);
                    form.put("module","share_topic");
                    form.put("title","分享");
                    form.put("content","我分享了话题");
                    form.put("api_url","http://i.huati.weibo.com/page/modulepublisher");
                    form.put("location","page_"+domain+"_home");
                    form.put("pdetail",id);
                    form.put("pub_source","2");
                    form.put("api","http://i.huati.weibo.com/aj/publisher/?page_id="+id);
                    form.put("longtext","1");
                    break;
            }
            return weiboHttpRequest(form, headers, url + getTime(), type);
        } catch (Exception e) {
            error("mblogAdd(JSONObject Cookie,String text)异常", e);
        }
        return null;
    }

    /**
     * 设置异地登陆保护
     * @param Cookie
     * @param uid
     * @return
     */
    public boolean setWeiboLoginProtect(JSONObject Cookie,String uid){
        try{
            JSONObject headers = new JSONObject();
            headers.put("Cookie",Cookie);
            headers.put("Referer","http://security.weibo.com/security/index");
            headers.put("Upgrade-Insecure-Requests","1");
            headers.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
            HttpResponse response = Request(null, headers, "http://security.weibo.com/security/index");
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                String content = getEntity(response);
                if(content!=null && content.contains("randstring")){
                    JSONObject form = new JSONObject();
                    String[] arr = content.split("randstring = '")[1].split("';");
                    String randstring = arr[0].trim();
                    String timestamp = arr[1].split("timestamp = '")[1].split("timestamp = '")[0].trim();
                    form.put("randstring",randstring);
                    form.put("timestamp",timestamp);
                    form.put("uid",uid);
                    form.put("protect_setting","2");
                    form.put("province0","11");
                    form.put("city0","-1");
                    form.put("province1","44");
                    form.put("city1","3");
                    form.put("province2","44");
                    form.put("city2","1");
                    response = Request(form,headers,"https://security.weibo.com/security/ajprotect");
                    if(response!=null && response.getStatusLine().getStatusCode()==200){
                        content = getEntity(response);
                        if(content!=null && content.contains("http://security.weibo.com/security/proxy?callback=")){
                            content = URLDecoder.decode(Jsoup.parse(content).getElementsByTag("meta").get(0).attr("content").split("url='")[1].split("'")[0].trim(), "utf-8");
                            JSONObject j = new JSONObject(content.split("callback=")[1]);
                            info(uid+j.getString("msg"));
                            return j.getString("msg").equals("登录保护设置成功！");
                        }
                    }
                }else{
                    info(content);
                }
            }
        }catch (Exception e){
            error("setWeiboLoginProtect(JSONObject user)异常", e);
        }
        return false;
    }

    /**
     * 修改用户头像
     * @param Cookie
     * @param Filedata  头像图片base64
     * @return
     */
    public boolean UploadPicture(JSONObject Cookie, String Filedata) {
        try {
            Filedata = Filedata==null?getBase64Image():Filedata;
            if(Filedata!=null){
                JSONObject headers = new JSONObject();
                JSONObject form = new JSONObject();
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
                headers.put("Referer","http://www.weibo.com");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                headers.put("Cookie",Cookie);
                form.put("type", "jpeg");
                form.put("aw", "200");
                form.put("ay", "0");
                form.put("ax", "0");
                form.put("Filedata", Filedata);
                HttpResponse response = Request(form,headers,"http://account.weibo.com/set/aj5/photo/uploadv6?cb=http://www.weibo.com/aj/static/upimgback.html?_wv=5&callback=STK_ijax_" + System.currentTimeMillis());
                if(response!=null){
                    String content = getEntity(response);
                    int code = response.getStatusLine().getStatusCode();
                    if(code==302){
                        String url = getLocation(response);
                        info("上传头像回调地址："+url);
                        if(url != null && url.contains("sinaimg.cn")){
                            return true;
                        }else{
                            info("上传头像完毕无法获取到回调地址");
                        }
                    }else{
                        info("修改头像失败："+code+content);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getBase64Image() {
        try{

        }catch (Exception e){
            error("getBase64Image()异常",e);
        }
        return null;
    }
}
