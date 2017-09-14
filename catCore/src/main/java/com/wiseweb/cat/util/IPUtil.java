package com.wiseweb.cat.util;

import java.io.IOException;

import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * 2014-7-8
 *
 * @author 贾承斌
 */
public class IPUtil {

    private static final String URL = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";

    /**
     * 获取ip地址所在地区
     *
     * @return
     */
    public static JSONObject getIPArea(String ip) {
        JSONObject area = null;
        try {
            String url;
            if (ip==null||ip.equals("127.0.0.1") || ip.equals("localhost")) {
                url = URL;
            } else {
                url = URL + ip;
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse respones;
            respones = httpclient.execute(httpGet);

            //检验状态码，如果成功接收数据
            int code = respones.getStatusLine().getStatusCode();
            if (code == 200) {
                String re = EntityUtils.toString(respones.getEntity());
                area = new JSONObject(re);


                //area=json.getString("country")+","+json.getString("province")+","+json.getString("city");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return area;
    }
/**
 * 通过ip但会数据 地区字符串
 * @param ip
 * @return 
 */
    public static String getAreaByIP(String ip) {
        try {
            JSONObject area = getIPArea(ip);
            if(area==null){
                return null;
            }
//            return area.getString("country") + "," + area.getString("province") + "," + area.getString("city");
            return area.getString("city");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
