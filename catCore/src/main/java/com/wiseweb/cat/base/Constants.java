package com.wiseweb.cat.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义任务常量 主要是任务代码 2014-6-16
 *
 * @author 贾承斌
 */
public class Constants {
    //-----------------------常量----------------------
    public static final int DEFAULT_DELAY_SCHEDULE_SECOND = 0;//默认启动延迟秒
    public static final int DEFAULT_SUPPLEMENT_DELAY_SECOND = 30;//补全延迟描述
    public static final int DEFAULT_SCHEDULE_SECOND = 30;//补全延迟描述
    public static final int DEFAULT_TASK_SAFE_TIME = 30*1000;//任务执行安全值 30秒
    //-----------------------软件软件参数---------------------
    public static final String CATSERVER_VERSION = "V1.0.0"; //认真信息

    //---------------------任务对象采集名称、数据库字段----------------------

    //----------------------任务状态----------------------------

    /**
     * 任务状态：等待执行
     */
    public static final int TASK_STATUS_WAIT = 0;//等待
    /**
     * 任务状态：已经完成
     */
    public static final int TASK_STATUS_COMPLETE = 1;//完成、成功
    /**
     * 任务状态：开始，进行中
     */
    public static final int TASK_STATUS_START = 2;//已经开始
    /**
     * 任务状态：暂停
     */
    public static final int TASK_STATUS_PAUSE = 3;//暂停
    /**
     * 任务状态：失败
     */
    public static final int TASK_STATUS_FAILED = 4;//失败

    /**
     * 任务状态：取消
     */
    public static final int TASK_STATUS_CANCEL = 5;//取消
    /**
     * 任务超时 超出规定的执行区间没有完成
     */
    public static final int TASK_STATUS_OUTTIME = 6;//超时

    /**
     * 任务状态：错误：参数错误引起的任务无法执行
     */
    public static final int TASK_STATUS_ERROR = 100;//

    //-------------------------task 类型-----------------------------
    //-----------------------微博-----------------------------
    /**
     * 腾讯微博发送
     */
    public static final String WEIBO_TENCENT_POST = "101";
    /**
     * 腾讯微博评论
     */
    public static final String WEIBO_TENCENT_COMMENTS = "102";
    /**
     * 腾讯转发
     */
    public static final String WEIBO_TENCENT_REPOST = "103";
    /**
     * 腾讯微博赞
     */
    public static final String WEIBO_TENCENT_SUPPORT = "104";
    /**
     * 腾讯评论加转发
     */
    public static final String WEIBO_TENCENT_COMMENT_REPOST = "105";



    /**
     * 新浪微博直发
     */
    public static final String WEIBO_SINA_POST = "201";
    /**
     * 新浪微博评论
     */
    public static final String WEIBO_SINA_COMMENT = "202";
    /**
     * 特能新浪转发
     */
    public static final String WEIBO_SINA_REPOST = "203";
    /**
     * 新浪微博赞
     */
    public static final String WEIBO_SINA_SUPPORT = "204";
    /**
     *微博转评
     */
    public static final String WEIBO_SINA_COMMENT_AND_REPOST = "205";
    /**
     *加粉
     */
    public static final String WEIBO_SINA_ADD_ATTENTION_FOR_USER = "210";
    /**
     *评论赞
     */
    public static final String WEIBO_SINA_SUPPORT_FOT_COMMENT = "211";
    /**
     *电影点评
     */
    public static final String WEIBO_SINA_MOVIE_SCORE = "212";
    /**
     *话题加粉
     */
    public static final String WEIBO_SINA_ADD_ATTENTION_FOR_TOPIC = "213";
    /**
     * 话题直发
     */
    public static final String WEIBO_SINA_TOPIC_POST = "215";
    /**
     * 电影点赞
     */
    public static final String WEIBO_SINA_MOVIE_SUPPORT = "216";
    /**
     * 电影想看
     */
    public static final String WEIBO_SINA_MOVIE_SEE = "217";
    /**
     * 电影关注
     */
    public static final String WEIBO_SINA_MOVIE_FOLLOW = "218";

