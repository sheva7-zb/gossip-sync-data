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
 */
@ToString
public class GossipData implements Serializable {

    private GossipDigest digest;
    private String data;

    /**
     * true表示仅关注摘要，默认false
     */
    private boolean onlyDigest;


    public GossipData(GossipDigest digest, String data) {
        super();
        this.digest = digest;
        this.data = data;
    }
    public GossipData(GossipDigest digest, boolean onlyDigest) {
        super();
        this.digest = digest;
        this.onlyDigest = onlyDigest;
    }

    public GossipData(){
        super();
    }

    public GossipDigest getDigest() {
        return digest;
    }

    public void setDigest(GossipDigest digest) {
        this.digest = digest;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isOnlyDigest() {
        return onlyDigest;
    }

    public void setOnlyDigest(boolean onlyDigest) {
        this.onlyDigest = onlyDigest;
    }

}