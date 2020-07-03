/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.model;

import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zb
 * 收到同步请求后，将本地更旧的节点（摘要）和更新的（数据）发给from
 */
@ToString
public class AckMessage implements Serializable {

    private Map<String, GossipData> olderMap;
    private Map<String, GossipData> newerMap;

    public AckMessage(){
        super();
    }
    public AckMessage(Map<String, GossipData> olderMap, Map<String, GossipData> newerMap) {
        super();
        this.olderMap = olderMap;
        this.newerMap = newerMap;
    }

    public Map<String, GossipData> getOlderMap() {
        return olderMap;
    }

    public void setOlderMap(Map<String, GossipData> olderMap) {
        this.olderMap = olderMap;
    }

    public Map<String, GossipData> getNewerMap() {
        return newerMap;
    }

    public void setNewerMap(Map<String, GossipData> newerMap) {
        this.newerMap = newerMap;
    }
}