    /**
     *微博投票
     */
    public static final String WEIBO_SINA_VOTE = "219";
    /**
     * 微博登陆
     */
    public static final String WEIBO_SINA_LOGIN = "220";
    /**
     * 视频点赞
     */
    public static final String WEIBO_SINA_VOIDE_SUPPORT = "221";
    /**
     * 音乐点赞
     */
    public static final String WEIBO_SINA_AUDIO_SUPPORT = "222";

    //-------------------------格瓦拉-------------------------
    /**评论支持*/
    public static final String GEWARA_COMMENT_SUPPORT = "1201";

    /**用户关注*/
    public static final String GEWARA_USER_FOLLOW = "1202";

    /**电影期待*/
    public static final String GEWARA_MOVIE_EXPECT = "1203";

    /**评论回复*/
    public static final String GEWARA_COMMENT_REPLY = "1204";

    /**电影评论*/
    public static final String GEWARA_MOVIE_COMMENT = "1205";


    /**
     *
     */

    //-------------------------新闻---------------------------
    /**网易新闻Web评论*/
    public static final String NEW_163_COMMENTS = "303";
    /**新浪新闻Web评论*/
    public static final String NEW_SINA_COMMENTS = "206";
    /**搜狐新闻Web评论*/
    public static final String NEW_SOHU_COMMENTS = "403";
    /**凤凰新闻Web评论*/
    public static final String NEW_IFENG_COMMENTS = "501";
    /**网易新闻APP评论*/
    public static final String NEW_163_APP_COMMENTS = "305";
    /**新浪新闻APP评论*/
    public static final String NEW_SINA_APP_COMMENTS = "208";
    /**搜狐新闻APP评论*/
    public static final String NEW_SOHU_APP_COMMENTS = "405";
    /**凤凰新闻APP评论*/
    public static final String NEW_IFENG_APP_COMMENTS = "503";
    /**澎湃新闻APP评论*/
    public static final String NEW_PENGPAI_APP_COMMENTS = "802";
    /**头条新闻APP评论*/
    public static final String NEW_TOUTIAO_APP_COMMENTS = "1002";
    
    /*---------------------------新浪支持----------------------------*/
    /**网易新闻Web评论支持*/
    public static final String NEW_163_SUPPORT = "304";
    /**新浪新闻Web评论支持*/
    public static final String NEW_SINA_SUPPORT = "207";
    /**搜狐新闻Web评论支持*/
    public static final String NEW_SOHU_SUPPORT = "404";
    /**凤凰新闻Web评论支持*/
    public static final String NEW_IFENG_SUPPORT = "502";
    /**网易新闻APP评论支持*/
    public static final String NEW_163_APP_SUPPORT = "306";
    /**新浪新闻APP评论支持*/
    public static final String NEW_SINA_APP_SUPPORT = "209";
    /**搜狐新闻APP评论支持*/
    public static final String NEW_SOHU_APP_SUPPORT = "406";
    /**凤凰新闻APP评论支持*/
    public static final String NEW_IFENG_APP_SUPPORT = "504";
    /**澎湃新闻APP评论支持*/
    public static final String NEW_PENGPAI_APP_SUPPORT = "801";
    /**头条新闻APP评论支持*/
    public static final String NEW_TOUTIAO_APP_SUPPORT = "1001";



    //---------------论坛---------------
    //网易
    public static final String BBS_163_POST = "301";
    public static final String BBS_163_COMMENT = "302";
    //搜狐
    public static final String BBS_SOHU_POST = "401";
    public static final String BBS_SOHU_COMMENT = "402";
    //新华网 强国论坛
    public static final String BBS_PEOPLE_POST = "701";
    public static final String BBS_PEOPLE_COMMENT = "702";
    //新华论坛
    public static final String BBS_XINHUA_POST = "601";
    public static final String BBS_XINHUA_SUPPORT = "602";


    //-------------------通用任务-----------------

    public static final String GENERAL_ROBOT = "G01";
    public static final String GENERAL_HTTP = "G02";

