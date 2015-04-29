package com.adchina.rpc.registry.zookeeper;

import com.adchina.rpc.registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于 ZooKeeper 的服务注册接口实现
 *
 * @author huangyong
 * @since 1.0.0
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private String zkAddress;

    public ZooKeeperServiceRegistry(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        // 创建 root 节点（持久）
        String rootPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
            LOGGER.debug("create root node: {}", rootPath);
        }
        // 创建 service 节点（永久）
        String servicePath = rootPath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create server node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.debug("create address node: {}", addressNode);
    }
}