/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.model;

import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author zb
 * 首先发起同步请求，包含集群名和gossip摘要
 */
@ToString
public class SyncMessage implements Serializable {
    private List<GossipDigest> digestList;

    public SyncMessage() {
        super();
    }

    public SyncMessage(List<GossipDigest> digestList) {
        super();
        this.digestList = digestList;
    }
    public List<GossipDigest> getDigestList() {
        return digestList;
    }

    public void setDigestList(List<GossipDigest> digestList) {
        this.digestList = digestList;
    }

}
