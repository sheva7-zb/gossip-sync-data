/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package cn.cechealth.daq.light.gossip.model;

import cn.cechealth.daq.light.gossip.util.DateUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zb
 * 摘要  包含节点信息和心跳时间和当前版本
 */
@Data
public class GossipDigest implements Serializable, Comparable<GossipDigest> {
    private String id;
    private GossipNode endpoint;
    private long heartbeatTime;
    private long version;
    private long versionTime;
    private String versionInfo;

    @Override
    public int compareTo(GossipDigest o) {
        if (version != o.version) {
            return (int) (version - o.version);
        }
        return (int) (versionTime - o.versionTime);
    }

    @Override
    public String toString(){
        return "version: " + this.version;
    }

    public GossipDigest(){
        super();
    }

    public GossipDigest(GossipNode endpoint, long version, long versionTime) {
        this(endpoint, version, versionTime, null);
    }

    public GossipDigest(GossipNode endpoint, long version, long versionTime, String versionInfo) {
        super();
        //this.endpoint = new InetSocketAddress(InetAddress.getByName(endpoint.getIpAddress()), endpoint.getPort());
        this.endpoint = endpoint;
        this.heartbeatTime = DateUtil.currentTimeMillis();
        this.versionTime = versionTime;
        this.version = version;
        this.id = endpoint.getId();
        this.versionInfo = versionInfo;
    }
}
