/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package cn.cechealth.daq.light.gossip.core;

import cn.cechealth.daq.light.gossip.event.GossipListener;
import cn.cechealth.daq.light.gossip.model.GossipSeed;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zb
 */
@Slf4j
public class GossipService {

    public static void init(String nodeId, String cluster, String ipAddress, Integer port, List<GossipSeed> seedNodes, GossipSettings settings, GossipListener listener) throws Exception {
        checkParams(nodeId, cluster, ipAddress, port, seedNodes);
        getGossipManager().init(nodeId, cluster, ipAddress, port, seedNodes, settings, listener);
    }

    public static GossipManager getGossipManager() {
        return GossipManager.getInstance();
    }

    public static void start() {
        if (getGossipManager().isActive()) {
            log.info("Cgossip already workinig");
            return;
        }
        getGossipManager().start();
    }

    public static void execOnecImmediately() throws Exception {
        if (getGossipManager().isActive()) {
            getGossipManager().execOnecImmediately();
        }
    }

    public static void shutdown() {
        if (getGossipManager().isActive()) {
            getGossipManager().shutdown();
        }
    }

    private static void checkParams(String nodeId, String cluster, String ipAddress, Integer port, List<GossipSeed> seedNodes) throws Exception {
        String f = "[%s] is required!";
        String who = null;
        if (StringUtil.isNullOrEmpty(nodeId)) {
            who = "nodeId";
        } else if (StringUtil.isNullOrEmpty(cluster)) {
            who = "cluster";
        } else if (StringUtil.isNullOrEmpty(ipAddress)) {
            who = "ip";
        } else if (StringUtil.isNullOrEmpty(String.valueOf(port))) {
            who = "port";
//        } else if (seedNodes == null || seedNodes.isEmpty()) {
//            who = "seed node";
        }
        if (who != null) {
            throw new IllegalArgumentException(String.format(f, who));
        }
    }
}