    //------------------语料类型-----------------
    public static final int DOC_TYPE_CUSTOM = 1; //自定义
    public static final int DOC_TYPE_SYSTEM = 2; //系统
    public static final int DOC_TYPE_KSY = 3; //关键字
    public static final int DOC_TYPE_SET = 4; //预料集

    //----------------账号类型----------------------
    public static final int ACCOUNT_TYPE_CUSTOM = 1; //自有
    public static final int ACCOUNT_TYPE_SYSTEM = 2; //系统

    //平台编号
    public static final int ACCOUNT_PLATFORM_ID_SINA = 1; //新浪
    public static final int ACCOUNT_PLATFORM_ID_TENCENT = 2; //腾讯
    public static final int ACCOUNT_PLATFORM_ID_163 = 3; //网易
    public static final int ACCOUNT_PLATFORM_ID_SOHU = 4; //搜狐
    public static final int ACCOUNT_PLATFORM_ID_IFENG = 5; //凤凰
    public static final int ACCOUNT_PLATFORM_ID_XINHUA = 6; //新华
    public static final int ACCOUNT_PLATFORM_ID_PEOPLE = 7; //倾国

    //平台名称
    public static final String ACCOUNT_PLATFORM_NAME_SINA = "新浪"; //新浪
    public static final String ACCOUNT_PLATFORM_NAME_TENCENT = "腾讯"; //腾讯
    public static final String ACCOUNT_PLATFORM_NAME_163 = "网易"; //网易
    public static final String ACCOUNT_PLATFORM_NAME_SOHU = "搜狐"; //搜狐
    public static final String ACCOUNT_PLATFORM_NAME_IFENG = "凤凰"; //凤凰
    public static final String ACCOUNT_PLATFORM_NAME_XINHUA = "新华"; //新华
    public static final String ACCOUNT_PLATFORM_NAME_PEOPLE = "强国"; //倾国

    //----------------mongo collections 名称-----------------------------
    public static final String DB_COLLECTIONS_CLIENTS = "clients"; //执行端
    public static final String DB_COLLECTIONS_AREAS = "areas"; //地区
    public static final String DB_COLLECTIONS_ACCOUNTS = "accounts"; //账号
    public static final String DB_COLLECTIONS_TASK = "task"; //任务
    public static final String DB_COLLECTIONS_SUBTASK = "subTask"; //子任务

    //--------------------message -------------
    public static final String MESSAGE_FIELD_VERSON = "version"; //版本
    public static final String MESSAGE_FIELD_AUTH = "auth"; //认真信息
    public static final String MESSAGE_FIELD_AREA = "area"; //地区
    public static final String MESSAGE_FIELD_MAC = "MAC"; //mac地址
    public static final String MESSAGE_DEFAULT_VERSION = "unknow";
    public static final String MESSAGE_FIELD_BROADCAST_TYPE = "broadcast_type";
    public static final String MESSAGE_DEFAULT_SCHEDULE_TYPE = "schedule_type";
    public static final String MESSAGE_FIELD_CLIENT_ID = "client_id";//执行端 id 20141204

    //-------------------client 参数--------------
    public static final String CLIENT_ATTRIBUTE_AREA = "area"; //执行端 位置
    public static final String CLIENT_ATTRIBUTE_IP = "ip"; //执行端 ip
    public static final String CLIENT_ATTRIBUTE_CREATETIME = "createTime"; //执行端 创建时间
    public static final String CLIENT_ATTRIBUTE_LASTWRITETIME = "lastWriteTime"; //执行端 最后写入时间
    public static final String CLIENT_ATTRIBUTE_LASTREADTIME = "lastReadTime"; //执行端 最后去读使劲

    //----------------------任务参数--------------------
    public static final String URL = "url"; //执行url
    public static final String WEIBO_ID = "weiboId";//微博id
    public static final String EXE_URL = "exeurl"; //执行url
    public static final String EXE_REFERER = "Referer"; //引用
    public static final String COMMENT_URL = "comment_url";//评论地址
    public static final String COMMENT_CONTENT = "comment_content";//评论内容

