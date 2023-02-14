/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.kafka.server;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;
import com.googlecode.concurrentlinkedhashmap.Weighers;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;
import sun.nio.ch.FileChannelImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OSUtil {

    private static final Logger log = LoggerFactory.getLogger(OSUtil.class);
    public static final String DEFAULT_DISKNAME = "alldisks";

    //test may be call start many times
    private static boolean started = false;

    enum OS {
        Others,
        Mac,
        Linux
    };

    //os type
    private OS osType;
    // all used disk by configuration of log.dir
    private List<DiskAndMountpoint> allDisks;
    // the disks io stat that read from /proc/diskstats by last time
    private HashMap<String, DiskStats> lastAllDiskStats;
    // the current disks's status that compute by current disk stat and last disk stat in one cycle
    private HashMap<String, DiskStatus> allDiskStatus;

    private ScheduledExecutorService executorService;

    private boolean supportIoUtil;
    private int ioUtilFrequncy = 1000;

    //Cache all the file's page info.
    private PageInfoCache pageInfoCache;
    //os page size
    private int pageSize;

    private static class OSUtilHolder {
        private static final OSUtil INSTANCE = new OSUtil();
    }

    public static OSUtil instance() {
        return OSUtilHolder.INSTANCE;
    }

    synchronized public static void start(List<String> dirs, ScheduledExecutorService executorService) throws Exception {
        if (started) return;
        OSUtilHolder.INSTANCE.init(dirs, executorService);
        started = true;
    }

    private OSUtil() {
        initOSType();
        allDisks = new ArrayList<>();
    }

    private void init(List<String> dirs, ScheduledExecutorService executorService) throws Exception {
        //init used disk by config : log.dirs
        initUsedDisks(dirs);

        //try to init iostat from os,if yes start schedule for monitor iostat
        initDiskStats();
        this.executorService = executorService;
        if (osType == OS.Linux && supportIoUtil) {
            pageSize = getNativePagesize();
            pageInfoCache = new PageInfoCache(pageSize, 3000);

            executorService.schedule(() -> {
                try {
                    computeDiskStatusForLinux();
                } catch (Exception e) {
                    log.error("compute disk status exception: ", e);
                }
            }, ioUtilFrequncy, TimeUnit.MILLISECONDS);
        }
    }

    private void initOSType() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("linux")) {
            osType = OS.Linux;
        } else if (os.toLowerCase().startsWith("mac")) {
            osType = OS.Mac;
        } else {
            osType = OS.Others;
        }
        log.info("current os: {}", os);
    }

    private void initUsedDisks(List<String> dirs) throws Exception {
        if (osType == OS.Linux) {
            allDisks = readDiskInfoForLinux(dirs);
        } else if (osType == OS.Mac) {
            allDisks = readDiskInfoForMac(dirs);
        }
        log.info("all used disks :" + allDisks.stream().
                map(DiskAndMountpoint::toString).collect(Collectors.joining(",")));
    }

    private void initDiskStats() {
        allDiskStatus = new HashMap<>();
        for (DiskAndMountpoint diskAndMountpoint : allDisks) {
            allDiskStatus.put(diskAndMountpoint.diskName, new DiskStatus());
        }
        if (osType == OS.Linux) {
            HashMap<String, DiskStats> currentDiskStats = readDiskStatsForLinux();
            if (currentDiskStats != null) {
                lastAllDiskStats = currentDiskStats;
                //iostat should contain used disks
                if (lastAllDiskStats.keySet().containsAll(allDiskStatus.keySet())) {
                    supportIoUtil = true;
                }
            }
        }
        log.info("current system is {} support io status", supportIoUtil ? "" : "not");
    }

    double getIoUtil(String diskname) {
        assert allDiskStatus != null;
        DiskStatus diskStatus = allDiskStatus.get(diskname);
        return diskStatus.ioUtil;
    }

    public HashMap<String, DiskStatus> getAllDiskStatus() {
        assert allDiskStatus != null;
        return allDiskStatus;
    }

    boolean supportCheckFileInCache() {
        return supportIoUtil;
    }

    boolean supportIoUtil() {
        return supportIoUtil;
    }

    //check the read file in page cache for give offset and length
    boolean isCached(String filename, long offset, int length) {
        return pageInfoCache.inPageCache(filename, offset, length);
    }

    void loadPageCache(String filename, long offset, int length) {
        if (supportIoUtil) {
            pageInfoCache.loadPageCache(filename, offset, length);
        }
    }


    public String getDiskName(String filename) {
        assert filename != null && !filename.isEmpty();
        if (osType == OS.Linux || osType == OS.Mac) {
            for (DiskAndMountpoint diskAndMountpoint : allDisks) {
                if (filename.startsWith(diskAndMountpoint.mountpoint)) {
                    return diskAndMountpoint.diskName;
                }
            }
        }
        return DEFAULT_DISKNAME;
    }

    List<String> getAllDisks() {
        List<String> allDiskNames = new ArrayList<>();
        allDisks.forEach(diskAndMountpoint -> {
            allDiskNames.add(diskAndMountpoint.diskName);
        });
        return allDiskNames;
    }

    private List<DiskAndMountpoint> readDiskInfoForLinux(List<String> dirs) throws Exception {
        List<DiskAndMountpoint> diskMountPoints = new ArrayList<>();
        String result = execSystemCommand("df");
        String reg = "^/dev/([\\w]+)\\d\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+([^\\s]+)$";
        Pattern pattern = Pattern.compile(reg, Pattern.MULTILINE);
        assert result != null;
        Matcher matcher = pattern.matcher(result);
        DiskAndMountpoint root = new DiskAndMountpoint(DEFAULT_DISKNAME, "/");
        while (matcher.find()) {
            String mountDir = matcher.group(2);
            String diskName = matcher.group(1);
            if ("/".equals(mountDir)) {
                root = new DiskAndMountpoint(diskName, mountDir);
                continue;
            }
            dirs.forEach(dir -> {
                if (dir.startsWith(mountDir)) {
                    diskMountPoints.add(new DiskAndMountpoint(matcher.group(1), matcher.group(2)));
                }
            });
        }
        if (diskMountPoints.isEmpty()) {
            diskMountPoints.add(root);
        }
        diskMountPoints.sort((x, y) -> y.mountpoint.length() - x.mountpoint.length());
        assert !diskMountPoints.isEmpty();
        return diskMountPoints;
    }


    private List<DiskAndMountpoint> readDiskInfoForMac(List<String> dirs) throws Exception {
        List<DiskAndMountpoint> diskMountPoints = new ArrayList<>();
        String result = execSystemCommand("df");
        String reg = "^/dev/([\\w]+)\\d\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+([^\\s]+)$";
        Pattern pattern = Pattern.compile(reg, Pattern.MULTILINE);
        assert result != null;
        Matcher matcher = pattern.matcher(result);
        DiskAndMountpoint root = new DiskAndMountpoint(DEFAULT_DISKNAME, "/");
        while (matcher.find()) {
            String mountDir = matcher.group(2);
            String diskName = matcher.group(1);
            if ("/".equals(mountDir)) {
                root = new DiskAndMountpoint(diskName, mountDir);
                continue;
            }
            dirs.forEach(dir -> {
                if (dir.startsWith(mountDir)) {
                    diskMountPoints.add(new DiskAndMountpoint(matcher.group(1), matcher.group(2)));
                }
            });
        }
        if (diskMountPoints.isEmpty()) {
            diskMountPoints.add(root);
        }
        diskMountPoints.sort((x, y) -> y.mountpoint.length() - x.mountpoint.length());
        assert !diskMountPoints.isEmpty();
        return diskMountPoints;
    }

    private HashMap<String, DiskStats> readDiskStatsForLinux() {
        try {
            HashMap<String, DiskStats> allDiskStats = new HashMap<>();
            String result = readFile("/proc/diskstats");
            String[] lines = result.split("\n");
            for (String line : lines) {
                String[] elments = line.substring(13).split(" ");
                String diskName = elments[0];
                if (allDiskStatus.containsKey(diskName)) {
                    long read = Long.parseLong(elments[1]);
                    long readBytes = Long.parseLong(elments[3]) * 512;
                    long write = Long.parseLong(elments[5]);
                    long writeBytes = Long.parseLong(elments[7]) * 512;
                    long ioTime = Long.parseLong(elments[10]);
                    DiskStats diskStats = new DiskStats(read, readBytes, write, writeBytes, ioTime);
                    allDiskStats.put(diskName, diskStats);
                }
            }
            return allDiskStats;
        } catch (Exception e) {
            log.error("read disk status exception: ", e);
        }
        return null;
    }

    private void computeDiskStatusForLinux() {
        HashMap<String, DiskStats> currentDiskStats = readDiskStatsForLinux();
        assert currentDiskStats != null;
        assert currentDiskStats.size() == allDiskStatus.size();

        for (Map.Entry<String, DiskStatus> entry: allDiskStatus.entrySet()) {
            String diskname = entry.getKey();
            DiskStats now = currentDiskStats.get(diskname);
            if (now == null) {
                now = new DiskStats();
            }
            DiskStats old = lastAllDiskStats.get(diskname);
            now.computeDiskStatus(old, entry.getValue());
        }
        lastAllDiskStats = currentDiskStats;

        executorService.schedule(() -> {
            try {
                computeDiskStatusForLinux();
            } catch (Exception e) {
                log.error("compute disk status exception: ", e);
            }
        }, ioUtilFrequncy, TimeUnit.MILLISECONDS);
    }

    public void setIoUtilFrequency(int ioUtilFrequncy) {
        this.ioUtilFrequncy = ioUtilFrequncy;
    }

    //for testing
    public void dropPageCache()  {
        if (osType == OS.Linux)
            execSystemCommand("echo 1 > /proc/sys/vm/drop_caches");
    }

    private String execSystemCommand(String command) {
        try {
            String[] cmd = {"/bin/sh", "-c", command};
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = process.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            String resutl = new String(bytes, Charset.defaultCharset());
            inputStream.close();

            inputStream = process.getErrorStream();
            bytes = inputStream.readAllBytes();
            String error = new String(bytes, Charset.defaultCharset());
            log.info("command:{}\nstdout:\n{}stderr:\n{}", command, resutl, error);
            inputStream.close();
            return resutl;
        } catch (Exception e) {
            log.error("execute system command:{}\nexception:\n{}", command, e);
        }
        return null;
    }


    private String readFile(String filename) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(filename));
            byte[] bytes = inputStream.readAllBytes();
            String resutl = new String(bytes, Charset.defaultCharset());
            inputStream.close();
            return resutl;
        } catch (Exception e) {
            log.error("read file {} exception: ", filename, e);
        }
        return "";
    }

    static class DiskAndMountpoint {
        String diskName;
        String mountpoint;

        DiskAndMountpoint(String diskName, String mountpoint) {
            this.diskName = diskName;
            this.mountpoint = mountpoint;
        }

        @Override
        public String toString() {
            return String.format("[%s,%s]", diskName, mountpoint);
        }
    }

    static class DiskStatus {
        private double ioUtil;
        private double readPerSecond;
        private double writePerSecond;
        private double readBytesPerSecond;
        private double writeBytesPerSecond;

        @Override
        public String toString() {
            return String.format("[%f,%f,%f,%f,%f]",
                    readPerSecond, readBytesPerSecond, writePerSecond, writeBytesPerSecond, ioUtil);
        }
    }

    static class DiskStats {
        private long read;
        private long readBytes;
        private long write;
        private long writeBytes;
        private long ioTime;
        private long timestamp;

        public DiskStats() {
        }

        public DiskStats(long read, long readBytes, long write, long writeBytes, long ioTime) {
            this.read = read;
            this.readBytes = readBytes;
            this.write = write;
            this.writeBytes = writeBytes;
            this.ioTime = ioTime;
            this.timestamp = System.currentTimeMillis();
        }

        public void computeDiskStatus(DiskStats old, DiskStatus ds) {
            double time = (timestamp - old.timestamp) / 1000.0;
            if (time == 0) return;

            ds.readPerSecond = (read - old.read) / time;
            ds.readBytesPerSecond = (readBytes - old.readBytes) / time;
            ds.writePerSecond = (write - old.write) / time;
            ds.writeBytesPerSecond = (writeBytes - old.writeBytes) / time;
            ds.ioUtil = ((double) ioTime - old.ioTime) / (timestamp - old.timestamp);
        }

        @Override
        public String toString() {
            return String.format("[%d,%d,%d,%d,%d]",
                    read, readBytes, write, writeBytes, ioTime);
        }
    }

    // This is the idiomatic JNA way of dealing with native code
    // it is also possible to use native JNA methods
    // and alternatively JNI
    public interface CLibray extends Library {
        CLibray INSTANCE = (CLibray) Native.load("c", CLibray.class);

        // int mincore(void *addr, size_t length, unsigned char *vec);
        // JNA will automagically convert the parameters to native parameters
        int mincore(Pointer addr, long length, Pointer vec);
    }

    private static int getNativePagesize() throws Exception {
        // we can do this with reflection
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        return unsafe.pageSize();
    }

    public static void unmap(final MappedByteBuffer buffer) throws Exception {
        if (null != buffer) {
            final Method method = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
            method.setAccessible(true);
            method.invoke(null, buffer);
        }
    }

    // This class used to lookup the read data of the file is in the pagecahe or not
    // the data in cache means the most of pages for read data is in the page cache.
    private class PageInfo {

        private static final int RefreshFileSize = 100 * 1024 * 1024;
        private static final int RefreshTimeMs = 30 * 1000;
        private final String filename;
        private final FileChannel fc;
        private MappedByteBuffer mmaped;
        private AtomicBoolean inRefresh = new AtomicBoolean(false);
        private ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();

        // the array indicate each page of the file is in pagecache or not.
        private byte[] pageInfo = new byte[0];
        // page info load timestamp
        private long timestamp = 0;
        private long filesize = -RefreshFileSize;

        public PageInfo(String filename) throws Exception {
            // calls mmap
            this.filename = filename;
            fc = FileChannel.open(Paths.get(filename), StandardOpenOption.READ);
            refreshPageInfo();
        }

        public void close() throws Exception {
           fc.close();
           unmap(mmaped);
        }

        synchronized void refreshPageInfo() throws Exception {
            if (System.currentTimeMillis() - timestamp < RefreshTimeMs) {
                return;
            }

            ReentrantReadWriteLock.WriteLock lock = rwlock.writeLock();
            lock.lock();
            try {
                long size = fc.size();
                if (size - filesize >= RefreshFileSize) {
                    unmap(mmaped);
                    mmaped = fc.map(FileChannel.MapMode.READ_ONLY, 0, size);
                    filesize = size;
                }
            } finally {
                lock.unlock();
            }

            // we need to prepare the vec array that will hold the results
            // the vec must point to an array containing at least (length+PAGE_SIZE-1) / PAGE_SIZE bytes
            int pages = (int) ((filesize + (pageSize - 1)) / pageSize);
            Memory vec = new Memory(pages);
            // do the mincore call using JNA (we could just as well used JNI)
            Pointer mapPointer = Native.getDirectBufferPointer(mmaped);
            int result = CLibray.INSTANCE.mincore(mapPointer, filesize, vec);
            if (result != 0) {
                throw new RuntimeException("Call to mincore failed with return value " + result);
            }
            if (pageInfo.length == pages) {
                vec.read(0, pageInfo, 0, pages);
            } else {
                pageInfo = vec.getByteArray(0, pages);
            }
            log.debug("load file {} page info, {}", filename, this);
            timestamp = System.currentTimeMillis();
            inRefresh.set(false);
        }

        boolean inPageCache(long offset, int length, int pagesize) {
            if (System.currentTimeMillis() - timestamp > RefreshTimeMs &&
                    !inRefresh.compareAndExchange(false, true)) {
                executorService.execute(() -> {
                    try {
                        refreshPageInfo();
                    } catch (Exception e) {
                        log.error("refresh pageinfo exception: ", e);
                    }
                });
            }
            byte[] currentPageInfo = pageInfo;
            int start = (int) (offset / pagesize);
            int end = (int) (offset + length) / pagesize;
            int tail = Math.min(end, currentPageInfo.length);
            int total = end - start;
            int outMemoryCount = 0;
            for (int i = start; i < tail; i++) {
                if (currentPageInfo[i] != 1) {
                    outMemoryCount++;
                }
            }
            // the 80% pages in page cache should regconize in cache
            return (double) outMemoryCount / total <= 0.2;
        }

        void loadPageCache(long offset, int length, int pagesize) throws Exception {
            ReentrantReadWriteLock.ReadLock lock = rwlock.readLock();
            try {
                lock.lock();
                long end = Math.min(offset + length, filesize);
                for (int i = (int) offset; i < end; i += pagesize) {
                    mmaped.get(i);
                }
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String toString() {
            int count = 0;
            for (byte b : pageInfo) {
                if (b == 1) {
                    count++;
                }
            }
            return String.format("file size: %d, total pages: %d, in cache pages: %d, rate: %f",
                    filesize, pageInfo.length, count, count * 1.0 / pageInfo.length);
        }
    }

    // This class cached maximum pageinfos by filename. the fileinfo record the status of file pages in pagecache
    // the life cycle of pageinfo is 30 seconds. when someone detect it has expired. it should reload the pageinfo.
    // the cache use lru cache, when the lru cache is full. it drop the Least recently used entry from cache and load new one.
    private class PageInfoCache {

        private ConcurrentLinkedHashMap<String, PageInfo> pageInfos;
        private int pagesize;

        PageInfoCache(int pagesize, int maxSize) {
            pageInfos = new ConcurrentLinkedHashMap.Builder<String, PageInfo>().
                    maximumWeightedCapacity(maxSize).weigher(Weighers.singleton()).
                    listener(new EvictionListener<String, PageInfo>() {
                        @Override
                        public void onEviction(String key, PageInfo value) {
                            try {
                                value.close();
                            } catch (Exception e) {
                                log.error("close pageinfo exception: ", e);
                            }
                        }
                    }).build();
            this.pagesize = pagesize;
        }

        boolean inPageCache(String filename, long offset, int length) {
            try {
                PageInfo pageInfo = pageInfos.get(filename);
                if (pageInfo == null) {
                    pageInfo = getPageInfo(filename);
                }
                return pageInfo.inPageCache(offset, length, pagesize);
            } catch (Exception e) {
                log.error("check inCache exception: ", e);
                return false;
            }
        }

        void loadPageCache(String filename, long offset, int length) {
            try {
                PageInfo pageInfo = pageInfos.get(filename);
                if (pageInfo == null) {
                    pageInfo = getPageInfo(filename);
                }
                pageInfo.loadPageCache(offset, length, pagesize);
            } catch (Exception e) {
                log.error("load pagecache exception: ", e);
            }
        }

        synchronized private PageInfo getPageInfo(String filename) throws Exception {
            //try to get pageinfo from cache again. it maybe get it from other thread loaded.
            PageInfo pageInfo = pageInfos.get(filename);
            if (pageInfo != null) {
                return pageInfo;
            } else {
                pageInfo = new PageInfo(filename);
                pageInfos.put(filename, pageInfo);
            }
            return pageInfo;
        }
    }
}
