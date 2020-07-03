/*********************************************************************************
 * Copyright (c) 2019 中电健康云科技有限公司
 * 版本      DATE                 BY             REMARKS
 * ----  -----------  ---------------  ------------------------------------------
 * 1.0    2019-12-17      zhoubin           init.
 ********************************************************************************/

package src.main.java.cn.sheva7.daq.light.gossip.core;

import src.main.java.cn.sheva7.daq.light.gossip.event.GossipListener;
import cn.sheva7.daq.light.gossip.model.*;
import src.main.java.cn.sheva7.daq.light.gossip.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author zb
 */
@Slf4j
public class GossipManager {
    private boolean isActive = false;
    private long executeGossipTime = 2000;
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private ScheduledExecutorService doGossipExecutor;
    private ScheduledExecutorService clearExecutor;
    private Map<String, GossipDigest> endpointNodes = new ConcurrentHashMap<>();
    private List<GossipNode> liveNodes = new ArrayList<>();
    private List<GossipNode> deadNodes = new ArrayList<>();
    private Map<GossipNode, CandidateNodeState> candidateNodes = new ConcurrentHashMap<>();
    private GossipSettings settings;
    private GossipNode localGossipNode;
    private String cluster;
    private GossipListener listener;
    private Random random = new Random();
    private static GossipManager instance = new GossipManager();

    private GossipManager() {
    }

    public static GossipManager getInstance() {
        return instance;
    }

