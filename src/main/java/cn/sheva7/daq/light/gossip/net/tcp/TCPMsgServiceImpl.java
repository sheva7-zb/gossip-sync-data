/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.net.tcp;

import src.main.java.cn.sheva7.daq.light.gossip.core.GossipManager;
import cn.sheva7.daq.light.gossip.handler.*;
import src.main.java.cn.sheva7.daq.light.gossip.model.GossipMessage;
import src.main.java.cn.sheva7.daq.light.gossip.model.MessageType;
import src.main.java.cn.sheva7.daq.light.gossip.net.MsgService;
import src.main.java.cn.sheva7.daq.light.gossip.net.VertxContext;
import src.main.java.cn.sheva7.daq.light.gossip.util.ZipStrUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import io.vertx.core.net.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author zb
 */
@Slf4j
public class TCPMsgServiceImpl implements MsgService {
    private NetServer netServer;
    private NetClient netClient;
    private static final int TCP_RECEIVE_BUFFER_SIZE = 1000000;
    private static final int TCP_SEND_BUFFER_SIZE = 1000000;

    @Override
    public void listen(String ipAddress, int port) {
        NetServerOptions netServerOptions = new NetServerOptions().setPort(port)
                .setSendBufferSize(TCP_SEND_BUFFER_SIZE)
                .setReceiveBufferSize(TCP_RECEIVE_BUFFER_SIZE)
                .setIdleTimeout(3000)
                .setLogActivity(true);

        netServer = VertxContext.vertx().createNetServer(netServerOptions);
        netServer.connectHandler(socket -> {
//            socket.closeHandler(v -> {
//                log.info("The server socket has been closed");
//            });
            socket.handler(buffer -> {
                handleMsg(buffer.toString());
            });
        });
        netServer.listen(port, asyncResult -> {
            if (asyncResult.succeeded()) {
                log.info("TCP Listen success ！port {}", port);
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
            log.error("unCompress error data:" + data);
            log.error("unCompress error", e);
            return;
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
            final String dataStr = ZipStrUtil.compress(data);
            log.trace("sendMsg length after compress ==> " + dataStr.length());

            if (netClient == null) {
                NetClientOptions netClientOptions = new NetClientOptions().
                        setConnectTimeout(10000).
                        setIdleTimeout(3000).
                        setReconnectAttempts(1).
                        setReconnectInterval(3000).
//                        setLocalAddress("192.168.7.31").
                        setSendBufferSize(TCP_SEND_BUFFER_SIZE).
                        setReceiveBufferSize(TCP_RECEIVE_BUFFER_SIZE).
                        setLogActivity(true);

                netClient = VertxContext.vertx().createNetClient(netClientOptions);
            }
            for (String targetIpHost: targetIp.split(",")) {
                netClient.connect(targetPort, targetIpHost, ar -> {
                    if (ar.succeeded()) {
                        NetSocket socket = ar.result();
                        socket.closeHandler(v -> {
                            //log.info(" closeHandler The client socket has been closed" + v);
                        });
                        //发送完成就主动关闭，因为不用收到结果
                        socket.write(dataStr).end();
                    } else {
                        log.error("Failed to connect: " + ar.cause().getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void unListen() {
        if (netServer != null) {
            netServer.close(asyncResult -> {
                if (asyncResult.succeeded()) {
                    log.info("netServer was close!");
                } else {
                    log.error("Close netServer an error has occurred. " + asyncResult.cause().getMessage());
                }
            });
        }
        if (netClient != null) {
            netClient.close();
        }
    }
}
