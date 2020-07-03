/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-06      zhoubin           init.
 ********************************************************************************/
package src.main.java.cn.sheva7.daq.light.gossip.test;

/**
 * @Description:
 * @Author: zhoubin
 * @Date: 2019-12-06 09:09
 */
public class MyGossip {

//    public static void main(String[] args) {
//
//        int gossip_port = Integer.parseInt(System.getProperty("gossip.server.port", "50011"));
//
//        String cluster = "gossip_cluster";
//
//        GossipSettings settings = new GossipSettings();
//        settings.setGossipInterval(10000);
//
//        GossipService gossipService = null;
//        try {
//            String myIpAddress = InetAddress.getLocalHost().getHostAddress();
//            List<GossipNode> seedNodes = new ArrayList<>();
//            GossipNode seed = new GossipNode();
//            seed.setId("种子节点16.44：50001");
//            seed.setCluster(cluster);
//            seed.setIpAddress("192.168.16.44");
//            seed.setPort(50001);
//            seedNodes.add(seed);
//
//            gossipService = new GossipService(cluster, myIpAddress, gossip_port , System.getProperty("gossip.server.id", null), seedNodes, settings, (node, state) ->System.out.println("node:" + node + "  state: " + state));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        gossipService.start();
//        gossipService.shutdown();
//    }
}
