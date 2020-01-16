/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/
package cn.cechealth.daq.light.gossip.algorithm.impl;

import cn.cechealth.daq.light.gossip.algorithm.GossipNodeSelector;
import cn.cechealth.daq.light.gossip.core.GossipManager;
import cn.cechealth.daq.light.gossip.model.GossipNode;
import cn.cechealth.daq.light.gossip.model.GossipSeed;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-12-17 10:55
 */
@Slf4j
public class DefaultGossipNodeSelector implements GossipNodeSelector {

    Random random = new Random();

    @Override
    public GossipNode selectOneLiveNode(List<GossipNode> liveNodes) {
        return selectOneNodeRandom(liveNodes);
    }

    @Override
    public GossipNode selectOneDeadNode(List<GossipNode> deadNodes) {
        return selectOneNodeRandom(deadNodes);
    }

    @Override
    public GossipSeed selectOneSeedNode(List<GossipSeed> seedNodes) {
        return selectOneSeedRandom(seedNodes);
    }

    @Override
    public List<GossipNode> selectAllLiveNodes(List<GossipNode> liveNodes) {
        List<GossipNode> targetList = new ArrayList<>();
        for (GossipNode node: liveNodes) {
            if(!node.equals(GossipManager.getInstance().getSelf())){
                targetList.add(node);
            }
        }
        return targetList;
    }

    @Override
    public List<GossipNode> selectAllDeadNodes(List<GossipNode> deadNodes) {
        List<GossipNode> targetList = new ArrayList<>();
        for (GossipNode node: deadNodes) {
            if(!node.equals(GossipManager.getInstance().getSelf())){
                targetList.add(node);
            }
        }
        return targetList;
    }

    @Override
    public List<GossipSeed> selectAllSeedNodes(List<GossipSeed> seedNodes) {
        List<GossipSeed> targetList = new ArrayList<>();
        for (GossipSeed seed: seedNodes) {
            if(!seed.equalsNode(GossipManager.getInstance().getSelf())){
                targetList.add(seed);
            }
        }
        return targetList;
    }

    private GossipNode selectOneNodeRandom(List<GossipNode> nodes) {
        int liveSize = nodes.size();
        if (liveSize <= 0) {
            return null;
        }
        int index = (liveSize == 1) ? 0 : random.nextInt(liveSize);
        GossipNode target = nodes.get(index);
        if (target.equals(GossipManager.getInstance().getSelf())) {
            int mSize = nodes.size();
            if (mSize == 1) {
                return null;
            } else {
                target = nodes.get((index + 1) % mSize);
            }
        }
        return target;
    }

    private GossipSeed selectOneSeedRandom(List<GossipSeed> seeds) {
        int liveSize = seeds.size();
        if (liveSize <= 0) {
            return null;
        }
        int index = (liveSize == 1) ? 0 : random.nextInt(liveSize);
        GossipSeed target = seeds.get(index);
        if (target.equalsNode(GossipManager.getInstance().getSelf())) {
            int mSize = seeds.size();
            if (mSize == 1) {
                return null;
            } else {
                target = seeds.get((index + 1) % mSize);
            }
        }
        return target;
    }
}
