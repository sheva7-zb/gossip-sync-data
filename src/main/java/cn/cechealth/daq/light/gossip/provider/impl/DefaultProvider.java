/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-12      zhoubin           init.
 ********************************************************************************/
package cn.cechealth.daq.light.gossip.provider.impl;

import cn.cechealth.daq.light.gossip.model.GossipData;
import cn.cechealth.daq.light.gossip.model.GossipDigest;
import cn.cechealth.daq.light.gossip.provider.GossipDataProvider;
import cn.cechealth.daq.light.gossip.util.DateUtil;
import cn.cechealth.daq.light.gossip.util.VersionHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-12-12 14:15
 */
@Slf4j
public class DefaultProvider implements GossipDataProvider {

    @Override
    public String getSyncDataByCompare(String nodeId, GossipDigest remoteGossipDigest, GossipDigest localGossipDigest) throws Exception {
        return "default data: " + getVersion(nodeId) + ", " + DateUtil.format(getVersionTime(localGossipDigest.getId()));
    }

    @Override
    public void mergeSyncData(String nodeId, GossipData remoteGossipData, GossipDigest localGossipDigest) throws Exception {
        log.trace("mergeSyncData  remoteGossipData :" + remoteGossipData);
    }

    @Override
    public long getVersion(String nodeId) {
        return VersionHelper.getInstance().nextVersion();
    }

    @Override
    public long getVersionTime(String nodeId) {
        return DateUtil.currentTimeMillis();
    }

    @Override
    public String getVersionInfo(String nodeId) {
        return null;
    }
}
