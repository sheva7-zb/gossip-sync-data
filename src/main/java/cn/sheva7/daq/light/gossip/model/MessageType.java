/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.model;

/**
 * @author zb
 */
public enum MessageType {
    /**
     *  1.同步请求
     */
    SYNC_MESSAGE("sync_message"),
    /**
     *  2.对sync_message的应答
     */
    ACK_MESSAGE("ack_message"),
    /**
     *  3.对ack_message的应答
     */
    ACK2_MESSAGE("ack2_message"),
    /**
     *
     */
    SHUTDOWN("shutdown");

    private final String type;

    MessageType(String type) {
        this.type = type;
    }
}
