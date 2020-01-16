/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-12      zhoubin           init.
 ********************************************************************************/
package cn.cechealth.daq.light.gossip.provider;

import cn.cechealth.daq.light.gossip.model.GossipData;
import cn.cechealth.daq.light.gossip.model.GossipDigest;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-12-12 13:59
 */
public interface GossipDataProvider {

    /**
     * 根据远程和本地节点摘要比较后，得到本地需要同步/交换的数据包，并返回
     * @param nodeId
     * @param remoteGossipDigest maybe null
     * @param localGossipDigest
     * @return GossipData
     * @throws Exception
     */
    String getSyncDataByCompare(@NotNull String nodeId, GossipDigest remoteGossipDigest, @NotNull GossipDigest localGossipDigest) throws Exception;

    /**
     * 接收合并远程的同步/交换的数据，需自行在本地实现数据的更新同步合并等
     * @param nodeId
     * @param remoteGossipData
     * @param localGossipDigest maybe null
     * @throws Exception
     */
    void mergeSyncData(@NotNull String nodeId, @NotNull GossipData remoteGossipData, GossipDigest localGossipDigest) throws Exception;

    /**
     * 根据节点id返回版本，优先比较版本大小，再比较版本时间
     * @param nodeId
     * @return
     */
    long getVersion(String nodeId);

    /**
     * 根据节点id返回版本时间
     * @param nodeId
     * @return
     */
    long getVersionTime(String nodeId);

    /**
     * 根据节点id返回详细的版本信息，可以是任何版本信息，业务自定义
     * @param nodeId
     * @return
     */
    String getVersionInfo(String nodeId);

}
