/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-31      zhoubin           init.
 ********************************************************************************/
package cn.cechealth.daq.light.gossip.net;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-12-31 11:13
 */
public class VertxContext {

    private static Vertx vertx = null;

    public synchronized static Vertx vertx(){
        if(vertx == null){
            vertx = Vertx.vertx();
        }
        return vertx;
    }

    public static void close(Handler<AsyncResult<Void>> var1){
        if(vertx != null){
            vertx.close(var1);
        }
    }

    public static void close(){
        if(vertx != null){
            vertx.close();
        }
    }
}
