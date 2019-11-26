package com.example.demo;


import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Slf4j
public class MessageEventHandler {

    private static Logger logger = LoggerFactory.getLogger(TonyUdpServer.class);


    private static ConcurrentMap<String, SocketIOClient> socketIOClientMap = new ConcurrentHashMap<>();


    /**
     * 客户端连接的时候触发
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        HandshakeData handshakeData = client.getHandshakeData();
        String mac = handshakeData.getAddress().getHostName();
        //存储SocketIOClient，用于发送消息
        socketIOClientMap.put(mac, client);
        logger.info("客户端:" + client.getSessionId() + "已连接,mac=" + mac);
    }

    /**
     * 客户端关闭连接时触发
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        logger.info("客户端:" + client.getSessionId() + "断开连接");
    }

    /**
     * 登陆接口
     *
     * @param client  　客户端信息
     * @param request 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "add user")
    public void onAddUserEvent(SocketIOClient client, AckRequest request, String data) {
        logger.info("===onAddUserEvent====" + data);
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("numUsers", 1);
        sendBroadcast("login", jsonpObject);
    }


    /**
     * 刷新数据库接口(用于小批量数据更新)
     *
     * @param client  　客户端信息
     * @param request 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "refreshData")
    public void onRefreshDataEvent(SocketIOClient client, AckRequest request, String data) {
        logger.info("===onRefreshDataEvent====" + data);
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("numUsers", data);
        sendBroadcast("refreshAction", jsonpObject);
    }

    /**
     * 覆盖数据库接口(用于数据库文件更新)
     *
     * @param client  　客户端信息
     * @param request 请求信息
     * @param data    　客户端发送数据
     */
    @OnEvent(value = "replaceData")
    public void onReplaceDataEvent(SocketIOClient client, AckRequest request, String data) {
        logger.info("===onReplaceDataEvent====" + data);
        //回发消息
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("type", data);
        sendBroadcast("replaceAction", jsonpObject);

    }


    /**
     * 广播消息
     */
    private void sendBroadcast(String event, JSONObject data) {
        for (SocketIOClient client : socketIOClientMap.values()) {
            if (client.isChannelOpen()) {
                logger.info("===sendBroadcast====" + client.getRemoteAddress().toString());
                client.sendEvent(event, "当前时间", System.currentTimeMillis());
            }
        }
    }
}
