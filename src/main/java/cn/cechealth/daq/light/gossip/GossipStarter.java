//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.cechealth.daq.light.gossip;

import cn.cechealth.daq.light.gossip.algorithm.impl.DefaultGossipNodeSelector;
import cn.cechealth.daq.light.gossip.core.GossipService;
import cn.cechealth.daq.light.gossip.core.GossipSettings;
import cn.cechealth.daq.light.gossip.event.GossipListener;
import cn.cechealth.daq.light.gossip.model.GossipSeed;
import cn.cechealth.daq.light.gossip.net.MsgServerType;
import cn.cechealth.daq.light.gossip.net.tcp.TCPMsgServiceImpl;
import cn.cechealth.daq.light.gossip.provider.impl.DefaultProvider;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GossipStarter {
    private static final Logger log = LoggerFactory.getLogger(GossipStarter.class);

    public GossipStarter() {
    }

    public static void main(String[] args) throws Exception {
        GossipStarter starter = new GossipStarter();
        starter.startGossip();
    }

    public void startGossip() throws Exception {
        String nodeId = "abc";
        String cluster = "my_gossip_cluster";
        String myIpAddress = InetAddress.getLocalHost().getHostAddress();
        int port = 50000;
        List<GossipSeed> seedNodes = new ArrayList();
        GossipSeed seed = new GossipSeed();
        seed.setCluster("my_gossip_cluster");
        seed.setIp("192.168.7.62");
        seed.setPort(50000);
        seedNodes.add(seed);
        GossipService.init(nodeId, cluster, myIpAddress, port, seedNodes, this.createGossipSetting(), (node, state) -> {
            log.info("node:" + node + "  state: " + state);
        });
        GossipService.start();
    }

    private GossipSettings createGossipSetting() {
        int gossipInterval = 10000;
        int networkDelay = 2000;
        String msgServerType = "UDP";
        GossipSettings settings = new GossipSettings();
        settings.setGossipInterval(gossipInterval);
        settings.setNetworkDelay(networkDelay);
        settings.setProvider(new DefaultProvider());
        settings.setNodeSelector(new DefaultGossipNodeSelector());
        if (MsgServerType.TCP.type().equals(msgServerType)) {
            settings.setMsgService(new TCPMsgServiceImpl());
        }
        return settings;
    }
}
