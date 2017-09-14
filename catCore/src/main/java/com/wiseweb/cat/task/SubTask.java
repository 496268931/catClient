package com.wiseweb.cat.task;

import com.wiseweb.cat.base.Constants;
import com.wiseweb.json.JSONException;
import com.wiseweb.json.JSONObject;


/**
 * 2014-6-11
 *
 * @author 贾承斌
 * 驱蚊器为此
 */
public class SubTask {
    private long task_id;//任务ID
    private long sub_id;//子任务ID
    private String ip;//子任务ID
    private String time;
   // private int action;
    /**
     * 状态:0 等待执行 。1.执行成功 2.执行失败
     */
    private int status = 0;//
    /**
     * 任务类型：具体的执行类型，每个平台的每种任务状态都被不同编码
     */
    private int task;//:
    /**
     * 任务账号
     */
    private JSONObject account;//账号
    /**
     * 语料
     */
    private String doc;//语料
    /**
     * 执行任务的便签， URL 地址  ID等
     */
    private JSONObject tag;//标签 执行任务需要的标签
    /**
     * 执行结果参数
     */
    private String result_url;
    
    private int code;
    
    private String codeRemark;
    

    public SubTask(long task_id, long sub_id) {
        this.task_id = task_id;
        this.sub_id = sub_id;
    }
    /**
     * 转换为json 字符串
     * @return 
     */
    public JSONObject toJSONObject (){
        JSONObject json;
        try {
             json = new JSONObject();
           //  json.put(Constants.ACTION, this.action);
             json.put(Constants.TASK_ID, this.task_id);
             json.put(Constants.TASK, this.task);
             json.put(Constants.TASK_TAG, this.tag);
             json.put(Constants.DOC, this.doc);
             json.put(Constants.SUB_ID, this.sub_id);
             json.put(Constants.ACCOUNT, this.account);
        } catch (JSONException e) {
            return null;
        }
        
        return json;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

   
    

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public long getSub_id() {
        return sub_id;
    }

    public void setSub_id(long sub_id) {
        this.sub_id = sub_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }
/**
 * 
 * @param status 
 */
    public void setStatus(int status) {
        this.status = status;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public JSONObject getAccount() {
        return account;
    }

    public void setAccount(JSONObject account) {
        this.account = account;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public JSONObject getTag() {
        return tag;
    }

    public void setTag(JSONObject tag) {
        this.tag = tag;
    }

    public String getResult() {
        return result_url;
    }

    public void setResult(String result) {
        this.result_url = result;
    }

    public String getCodeRemark() {
        return codeRemark;
    }

    public void setCodeRemark(String codeRemark) {
        this.codeRemark = codeRemark;
    }

    
    
    
}
