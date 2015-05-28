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

    private final ZkClient zkClient;

    private String systemName;

    private int instanceId;

    public ZooKeeperServiceRegistry(String zkAddress, String systemName, int instanceId) {
        // 创建 ZooKeeper 客户端
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        this.systemName = systemName;
        this.instanceId = instanceId;
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            LOGGER.debug("create registry node: {}", registryPath);
        }
        // 创建 system 节点（持久）
        String systemPath = registryPath + "/" + systemName;
        if (!zkClient.exists(systemPath)) {
            zkClient.createPersistent(systemPath);
            LOGGER.debug("create system node: {}", systemPath);
        }
        // 创建 instance 节点（持久）
        String instancePath = systemPath + "/" + instanceId;
        if (!zkClient.exists(instancePath)) {
            zkClient.createPersistent(instancePath);
            LOGGER.debug("create instance node: {}", instancePath);
        }
        // 创建 service 节点（持久）
        String servicePath = instancePath + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.debug("create address node: {}", addressNode);
    }
}