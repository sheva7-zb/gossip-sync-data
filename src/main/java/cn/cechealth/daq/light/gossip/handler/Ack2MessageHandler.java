/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package cn.cechealth.daq.light.gossip.handler;

import cn.cechealth.daq.light.gossip.core.GossipManager;
import cn.cechealth.daq.light.gossip.model.Ack2Message;
import cn.cechealth.daq.light.gossip.model.GossipData;
import cn.cechealth.daq.light.gossip.model.SyncMessage;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author zb
 */
@Slf4j
public class Ack2MessageHandler implements MessageHandler {
    @Override
    public void handle(String cluster, Object data, String from) {
        if(data == null){
            return;
        }
        Ack2Message ack2Message;
        if(data instanceof JSONObject){
            ack2Message = JSONObject.toJavaObject((JSONObject) data, Ack2Message.class);
        }else if(data instanceof SyncMessage){
            ack2Message = (Ack2Message) data;
        }else{
            log.error("error ack2Message! " + data);
            return;
        }
        Map<String, GossipData> yourNewerEndpoints = ack2Message.getNewerMap();
        GossipManager.getInstance().applyStateAndData2Local(yourNewerEndpoints);
    }
}
