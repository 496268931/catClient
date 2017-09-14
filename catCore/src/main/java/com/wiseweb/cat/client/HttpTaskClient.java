package com.wiseweb.cat.client;

import com.wiseweb.cat.base.BaseClass;
import com.wiseweb.cat.base.Configer;
import com.wiseweb.cat.base.ConfigerFactory;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.task.TaskController;
import com.wiseweb.cat.util.Base64;
import com.wiseweb.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 贾承斌 on 15/11/7.
 */
public class HttpTaskClient extends BaseClass {

    private String url;
    private TaskController taskController;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(5);
    List<String> mList = new ArrayList<String>();
    private long default_check_broadCastTask_time = 10;

    private static final String HTTP_CHECK_BROADCAST = "/checkbc";
    private static final String HTTP_REPORT_BROADCAST = "/reportbc";
    private static final String HTTP_GET_ACCOUNT = "/getAccount";
    private static final String HTTP_UPDATE_ACCOUNT = "/updateAccount";
    private static final String HTTP_UPDATE_TASK = "/updateTask";
    private static final String HTTP_GET_DOC = "/getDoc";
    private static final String HTTP_REG = "/reg";

    /**
     * 创建http api 对象
     *
     * @
     */
    public HttpTaskClient(TaskController taskController) {
        this.taskController = taskController;
        this.url = ConfigerFactory.getConfiger().getApiurl();
    }

    public void updateTask(final Map<String, String> params) {
        info("更新任务信息");
        params.put(Constants.MESSAGE_FIELD_CLIENT_ID, ConfigerFactory.getConfiger().getClientId());

        String re = doPOST(HTTP_UPDATE_TASK, params);
        if (re != null) {
            try {
                JSONObject broadCast = new JSONObject(re);
                int result = broadCast.getInt(Constants.API_PARAMS_RESULT);
                if (result == Constants.API_PARAMS_RESULT_SUCCESS) {
                    info("标识任务失败上报成功");
                } else if (result == Constants.API_PARAMS_RESULT_FAILED) {
                    info("标识任务失败上报失败。");
                }
            } catch (Exception e) {
                error("异常", e);
            }
        }

    }

