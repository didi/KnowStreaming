package com.xiaojukeji.kafka.manager.common.zookeeper;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.exception.ConfigException;
import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ThreadUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author limeng
 * @date 2017/12/22
 */
public class ZkConfigImpl implements ConfigClient, ConnectionStateListener {
    private static final int DEFAULT_SESSION_TIMEOUT_MS = 12000;
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = 3000;
    private static final int DEFAULT_THREAD_POOL_SIZE = Math.max(Runtime.getRuntime().availableProcessors(), 16);

    private final static Logger logger = LoggerFactory.getLogger(ZkConfigImpl.class);

    final byte[] EMPTY = new byte[0];

    /**
     * 监听连接状态
     */
    private final Map<String, java.util.concurrent.locks.Lock> registerLocks = new ConcurrentHashMap<>();
    private Map<String, StateChangeListener> connectionListenerMap = new ConcurrentHashMap<>();
    private Set<StateChangeListener> connectionStateListeners = new HashSet<>();

    /**
     * 监听节点数据变化的缓存
     */
    private final Map<String, java.util.concurrent.locks.Lock> dataPathLocks = new ConcurrentHashMap<>();
    private final Map<String, NodeCache> dataWatchers = new ConcurrentHashMap<>();
    private final Map<String, List<StateChangeListener>> dataListeners = new ConcurrentHashMap<>();

    /**
     * 监听子节点变化的缓存
     */
    private final Map<String, java.util.concurrent.locks.Lock> childrenPathLocks = new ConcurrentHashMap<>();
    private final Map<String, PathChildrenCache> childrenWatcher = new ConcurrentHashMap<>();
    private final Map<String, List<StateChangeListener>> childrenListeners = new ConcurrentHashMap<>();

    /**
     * 所有持有的锁
     */
    private final Map<String, Lock> lockMap = new ConcurrentHashMap<>();

    private final CuratorFramework curator;
    private final ExecutorService executor;

    public ZkConfigImpl(String zkAddress) {
        this(zkAddress, DEFAULT_SESSION_TIMEOUT_MS, DEFAULT_CONNECTION_TIMEOUT_MS);
    }

    public ZkConfigImpl(String zkAddress, int sessionTimeoutMs, int connectionTimeoutMs) {
        this(zkAddress, sessionTimeoutMs, connectionTimeoutMs, DEFAULT_THREAD_POOL_SIZE);
    }

    public ZkConfigImpl(String zkAddress, int sessionTimeoutMs, int connectionTimeoutMs, int threadPoolSize) {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(zkAddress);
        builder.retryPolicy(retryPolicy);
        builder.sessionTimeoutMs(sessionTimeoutMs).connectionTimeoutMs(connectionTimeoutMs);
        curator = builder.build();
        curator.getConnectionStateListenable().addListener(this);
        curator.start();
        executor = Executors.newFixedThreadPool(threadPoolSize, ThreadUtils.newThreadFactory("PathChildrenCache"));
    }

    private synchronized java.util.concurrent.locks.Lock getRegisterLock(String registerPath) {
        registerLocks.putIfAbsent(registerPath, new ReentrantLock());
        return registerLocks.get(registerPath);
    }

    private synchronized java.util.concurrent.locks.Lock getDataPathLock(String dataPath) {
        dataPathLocks.putIfAbsent(dataPath, new ReentrantLock());
        return dataPathLocks.get(dataPath);
    }

