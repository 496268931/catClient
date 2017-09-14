package com.wiseweb.weibo;

import com.wiseweb.cat.base.ConfigerFactory;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.client.HttpClientUtil;
import com.wiseweb.client.util.HttpClien;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/1/18.
 */
public class WeiboWapLogin extends HttpClien {
    public static WeiboWapLogin weiboWapLogin;

    public static void main(String[] ages) {
        WeiboWapLogin weiboWapLogin = getWeiboWapLogin();
        weiboWapLogin.getUserLogin();
    }

    public static WeiboWapLogin getWeiboWapLogin() {
        weiboWapLogin = weiboWapLogin == null ? new WeiboWapLogin() : weiboWapLogin;
        return weiboWapLogin;
    }

    public void getUserLogin() {
        try {
//                Mongo m = new Mongo(new MongoURI("mongodb://hx:1qaz2wsx@114.215.170.176/hx"));
//                DBCollection dbc = m.getDB("hx").getCollection("accounts");
//                DBObject query = new BasicDBObject();
//                query.put("platformName", "新浪");
//                query.put("area","淮安");
//                query.put("task","220");
//                DBObject data = dbc.findOne(query, new BasicDBObject("username", 1).append("password",1));
//                if(data!= null){
//                    JSONObject account = new JSONObject(data.toString());
//                    info("登陆帐号："+account);
//                    WeiboWapLogin(account);
//                }
//                m.close();
            HttpClientUtil client = new HttpClientUtil();
            String str = client.httpGetRequest
                    ("http://114.215.170.176:3000/getLoginAccount?area=淮安");//徐州
            //System.out.println(str);
            JSONObject account = new JSONObject(str);
            if((account.getInt("result" )== 1)&&(account.getJSONObject("data")!=null)){
                WeiboWapLogin(account.getJSONObject("data"));
            }
        } catch (Exception e) {
            error("getUserLogin()异常", e);
        }
    }

    /**
     * 微博登录
     * @param user
     * @return
     */
    public JSONObject WeiboWapLogin(JSONObject user) {
        JSONObject result = new JSONObject();
        try {
            result.put(Constants.STATUS, "2");
//            result.put("_id",user.getJSONObject("_id").getString("$oid"));
            result.put("_id",user.getString("_id"));
            String username = user.getString(Constants.ACCOUNT_USERNAME).trim();
            String password = user.getString(Constants.ACCOUNT_PASSWORD).trim();
            JSONObject Cookies = new JSONObject();
            JSONObject cookie = new login(username,password).login();
            if (cookie.has("err")) {
                result.put(Constants.CODE_REMARK, "帐号异常");
                result.put(Constants.STATUS, "0");
            } else {
                Cookies.put("weibo_com", cookie);
                result.put(Constants.CODE_REMARK, "帐号登陆成功");
                result.put(Constants.STATUS, "1");
                long time = Long.parseLong(new Date().getTime() + "") + (90 * 24 * 60 * 60 * 1000L);
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
                System.out.println("帐号过期时间：" + date);
                Cookies.put("time", time);
                Cookies.put("date", date);
                result.put(Constants.TASK, Constants.WEIBO_SINA_LOGIN);
                result.put(Constants.ACCOUNT_AUTHINFO, Cookies.toString());
            }
        } catch (Exception e) {
            error("新浪微博手机触屏版登陆异常", e);
            result.put(Constants.CODE_REMARK, "新浪微博手机触屏版登陆异常" + e.toString());
        }
        JSONObject form = new JSONObject(result.toString());
        if (result.has(Constants.ACCOUNT_AUTHINFO)) {
            result.remove(Constants.ACCOUNT_AUTHINFO);
        }
        System.out.println("登陆结果：" + result.toString());
        updateAccountStatus(form);
        return result;
    }

    private void updateAccountStatus(JSONObject form) {
        try {
            HttpResponse response = Request(form, null, ConfigerFactory.getConfiger().getApiurl() + "/updateAccount");
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                System.out.println("帐号状态上报返回结果：" + getEntity(response));
            }
            start();
        } catch (Exception e) {
            error("异常", e);
        }
    }


    public long getCookieOverdueTime(JSONObject Cookie) {
        try {
            String times = Cookie.getString("SSOLoginState") + "000";
            long time = Long.parseLong(times) + (90 * 24 * 60 * 60 * 1000L);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void start() {
        //String vpnName = "xuzhou";
        //String vpnUsername = "051655281172";
        //String vpnPassword = "128167";
        String vpnName = "huaian";
        String vpnUsername = "051710702041";
        String vpnPassword = "320821";
        try {
            System.out.println("**********************切换ip**********************");
            DisConnectVpn(vpnName);
            while (!ConnectVpn(vpnName, vpnUsername, vpnPassword)) {
            }
        } catch (Exception e) {
            System.out.println("切换IP异常");
        }
    }

    public static String executeCmd(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("cmd /c" + cmd);
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb.toString());
            return sb.toString();
        } catch (Exception e) {
            System.out.println("执行命令行网络拨号异常");
        }
        return null;
    }

    public static boolean ConnectVpn(String VpnName, String username, String password) {
        try {
            String cmd = "rasdial " + VpnName + " " + username + " " + password;
            String res = executeCmd(cmd);
            return res.contains("命令已完成");
        } catch (Exception e) {
            System.out.println("网络拨号异常");
        }
        return false;
    }

    public static boolean DisConnectVpn(String VpnName) {
        try {
            String cmd = "rasdial " + VpnName + " /disconnect";
            String res = executeCmd(cmd);
            return res.contains("命令已完成");
        } catch (Exception e) {
            System.out.println("网络断开异常");
        }
        return false;
    }
}
