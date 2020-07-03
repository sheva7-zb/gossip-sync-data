/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-26      zhoubin           init.
 ********************************************************************************/
package src.main.java.cn.sheva7.daq.light.gossip.net;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-12-26 15:35
 */
public enum MsgServerType {

    /**
     *
     */
    TCP("TCP"),
    /**
     *
     */
    UDP("UDP");

    private String type;

    MsgServerType(String type){
        this.type = type;
    }

    public String type(){
        return this.type;
    }
}
