/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package cn.cechealth.daq.light.gossip.model;

/**
 * @author zb
 */
public enum GossipState {
    /**
     *
     */
    UP("up"),
    /**
     *
     */
    DOWN("down"),
    /**
     *
     */
    JOIN("join");

    private final String state;

    GossipState(String state) {
        this.state = state;
    }

}
