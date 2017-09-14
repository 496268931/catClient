


package com.wiseweb.cat.base;

/**
 * 2014-5-12
 * @author 贾承斌
 * 消息类
 */
public class JMessage implements java.io.Serializable{
        
    private int type;//类型
    private long num;//流水
    private int version;//信息版本T
    private String content;//内容

    public JMessage(int type, long num, int version, String content) {
        this.type = type;
        this.num = num;
        this.version = version;
        this.content = content;
    }
    
    

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    

}
