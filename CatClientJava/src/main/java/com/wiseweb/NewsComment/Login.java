package com.wiseweb.NewsComment;

import com.wiseweb.client.util.HttpClien;
import com.wiseweb.client.util.Httpclient;
import com.wiseweb.cat.base.ConfigerFactory;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskClientCallback;
import com.wiseweb.cat.util.Base64;
import com.wiseweb.cat.util.MD5;
import com.wiseweb.cat.util.Util;
import com.wiseweb.client.util.SinaCode;
import com.wiseweb.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.apache.log4j.helpers.LogLog.error;

public class Login extends Httpclient{

    /**根据帐号信息，判断是否需要登陆
     *  @param key
     * @param account
     * @return
     */
    public Map<String,String> getCookie(String key,JSONObject account){
        Map<String,String> cookie = new HashMap<String,String>();
        try{
            if (account.has(Constants.ACCOUNT_AUTHINFO)){//判断认证信息是否存在
                JSONObject auth_info = account.getJSONObject(Constants.ACCOUNT_AUTHINFO);
                if (auth_info.has(key) && auth_info.get(key) instanceof JSONObject) {//判断认证信息是否存在Cookie信息
                    long time = auth_info.getJSONObject(key).getLong("time");
                    if(time - System.currentTimeMillis() > 10000){//如果Cookie不过期
                        cookie = Util.getMap(auth_info.getJSONObject(key));
                        cookie.remove("time");
                    }
                    return cookie;
                }
            }
        }catch(Exception e){
            cookie.clear();
            error("异常",e);
        }
        return cookie;
    }
//
//      public static void main(String[] ages){
//              try{
//                      Login l = new Login();
//                      Map<String,String> hmap = new HashMap<String,String>();
//                      JSONObject r = new JSONObject();
//                      JSONObject account = new JSONObject();
//                      account.put("username","tongpiea54711@163.com");
//                      account.put("password", "ca2d3e4");
//                      l.WyNewsWeb(hmap,account,r,null);
//                      System.out.println(hmap+"\n"+r);
//              }catch(Exception e){
//                      e.printStackTrace();
//
//              }
//      }


