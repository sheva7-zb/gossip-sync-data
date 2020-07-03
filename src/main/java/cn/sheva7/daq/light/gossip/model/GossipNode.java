/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.model;

import java.io.Serializable;

/**
 * @author zb
 * 节点信息
 */
public class GossipNode implements Serializable {
    private String id;
    /**
     * 多个ip用逗号隔开，例如在双网卡机器上
     */
    private String ipAddress;
    private Integer port;
    private String cluster;
    private GossipState state;
    /**
     * 双网卡时，节点经过检查只有一个ip能联通，则使用
     */
    private String connectedIp;

    public GossipNode() {
        super();
    }

    public GossipNode(String cluster, String ipAddress, Integer port, String id, GossipState state) {
        super();
        this.cluster = cluster;
        this.ipAddress = ipAddress;
        this.port = port;
        this.id = id;
        this.state = state;
    }

    public GossipState getState() {
        return state;
    }

    public void setState(GossipState state) {
        this.state = state;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConnectedIp() {
        return connectedIp;
    }

    public void setConnectedIp(String connectedIp) {
        this.connectedIp = connectedIp;
    }

    @Override
    public String toString() {
        return this.id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GossipNode node = (GossipNode) o;

        if (!cluster.equals(node.cluster)) {
            return false;
        }
        if (!ipAddress.equals(node.ipAddress)) {
            return false;
        }
        return port.equals(node.port);
    }

    @Override
    public int hashCode() {
        int result = cluster.hashCode();
        result = 31 * result + ipAddress.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }

    public String ipAndPort() {
        return ipAddress.concat(":").concat(String.valueOf(port));
    }

}
