package com.wiseweb.cat.base;

import com.wiseweb.json.JSONObject;

/**
 * 2014-5-14
 *
 * @author 贾承斌
 */
public class JMessageFactory {


    /**
     * 获取返回方法
     *
     * @param resMessage
     * @return
     */
    public static JMessage getResponseMessage(JMessage resMessage) {
        JMessage res = new JMessage(Constants.TYPE_RESPONSE, resMessage.getNum(), resMessage.getVersion(), null);

        return res;
    }

    /**
     *
     * @param resMessage
     * @param result
     * @return
     */
    public static JMessage getAuthResponseMessage(JMessage resMessage, int result) {
        JMessage res = new JMessage(Constants.TYPE_AUTH_RESPONSE, resMessage.getNum(), resMessage.getVersion(), String.valueOf(result));
        return res;
    }

    /**
     * 获取请求方法
     *
     * @param content
     * @return
     */
    public static JMessage getRequsetMessageByContent(String content) {
        JMessage req = new JMessage(Constants.TYPE_REQUEST, System.currentTimeMillis(), Constants.DEFAULT_VERSION, content);
        return req;
    }

    public static JMessage getNewTaskMessageByJson(String json) {
        JMessage m = null;
        m = new JMessage(Constants.TYPE_TASK_NEW, System.currentTimeMillis(), Constants.DEFAULT_VERSION, json);
        return m;
    }

    /**
     * 生成错误信息
     *
     * @param code
     * @return
     */
    public static JMessage getERRMessage(long num, int code) {
        JMessage m = null;
        m = new JMessage(Constants.TYPE_ERR, num, Constants.DEFAULT_VERSION, String.valueOf(code));
        return m;
    }

    /**
     * 认证信息
     *
     * @return
     */
    public static Object getAuthMessage(JSONObject content) {
       // JSONObject content = new JSONObject();
        return new JMessage(Constants.TYPE_AUTH, System.currentTimeMillis(), Constants.DEFAULT_VERSION, content.toString());
    }

    /**
     * 心跳信息
     *
     * @return
     */
    public static Object getBeatsMessage() {
        return new JMessage(Constants.TYPE_BEATS, System.currentTimeMillis(), Constants.DEFAULT_VERSION, null);
    }

    public static JMessage getBroadCastRequsetMessage(String types) {
         return new JMessage(Constants.ACTION_BROADCAST_REQUEST, System.currentTimeMillis(), Constants.DEFAULT_VERSION,types);
    }

}
