package com.wiseweb.cat.client;

import com.wiseweb.cat.base.*;
import com.wiseweb.cat.base.Constants;
import com.wiseweb.cat.base.JMessage;
import com.wiseweb.cat.base.JMessageFactory;
import com.wiseweb.cat.task.TaskController;
import com.wiseweb.json.JSONException;
import com.wiseweb.json.JSONObject;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 贾承斌 on 15/11/7.
 */
public class TcpTaskClient extends IoHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger();
    private static final Configer configer = ConfigerFactory.getConfiger();
    private TaskController taskController;

    private IoSession session;


    private long delayTime = 10 * 1000;
    private Timer timer = new Timer(true);

    public TcpTaskClient(TaskController taskController) {
        this.taskController=taskController;
    }

    public void connect() {
        if (configer.getScheduleTask() == null) {
            logger.info("没有调度类型");
        } else {
            if (session != null) {
                disConnect();
            }
            tcpConnect();
        }
    }
    public void disConnect() {
        if (session != null) {
            session.close(true);
        }
    }
    private void reConnect() {
        logger.info(delayTime / 1000 + " 秒重新连接");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connect();
            }
        }, delayTime);
    }
    private void tcpConnect() {
        NioSocketConnector connector = new NioSocketConnector();
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        if (session != null && session.isConnected()) {
            logger.info("断开原有连接");
        }
        chain.addLast("messageFilter", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        connector.setHandler(this);
        connector.setConnectTimeoutMillis(30 * 1000);
        logger.info("连接：" + configer.getScheduleHost() + ":" + configer.getSchedulePort());
        ConnectFuture future1 = connector.connect(new InetSocketAddress(configer.getScheduleHost() , configer.getSchedulePort()));
        future1.awaitUninterruptibly();
        if (!future1.isConnected()) {
            logger.info("连接失败");
            reConnect();
            return;
        }
        session = future1.getSession();
        logger.info("连接成功");
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        if (message instanceof JMessage) {
            JMessage m = (JMessage) message;
            logger.info("发送" + m.getNum() + "信息:" + m.getContent());
        } else if (message instanceof String) {
            logger.info("发送String信息:" + message);
        } else {
            logger.info("信息" + message.toString());
        }
    }

    public void sendJMessage(JMessage message) {
        logger.debug("发送信息" + message.getContent());
        if (session != null && session.isConnected()) {
            session.write(message);
        }

    }

    /**
     * 信息接收方法
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        if (message instanceof JMessage) { //处理信息对象，字符串咱不处理
            JMessage m = (JMessage) message;

            if (m.getType() == Constants.TYPE_RESPONSE) {//处理返回信息
                logger.info("收到" + m.getNum() + "返回信息。");
            } else if (m.getType() == Constants.TYPE_AUTH_RESPONSE) {//处理认证返回信息
                logger.info("收到" + m.getNum() + "返回信息。");
                int re = Integer.valueOf(m.getContent());
                if (re == 0) {
                    session.getConfig().setIdleTime(IdleStatus.READER_IDLE, 30);//空闲
                    session.getConfig().setIdleTime(IdleStatus.WRITER_IDLE, 15);//空闲
                } else {
                    session.close(true);
                }
            } else if (m.getType() == Constants.TYPE_REQUEST) { //处理请求信息
                logger.info("收到" + m.getNum() + "信息:" + m.getContent());

                try {
                    JSONObject taskjson = new JSONObject(m.getContent());
                    taskController.addRunningScheduleTask(taskjson);
                } catch (JSONException e) {
                    logger.error("转换数据异常", e);
                }
                //添加使用逻辑
            } else if(m.getType() == Constants.TYPE_BEATS){
                logger.debug("-----------收到心跳返回-------------");
            }
            session.write(JMessageFactory.getResponseMessage(m));
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.info("连接异常");
        cause.printStackTrace();
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

        if (status == IdleStatus.READER_IDLE) {//读取超时
            //读取超时 退出
            logger.info("--------------读取超时 退出-----------");
            session.close(true);

        } else if (status == IdleStatus.WRITER_IDLE) {
            logger.info("--------------发送心跳信息-----------");
            session.write(JMessageFactory.getBeatsMessage());
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("--------------连接关闭-----------");
        reConnect();
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("连接打开");
        session.write(JMessageFactory.getAuthMessage(getAuthJson()));
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, 60);//空闲
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info("连接创建");
    }

    private JSONObject getAuthJson() {
        JSONObject content = new JSONObject();
        content.put(Constants.MESSAGE_FIELD_AUTH, configer.getKey());
        content.put(Constants.MESSAGE_FIELD_VERSON, configer.getVersion());
        content.put(Constants.MESSAGE_FIELD_CLIENT_ID, configer.getClientId());
        content.put(Constants.MESSAGE_DEFAULT_SCHEDULE_TYPE, configer.getScheduleTask());
        content.put(Constants.MESSAGE_FIELD_BROADCAST_TYPE, configer.getBroadcastTask());
        content.put(Constants.MESSAGE_FIELD_AREA, configer.getArea());
        content.put(Constants.MESSAGE_FIELD_MAC, configer.getMAC());

        return content;
    }

    public void start() {
        connect();
    }

    public void stop() {
        disConnect();
    }
}
