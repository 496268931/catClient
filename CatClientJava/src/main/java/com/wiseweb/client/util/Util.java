package com.wiseweb.client.util;

import com.wiseweb.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created by Administrator on 2016/1/15.
 */
public class Util extends Log4JLogger{

    public JSONObject getHeader(){
        JSONObject headers = new JSONObject();
        headers.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
        return headers;
    }

    public long getTime(){
        return System.currentTimeMillis();
    }

    public static String getMAC(){
        String MAC = null;
        try{
            //获取网卡，获取地址
            byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            StringBuffer sb = new StringBuffer("");
            for(int i=0; i<mac.length; i++) {
                if(i!=0) {
                    sb.append("-");
                }
                //字节转换为整数
                int temp = mac[i]&0xff;
                String str = Integer.toHexString(temp);
                if(str.length()==1) {
                    sb.append("0"+str);
                }else {
                    sb.append(str);
                }
            }
            MAC = sb.toString().toUpperCase().trim();
        }catch (Exception e){
           e.printStackTrace();
        }
        return MAC;
    }
}