    public void init(String nodeId, String cluster, String ipAddress, Integer port, List<GossipSeed> seedNodes, GossipSettings settings, GossipListener listener) {
        this.settings = settings;
        this.cluster = cluster;
        this.localGossipNode = createLocalGossipNode(nodeId, cluster, ipAddress, port);
        this.listener = listener;
        this.settings.setSeedNodes(seedNodes);
        doGossipExecutor = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("gossip-sync-data-pool-%d").daemon(true).build());
        clearExecutor = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("gossip-clear-pool-%d").daemon(true).build());
        fireGossipEvent(localGossipNode, GossipState.JOIN);
    }

    private GossipNode createLocalGossipNode(String nodeId, String cluster, String ipAddress, Integer port) {
        GossipNode node = new GossipNode();
        node.setCluster(cluster);
        node.setIpAddress(ipAddress);
        node.setPort(port);
        node.setId(nodeId);
        node.setState(GossipState.JOIN);
        return node;
    }

    public void start() {
        log.info("Starting gossip! cluster {} ip {} port {} id {}", localGossipNode.getCluster(),
                localGossipNode.getIpAddress(), localGossipNode.getPort(), localGossipNode.getId());
        settings.getMsgService().listen(getSelf().getIpAddress(), getSelf().getPort());
        doGossipExecutor.scheduleAtFixedRate(new GossipTask(), settings.getGossipInterval(), settings.getGossipInterval(), TimeUnit.MILLISECONDS);
        isActive = true;
    }

    public void execOnecImmediately() throws Exception {
        if (isActive) {
            doGossipExecutor.schedule(new GossipTask(), 0, TimeUnit.MILLISECONDS);
        } else {
            throw new Exception("GossipManager not start");
        }
    }

    public GossipData getGossipData(GossipDigest remoteGossipDigest, @NotNull GossipDigest localGossipDigest) throws Exception {
        return new GossipData(localGossipDigest,
                this.settings.getProvider().getSyncDataByCompare(localGossipDigest.getId(), remoteGossipDigest, localGossipDigest));
    }

    public long getGossipDataVersion(String nodeId) {
        return this.settings.getProvider().getVersion(nodeId);
    }

    public long getGossipDataVersionTime(String nodeId) {
        return this.settings.getProvider().getVersionTime(nodeId);
    }

    public String getGossipVersionInfo(String nodeId) {
        return this.settings.getProvider().getVersionInfo(nodeId);
    }

    public List<GossipNode> getLiveNodes() {
        return liveNodes;
    }

    public List<GossipNode> getDeadNodes() {
        return deadNodes;
    }

    public GossipSettings getSettings() {
        return settings;
    }

    public GossipNode getSelf() {
        return localGossipNode;
    }

    public String getSelfId() {
        return localGossipNode.getId();
    }

    public String getSelfIp() {
        return localGossipNode.getIpAddress();
    }

    public boolean isActive() {
        return isActive;
    }

    public Map<String, GossipDigest> getEndpointNodes() {
        return endpointNodes;
    }

    public Map<GossipNode, CandidateNodeState> getCandidateNodes() {
        return candidateNodes;
    }

    public String getCluster() {
        return cluster;
    }

    class GossipTask implements Runnable {

        @Override
        public void run() {
            //周期性自我更新
            try {
                if (isActive) {
                    endpointNodes.put(getSelfId(), new GossipDigest(getSelf(),
                            getGossipDataVersion(getSelfId()), getGossipDataVersionTime(getSelfId()), getGossipVersionInfo(getSelfId())));
                    if (isDiscoverable(getSelf())) {
                        up(getSelf());
                    }
                    if (log.isTraceEnabled()) {
                        long newVersion = endpointNodes.get(getSelfId()).getVersion();
                        log.trace(String.format("Now my heartbeat version is %d", newVersion));
                    }
                    gossip2Node();
                    checkNodeStatus();
                    log.info("live node : " + getLiveNodes());
                    log.info("dead node : " + getDeadNodes());
                    log.info("endpoint : " + getEndpointNodes());
                    log.info("candidate node : " + getCandidateNodes());
                }
            } catch (Exception e) {
                log.error("GossipTask error =>", e);
            }

        }
    }

    private void gossip2Node() {
        List<GossipDigest> digests = new ArrayList<>(this.getEndpointNodes().values());
        if (digests.size() > 0) {
            Collections.shuffle(digests, random);

            String syncMessage = GossipMessage.encodeSyncMessage(digests, getSelf());

            //1. 选择一个live node 发送同步信息     如果发送的节点是种子节点，记录下
            boolean alreadySend2seed = gossip2LiveNode(syncMessage);

            //2. 选择一个dead node 发送同步信息
            gossip2DeadNode(syncMessage);

            //3. 如果没发送给种子节点或者 种子节点数量大于live node，就找出一个种子节点进行同步
            if (!alreadySend2seed || liveNodes.size() <= settings.getSeedNodes().size()) {
                gossip2SeedNode(syncMessage);
            }
        }
    }

    /**
     * send sync message to a live node
     *
     * @param data sync data
     * @return if send to a seed node then return TURE
     */
    private boolean gossip2LiveNode(String data) {
        GossipNode liveNode = getSettings().getNodeSelector().selectOneLiveNode(getLiveNodes());
        send2Node(data, liveNode);
        return (liveNode != null) && getSettings().seedNodesContainsNode(liveNode);
    }

    /**
     * send sync message to a dead node
     *
     * @param data sync data
     */
    private void gossip2DeadNode(String data) {
        GossipNode deadNode = getSettings().getNodeSelector().selectOneDeadNode(getDeadNodes());
        send2Node(data, deadNode);
    }

    /**
     * send sync message to a seed node
     *
     * @param data sync data
     */
    private void gossip2SeedNode(String data) {
        GossipSeed seedNode = getSettings().getNodeSelector().selectOneSeedNode(getSettings().getSeedNodes());
        if (seedNode != null) {
            int size = settings.getSeedNodes().size();
            if (liveNodes.size() == 1) {
                send2SeedNode(data, seedNode);
            } else {
                double prob = size / Double.valueOf(liveNodes.size());
                if (random.nextDouble() < prob) {
                    send2SeedNode(data, seedNode);
                }
            }
        }
    }

    private void send2SeedNode(String data, GossipSeed targetSeedNode) {
        if (data != null && targetSeedNode != null) {
            try {
                settings.getMsgService().sendMsg(targetSeedNode.getIp(), targetSeedNode.getPort(), data);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void send2Node(String data, GossipNode targetNode) {
        if (data != null && targetNode != null) {
            try {
                settings.getMsgService().sendMsg(
                        StringUtils.isNotBlank(targetNode.getConnectedIp()) ? targetNode.getConnectedIp() : targetNode.getIpAddress(),
                        targetNode.getPort(), data);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void checkNodeStatus() {
        try {
            Map<String, GossipDigest> endpoints = getEndpointNodes();
            for (Map.Entry<String, GossipDigest> entry : endpoints.entrySet()) {
                String k = entry.getKey();
                if (!k.equals(this.getSelfId())) {
                    GossipDigest gossipDigest = entry.getValue();
                    long now = DateUtil.currentTimeMillis();
                    long duration = now - gossipDigest.getHeartbeatTime();
                    long convictedTime = convictedTime();
                    log.info("check : " + k + " state : " + gossipDigest.toString() + " duration : " + duration + " convictedTime : " + convictedTime);
                    if (duration > convictedTime && (isAlive(gossipDigest.getEndpoint()) || getLiveNodes().contains(gossipDigest.getEndpoint()))) {
                        downing(gossipDigest);
                    }
                    if (duration <= convictedTime && (isDiscoverable(gossipDigest.getEndpoint()) || getDeadNodes().contains(gossipDigest.getEndpoint()))) {
                        up(gossipDigest.getEndpoint());
                    }
                }
            }
            checkCandidate();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private int convergenceCount() {
        int size = getEndpointNodes().size();
        int count = (int) Math.floor(Math.log10(size) + Math.log(size) + 1);
        return count;
    }

    private long convictedTime() {
        return ((convergenceCount() * (settings.getNetworkDelay() * 3 + executeGossipTime)) << 1) + settings.getGossipInterval();
    }

    private boolean isDiscoverable(GossipNode node) {
        return node.getState() == GossipState.JOIN || node.getState() == GossipState.DOWN;
    }

    private boolean isAlive(GossipNode node) {
        return node.getState() == GossipState.UP;
    }

    public GossipListener getListener() {
        return listener;
    }

    private void fireGossipEvent(GossipNode node, GossipState state) {
        if (getListener() != null) {
            getListener().gossipEvent(node, state);
        }
    }

    private void clearNode(GossipNode node) {
        rwLock.writeLock().lock();
        try {
            endpointNodes.remove(node);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void down(GossipNode node) {
        down(node, false);
    }

    public void down(GossipNode node, boolean clear) {
        log.info("down ===> " + node);
        try {
            rwLock.writeLock().lock();
            node.setState(GossipState.DOWN);
            liveNodes.remove(node);
            if (!deadNodes.contains(node)) {
                deadNodes.add(node);
            }
            if (clear) {
                clearExecutor.schedule(() -> clearNode(node), getSettings().getDeleteThreshold() * getSettings().getGossipInterval(), TimeUnit.MILLISECONDS);
            }
            fireGossipEvent(node, GossipState.DOWN);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void up(GossipNode node) {
        try {
            rwLock.writeLock().lock();
            node.setState(GossipState.UP);
            if (!liveNodes.contains(node)) {
                liveNodes.add(node);
            }
            if (candidateNodes.containsKey(node)) {
                candidateNodes.remove(node);
            }
            if (deadNodes.contains(node)) {
                deadNodes.remove(node);
                if (!node.equals(getSelf())) {
                    log.info("up ===> " + node);
                    fireGossipEvent(node, GossipState.UP);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    private void downing(GossipDigest gossipDigest) {
        log.info("downing ===>" + gossipDigest);
        try {
            if (candidateNodes.containsKey(gossipDigest.getEndpoint())) {
                CandidateNodeState cState = candidateNodes.get(gossipDigest.getEndpoint());
                if (gossipDigest.getHeartbeatTime() == cState.getHeartbeatTime()) {
                    cState.updateCount();
                } else if (gossipDigest.getHeartbeatTime() > cState.getHeartbeatTime()) {
                    candidateNodes.remove(gossipDigest.getEndpoint());
                }
            } else {
                candidateNodes.put(gossipDigest.getEndpoint(), new CandidateNodeState(gossipDigest.getHeartbeatTime()));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void checkCandidate() {
        Set<GossipNode> keys = candidateNodes.keySet();
        for (GossipNode m : keys) {
            if (candidateNodes.get(m).getDowningCount().get() >= convergenceCount()) {
                down(m);
                candidateNodes.remove(m);
            }
        }
    }

    public void applyStateAndData2Local(Map<String, GossipData> newerMap) {
        for (Map.Entry<String, GossipData> entry : newerMap.entrySet()) {
            String nodeId = entry.getKey();
            if (getSelfId().equals(nodeId)) {
                continue;
            }
            GossipDigest localDigest = null;
            if (this.getEndpointNodes().containsKey(nodeId)) {
                localDigest = endpointNodes.get(nodeId);
            }
            GossipData remoteData = entry.getValue();

            if (localDigest != null) {
                long localVersion = localDigest.getVersion();
                long remoteVersion = remoteData.getDigest().getVersion();
                if (remoteVersion > localVersion) {
                    remoteStateAndDataReplaceLocal(nodeId, remoteData, localDigest);
                } else if (remoteVersion == localVersion) {
                    long localVersionTime = localDigest.getVersionTime();
                    long remoteVersionTime = remoteData.getDigest().getVersionTime();
                    if (remoteVersionTime > localVersionTime) {
                        remoteStateAndDataReplaceLocal(nodeId, remoteData, localDigest);
                    } else if (remoteVersionTime == localVersionTime) {
                        //更新心跳时间
                        remoteStateAndDataReplaceLocal(nodeId, remoteData, localDigest);
                    }
                }
            } else {
                remoteStateAndDataReplaceLocal(nodeId, remoteData, null);
            }
        }
    }

    private void remoteStateAndDataReplaceLocal(String nodeId, @NotNull GossipData remoteData, GossipDigest localDigest) {
        if (GossipState.UP.equals(remoteData.getDigest().getEndpoint().getState())) {
            up(remoteData.getDigest().getEndpoint());
        }
        if (GossipState.DOWN.equals(remoteData.getDigest().getEndpoint().getState())) {
            down(remoteData.getDigest().getEndpoint());
        }
        endpointNodes.put(nodeId, remoteData.getDigest());
        if (!remoteData.isOnlyDigest() && remoteData.getData() != null) {
            try {
                //更新gossip provider
                this.getSettings().getProvider().mergeSyncData(remoteData.getDigest().getId(), remoteData, localDigest);
            } catch (Exception e) {
                log.error("setOrUpdateData error, please check ===>", e);
            }
        }
    }

    protected void shutdown() {
        try {
            Thread.sleep(getSettings().getGossipInterval());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String data = GossipMessage.encodeShutdownMessage(getSelf());
        notify2All(data);
        isActive = false;
        doGossipExecutor.shutdown();
        getSettings().getMsgService().unListen();
    }

    /**
     * shutdown消息尽量通知所有live和seed节点
     *
     * @param data
     */
    private void notify2All(String data) {
        List<GossipNode> allLiveList = getSettings().getNodeSelector().selectAllLiveNodes(getLiveNodes());
        if (allLiveList.size() > 0) {
            for (GossipNode liveNode : allLiveList) {
                send2Node(data, liveNode);
            }
        } else {
            List<GossipSeed> allSeedList = getSettings().getNodeSelector().selectAllSeedNodes(getSettings().getSeedNodes());
            for (GossipSeed seedNode : allSeedList) {
                send2SeedNode(data, seedNode);
            }
        }
    }

}
