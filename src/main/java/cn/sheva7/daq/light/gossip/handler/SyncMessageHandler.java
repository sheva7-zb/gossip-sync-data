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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zb
 */
@Slf4j
public class SyncMessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, Object data, String from) throws Exception {
        if (data != null) {
            SyncMessage syncMessage;
            if (data instanceof JSONObject) {
                syncMessage = JSONObject.toJavaObject((JSONObject) data, SyncMessage.class);
            } else if (data instanceof SyncMessage) {
                syncMessage = (SyncMessage) data;
            } else {
                log.error("error syncMessage! " + data);
                return;
            }
            List<GossipDigest> syncDigestList = syncMessage.getDigestList();

            Map<String, GossipData> olderMap = new HashMap<>();
            Map<String, GossipData> newerMap = new HashMap<>();

            List<GossipNode> syncNodeList = new ArrayList<>();
            for (GossipDigest s : syncDigestList) {
                syncNodeList.add(s.getEndpoint());
                compareDigest(s, olderMap, newerMap);
            }
            // 找出我这有，但同步信息没有的节点数据，放入newerMap
            Map<String, GossipDigest> endpoints = GossipManager.getInstance().getEndpointNodes();
            for (GossipDigest localDigest : endpoints.values()) {
                if (!syncNodeList.contains(localDigest.getEndpoint())) {
                    newerMap.put(localDigest.getId(), GossipManager.getInstance().getGossipData(null, localDigest));
                }
//                if (localDigest.getEndpoint().equals(GossipManager.getInstance().getSelf())) {
//                    newerMap.put(localDigest.getId(), new GossipData(localDigest, true));
//                }
            }
            AckMessage ackMessage = new AckMessage(olderMap, newerMap);
            String ackStr = GossipMessage.encodeAckMessage(ackMessage, GossipManager.getInstance().getSelf());
            if (from != null) {
                String[] host = from.split(":");
                GossipManager.getInstance().getSettings().getMsgService().sendMsg(host[0], Integer.valueOf(host[1]), ackStr);
            }
        }
    }

    private void compareDigest(GossipDigest remoteGossipDigest, Map<String, GossipData> olderMap, Map<String, GossipData> newerMap) throws Exception {
        GossipDigest localGossipDigest = GossipManager.getInstance().getEndpointNodes().get(remoteGossipDigest.getId());
        if (localGossipDigest != null) {
            compareDigestByVersion(remoteGossipDigest, localGossipDigest, olderMap, newerMap);
        } else {
            olderMap.put(remoteGossipDigest.getId(), new GossipData());
        }
    }

    private void compareDigestByVersion(GossipDigest remoteGossipDigest, GossipDigest localGossipDigest, Map<String, GossipData> olderMap, Map<String, GossipData> newerMap) throws Exception {
        long remoteVersion = remoteGossipDigest.getVersion();
        long localVersion = localGossipDigest.getVersion();
        if (remoteVersion > localVersion) {
            olderMap.put(localGossipDigest.getId(), new GossipData(localGossipDigest, false));
        } else if (remoteVersion < localVersion) {
            newerMap.put(localGossipDigest.getId(),
                    GossipManager.getInstance().getGossipData(remoteGossipDigest, localGossipDigest));
        } else {
            compareDigestByVersionTime(remoteGossipDigest, localGossipDigest, olderMap, newerMap);
        }
    }

    private void compareDigestByVersionTime(GossipDigest remoteGossipDigest, GossipDigest localGossipDigest, Map<String, GossipData> olderMap, Map<String, GossipData> newerMap) throws Exception {
        long remoteVersionTime = remoteGossipDigest.getVersionTime();
        long localVersionTime = localGossipDigest.getVersionTime();
        if (remoteVersionTime > localVersionTime) {
            olderMap.put(localGossipDigest.getId(), new GossipData(localGossipDigest, false));
        } else if (remoteVersionTime < localVersionTime) {
            newerMap.put(localGossipDigest.getId(),
                    GossipManager.getInstance().getGossipData(remoteGossipDigest, localGossipDigest));
        } else {
            compareDigestByHeartbeatTime(remoteGossipDigest, localGossipDigest, olderMap, newerMap);
        }
    }

    private void compareDigestByHeartbeatTime(GossipDigest remoteGossipDigest, GossipDigest localGossipDigest, Map<String, GossipData> olderMap, Map<String, GossipData> newerMap) throws Exception {
        long remoteHeartbeatTime = remoteGossipDigest.getHeartbeatTime();
        long localHeartbeatTime = localGossipDigest.getHeartbeatTime();
        if (remoteHeartbeatTime > localHeartbeatTime) {
            olderMap.put(localGossipDigest.getId(), new GossipData(localGossipDigest, true));
        } else if (remoteHeartbeatTime < localHeartbeatTime) {
            newerMap.put(localGossipDigest.getId(), new GossipData(localGossipDigest, true));
        }
    }
}
