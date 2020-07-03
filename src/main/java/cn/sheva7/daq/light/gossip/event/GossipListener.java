
/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.event;


import src.main.java.cn.sheva7.daq.light.gossip.model.GossipNode;
import src.main.java.cn.sheva7.daq.light.gossip.model.GossipState;

/**
 * @author zb
 */
public interface GossipListener {
    void gossipEvent(GossipNode node, GossipState state);
}
