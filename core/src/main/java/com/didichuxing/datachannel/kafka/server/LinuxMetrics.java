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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//This class read system status for /proc file system in linux operating system
// disk status read by /proc/diskstats
// networking status read by /proc/net/dev
// memory status read by /proc/meminfo
// cpu load status read by /proc/loadavg
// cpu usage status read by /proc/stat
// use "man proc" can lookup the more information for these files
public class LinuxMetrics extends OSMetrics {

    private static final Logger log = LoggerFactory.getLogger(LinuxMetrics.class);

    private ScheduledExecutorService executorService;

    // disk
    private RandomAccessFile diskstatsFile;
    // networking
    private RandomAccessFile netFile;
    // load avgerage
    private RandomAccessFile loadavgFile;
    // memory
    private RandomAccessFile meminfoFile;
    // cpu
    private RandomAccessFile statFile;

    // the disks io stat that read from /proc/diskstats by last time
    private HashMap<String, DiskStats> lastAllDiskStats = new HashMap<>();

    // the net io stat that read from /proc/net/dev by last time
    private HashMap<String, NetStats> lastAllNetStats = new HashMap<>();

    // the cpu stat that read from /proc/loadavg  and by last time
    private CpuStats lastCpuStats = new CpuStats();


    // the current disks status that compute by current disk stat and last disk stat in one cycle
    private HashMap<String, DiskStatus> allDiskStatus = new HashMap<>();

    // the current net status that compute by current net stat and last net stat in one cycle
    private HashMap<String, NetStatus> allNetStatus =  new HashMap<>();

    // the current memory status
    private MemoryStatus memoryStatus = new MemoryStatus();

    // the current cpu status that compute by current cpu stat and last cpu stat in one cycle
    private CpuStatus cpuStatus = new CpuStatus();

    public LinuxMetrics(ScheduledExecutorService executorService) throws Exception {
        this.executorService = executorService;

        //open /proc files
        diskstatsFile = new RandomAccessFile("/proc/diskstats", "r");
        netFile = new RandomAccessFile("/proc/net/dev", "r");
        loadavgFile = new RandomAccessFile("/proc/loadavg", "r");
        meminfoFile = new RandomAccessFile("/proc/meminfo", "r");
        statFile = new RandomAccessFile("/proc/stat", "r");

        updateSystemStatus();
    }

    private HashMap<String, DiskStats> readDiskStats() {
        try {
            HashMap<String, DiskStats> allDiskStats = new HashMap<>();
            String line  = null;
            diskstatsFile.seek(0);
            while (null != (line = diskstatsFile.readLine())) {
                String[] elments = line.trim().split("\\s+");
                String diskName = elments[2];
                if (!diskName.startsWith("sd") || diskName.length() != 3) {
                    continue;
                }
                long read = Long.parseLong(elments[3]);
                long readBytes = Long.parseLong(elments[5]) * 512;
                long write = Long.parseLong(elments[7]);
                long writeBytes = Long.parseLong(elments[9]) * 512;
                long ioTime = Long.parseLong(elments[12]);
                DiskStats diskStats = new DiskStats(read, readBytes, write, writeBytes, ioTime);
                allDiskStats.put(diskName, diskStats);
            }
            return allDiskStats;
        } catch (Exception e) {
            log.error("read disk status exception: ", e);
        }
        return null;
    }

    private HashMap<String, NetStats> readNetStats() {
        try {
            HashMap<String, NetStats> allNetStats = new HashMap<>();
            String line  = null;
            netFile.seek(0);
            int lineNum = 0;
            while (null != (line = netFile.readLine())) {
                lineNum++;
                if (lineNum > 2) {
                    String[] elments = line.trim().split("\\s+");
                    String name = elments[0].replace(":", "");
                    if (!name.startsWith("eth")) {
                        continue;
                    }
                    long recvBytes = Long.parseLong(elments[1]);
                    long sendBytes = Long.parseLong(elments[9]);
                    NetStats netStats = new NetStats(sendBytes, recvBytes);
                    allNetStats.put(name, netStats);
                }
            }
            return allNetStats;
        } catch (Exception e) {
            log.error("read net status exception: ", e);
        }
        return null;
    }


    private void updateDiskStatus() {
        HashMap<String, DiskStats> allDiskStats = readDiskStats();
        assert allDiskStats != null;
        for (Map.Entry<String, DiskStats> entry: allDiskStats.entrySet()) {
            String diskname = entry.getKey();
            DiskStats old = lastAllDiskStats.get(diskname);
            if (old == null) {
                old = new DiskStats();
            }
            DiskStatus ds = allDiskStatus.get(diskname);
            if (ds != null) {
                allDiskStatus.put(diskname, entry.getValue().computeDiskStatus(old));
            } else {
                allDiskStatus.put(diskname, new DiskStatus());
            }
        }
        lastAllDiskStats = allDiskStats;
    }

    private void updateNetStatus() {
        HashMap<String, NetStats> allNetStats = readNetStats();
        assert allNetStats != null;
        for (Map.Entry<String, NetStats> entry: allNetStats.entrySet()) {
            String ethName = entry.getKey();
            NetStats old = lastAllNetStats.get(ethName);
            if (old == null) {
                old = new NetStats();
            }
            NetStatus ds = allNetStatus.get(ethName);
            if (ds != null) {
                allNetStatus.put(ethName, entry.getValue().computeNetStatus(old));
            } else {
                allNetStatus.put(ethName, new NetStatus());
            }
        }
        lastAllNetStats = allNetStats;
    }


