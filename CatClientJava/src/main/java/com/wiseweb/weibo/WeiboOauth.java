package com.wiseweb.weibo;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.client.util.HttpClien;
import com.wiseweb.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;

/**
 * Created by Administrator on 2016/1/19.
 */
public class WeiboOauth extends HttpClien{
    public static WeiboOauth weiboOauth;
    public static WeiboOauth getWeiboOauth(){
        weiboOauth = weiboOauth==null?new WeiboOauth():weiboOauth;
        return weiboOauth;
    }
    public  void Authorize(JSONObject Cookie,JSONObject account,JSONObject result) {
        try {
            String username = account.getString(Constants.ACCOUNT_USERNAME);
            String password = account.getString(Constants.ACCOUNT_PASSWORD);
            //时光网网页认证URL：http://passport.mtime.com/unitelogin/dispatch/weibo/web/
            //格瓦拉触屏版认证URL：https://open.weibo.cn/oauth2/authorize?client_id=2536251945&display=mobile&forcelogin=true&redirect_uri=http://m.gewara.com/touch/sinaLoginCallBack.xhtml?lastpage=http://m.gewara.com/h
            String OauthUrl = "https://open.weibo.cn/oauth2/authorize?client_id=2536251945&display=mobile&forcelogin=true&redirect_uri=http://m.gewara.com/touch/sinaLoginCallBack.xhtml?lastpage=http://m.gewara.com/h";
            HttpResponse response = Request(null,null,OauthUrl);
            if(response!=null && response.getStatusLine().getStatusCode()==200){
                String content = getEntity(response);
                int code = response.getStatusLine().getStatusCode();
                if(content!=null && content.length()>0){
                    JSONObject form = getAuthorizeData(content);
                    if(form==null || form.length()<5){
                        result.put(Constants.CODE_REMARK,"新浪微博认证时，获取请求表单数据失败");
                    }else{
                        form.put("userId",username);
                        form.put("passwd",password);
                        form.put("ticket",Cookie.getString("tgc"));
                        JSONObject headers = new JSONObject();
                        headers.put("Referer",OauthUrl);
                        headers.put("Cookie",Cookie);
                        OauthUrl = OauthUrl.split("\\?")[0].trim();
                        //认证请求
                        response = Request(form, headers, OauthUrl);
                        content = getEntity(response);
                        code = response.getStatusLine().getStatusCode();
                        if(content!=null && content.contains("访问出错了！")){
                            result.put(Constants.CODE_REMARK,"新浪微博认证失败,帐号异常");
                        }else{
                            //判断该帐号是否需要授权
                            if(code==200 || code==302){
                                String url = null;
                                switch(code){
                                    case 302://认证成功
                                        url = getLocation(response.getLastHeader("Location"));
                                        break;
                                    case 200://还需要授权
                                        form = getAuthorizeData(content);
                                        headers.put("Referer",OauthUrl);
                                        response = Request(form, headers, OauthUrl);
                                        code = response.getStatusLine().getStatusCode();
                                        url = getLocation(response.getLastHeader("Location"));
                                        break;
                                }
                                if(url==null){//判断回调地址是否会空
                                    result.put(Constants.CODE_REMARK,"新浪微博认证时，回调地址为null，认证失败");
                                }else {
                                    if (url.contains("access_token")) {//取token
                                        String[] sp = new URL(url).getQuery().trim().split("&");
                                        for (int i = 0; i < sp.length; i++) {
                                            String[] sp2 = sp[i].trim().split("=");
                                            Cookie.put(sp2[0].trim(), sp2[1].trim());
                                        }
                                    } else if (url.contains("code")) {//取cookie
                                        Cookie.put("code", url.trim());
                                    }else {
                                        result.put(Constants.CODE_REMARK, "新浪微博认证时，回调地址有问题:"+url);
                                    }
                                }
                            }else{
                                result.put(Constants.CODE_REMARK,"新浪微博认证时，返回结果不是200或302");
                            }
                        }
                    }
                }else{
                    result.put(Constants.CODE_REMARK,"请求新浪微博认证页面返回错误结果");
                }
            }else{
                result.put(Constants.CODE_REMARK,"请求新浪微博认证页面失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put(Constants.CODE_REMARK, "新浪微博认证异常" + e.toString());
        }
    }
    /**解析认证页面，获取授权参数
     * @param text
     */
    public JSONObject getAuthorizeData(String text){
        JSONObject form = new JSONObject();
        try {
            Document doc = Jsoup.parse(text);
            Elements e = doc.getElementsByTag("input");
            for(int i=0;i<e.size();i++){
                switch(e.get(i).attr("name")){
                    case "display":
                        form.put("display",""+e.get(i).attr("value"));
                        break;
                    case "action":
                        form.put("action",""+e.get(i).attr("value"));
                        break;
                    case "scope":
                        form.put("scope",""+e.get(i).attr("value"));
                        break;
                    case "ticket":
                        form.put("ticket",""+e.get(i).attr("value"));
                        break;
                    case "isLoginSina":
                        form.put("isLoginSina",""+e.get(i).attr("value"));
                        break;
                    case "withOfficalFlag":
                        form.put("withOfficalFlag",""+e.get(i).attr("value"));
                        break;
                    case "withOfficalAccount":
                        form.put("withOfficalAccount",""+e.get(i).attr("value"));
                        break;
                    case "regCallback":
                        form.put("regCallback",""+e.get(i).attr("value"));
                        break;
                    case "redirect_uri":
                        form.put("redirect_uri",""+e.get(i).attr("value"));
                        break;
                    case "client_id":
                        form.put("client_id",""+e.get(i).attr("value"));
                        break;
                    case "appkey62":
                        form.put("appkey62",""+e.get(i).attr("value"));
                        break;
                    case "state":
                        form.put("state",""+e.get(i).attr("value"));
                        break;
                    case "from":
                        form.put("from",""+e.get(i).attr("value"));
                        break;
                    case "offcialMobile":
                        form.put("offcialMobile",""+e.get(i).attr("value"));
                        break;
                    case "uid":
                        form.put("uid",""+e.get(i).attr("value"));
                        break;
                    case "url":
                        form.put("url",""+e.get(i).attr("value"));
                        break;
                    case "visible":
                        form.put("visible",""+e.get(i).attr("value"));
                        break;
                    case "version":
                        form.put("version",""+e.get(i).attr("value"));
                        break;
                    case "sso_type":
                        form.put("sso_type",""+e.get(i).attr("value"));
                        break;
                    case "quick_auth":
                        form.put("quick_auth",""+e.get(i).attr("value"));
                        break;
                    case "response_type":
                        form.put("response_type",""+e.get(i).attr("value"));
                        break;
                    case "verifyToken":
                        form.put("verifyToken",""+e.get(i).attr("value"));
                        break;
                    case "userId":
                        form.put("userId",""+e.get(i).attr("value"));
                        break;
                    case "passwd":
                        form.put("passwd",""+e.get(i).attr("value"));
                        break;
                    case "switchLogin":
                        form.put("switchLogin",""+e.get(i).attr("value"));
                        break;
                }
            }
            return form;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取重定向地址
     * @return
     */
    public String getLocation(Header h) {
        if(h!=null){
            try {
                return h.getValue().replace("#","?");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
