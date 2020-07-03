/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zb
 */
public class VersionHelper {
    private static AtomicLong v = new AtomicLong(0);
    private static VersionHelper ourInstance = new VersionHelper();

    public static VersionHelper getInstance() {
        return ourInstance;
    }

    private VersionHelper() {
    }

    public long nextVersion() {
        return v.incrementAndGet();
    }
}
