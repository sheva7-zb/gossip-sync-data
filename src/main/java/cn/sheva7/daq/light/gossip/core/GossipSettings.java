/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.core;


import src.main.java.cn.sheva7.daq.light.gossip.algorithm.GossipNodeSelector;
import src.main.java.cn.sheva7.daq.light.gossip.algorithm.impl.DefaultGossipNodeSelector;
import src.main.java.cn.sheva7.daq.light.gossip.model.GossipNode;
import src.main.java.cn.sheva7.daq.light.gossip.model.GossipSeed;
import src.main.java.cn.sheva7.daq.light.gossip.net.MsgService;
import src.main.java.cn.sheva7.daq.light.gossip.net.udp.UDPMsgServiceImpl;
import src.main.java.cn.sheva7.daq.light.gossip.provider.GossipDataProvider;
import src.main.java.cn.sheva7.daq.light.gossip.provider.impl.DefaultProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zb
 */
public class GossipSettings {
    //Time between gossip ping in ms. Default is 10 second
    private int gossipInterval = 10000;

    //Network delay in ms. Default is 2000ms
    private int networkDelay = 2000;

    //Which message sync implementation. Default is UDPMsgService.class
    private MsgService msgService = new UDPMsgServiceImpl();

    //Delete the deadth node when the sync message is not received more than [deleteThreshold] times
    private int deleteThreshold = 3;

    private List<GossipSeed> seedNodes = new ArrayList<>();

    private GossipDataProvider provider = new DefaultProvider();

    private GossipNodeSelector nodeSelector = new DefaultGossipNodeSelector();

    public GossipDataProvider getProvider() {
        return provider;
    }

    public void setProvider(GossipDataProvider provider) {
        this.provider = provider;
    }

    public int getGossipInterval() {
        return gossipInterval;
    }

    public void setGossipInterval(int gossipInterval) {
        this.gossipInterval = gossipInterval;
    }

    public int getNetworkDelay() {
        return networkDelay;
    }

    public void setNetworkDelay(int networkDelay) {
        this.networkDelay = networkDelay;
    }

    public List<GossipSeed> getSeedNodes() {
        return seedNodes;
    }

    public boolean seedNodesContainsNode(GossipNode node){
        for (GossipSeed seed: this.seedNodes){
            if(node.getPort().equals(seed.getPort()) &&
                    (node.getIpAddress().equals(seed.getIp()) || node.getIpAddress().contains(seed.getIp()))){
                return true;
            }
        }
        return false;
    }


    public void setSeedNodes(List<GossipSeed> seedNodes) {
        List<GossipSeed> tempSeedNodes = new ArrayList<>();
        if (seedNodes != null && !seedNodes.isEmpty()) {
            for (GossipSeed seed : seedNodes) {
                if (!GossipManager.getInstance().getSelf().getPort().equals(seed.getPort()) ||
                        !GossipManager.getInstance().getSelfIp().contains(seed.getIp())) {
                    if (!tempSeedNodes.contains(seed)) {
                        tempSeedNodes.add(seed);
                    }
                }
            }
        }
        this.seedNodes = tempSeedNodes;
    }

    public MsgService getMsgService() {
        return msgService;
    }

    public void setMsgService(MsgService msgService) {
        this.msgService = msgService;
    }

    public GossipNodeSelector getNodeSelector() {
        return nodeSelector;
    }

    public void setNodeSelector(GossipNodeSelector nodeSelector) {
        this.nodeSelector = nodeSelector;
    }

    public int getDeleteThreshold() {
        return deleteThreshold;
    }

    public void setDeleteThreshold(int deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }
}
