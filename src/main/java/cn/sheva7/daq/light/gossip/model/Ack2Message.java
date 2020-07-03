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
 * 本地收到AckMessage并处理后，将本地更新的（数据）再发出去
 */
@ToString
public class Ack2Message implements Serializable {

    private Map<String, GossipData> newerMap;

    public Ack2Message(){
        super();
    }

    public Ack2Message(Map<String, GossipData> newerMap) {
        super();
        this.newerMap = newerMap;
    }

    public Map<String, GossipData> getNewerMap() {
        return newerMap;
    }

    public void setNewerMap(Map<String, GossipData> newerMap) {
        this.newerMap = newerMap;
    }


}