    private synchronized java.util.concurrent.locks.Lock getChildrenPathLock(String childrenPath) {
        childrenPathLocks.putIfAbsent(childrenPath, new ReentrantLock());
        return childrenPathLocks.get(childrenPath);
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {

        StateChangeListener.State state;
        switch (newState) {
            case LOST:
                logger.error("[zk] current connection status is {}", newState);
                releaseLocks();
                state = StateChangeListener.State.CONNECTION_DISCONNECT;
                break;
            case CONNECTED:
            case RECONNECTED:
                logger.warn("[zk] current connection status is {}", newState);
                state = StateChangeListener.State.CONNECTION_RECONNECT;
                break;
            default:
                logger.info("[zk] current connection status is {}", newState);
                return;
        }
        for (StateChangeListener listener : connectionListenerMap.values()) {
            listener.onChange(state, null);
        }

        for (StateChangeListener listener : connectionStateListeners) {
            listener.onChange(state, null);
        }
    }

    @Override
    public void addStateChangeListener(StateChangeListener listener) {
        connectionStateListeners.add(listener);
    }

    @Override
    public boolean checkPathExists(String path) throws ConfigException {
        try {
            return curator.checkExists().forPath(path) != null;
        } catch (Exception e) {
            String info = String.format("[zk] Failed to check EXIST for path [%s]", path);
            logger.warn(info);
            throw new ConfigException(e);
        }
    }

    @Override
    public Stat getNodeStat(String path) throws ConfigException {
        try {
            return curator.checkExists().forPath(path);
        } catch (Exception e) {
            String info = String.format("[zk] Failed to get node stat for path [%s]", path);
            logger.warn(info);
            throw new ConfigException(e);
        }
    }

    @Override
    public Stat setNodeStat(String path, String data) throws ConfigException {
        try {
            return curator.setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    @Override
    public Stat setOrCreatePersistentNodeStat(String path, String data) throws ConfigException {
        try {
            return curator.setData().forPath(path, data.getBytes());
        } catch (KeeperException.NoNodeException e) {
            try {
                curator.create().withMode(CreateMode.PERSISTENT).forPath(path);
                return setNodeStat(path, data);
            } catch (KeeperException.NodeExistsException nee) {
                return setNodeStat(path, data);
            } catch (Exception e2) {
                throw new ConfigException(e2);
            }
        } catch (Exception e) {
            throw  new ConfigException(e);
        }
    }

    @Override
    public String createPersistentSequential(String path, String data) throws ConfigException {
        try {
            return curator.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, data.getBytes());
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }
//
//    @Override
//    public <T> void save(String path, T data) throws ConfigException {
//        try {
//            byte[] bytes = EMPTY;
//            if (data != null) {
//                bytes = JSON.toJSONBytes(data);
//            }
//            Stat stat = curator.checkExists().forPath(path);
//            if (stat == null) {
//                curator.create().creatingParentsIfNeeded().forPath(path, bytes);
//            } else {
//                curator.setData().forPath(path, bytes);
//            }
//        } catch (Exception e) {
//            logger.warn("create {} failed", path);
//            throw new ConfigException(e);
//        }
//    }
//
//    @Override
//    public <T> void saveIfNotExisted(String path, T data) throws ConfigException {
//        try {
//            byte[] bytes = EMPTY;
//            if (data != null) {
//                bytes = JSON.toJSONBytes(data);
//            }
//            Stat stat = curator.checkExists().forPath(path);
//            if (stat == null) {
//                curator.create().creatingParentsIfNeeded().forPath(path, bytes);
//            }
//        } catch (Exception e) {
//            logger.warn("create {} failed", path, e);
//            throw new ConfigException(e);
//        }
//    }

//    @Override
//    public <T> void register(final String path, final T data) throws ConfigException {
//        java.util.concurrent.locks.Lock registerLock = getRegisterLock(path);
//        registerLock.lock();
//        try {
//            byte[] bytes = EMPTY;
//            if (data != null) {
//                bytes = JSON.toJSONBytes(data);
//            }
//            if (!connectionListenerMap.containsKey(path)) {
//                connectionListenerMap.put(path, new StateChangeListener() {
//                    @Override
//                    public void onChange(State state, String stateChangePath) {
//                        logger.warn("on state change " + state);
//                        switch (state) {
//                            case CONNECTION_RECONNECT:
//                                try {
//                                    register(path, data);
//                                } catch (ConfigException e) {
//                                    logger.warn("register {} failed", path);
//                                }
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                });
//            }
//            try {
//                deletePath(path);
//                logger.warn("register reconnect delete {} succeed", path);
//            } catch (ConfigException e) {
//                logger.warn("register reconnect delete {} failed", path);
//            }
//            curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, bytes);
//            logger.info("register reconnect create {} succeed", path);
//        } catch (Exception e) {
//            logger.warn("register reconnect create {} failed", path);
//            throw new ConfigException(e);
//        } finally {
//            registerLock.unlock();
//        }
//    }

    @Override
    public <T> T get(String path, Class<T> clazz) throws ConfigException {
        try {
            byte[] bytes = curator.getData().forPath(path);
            return JSON.parseObject(bytes, clazz);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    @Override
    public String get(String path) throws ConfigException {
        try {
            byte[] bytes = curator.getData().forPath(path);
            return new String(bytes);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    @Override
    public void delete(String path) throws ConfigException {
        try {
            connectionListenerMap.remove(path);
            if (curator.checkExists().forPath(path) != null) {
                curator.delete().deletingChildrenIfNeeded().forPath(path);
            }
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

//    private void deletePath(String path) throws ConfigException {
//        try {
//            if (curator.checkExists().forPath(path) != null) {
//                curator.delete().deletingChildrenIfNeeded().forPath(path);
//            }
//        } catch (Exception e) {
//            throw new ConfigException(e);
//        }
//    }

    @SuppressWarnings("all")
    @Override
    public void watch(final String path, final StateChangeListener listener) throws ConfigException {
        java.util.concurrent.locks.Lock dataLock = getDataPathLock(path);
        dataLock.lock();
        try {
            NodeCache nodeCache = dataWatchers.get(path);
            if (nodeCache == null) {
                nodeCache = new NodeCache(curator, path);
                nodeCache.start();
                dataWatchers.put(path, nodeCache);
                nodeCache.getListenable().addListener(new NodeCacheListener() {
                    @Override
                    public void nodeChanged() throws Exception {
                        listener.onChange(StateChangeListener.State.NODE_DATA_CHANGED, path);
                    }
                });
                List<StateChangeListener> listeners = new ArrayList<>();
                listeners.add(listener);
                dataListeners.put(path, listeners);
            } else {
                List<StateChangeListener> listeners = dataListeners.get(path);
                Preconditions.checkState(listeners != null);
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                    nodeCache.getListenable().addListener(new NodeCacheListener() {
                        @Override
                        public void nodeChanged() throws Exception {
                            listener.onChange(StateChangeListener.State.NODE_DATA_CHANGED, path);
                        }
                    });
                }
            }
        } catch (Exception e) {
            throw new ConfigException(e);
        } finally {
            dataLock.unlock();
        }
    }

    @Override
    public List<String> getChildren(String path) throws ConfigException{
        try {
            return curator.getChildren().forPath(path);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    @Override
    public void watchChildren(final String path, final StateChangeListener listener) throws ConfigException {
        java.util.concurrent.locks.Lock childrenLock = getChildrenPathLock(path);
        childrenLock.lock();
        try {
            PathChildrenCache pathChildrenCache = childrenWatcher.get(path);
            if (pathChildrenCache == null) {
                pathChildrenCache = new PathChildrenCache(curator, path, false, false, executor);
                pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                childrenWatcher.put(path, pathChildrenCache);

                pathChildrenCache.getListenable().addListener(new PathChildrenCacheListenerImpl(listener));
                List<StateChangeListener> listeners = new ArrayList<>();
                listeners.add(listener);
                childrenListeners.put(path, listeners);
            } else {
                List<StateChangeListener> listeners = childrenListeners.get(path);
                Preconditions.checkState(listeners != null);
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                    pathChildrenCache.getListenable().addListener(new PathChildrenCacheListenerImpl(listener));
                }
            }
        } catch (Exception e) {
            throw new ConfigException(e);
        } finally {
            childrenLock.unlock();
        }
    }

    @Override
    public void cancelWatchChildren(String path) {
        java.util.concurrent.locks.Lock childrenLock = getChildrenPathLock(path);
        childrenLock.lock();
        try {
            PathChildrenCache pathChildrenCache = childrenWatcher.get(path);
            if (pathChildrenCache != null) {
                try {
                    pathChildrenCache.close();
                } catch (IOException e) {
                    logger.warn("close node cache for path {} error", path, e);
                }
            }
            childrenWatcher.remove(path);
            childrenListeners.remove(path);
        } finally {
            childrenLock.unlock();
        }
    }

    private static class PathChildrenCacheListenerImpl implements PathChildrenCacheListener {
        StateChangeListener listener;

        public PathChildrenCacheListenerImpl(StateChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            String path = event.getData() == null ? null : event.getData().getPath();
            switch (event.getType()) {
                case CHILD_ADDED:
                    listener.onChange(StateChangeListener.State.CHILD_ADDED, path);
                    break;
                case CHILD_UPDATED:
                    listener.onChange(StateChangeListener.State.CHILD_UPDATED, path);
                    break;
                case CHILD_REMOVED:
                    listener.onChange(StateChangeListener.State.CHILD_DELETED, path);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public <T> void lock(String path, long timeoutMS, T t) throws ConfigException {
        try {
            Lock lock = lockMap.get(path);
            if (lock != null) {
                if (lock.isAcquiredInThisProcess()) {
                    return;
                }
                lock.release();
                lockMap.remove(path);
            }
            InterProcessSemaphoreMutex mutex = new InterProcessSemaphoreMutex(curator, path);
            boolean locked = mutex.acquire(timeoutMS, TimeUnit.MILLISECONDS);
            if (!locked) {
                throw new ConfigException("lock " + path + " failed " + timeoutMS);
            }
            if (t != null) {
                curator.setData().forPath(path, JSON.toJSONBytes(t));
            }
            lock = new Lock(mutex, path);
            lockMap.put(path, lock);
        } catch (Exception e) {
            logger.warn("lock {} failed", path, e);
            throw new ConfigException(e);
        }
    }

    @Override
    public void unLock(String path) {
        Lock lock = lockMap.remove(path);
        if (lock != null) {
            lock.release();
        }
    }

    public class Lock {
        InterProcessSemaphoreMutex mutex;
        String path;

        public Lock(InterProcessSemaphoreMutex mutex, String path) {
            this.mutex = mutex;
            this.path = path;
        }

        public void release() {
            lockMap.remove(path);
            try {
                mutex.release();
            } catch (Exception e) {
                logger.warn("release path {} lock error {}", path, e.getMessage());
            }
        }

        public boolean isAcquiredInThisProcess() {
            return mutex.isAcquiredInThisProcess();
        }
    }

    @Override
    public void close() {
        connectionListenerMap.clear();
        connectionStateListeners.clear();
        for (NodeCache nodeCache : dataWatchers.values()) {
            try {
                nodeCache.close();
            } catch (Exception e) {
                logger.warn("close node cache error", e);
            }
        }
        dataWatchers.clear();
        for (PathChildrenCache pathChildrenCache : childrenWatcher.values()) {
            try {
                pathChildrenCache.close();
            } catch (IOException e) {
                logger.warn("close children cache error", e);
            }
        }
        childrenWatcher.clear();
        releaseLocks();
        curator.close();
        executor.shutdown();
    }

    private void releaseLocks() {
        for (Lock lock : lockMap.values()) {
            lock.release();
        }
        lockMap.clear();
    }
}
