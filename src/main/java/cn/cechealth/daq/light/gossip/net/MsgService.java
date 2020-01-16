/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package cn.cechealth.daq.light.gossip.net;

import java.io.IOException;

/**
 * @author zb
 */
public interface MsgService {
    void listen(String ipAddress, int port);

    void handleMsg(String data);

    void sendMsg(String targetIp, Integer targetPort, String data) throws IOException;

    void unListen();
}
