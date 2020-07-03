/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.handler;

import src.main.java.cn.sheva7.daq.light.gossip.core.GossipManager;
import cn.sheva7.daq.light.gossip.model.*;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zb
 */
@Slf4j
public class AckMessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, Object data, String from) throws Exception {
        if(data == null){
            return;
        }
        AckMessage ackMessage;
        if(data instanceof JSONObject){
            ackMessage = JSONObject.toJavaObject((JSONObject) data, AckMessage.class);
        }else if(data instanceof SyncMessage){
            ackMessage = (AckMessage) data;
        }else{
            log.error("error ackMessage! " + data);
            return;
        }

        Map<String, GossipData> olderMap = ackMessage.getOlderMap();
        Map<String, GossipData> newerMap = ackMessage.getNewerMap();

        //你比我本地新的节点数据更新替换
        if (newerMap != null && newerMap.size() > 0) {
            GossipManager.getInstance().applyStateAndData2Local(newerMap);
        }

        //我本地比你新的节点数据，通过Ack2发送给你
        Map<String, GossipData> myNewerEndpoints = new HashMap<>();
        if (olderMap != null && olderMap.size() > 0) {
            for (Map.Entry<String, GossipData> remoteEntry: olderMap.entrySet()){
                String yourOlderNodeId = remoteEntry.getKey();
                GossipData remGossipData = remoteEntry.getValue();
                GossipDigest localGossipDigest = GossipManager.getInstance().getEndpointNodes().get(yourOlderNodeId);
                if(localGossipDigest!=null){
                    GossipData myNewerData;
                    if(remGossipData.isOnlyDigest()){
                        myNewerData = new GossipData(localGossipDigest, true);
                    }else{
                        myNewerData = GossipManager.getInstance().getGossipData(remGossipData.getDigest(), localGossipDigest);
                    }
                    myNewerEndpoints.put(yourOlderNodeId, myNewerData);
                }
            }
        }
        if (myNewerEndpoints.size() > 0) {
            Ack2Message ack2Message = new Ack2Message(myNewerEndpoints);
            String ack2Str = GossipMessage.encodeAck2Message(ack2Message, GossipManager.getInstance().getSelf());
            if (from != null) {
                String[] host = from.split(":");
                GossipManager.getInstance().getSettings().getMsgService().sendMsg(host[0], Integer.valueOf(host[1]), ack2Str);
            }
        }
    }
}
