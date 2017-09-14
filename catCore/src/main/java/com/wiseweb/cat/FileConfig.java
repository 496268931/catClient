package com.wiseweb.cat;

import com.wiseweb.cat.base.DefaultConfiger;
import com.wiseweb.cat.base.Constants;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by apple on 15/11/30.
 */
public class FileConfig extends DefaultConfiger{
    private String path = "";
    public void Config(){
        try{
//            //加载.properties文件
////            this.path = DefaultConfiger.class.getResource("/").getPath();
//            String log4j = this.path + "config/log4j.properties";
//            PropertyConfigurator.configure(log4j);
//            LoggerFactory.initLogger((Logger) new Log4JLogger());
            //读取配置文件
            String config = this.path + "config/config.properties";
            FileInputStream inputFile = new FileInputStream(config);
            Properties propertie = new Properties();
            propertie.load(inputFile);
            inputFile.close();
            Iterator i = (Iterator) propertie.keys();
            while(i.hasNext()){
                try{
                    String key = ((String)i.next()).trim();
                    String value = propertie.getProperty(key).trim();
                    System.out.println(key+"="+value);
                    switch(key){
                        case Constants.CONFIG_APIURL:
                            setApiurl(value);
                            break;
                        case Constants.CONFIG_BROADCAST:
                            setBroadcastTask(value);
                            break;
                        case Constants.CONFIG_CLIENT_ID:
                            setClientId(value);
                            break;
                        case Constants.CONFIG_HOST:
                            setScheduleHost(value);
                            break;
                        case Constants.CONFIG_KEY:
                            setKey(value);
                            break;
                        case Constants.CONFIG_PROT:
                            setSchedulePort(Integer.parseInt(value));
                            break;
                        case Constants.CONFIG_SCHEDULE:
                            setScheduleTask(value);
                            break;
                        case Constants.CONFIG_VERSION:
                            setVersion(value);
                            break;
                        case Constants.CONFIG_NEWS_IFENG_SINA_OAUTH_MSG:
                            this.oauthUrl.put(key,value);
                            break;
                        case Constants.CONFIG_SINA_WEIBO_APP_OAUTH_MSG:
                            this.oauthUrl.put(key,value);
                            break;
                        default:
                            addTaskSleepTime(key,Integer.parseInt(value));
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            propertie.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