    public void asyncUpdateTask(final Map<String, String> params) {
        scheduledThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                updateTask(params);
            }
        });
    }

    public void syncUpdateTask(final Map<String, String> params) {
        updateTask(params);
    }

    public void asyncCheckBroadCastTask() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                checkBroadCastTask();
            }
        }, default_check_broadCastTask_time, default_check_broadCastTask_time, TimeUnit.SECONDS);
    }

    public void syncCheckBroadCastTask() {
        checkBroadCastTask();
    }

    private void checkBroadCastTask() {

        try {
            if (mList.size() > 0) {
                String message = mList.get(0);
                mList.remove(0);
                info("==========>定时上报任务：" + message);
                BroadCastTaskReport(message);
            }
            info("==========>" + default_check_broadCastTask_time + "秒后检查。");
            checkBroadCast();
        } catch (Exception e) {
            debug("这个异常可厉害了，可以导致定时去取任务\n" + e);
        }
    }


    public void BroadCastTaskReport(final String message) {
        info("上报信息：" + message);
        try {
            String re = reportBroadCast(ConfigerFactory.getConfiger().getClientId(), message);
            if (re != null) {
                JSONObject broadCast = new JSONObject(re);
                int result = broadCast.getInt(Constants.API_PARAMS_RESULT);
                if (result == Constants.API_PARAMS_RESULT_SUCCESS) {
                    info("上报成功");
                } else if (result == Constants.API_PARAMS_RESULT_FAILED) {
                    info("上报失败。");
                }
            } else {
                info("上报失败,请求异常!\n" + message);
                mList.add(message);
            }
        } catch (Exception e) {
            error("异常", e);
        }

    }

    public void checkBroadCast() {

        String broadcastTask = this.taskController.getBroadcastType();
        String runningTask = this.taskController.getRunningBroadcastTaskID();
        try {
            if (broadcastTask != null) {
                String re = checkBroadCast("client_id=" + ConfigerFactory.getConfiger().getClientId() + "&task=" + broadcastTask + "&ids=" + runningTask);
                if (re != null) {
                    JSONObject broadCast = new JSONObject(re);
                    int result = broadCast.getInt(Constants.API_PARAMS_RESULT);
                    if (result == Constants.API_PARAMS_RESULT_SUCCESS) {
                        if (broadCast.isNull(Constants.API_PARAMS_CONTENT)) {
                            info("无任务执行");
                        } else {
                            JSONObject c = broadCast.getJSONObject(Constants.API_PARAMS_CONTENT);
                            taskController.addRunningBroadcastTask(c);
                        }

                    } else {
                        info("查询失败");
                    }
                }

            }
        } catch (Exception e) {
            error("异常", e);
        }
    }

    /**
     * 查询广播任务
     *
     * @param params
     * @return
     */
    public String checkBroadCast(String params) {
        String re = null;
        re = this.doGET(HTTP_CHECK_BROADCAST, params);
        return re;

    }

    public String doGET(String route, String paras) {
        String re = null;
        String exeURL = this.url + route + "?" + paras;//执行
        try {
            HttpClient httpclient = new DefaultHttpClient();
            //请求超时
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            //读取超时
            //httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
            HttpGet httpGet = new HttpGet(exeURL);
            HttpResponse response;
            debug("GET访问" + exeURL);
            response = httpclient.execute(httpGet);

            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                re = EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                info("访问失败:" + code);
            }
//            ((AbstractHttpClient) httpclient).close();
        } catch (IOException ex) {
            error("访问出现异常:", ex);
        }
        info("訪問結束");
        return re;
    }

    public String doPOST(String route, Map<String, String> paras) {
        String re = null;
        try {
            String exeURL = this.url + route;//执行
            HttpClient httpclient = new DefaultHttpClient();
            //请求超时
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            //读取超时
            //httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
            HttpPost httpPost = this.postForm(exeURL, paras);
            HttpClientContext cou;

            HttpResponse response;
            debug("POST访问" + exeURL);
            response = httpclient.execute(httpPost);

            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                re = EntityUtils.toString(response.getEntity(), "utf-8");
            } else {
                info("访问失败:" + code);
                return null;
            }
        } catch (IOException ex) {
            error("访问出现异常:", ex);

        }
        return re;
    }

    /**
     * 設置post參數
     *
     * @param url
     * @param params
     * @return
     */
    private HttpPost postForm(String url, Map<String, String> params) {

        HttpPost httpost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (!params.isEmpty()) {

            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        try {
            info("set utf-8 form entity to httppost");
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            error("不支持编码", e);
            return null;
        }

        return httpost;
    }

    public String reportBroadCast(String client_id, String params) {
        String re = null;
        try {
            Map<String, String> map = new HashMap();
            map.put("report", params);
            map.put("client_id", client_id);
            re = this.doPOST(HTTP_REPORT_BROADCAST, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }


    public JSONObject getOneAccount(String task_id) {
        JSONObject a = null;
        try {
            if (task_id != null) {
                String re = doGET(HTTP_GET_ACCOUNT, "client_id=" + ConfigerFactory.getConfiger().getClientId() + "&task_id=" + task_id);
                if (re == null) return null;
                JSONObject r = new JSONObject(re);
                Base64 base64 = new Base64();
                if (r.has(Constants.API_PARAMS_CONTENT)) {
                    String accountStr = new String(base64.decode(r.getString(Constants.API_PARAMS_CONTENT)),"utf-8");
                    if (accountStr != null) {
                        try {
                            a = new JSONObject(accountStr);
                        } catch (Exception e) {
                            error("异常", e);
                            info("帐号解密后：" + accountStr);
                            return null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            error("异常", e);
        }
        return a;
    }

    public void asyncUpdateAccount(final String _id, final Map<String, String> params) {
        scheduledThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                updateAccount(_id, params);
            }
        });
    }

    public void syncUpdateAccount(final String _id, final Map<String, String> params) {
        updateAccount(_id, params);
    }

    public void updateAccount(final String _id, final Map<String, String> params) {
        info("更新账号");
        params.put(Constants.ACCOUNT_ID, _id);
        String re = doPOST(HTTP_UPDATE_ACCOUNT, params);
        if (re != null) {
            try {
                JSONObject broadCast = new JSONObject(re);
                int result = broadCast.getInt(Constants.API_PARAMS_RESULT);
                if (result == Constants.API_PARAMS_RESULT_SUCCESS) {
                    info("上报成功");
                } else if (result == Constants.API_PARAMS_RESULT_FAILED) {
                    info("上报失败。");
                }
            } catch (Exception e) {
                error("异常", e);
            }
        }

    }

    public String getDoc(String task_id) {
        String doc = null;
        if (task_id != null) {
            String re = doGET(this.HTTP_GET_DOC,"client_id=" + ConfigerFactory.getConfiger().getClientId() + "&task_id=" + task_id);
            if (re != null) {
                JSONObject reObject = new JSONObject(re);
                if (reObject.has(Constants.API_PARAMS_RESULT) && reObject.getInt(Constants.API_PARAMS_RESULT) == Constants.API_PARAMS_RESULT_SUCCESS) {
                    Base64 base64 = new Base64();
                    doc = reObject.has(Constants.API_PARAMS_CONTENT) ? new String(base64.decode(reObject.getString(Constants.API_PARAMS_CONTENT))) : null;
                }
            }

        }
        return doc;
    }

    public boolean reg() {
        Configer configer =  ConfigerFactory.getConfiger();
        String re = doGET(HTTP_REG,"client_id=" + configer.getClientId()
                +"&version="+configer.getVersion()
                +"&area="+configer.getArea())
                +"&mac="+configer.getMAC();
        if (re != null) {
            JSONObject reObject = new JSONObject(re);
            if (reObject.has(Constants.API_PARAMS_RESULT) && reObject.getInt(Constants.API_PARAMS_RESULT) == Constants.API_PARAMS_RESULT_SUCCESS) {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }


    public void start() {
        asyncCheckBroadCastTask();
    }

    public void stop() {
        scheduledThreadPoolExecutor.shutdown();
    }
}