    //----------------------token字段--------------------------
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ACCESS_TOKEN_EXPRESS_IN = "express_in";//toke 有效期
    public static final String ACCESS_TOKEN_AUTH_TIME = "auth_time";//toke 认证事件

    public static final String ACCOUNT_ID = "_id";//帐号id
    public static final String ACCOUNT_USERNAME = "username";//帐号
    public static final String ACCOUNT_PASSWORD = "password";//密码
    public static final String ACCOUNT_DISPLAYNAME = "displayName";//昵称
    public static final String ACCOUNT_AUTHINFO = "auth_info";//认证信息
    public static final String ACCOUNT_COOKIE = "cookie";//cookie 保存登录状态
    public static final String ACCESS_COOKIE_EXPRESS_IN = "cookie_express_in";//cookie 有效期
    public static final String ACCESS_USER_ID = "user_id";//cookie 有效期
    public static final String OAUTH_CLIENT_ID = "OauthClientId";//认证信息client_id
    public static final String OAUTH_REDIRECT_URL = "OauthRedirectUrl";//认证信息redirect_url
    public static final String REMARK = "remark";//更新帐号时备注信息
    //----------------------------------------------------------------------
    //----------------------cookie字段--------------------------
    /**新浪微博web版Cookie*/
    public static final String WEIBO_COM_COOKIE = "weibo_com";
    /**新浪微博触平版和手机版通用Cookie*/
    public static final String SINA_CN_COOKIE = "sina_cn";
    /**新浪微博触平板Cookie*/
    public static final String WEIBO_CN_COOKIE = "weibo_cn";
    /**新浪Cookie*/
    public static final String SINA_COM_CN_COOKIE = "weibo_cn";
    /**凤凰新闻新浪认证返回的Cookie信息*/
    public static final String NEWS_IFENG_SINA_OAUTH_COOKIE = "NewsIfengSinaOauthCookie";
    /**头条新闻登陆返回的Cookie信息*/
    public static final String NEWS_TOUTIAO_LOGIN_COOKIE = "NewsTouTiaoLoginCookie";
    /**网易新闻登陆返回的cookie信息*/
    public static final String NEWS_163_LOGIN_COOKIE = "News163LoginCookie";
    /**搜狐新闻登陆返回的Cookie信息*/
    public static final String NEWS_SOHU_LOGIN_COOKIE = "NewsSohuLoginCookie";
    /**澎湃新闻登陆返回的Cookie信息*/
    public static final String NEWS_PENGPAI_LOGIN_COOKIE = "NewsPengPaiLoginCookie";
    /**新浪微博认证返回的Token信息*/
    public static final String SINA_WEIBO_APP_OAUTH_TOKEN = "SinaWeiboAppOauthToken";
    /**新浪微博手机触平板登陆返回的Cookie信息*/
    public static final String SINA_WEIBO_PHONE_LOGIN_COOKIE = "SinaWeiboPhoneLoginCookie";
    /**强国登陆返回的Cookie信息*/
    public static final String PEOPLE_LOGIN_COOKIE = "PeoPleLoginCookie";

    //----------------------------------------------------------------------
    //----------------------oauth字段--------------------------
    /**凤凰新闻新浪认证信息*/
    public static final String NEWS_IFENG_SINA_OAUTH_MSG = "NewsIfengSinaOauthMsg";//
    /**新浪微博认证信息*/
    public static final String SINA_WEIBO_APP_OAUTH_MSG = "SinaWeiboAppOauthMsg";//
    //----------------------------------------------------------------------




    public static final String CODE_REMARK = "remark";//代码标注
    public static final String CODE = "code";//代码
    public static final String AREA = "arew";//地区
    public static final String CARRY = "carry";//携带，微博，评论接待转发，
    /**
     * 参考id ，用来标记微博或者其他任务中一些id，转发微博的ID,评论ID
     *
     */
    public static final String REFERENCE_ID = "reference_id";//

