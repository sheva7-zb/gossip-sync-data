/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.net.udp;

import src.main.java.cn.sheva7.daq.light.gossip.core.GossipManager;
import cn.sheva7.daq.light.gossip.handler.*;
import src.main.java.cn.sheva7.daq.light.gossip.model.GossipMessage;
import src.main.java.cn.sheva7.daq.light.gossip.model.MessageType;
import src.main.java.cn.sheva7.daq.light.gossip.net.MsgService;
import src.main.java.cn.sheva7.daq.light.gossip.net.VertxContext;
import src.main.java.cn.sheva7.daq.light.gossip.util.ZipStrUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author zb
 */
@Slf4j
public class UDPMsgServiceImpl implements MsgService {
    DatagramSocket socket;

    private static final int UDP_RECEIVE_BUFFER_SIZE = 1000000;
    private static final int UDP_SEND_BUFFER_SIZE = 1000000;

    @Override
    public void listen(String ipAddress, int port) {
        socket = VertxContext.vertx().createDatagramSocket(new DatagramSocketOptions()
                .setReceiveBufferSize(UDP_RECEIVE_BUFFER_SIZE)
                .setSendBufferSize(UDP_SEND_BUFFER_SIZE));
        socket.listen(port, "0.0.0.0", asyncResult -> {
            if (asyncResult.succeeded()) {
                socket.handler(packet -> handleMsg(packet.data().toString()));
            } else {
                log.error("Listen failed " + asyncResult.cause());
            }
        });
    }

    @Override
    public void handleMsg(String data) {
        int length = data.length();
        try {
            data = ZipStrUtil.unCompress(data);
        } catch (IOException e) {
            log.error("unCompress error", e);
        }
        GossipMessage gossipMessage = JSONObject.parseObject(data, GossipMessage.class);

        String msgType = gossipMessage.getMsqType();
        log.trace("receive gossipMessage type: {}, length: {}, after unCompress length: {}", msgType, length, data.length());
        Object tempData = gossipMessage.getData();
        String cluster = gossipMessage.getCluster();
        String from = gossipMessage.getFrom();
        if (StringUtil.isNullOrEmpty(cluster) || !GossipManager.getInstance().getCluster().equals(cluster)) {
            log.error("This message shouldn't exist my world!" + data);
            return;
        }
        MessageHandler handler = null;
        MessageType type = MessageType.valueOf(msgType);
        if (type == MessageType.SYNC_MESSAGE) {
            handler = new SyncMessageHandler();
        } else if (type == MessageType.ACK_MESSAGE) {
            handler = new AckMessageHandler();
        } else if (type == MessageType.ACK2_MESSAGE) {
            handler = new Ack2MessageHandler();
        } else if (type == MessageType.SHUTDOWN) {
            handler = new ShutdownMessageHandler();
        } else {
            log.error("Not supported message type");
        }
        if (handler != null) {
            try {
                handler.handle(cluster, tempData, from);
            } catch (Exception e) {
                log.error("handleMsg error, please check ===>", e);
            }
        }
    }

    @Override
    public void sendMsg(String targetIp, Integer targetPort, String data) throws IOException {
        if (targetIp != null && targetPort != null && data != null) {
            String dataStr = ZipStrUtil.compress(data);
            log.trace("sendMsg length after compress ==> " + dataStr.length());
            for (String targetIpHost: targetIp.split(",")) {
                socket.send(dataStr, targetPort, targetIpHost, asyncResult -> {
                    if (!asyncResult.succeeded()) {
                        log.error("sendMsg result: " + asyncResult.succeeded());
                    }
                });
            }
        }
    }

    @Override
    public void unListen() {
        if (socket != null) {
            socket.close(asyncResult -> {
                if (asyncResult.succeeded()) {
                    log.info("Socket was close!");
                } else {
                    log.error("Close socket an error has occurred. " + asyncResult.cause().getMessage());
                }
            });
        }
    }
}
