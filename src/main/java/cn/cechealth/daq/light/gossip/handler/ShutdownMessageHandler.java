/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package cn.cechealth.daq.light.gossip.handler;

import cn.cechealth.daq.light.gossip.core.GossipManager;
import cn.cechealth.daq.light.gossip.model.GossipNode;
import cn.cechealth.daq.light.gossip.model.SyncMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zb
 */
@Slf4j
public class ShutdownMessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, Object data, String from) {
        if(data == null){
            return;
        }
        log.trace("receive shutdown msg data: {}", data);
        GossipNode whoShutdown;
        if(data instanceof JSONObject){
            whoShutdown = JSONObject.toJavaObject((JSONObject) data, GossipNode.class);
        }else if(data instanceof SyncMessage){
            whoShutdown = (GossipNode) data;
        }else{
            log.error("error whoShutdown! " + data);
            return;
        }
        if (whoShutdown != null) {
            //主动down的需要清除掉endpoint
            GossipManager.getInstance().down(whoShutdown, true);
        }
    }
}
