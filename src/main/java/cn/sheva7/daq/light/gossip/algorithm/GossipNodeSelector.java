/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/
package src.main.java.cn.sheva7.daq.light.gossip.algorithm;

import src.main.java.cn.sheva7.daq.light.gossip.model.GossipNode;
import src.main.java.cn.sheva7.daq.light.gossip.model.GossipSeed;

import java.util.List;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-12-17 11:17
 */
public interface GossipNodeSelector {

    GossipNode selectOneLiveNode(List<GossipNode> liveNodes);

    GossipNode selectOneDeadNode(List<GossipNode> deadNodes);

    GossipSeed selectOneSeedNode(List<GossipSeed> seedNodes);

    List<GossipNode> selectAllLiveNodes(List<GossipNode> liveNodes);

    List<GossipNode> selectAllDeadNodes(List<GossipNode> deadNodes);

    List<GossipSeed> selectAllSeedNodes(List<GossipSeed> seedNodes);

}