    /**网易Web登陆
     * @param hmap
     * @param account
     * @param r
     * @return
     */
    public boolean WyNewsWeb(Map<String,String> hmap,JSONObject account,JSONObject r, TaskClientCallback callback){
        try {
            Map<String,String> map = new HashMap<String, String>();
            map.put("username",account.getString(Constants.ACCOUNT_USERNAME));
            map.put("password",account.getString(Constants.ACCOUNT_PASSWORD));
            map.put("type","1");
            map.put("product","163");
            map.put("savelogin","1");
            map.put("url","http://www.163.com/special/0077450P/login_frame.html");
            map.put("url2","http://www.163.com/special/0077450P/login_frame.html");
            map.put("noRedirect","1");
            hmap.put("Referer","http://www.163.com/");
            HttpResponse res = this.getHttpResponse(map,hmap,"https://reg.163.com/logins.jsp");
            if(res!=null){
                String text = Util.getContent(res);
                int code = Util.getCode(res);
                if(hmap.get("NTES_SESS")!=null){//登陆成功
                    hmap.remove("Referer");
                    return true;
                }else{//登陆失败
                    r.put(Constants.CODE_REMARK,"网易新闻Web登陆失败");
                    if(text.contains("http://www.163.com/special/0077450P/login_frame.html?errorType=460&errorUsername="+account.getString(Constants.ACCOUNT_USERNAME))){
                        r.put(Constants.CODE_REMARK,r.getString(Constants.CODE_REMARK)+"：密码错误!");
                        Map<String, String> info = new HashMap<String, String>();
                        info.put(Constants.STATUS, "0");
                        info.put(Constants.REMARK,"密码错误");
                        callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID), info);
                    }
                    return false;
                }
            }else{
                r.put(Constants.CODE_REMARK,"网易新闻Web登陆时请求失败");
                return false;
            }
        } catch (Exception e) {
            error("异常", e);
        }
        return false;
    }

    /**搜狐新闻Web登陆
     * @param hmap
     * @param account
     * @param r
     * @param callback
     * @return
     */
    public boolean SohuNewsWeb(Map<String, String> hmap, JSONObject account,JSONObject r, TaskClientCallback callback) {
        try {
            HttpResponse res = this.getHttpResponse(null,hmap,"https://passport.sohu.com/sso/login.jsp?userid="+account.getString(Constants.ACCOUNT_USERNAME).trim()+"&password="+MD5.Encrypt(account.getString(Constants.ACCOUNT_PASSWORD).trim())+"&pwdtype=1&appid=1019&&persistentcookie=0&s=1477387296451&b=7&w=1920&v=26");
            if(res!=null){
                String text = Util.getContent(res);
                int code = Util.getCode(res);
                if(text!=null && text.contains("success")){
                    return true;
                }else{
                    r.put(Constants.CODE_REMARK,"登陆失败1，Code="+code);
                }
            }else{
                r.put(Constants.CODE_REMARK,"登陆时请求失败");
            }
        } catch (Exception e) {
            r.put(Constants.CODE_REMARK, "登陆异常" + e.toString());
            error("搜狐新闻Web登陆异常\n" + account.toString(), e);
        }
        return false;
    }

    /**澎湃新闻App登陆
     * @param hmap
     * @param account
     * @param r
     * @param callback
     * @return
     */
    public boolean PengPaiNewsApp(Map<String, String> hmap,String uuid, JSONObject account,JSONObject r, TaskClientCallback callback) {
        try{
            HttpResponse res = this.getHttpResponse(null,null,"http://app.thepaper.cn/clt/v2/login.msp?loginName="+account.getString(Constants.ACCOUNT_USERNAME).trim()+"&pwd="+account.getString(Constants.ACCOUNT_PASSWORD).trim()+"&WD-UUID="+uuid);
            if(res!=null){
                String text = Util.getContent(res);
                int code = Util.getCode(res);
                if(text!=null && text.indexOf("处理成功")!=-1){
                    return true;
                }else{
                    r.put(Constants.CODE_REMARK,"登陆失败，Code="+code);
                }
            }else{
                r.put(Constants.CODE_REMARK,"登陆时请求失败");
            }
        } catch (Exception e) {
            r.put(Constants.CODE_REMARK, "登陆异常" + e.toString());
            error("澎湃新闻App登陆异常\n" + account.toString(), e);
        }
        return false;
    }




    /**头条新闻App登陆
     * @param hmap
     * @param account
     * @param r
     * @param callback
     */
    public boolean TouTiaoNewsApp(Map<String, String> hmap, JSONObject account,JSONObject r, TaskClientCallback callback) {
        try{
            HttpResponse res = this.getHttpResponse(null,hmap,"https://ic.snssdk.com/user/mobile/login/v2?password="+account.getString(Constants.ACCOUNT_PASSWORD).trim()+"&mobile="+account.getString(Constants.ACCOUNT_USERNAME).trim()+"&uuid=860312028718532&device_id=1940743141");
            if(res!=null){
                String text = Util.getContent(res);
                int code = Util.getCode(res);
                if(text!=null && text.contains("success")){//登陆成功
                    return true;
                }else if(text!=null && text.contains("error")){
                    JSONObject j = new JSONObject(text).getJSONObject("data");
                    String key = j.has("alert_text")?"alert_text":"description";
                    text = text.contains("alert_text")?j.getString(key):j.getString(key);
                    if(text.contains("密码错误")){//密码错误
                        r.put(Constants.CODE_REMARK,"登陆失败，"+j.getString(key));
                        Map<String, String> info = new HashMap<String, String>();
                        info.put(Constants.STATUS, "0");
                        info.put(Constants.REMARK,j.getString(key));
//                        callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID), info);
                    }else{
                        r.put(Constants.CODE_REMARK,"登陆失败,"+j.getString(key));
                    }
                }else{
                    r.put(Constants.CODE_REMARK,"登陆失败,返回值="+text);
                }
            }else{
                r.put(Constants.CODE_REMARK,"登陆时请求失败");
            }
        } catch (Exception e) {
            r.put(Constants.CODE_REMARK, "登陆异常" + e.toString());
            error("头条新闻App登陆异常\n"+account.toString(),e);
        }
        return false;
    }



    /**凤凰新闻Web登陆
     * @param hmap
     * @param account
     * @param r
     * @param callback
     * @return
     */
    public boolean IfengNewsWeb(Map<String, String> hmap, JSONObject account,JSONObject r,TaskClientCallback callback) {
        try {
            boolean b = SinaWeiboPhoneLogin(hmap, account, r, Constants.NEWS_IFENG_SINA_OAUTH_MSG, 3, callback);
            if(!b)return false;
            String code = hmap.get("code").trim();
            hmap.clear();
            this.getHttpResponse(null, hmap, code);
            return b;
        } catch (Exception e) {
            r.put(Constants.CODE_REMARK, "登陆异常" + e.toString());
            error("凤凰新闻Web登陆异常", e);
        }
        return false;
    }



    public boolean Authorize(Map<String, String> hmap,JSONObject account,JSONObject r, String OauthMsg,TaskClientCallback callback) {
        try {
            String username = account.getString(Constants.ACCOUNT_USERNAME);
            String password = account.getString(Constants.ACCOUNT_PASSWORD);
            String OauthUrl = ConfigerFactory.getConfiger().getOauthUrl(OauthMsg).split(",")[new Random().nextInt(ConfigerFactory.getConfiger().getOauthUrl(OauthMsg).split(",").length)].trim();
            if(OauthUrl==null){
                r.put(Constants.CODE_REMARK,"从配置文件获取微博认证地址失败");
                return false;
            }
            HttpResponse res = this.getHttpResponse(null, null, OauthUrl);
            if(res!=null){
                String text = Util.getContent(res);
                int code = Util.getCode(res);
                if(code==200 && text!=null){
                    Map<String,String> map = getAuthorizeData(text);
                    if(map==null || map.size()<5){
                        info("新浪微博认证地址："+OauthUrl);
                        r.put(Constants.CODE_REMARK,"新浪微博认证时，获取请求表单数据失败");
                    }else{
                        map.put("userId",username);
                        map.put("passwd",password);
                        map.put("ticket",hmap.get("tgc"));
                        hmap.put("Referer",OauthUrl);
                        OauthUrl = OauthUrl.split("\\?")[0].trim();
                        //认证请求
                        res = this.getHttpResponse(map,hmap,OauthUrl);
                        text = Util.getContent(res);
                        code = Util.getCode(res);
                        if(text!=null && text.contains("访问出错了！")){
                            r.put(Constants.CODE_REMARK,"新浪微博认证失败,帐号异常");
                            Map<String, String> info = new HashMap<String, String>();
                            info.put(Constants.STATUS, "0");
                            info.put(Constants.REMARK,"新浪微博认证失败,帐号异常");
                            callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID), info);
                        }else{
                            //判断该帐号是否需要授权
                            if(code==200 || code==302){
                                String url = null;
                                switch(code){
                                    case 302://认证成功
                                        url = getLocation(res.getLastHeader("Location"));
                                        break;
                                    case 200://还需要授权
                                        map.clear();
                                        map = getAuthorizeData(text);
                                        hmap.put("Referer",OauthUrl);
                                        res = this.getHttpResponse(map, hmap,OauthUrl);
                                        url = getLocation(res.getLastHeader("Location"));
                                        break;
                                }
                                if(url==null){//判断回调地址是否会空
                                    r.put(Constants.CODE_REMARK,"新浪微博认证时，回调地址为null，认证失败");
                                }else {
                                    hmap.clear();
                                    if (url.contains("access_token")) {//取token
                                        String[] sp = new URL(url).getQuery().trim().split("&");
                                        for (int i = 0; i < sp.length; i++) {
                                            String[] sp2 = sp[i].trim().split("=");
                                            hmap.put(sp2[0].trim(), sp2[1].trim());
                                        }
                                    } else if (url.contains("code")) {//取cookie
                                        hmap.put(Constants.CODE, url.trim());
                                    }else {
                                        r.put(Constants.CODE_REMARK, "新浪微博认证时，回调地址有问题:"+url);
                                        return false;
                                    }
                                    return true;
                                }
                            }else{
                                r.put(Constants.CODE_REMARK,"新浪微博认证时，返回结果不是200或302");
                            }
                        }
                    }
                }else{
                    r.put(Constants.CODE_REMARK,"请求新浪微博认证页面陈功，但返回结果不正确");
                }
            }else{
                r.put(Constants.CODE_REMARK,"请求新浪微博认证页面时失败");
            }
        } catch (Exception e) {
            error("新浪微博认证异常", e);
            r.put(Constants.CODE_REMARK, "新浪微博认证异常" + e.toString());
        }
        return false;
    }
    /**
     * 获取重定向地址
     * @return
     */
    private String getLocation(Header h) {
        if(h!=null){
            try {
                return h.getValue().replace("#","?");
            } catch (Exception e) {
                error("异常",e);
            }
        }
        return null;
    }

    /**解析认证页面，获取授权参数
     * @param text
     */
    private Map<String,String> getAuthorizeData(String text){
        Map<String,String> map = new HashMap<String, String>();
        try {
            Document doc = Jsoup.parse(text);
            Elements e = doc.getElementsByTag("input");
            for(int i=0;i<e.size();i++){
                switch(e.get(i).attr("name")){
                    case "display":
                        map.put("display",""+e.get(i).attr("value"));
                        break;
                    case "action":
                        map.put("action",""+e.get(i).attr("value"));
                        break;
                    case "scope":
                        map.put("scope",""+e.get(i).attr("value"));
                        break;
                    case "ticket":
                        map.put("ticket",""+e.get(i).attr("value"));
                        break;
                    case "isLoginSina":
                        map.put("isLoginSina",""+e.get(i).attr("value"));
                        break;
                    case "withOfficalFlag":
                        map.put("withOfficalFlag",""+e.get(i).attr("value"));
                        break;
                    case "withOfficalAccount":
                        map.put("withOfficalAccount",""+e.get(i).attr("value"));
                        break;
                    case "regCallback":
                        map.put("regCallback",""+e.get(i).attr("value"));
                        break;
                    case "redirect_uri":
                        map.put("redirect_uri",""+e.get(i).attr("value"));
                        break;
                    case "client_id":
                        map.put("client_id",""+e.get(i).attr("value"));
                        break;
                    case "appkey62":
                        map.put("appkey62",""+e.get(i).attr("value"));
                        break;
                    case "state":
                        map.put("state",""+e.get(i).attr("value"));
                        break;
                    case "from":
                        map.put("from",""+e.get(i).attr("value"));
                        break;
                    case "offcialMobile":
                        map.put("offcialMobile",""+e.get(i).attr("value"));
                        break;
                    case "uid":
                        map.put("uid",""+e.get(i).attr("value"));
                        break;
                    case "url":
                        map.put("url",""+e.get(i).attr("value"));
                        break;
                    case "visible":
                        map.put("visible",""+e.get(i).attr("value"));
                        break;
                    case "version":
                        map.put("version",""+e.get(i).attr("value"));
                        break;
                    case "sso_type":
                        map.put("sso_type",""+e.get(i).attr("value"));
                        break;
                    case "quick_auth":
                        map.put("quick_auth",""+e.get(i).attr("value"));
                        break;
                    case "response_type":
                        map.put("response_type",""+e.get(i).attr("value"));
                        break;
                    case "verifyToken":
                        map.put("verifyToken",""+e.get(i).attr("value"));
                        break;
                    case "userId":
                        map.put("userId",""+e.get(i).attr("value"));
                        break;
                    case "passwd":
                        map.put("passwd",""+e.get(i).attr("value"));
                        break;
                    case "switchLogin":
                        map.put("switchLogin",""+e.get(i).attr("value"));
                        break;
                }
            }
            return map;
        } catch (Exception e) {
            error("异常", e);
        }
        return null;
    }

    /**
     *
     * @param hmap
     * @param account
     * @param r
     * @param OauthMsg
     * @param loginType  登陆类型 1-取触屏版Cookie 2-取新浪Cookie 3-需要通过新浪Cookie继续认证
     * @param callback
     * @return
     */
    public boolean SinaWeiboPhoneLogin(Map<String, String> hmap, JSONObject account,JSONObject r,String OauthMsg,int loginType,TaskClientCallback callback){
        try{
            if(loginType==3){
                hmap.putAll(getCookie(Constants.SINA_COM_CN_COOKIE, account));
                if(hmap.size()>0){
                    return Authorize(hmap,account,r,OauthMsg,callback);
                }
            }
            String username = account.getString(Constants.ACCOUNT_USERNAME).trim();
            String password = account.getString(Constants.ACCOUNT_PASSWORD).trim();
            //判断帐号是否需要输入验证码
            HttpResponse res = this.getHttpResponse(null, null, "https://login.sina.com.cn/sso/prelogin.php?checkpin=1&entry=mweibo&su=" + new Base64().encode(username.trim().getBytes()));
            String text = Util.getContent(res);
            Map<String,String> map = new HashMap<String, String>();
            if(text==null || !text.contains("showpin") || new JSONObject(text).getInt("showpin")==1){
                r.put(Constants.CODE_REMARK,"新浪微博手机触屏版登陆需要验证码");
                HttpResponse responses = new HttpClien().Request(null,null,"https://passport.weibo.cn/captcha/image");
                text = new HttpClien().getEntity(responses);
                JSONObject codeJson  = new JSONObject(text);
                String baseCode = codeJson.getJSONObject("data").getString("image").replace("data:image/png;base64,", "");
                map.put("pincode", SinaCode.getCodeFromImg(baseCode));
                map.put("pcid",codeJson.getJSONObject("data").getString("pcid"));
            }

            map.put("username",username);
            map.put("password",password);
            map.put("savestate","1");
            map.put("ec","0");
            map.put("pagerefer","https://passport.weibo.cn/signin/welcome?entry=mweibo&r=http://m.weibo.cn/&");
            map.put("entry","mweibo");
            map.put("loginfrom","");
            map.put("client_id","");
            map.put("code","");
            map.put("hff","");
            map.put("hfp","");
            hmap.put("Referer","https://passport.weibo.cn/signin/login?");
            res = this.getHttpResponse(map,hmap,"https://passport.weibo.cn/sso/login");
            if(res==null){
                r.put(Constants.CODE_REMARK, "新浪微博手机触屏版登陆请求失败");
            }else {
                text = Util.getContent(res);
                if (text != null && text.contains("retcode")) {
                    JSONObject j = new JSONObject(text);
                    int retcode = j.getInt("retcode");
                    String msg = j.getString("msg");
                    if(retcode==20000000){
                        try {
                            JSONObject auth_info = new JSONObject();
                            hmap.remove("Referer");
                            hmap.remove("SSOLoginState");
                            hmap.remove("ALF");
                            auth_info.put(Constants.SINA_CN_COOKIE, hmap);
                            auth_info.getJSONObject(Constants.SINA_CN_COOKIE).put("time",Util.getCookieValidityTime(Constants.SINA_COM_CN_COOKIE));
                            String uid = j.getJSONObject("data").getString("uid");
                            j = j.getJSONObject("data").getJSONObject("crossdomainlist");
                            String[] urls = new String[]{j.getString("weibo.com"),j.getString("sina.com.cn"),j.getString("weibo.cn")};
                            Map<String,String> hmap2 = new HashMap<>();
                            for(int i=0;i<urls.length;i++) {
                                try{
                                    getHttpResponse(null, hmap2, "https:" + urls[i].trim());
                                    if (hmap2.get("SUB") != null) {
                                        String key = i == 0 ? Constants.WEIBO_COM_COOKIE : (i == 1 ? Constants.SINA_COM_CN_COOKIE : Constants.WEIBO_CN_COOKIE);
                                        hmap2.remove("SSOLoginState");
                                        hmap2.remove("ALF");
                                        auth_info.put(key, hmap2);
                                        auth_info.getJSONObject(key).put("time", Util.getCookieValidityTime(Constants.SINA_COM_CN_COOKIE));
                                    }
                                    hmap2.clear();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                            //更新帐号Cookie信息
                            Map<String, String> info = new HashMap<String, String>();
                            info.put(Constants.STATUS, "1");
                            info.put(Constants.REMARK, "新浪微博触平版登陆成功");
                            info.put(Constants.ACCOUNT_AUTHINFO, auth_info.toString());
//                            callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID),info);
                            if(loginType!=1 && auth_info.has(Constants.SINA_COM_CN_COOKIE)){
                                hmap = Util.getMap(auth_info.getJSONObject(Constants.SINA_COM_CN_COOKIE));
                                hmap.remove("time");
                                if(loginType==3){
                                    return Authorize(hmap,account,r,OauthMsg,callback);
                                }
                            }
                            return true;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if (msg.contains("用户名或密码错误")) {
                        r.put(Constants.CODE_REMARK,msg);
                        Map<String, String> info = new HashMap<String, String>();
                        info.put(Constants.STATUS, "0");
                        info.put(Constants.REMARK,msg);
                        callback.updateAccountByID(account.getString(Constants.ACCOUNT_ID), info);
                    } else {
                        r.put(Constants.CODE_REMARK, "新浪微博手机触屏版登陆登陆失败,原因:" + msg);
                    }
                    return false;
                }
                r.put(Constants.CODE_REMARK, "新浪微博手机触屏版登陆登陆失败,原因未知");
            }
        }catch (Exception e){
            error("新浪微博手机触屏版登陆异常", e);
            r.put(Constants.CODE_REMARK, "新浪微博手机触屏版登陆异常" + e.toString());
        }
        return false;
    }

    /**
     * 强国论坛登陆
     * @param hmap
     * @param account
     * @param r
     * @return
     */
    public boolean BBSPeople(Map<String, String> hmap, JSONObject account,JSONObject r, TaskClientCallback callback){
        try{
            Map<String,String> map = new HashMap<String,String>();
            map.put("rememberMe","1");
            map.put("retUrl","refer");
            map.put("userName",account.getString(Constants.ACCOUNT_USERNAME).trim());
            map.put("password",account.getString(Constants.ACCOUNT_PASSWORD).trim());
            hmap.put("Referer","http://bbs1.people.com.cn/");
            HttpResponse res = this.getHttpResponse(map, hmap, "http://passport.people.com.cn/_login.do");
            int code = Util.getCode(res);
            if(code==302 && hmap.get("guzz_session_id")!=null && hmap.get("guzz_session_user")!=null){
                hmap.remove("_p_c_f_f");
                hmap.remove("Referer");
                return true;
            }
            r.put(Constants.CODE_REMARK,"登陆失败，Code="+code);
        }catch (Exception e){
            r.put(Constants.CODE_REMARK, "登陆异常");
            error("异常", e);
        }
        return false;
    }
}