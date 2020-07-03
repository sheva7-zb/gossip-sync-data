/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author zb
 * gossip传输交换信息的载体对象
 */
@Data
@ToString
public class GossipMessage<T> implements Serializable {

    private String msqType;
    private String cluster;
    private String from;
    private T data;

    public GossipMessage() {
        super();
    }

    public GossipMessage(String msqType, T data, String cluster, String from) {
        super();
        this.msqType = msqType;
        this.data = data;
        this.cluster = cluster;
        this.from = from;
    }

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }


    public static String encodeSyncMessage(List<GossipDigest> digests, GossipNode self) {
        return new GossipMessage(MessageType.SYNC_MESSAGE.name(), new SyncMessage(digests), self.getCluster(), self.ipAndPort()).toJSONString();
    }

    public static String encodeAckMessage(AckMessage ackMessage, GossipNode self) {
        return new GossipMessage(MessageType.ACK_MESSAGE.name(), ackMessage, self.getCluster(), self.ipAndPort()).toJSONString();
    }

    public static String encodeAck2Message(Ack2Message ack2Message, GossipNode self) {
        return new GossipMessage(MessageType.ACK2_MESSAGE.name(), ack2Message, self.getCluster(), self.ipAndPort()).toJSONString();
    }

    public static String encodeShutdownMessage(GossipNode self) {
        return new GossipMessage(MessageType.SHUTDOWN.name(), self, self.getCluster(), self.ipAndPort()).toJSONString();
    }

}