    //---------------------任务类型-------------------------
    public static final int TASK_TYPE_SCHEDULE = 901;
    public static final int TASK_TYPE_BROADCAST = 902;

    /**
     * 保存廣播任務執行分發
     */
    public final static Map<String, Integer> MAP_NEW_SUPPORT_COUNT = new HashMap();

    static {
        MAP_NEW_SUPPORT_COUNT.put(NEW_163_SUPPORT, 5);
        MAP_NEW_SUPPORT_COUNT.put(NEW_SINA_SUPPORT, 20);
        MAP_NEW_SUPPORT_COUNT.put(NEW_IFENG_SUPPORT, 20);
        MAP_NEW_SUPPORT_COUNT.put(NEW_SOHU_SUPPORT, 10);
    }
    //默认任务分配数量
    public static final int DEFAULT_BROADCAST_COUNT = 20;


    //--------------------机器人通信参数--------------------------
    public static final String ROBOT_CODE = "code";
    //    public static final String IP = "ip";
//    public static final String AREA = "area";
    public static final String ROBOT_CODE_REMARK = "code_remark";
    public static final String ROBOT_RESULT = "result";
    //----------------------------HTTP API 参数---------------------------
    public static final String API_PARAMS_RESULT = "result";//结果
    public static final int API_PARAMS_RESULT_SUCCESS = 1;//成功，有结果返回
    public static final int API_PARAMS_RESULT_FAILED = 0;//失败，不一定是执行失败，可能是没有返回结果

    public static final String API_PARAMS_CONTENT = "content";//返回内容
    public static final String API_PARAMS_ACCOUNT = "account";//账号信息
    public static final String API_PARAMS_DOC = "doc";//语料信息
    public static final String DOC_SPLIT = "####";


    //-------------------------tcp JMessage 通信使用的参数常量 原 JMessageFactory 常量------------------------
    public static final int TYPE_REQUEST = 101;//请求
    public static final int TYPE_RESPONSE = 102;//回复
    public static final int TYPE_AUTH = 103;//认证
    public static final int TYPE_AUTH_RESPONSE = 104;//认证
    public static final int TYPE_TASK_NEW = 105;//任务 新建
    public static final int TYPE_TASK_STOP = 106;//任务暂停
    public static final int TYPE_TASK_START = 107;//任务 开始
    public static final int TYPE_TASK_CANCEL = 108;//任务 取消
    public static final int TYPE_MANAGER = 109;//管理
    public static final int TYPE_ERR = 110;//错误信息
    public static final int TYPE_BEATS = 200;//错误信息
    public static final int DEFAULT_VERSION = 100;//默认版本


    /**
     * 任务动作：具体的任务执行类
     */
    public static final int DEFAULT_TASK_SCHEDULE_TIMES = 1;

    public static final String USER_ID = "userID";
    public static final String TASK_ID = "task_id";
    public static final String SUB_ID = "sub_id";
    public static final String TASK = "task";
    public static final String TASK_NAME = "name";
    public static final String TASK_USERNAME = "username";
    public static final String TASK_USER_DISPLAYNAME = "user_displayname";
    public static final String TASK_FOREGROUNDID = "foregroundID";
    public static final String TASK_SCHEDULE = "schedule";
    public static final String TASK_TYPE = "task_type";
    public static final String TASK_STATUS = "task_status";
    public static final String TASK_START_TIME = "start_time";
    public static final String TASK_STOP_TIME = "stop_time";
    public static final String TASK_IP_SET = "IPSet";
    public static final String TASK_USER_SET = "userSet";
    public static final String SUB_COUNT = "sub_count";
    public static final String TASK_NEED_SCHEDULE_TIMES = "need_schedule_times";
    public static final String TASK_SCHEDULE_MAX = "schedule_max";
    public static final String TASK_SCHEDULE_MIN = "schedule_min";
    public static final String Task_IS_SEND = "isSend";
    public static final String TASK_CREATE_TIME = "create_time";//已经开始
    public static final String TASK_TAG = "task_tag";
    public static final String TASK_TAG_DATA = "task_tag_data";
    public static final String TASK_TAG_DATA_FORM = "task_tag_data_form";
    public static final String TASK_TAG_DATA_HEADER = "task_tag_data_header";
    public static final String TASK_DOC = "doc";
    public static final String TASK_DOC_ID = "datas";
    public static final String TASK_DOC_SETID = "setid";
    public static final String TASK_SCHEDULE_START_TIME = "startTime";
    public static final String TASK_SCHEDULE_END_TIME = "endTime";
    public static final String TASK_SCHEDULE_COUNT = "count";
    public static final String ACTION = "action";//
    public static final String TAG = "TAG";//连接 或者标签
    public static final String FORM = "form";//参数字段名
    public static final String ACCOUNT = "account";//账号
    public static final String DOC = "doc";//语料
    public static final String COUNT = "count";//执行数量
    public static final String TIME = "time";//执行时间
    public static final String RESULT = "result";//执行结果
    public static final String STATUS = "status";

