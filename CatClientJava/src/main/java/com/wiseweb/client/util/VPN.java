package com.wiseweb.client.util;

import com.wiseweb.cat.base.BaseClass;
import com.wiseweb.cat.base.ClientTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/12/2.
 */
public class VPN extends BaseClass implements ClientTools {
    static String vpnName;
    static String vpnUsername;
    static String vpnPassword;
    static VPN vpn;
    @Override
    public void run() {
        try{
           start();
        }catch (Exception e){
            error("切换ip异常",e);
        }
    }

    public static VPN getVPN(){
        vpn = vpn==null?new VPN():vpn;
        return vpn;
    }

    public void start() {
        try{
            info("**********************切换ip**********************");
            if(Config.isVpn){
                DisConnectVpn(vpnName);
                while(!ConnectVpn(vpnName,vpnUsername,vpnPassword)){}
            }
        }catch (Exception e){
            error("切换IP异常",e);
        }
    }

    public String executeCmd(String cmd){
        try {
            Process p = Runtime.getRuntime().exec("cmd /c"+cmd);
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(),"gbk"));
            String line;
            while((line = br.readLine())!=null){
                sb.append(line);
            }
            info(sb.toString());
            return sb.toString();
        } catch (Exception e) {
           error("执行命令行网络拨号异常",e);
        }
        return null;
    }

    public boolean ConnectVpn(String VpnName,String username,String password){
        try {
            String cmd = "rasdial "+VpnName+" "+username+" " + password;
            String res = executeCmd(cmd);
            return res.contains("命令已完成");
        } catch (Exception e) {
            error("网络拨号异常", e);
        }
        return false;
    }

    public boolean DisConnectVpn(String VpnName){
        try {
            String cmd = "rasdial "+VpnName+" /disconnect";
            String res = executeCmd(cmd);
            return res.contains("命令已完成");
        } catch (Exception e) {
            error("网络断开异常", e);
        }
        return false;
    }
}
