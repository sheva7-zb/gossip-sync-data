/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package cn.cechealth.daq.light.gossip.model;

import lombok.ToString;

import java.io.Serializable;

/**
 * @author zb
 * 节点信息
 */
@ToString
public class GossipSeed implements Serializable {
    /**
     * 单个ip
     */
    private String ip;
    private Integer port;
    private String cluster;

    public GossipSeed() {
        super();
    }

    public GossipSeed(String cluster, String ipAddress, Integer port) {
        super();
        this.cluster = cluster;
        this.ip = ipAddress;
        this.port = port;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean equalsNode(GossipNode node) {
        if(!node.getPort().equals(this.getPort())){
            return false;
        }
        return node.getIpAddress().equalsIgnoreCase(this.getIp()) || node.getIpAddress().contains(this.getIp());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GossipSeed seed = (GossipSeed) o;

        if (!cluster.equals(seed.cluster)) {
            return false;
        }
        if (!ip.equals(seed.ip)) {
            return false;
        }
        return port.equals(seed.port);
    }

    @Override
    public int hashCode() {
        int result = cluster.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }
}