    private void updateMemoryStatus() {
        try {
            String line  = null;
            meminfoFile.seek(0);
            line = meminfoFile.readLine(); //total
            String[] elments = line.split("\\s+");
            memoryStatus.total = Long.parseLong(elments[1]) / 1024.0;

            line = meminfoFile.readLine(); //free
            elments = line.split("\\s+");
            memoryStatus.free = Long.parseLong(elments[1]) / 1024.0;

            line = meminfoFile.readLine(); //buffer
            elments = line.split("\\s+");
            memoryStatus.buffer = Long.parseLong(elments[1]) / 1024.0;

            line = meminfoFile.readLine(); //cached
            elments = line.split("\\s+");
            memoryStatus.cached = Long.parseLong(elments[1]) / 1024.0;
            meminfoFile.readLine(); //swap cached

            line = meminfoFile.readLine(); //active
            elments = line.split("\\s+");
            memoryStatus.used = Long.parseLong(elments[1]) / 1024.0;

            line = meminfoFile.readLine(); //inactive
            elments = line.split("\\s+");
            memoryStatus.used += Long.parseLong(elments[1]) / 1024.0;
        } catch (Exception e) {
            log.error("read memroy info exception: ", e);
        }
    }

    private void updateCpuStatus() {
        try {
            String line  = null;
            loadavgFile.seek(0);
            line = loadavgFile.readLine();
            String[] elments = line.split("\\s+");
            cpuStatus.load = Double.parseDouble(elments[0]);
            cpuStatus.load5 = Double.parseDouble(elments[1]);
            cpuStatus.load15 = Double.parseDouble(elments[2]);

            CpuStats now  = readCpuStats();
            assert now != null;
            CpuStatus cs = now.computeCpuStatus(lastCpuStats);
            cpuStatus.iowaitPercent = cs.iowaitPercent;
            cpuStatus.userPercent = cs.userPercent;
            cpuStatus.idlePercent = cs.idlePercent;
            cpuStatus.sysPercent = cs.sysPercent;
            lastCpuStats = now;
        } catch (Exception e) {
            log.error("read cpu info exception: ", e);
        }
    }

    private CpuStats readCpuStats() {
        try {
            CpuStats cpuStats = new CpuStats();
            statFile.seek(0);
            String line = statFile.readLine();
            String[] elments = line.split("\\s+");
            cpuStats.user = Long.parseLong(elments[1]);
            cpuStats.sys = Long.parseLong(elments[3]);
            cpuStats.idle = Long.parseLong(elments[4]);
            cpuStats.iowait = Long.parseLong(elments[5]);
            elments[0] = "0";
            cpuStats.total = Arrays.stream(elments).mapToLong(Long::parseLong).sum();
            return cpuStats;
        } catch (Exception e) {
            log.error("read cpu info exception: ", e);
        }
        return null;
    }

    void updateSystemStatus() {
        updateCpuStatus();
        updateMemoryStatus();
        updateDiskStatus();
        updateNetStatus();

        executorService.schedule(() -> {
            try {
                updateSystemStatus();
            } catch (Exception e) {
                log.error("read system status exception: ", e);
            }
        }, 1, TimeUnit.SECONDS);
    }

    public CpuStatus getCupStatus() {
        return cpuStatus;
    }

    public MemoryStatus getMemoryStatus() {
        return memoryStatus;
    }

    public NetStatus getNetStatus(String name) {
        return allNetStatus.get(name);
    }

    public HashMap<String, NetStatus> getAllNetStatus() {
        return allNetStatus;
    }

    public DiskStatus getDiskStatus(String name) {
        return allDiskStatus.get(name);
    }

    public HashMap<String, DiskStatus> getAllDiskStatus() {
        return allDiskStatus;
    }

    static class CpuStats {
        public long user;
        public long sys;
        public long idle;
        public long iowait;
        public long total;

        public CpuStatus computeCpuStatus(CpuStats old) {
            CpuStatus cs = new CpuStatus();
            double totalCpu = total - old.total;
            if (totalCpu == 0) {
                return cs;
            }
            cs.userPercent = (user - old.user) / totalCpu;
            cs.idlePercent = (idle - old.idle) / totalCpu;
            cs.sysPercent = (sys - old.sys) / totalCpu;
            cs.iowaitPercent = (iowait - old.iowait) / totalCpu;
            return cs;
        }

    }

    static class NetStats {
        private long sendBytes;
        private long recvBytes;
        private long timestamp;

        public NetStats() {
        }

        public NetStats(long send, long recive) {
            this.sendBytes = send;
            this.recvBytes = recive;
            this.timestamp = System.currentTimeMillis();
        }

        public NetStatus computeNetStatus(NetStats old) {
            NetStatus ns = new NetStatus();
            double time = (timestamp - old.timestamp) / 1000.0;
            if (time == 0) return ns;

            ns.sendPerSecond = (sendBytes - old.sendBytes) / 1048576.0 / time;
            ns.recvPerSecond = (recvBytes - old.recvBytes) / 1048576.0 / time;
            return ns;
        }

        @Override
        public String toString() {
            return String.format("[%d,%d]", sendBytes, recvBytes);
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

        public DiskStatus computeDiskStatus(DiskStats old) {
            DiskStatus ds = new DiskStatus();
            double time = (timestamp - old.timestamp) / 1000.0;
            if (time == 0) return ds;

            ds.readPerSecond = (read - old.read) / time;
            ds.readBytesPerSecond = (readBytes - old.readBytes) / 1048576.0 / time;
            ds.writePerSecond = (write - old.write) / time;
            ds.writeBytesPerSecond = (writeBytes - old.writeBytes) / 1048576.0 / time;
            ds.ioUtil = ((double) ioTime - old.ioTime) / (timestamp - old.timestamp);
            return ds;
        }
    }
}