    //-----------------token状态-----------------
    /**任务执行成功*/
    public static final int CODE_TASK_ACTIOIN_SUCCESS = 0;
    /**token不可用*/
    public static final int CODE_TOKEN_NOT_AVAILABLE = 1;
    /**token过期*/
    public static final int CODE_TOKEN_EXPIRE = 2;
    /**IP地址异常*/
    public static final int CODE_TOKEN_IP_EXCEPTION = 3;
    /**任务执行异常等等.....*/
    public static final int CODE_TASK_ACTION_FAIL = 4;
    /**任务执行结果 标签*/
    public static final String TASK_ACTION_RESULT = "TaskActionResult";
    /**
     * 动作：调度执行的任务信息
     */
    public static final int ACTION_SCHEDULE = 1;
    /**
     * 广播执行的任务信息
     */
    public static final int ACTION_BROADCAST = 2;
    /**
     * 获取广播后续任务信息 流程：调度发送执行执行，执行端每完成一次执行上报一次结果， 都执行完毕后向调度发总广播请求（此信息），
     * 如果有将继续发广播任务改执行端，如果没有则结束
     */
    public static final int ACTION_BROADCAST_COMPLETE = 5;
    /**
     * 调度任务结果报告，及向调度报告任务结果
     */
    public static final int ACTION_REPORT = 4;//调度任务报告，用户反馈任务执行结果
    /**
     * 广播任务报告
     */
    public static final int ACTION_BROADCAST_REPORT = 3;//广播任务报告，用户反馈任务执行结果
    /**
     * 取消任务：主要用户广播
     */
    public static final int ACTION_CANCEL = 6;

    public static final int ACTION_BROADCAST_REQUEST = 7;//认证

    // account 参数
    /**
     * 使用账号用户名
     */
//    public static final String ACCOUNT_USERNAME = "username";//用户名
//    /**
//     * 使用账号密码
//     */
//    public static final String ACCOUNT_PASSWORD = "password";//密码
//    /**
//     * 登陆便签
//     */
//    public static final String ACCOUNT_TOKEN = "token";//
//    /**
//     * 使用账号用昵称
//     */
//    public static final String ACCOUNT_NICKNAME = "nickname";//

    //err code
    public static final int ERR_JSON = 1;//错误信息


    /*配置文件*/
    public static final String CONFIG_HOST = "host";
    public static final String CONFIG_PROT = "prot";
    public static final String CONFIG_SCHEDULE = "schedule";
    public static final String CONFIG_BROADCAST = "broadcast";
    public static final String CONFIG_VERSION = "version";
    public static final String CONFIG_KEY = "key";
    public static final String CONFIG_CLIENT_ID = "client_id";
    public static final String CONFIG_APIURL = "apiurl";
    public static final String CONFIG_NEWS_IFENG_SINA_OAUTH_MSG = "NewsIfengSinaOauthMsg";
    public static final String CONFIG_SINA_WEIBO_APP_OAUTH_MSG = "SinaWeiboAppOauthMsg";


}
