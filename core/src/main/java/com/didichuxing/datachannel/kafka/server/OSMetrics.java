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

import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.Measurable;
import org.apache.kafka.common.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class OSMetrics {

    private static final Logger log = LoggerFactory.getLogger(OSMetrics.class);

    private DiskMetrics diskMetres;
    private MemoryMetrics memoryMetrics;
    private NetMetrics netMetrics;
    private CpuMetrics cpuMetrics;

    //The implementation for get system metrics
    private OSMetrics osMetricsImpl;

    public OSMetrics() {
    }

    public OSMetrics(Metrics metrics, ScheduledExecutorService executorService) throws Exception {
        String metricsGroupName = "OSMetrics";

        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("linux")) {
            osMetricsImpl = new LinuxMetrics(executorService);
            diskMetres = new DiskMetrics(metrics, metricsGroupName);
            memoryMetrics = new MemoryMetrics(metrics, metricsGroupName);
            netMetrics = new NetMetrics(metrics, metricsGroupName);
            cpuMetrics = new CpuMetrics(metrics, metricsGroupName);
        } else {
            log.warn("current os: {} not support system metrics", os);
        }
    }

    public CpuStatus getCupStatus() {
        return null;
    }

    public MemoryStatus getMemoryStatus() {
        return null;
    }

    public NetStatus getNetStatus(String name) {
        return null;
    }

    public HashMap<String, NetStatus> getAllNetStatus() {
        return null;
    }

    public DiskStatus getDiskStatus(String name) {
        return null;
    }

    public HashMap<String, DiskStatus> getAllDiskStatus() {
        return null;
    }

    static class DiskStatus {
        public double ioUtil;
        public double readPerSecond;
        public double writePerSecond;
        public double readBytesPerSecond;
        public double writeBytesPerSecond;

        @Override
        public String toString() {
            return String.format("[%f,%f,%f,%f,%f]",
                    readPerSecond, readBytesPerSecond, writePerSecond, writeBytesPerSecond, ioUtil);
        }
    }

    static class NetStatus {
        public double sendPerSecond;
        public double recvPerSecond;

        @Override
        public String toString() {
            return String.format("[%f,%f]", sendPerSecond, recvPerSecond);

        }
    }

    static class CpuStatus {
        public double load;
        public double load5;
        public double load15;
        public double userPercent;
        public double sysPercent;
        public double idlePercent;
        public double iowaitPercent;
    }

    static class MemoryStatus {
        public double total;
        public double free;
        public double buffer;
        public double cached;
        public double used;
    }

    private class DiskMetrics {

        public DiskMetrics(Metrics metrics, String metricGrpPrefix) {
            HashMap<String, DiskStatus> allDiskStatus = osMetricsImpl.getAllDiskStatus();
            Map<String, String> metricsTags = new LinkedHashMap<>();
            metricsTags.put("dev", "disk");
            for (Map.Entry<String, DiskStatus> entry: allDiskStatus.entrySet()) {
                String diskname = entry.getKey();
                metricsTags.put("name", diskname);

                Measurable readBytesMeasure = (config, now) -> {
                    DiskStatus ds = osMetricsImpl.getDiskStatus(diskname);
                    return ds.readBytesPerSecond;
                };
                MetricName name = metrics.metricName("ReadBytesPerSecond", metricGrpPrefix,
                        "The number of bytes to read per second", metricsTags);
                metrics.addMetric(name, readBytesMeasure);

                Measurable wrtieBytesMeasure = (config, now) -> {
                    DiskStatus ds = osMetricsImpl.getDiskStatus(diskname);
                    return ds.writeBytesPerSecond;
                };
                name = metrics.metricName("WriteBytesPerSecond", metricGrpPrefix,
                        "The number of bytes to write per second", metricsTags);
                metrics.addMetric(name, wrtieBytesMeasure);

                Measurable readMeasure = (config, now) -> {
                    DiskStatus ds = osMetricsImpl.getDiskStatus(diskname);
                    return ds.readPerSecond;
                };
                name = metrics.metricName("ReadPerSecond", metricGrpPrefix,
                        "The number of read per second", metricsTags);
                metrics.addMetric(name, readMeasure);

                Measurable writeMeasure = (config, now) -> {
                    DiskStatus ds = osMetricsImpl.getDiskStatus(diskname);
                    return ds.writePerSecond;
                };
                name = metrics.metricName("WritePerSecond", metricGrpPrefix,
                        "The number of write per second", metricsTags);
                metrics.addMetric(name, writeMeasure);

                Measurable ioutilMeasure = (config, now) -> {
                    DiskStatus ds = osMetricsImpl.getDiskStatus(diskname);
                    return ds.ioUtil;
                };
                name = metrics.metricName("IoUtil", metricGrpPrefix,
                        "The number of write per second", metricsTags);
                metrics.addMetric(name, ioutilMeasure);
            }
        }
    }

    private class NetMetrics {

        public NetMetrics(Metrics metrics, String metricGrpPrefix) {
            HashMap<String, NetStatus> allNetStatus = osMetricsImpl.getAllNetStatus();
            Map<String, String> metricsTags = new LinkedHashMap<>();
            metricsTags.put("dev", "net");
            for (Map.Entry<String, NetStatus> entry: allNetStatus.entrySet()) {
                String ethname = entry.getKey();
                metricsTags.put("name", ethname);

                Measurable sendBytesMeasure = (config, now) -> {
                    NetStatus ns = osMetricsImpl.getNetStatus(ethname);
                    return ns.sendPerSecond;
                };
                MetricName name = metrics.metricName("SendBytesPerSecond", metricGrpPrefix,
                        "The number of bytes to send per second", metricsTags);
                metrics.addMetric(name, sendBytesMeasure);

                Measurable recvBytesMeasure = (config, now) -> {
                    NetStatus ns = osMetricsImpl.getNetStatus(ethname);
                    return ns.recvPerSecond;
                };
                name = metrics.metricName("RecvBytesPerSecond", metricGrpPrefix,
                        "The number of bytes to recv per second", metricsTags);
                metrics.addMetric(name, recvBytesMeasure);
            }
        }
    }

    private class CpuMetrics {

        public CpuMetrics(Metrics metrics, String metricGrpPrefix) {
            Map<String, String> metricsTags = new LinkedHashMap<>();
            metricsTags.put("dev", "cpu");

            Measurable loadMeasure = (config, now) -> {
                CpuStatus cs = osMetricsImpl.getCupStatus();
                return cs.load;
            };
            MetricName name = metrics.metricName("loadavg", metricGrpPrefix,
                    "load average for realtime", metricsTags);
            metrics.addMetric(name, loadMeasure);

            Measurable load5Measure = (config, now) -> {
                CpuStatus cs = osMetricsImpl.getCupStatus();
                return cs.load5;
            };
            name = metrics.metricName("load5avg", metricGrpPrefix,
                    "load average for 5 minute", metricsTags);
            metrics.addMetric(name, load5Measure);

            Measurable load15Measure = (config, now) -> {
                CpuStatus cs = osMetricsImpl.getCupStatus();
                return cs.load15;
            };
            name = metrics.metricName("load15avg", metricGrpPrefix,
                    "load average for 15 minute", metricsTags);
            metrics.addMetric(name, load15Measure);

            Measurable userMeasure = (config, now) -> {
                CpuStatus cs = osMetricsImpl.getCupStatus();
                return cs.userPercent;
            };
            name = metrics.metricName("user", metricGrpPrefix,
                    "cpu user", metricsTags);
            metrics.addMetric(name, userMeasure);

            Measurable sysMeasure = (config, now) -> {
                CpuStatus cs = osMetricsImpl.getCupStatus();
                return cs.sysPercent;
            };
            name = metrics.metricName("sys", metricGrpPrefix,
                    "cpu system", metricsTags);
            metrics.addMetric(name, sysMeasure);

            Measurable idleMeasure = (config, now) -> {
                CpuStatus cs = osMetricsImpl.getCupStatus();
                return cs.idlePercent;
            };
            name = metrics.metricName("idle", metricGrpPrefix,
                    "cpu idle", metricsTags);
            metrics.addMetric(name, idleMeasure);

            Measurable iowaitMeasure = (config, now) -> {
                CpuStatus cs = osMetricsImpl.getCupStatus();
                return cs.iowaitPercent;
            };
            name = metrics.metricName("iowait", metricGrpPrefix,
                    "cpu iowait", metricsTags);
            metrics.addMetric(name, iowaitMeasure);
        }
    }

    private class MemoryMetrics {

        public MemoryMetrics(Metrics metrics, String metricGrpPrefix) {
            Map<String, String> metricsTags = new LinkedHashMap<>();
            metricsTags.put("dev", "memory");

            Measurable totalMeasure = (config, now) -> {
                MemoryStatus ms = osMetricsImpl.getMemoryStatus();
                return ms.total;
            };
            MetricName name = metrics.metricName("total", metricGrpPrefix,
                    "total memory for mb", metricsTags);
            metrics.addMetric(name, totalMeasure);

            Measurable bufferMeasure = (config, now) -> {
                MemoryStatus ms = osMetricsImpl.getMemoryStatus();
                return ms.buffer;
            };
            name = metrics.metricName("buffer", metricGrpPrefix,
                    "buffer memory for mb", metricsTags);
            metrics.addMetric(name, bufferMeasure);

            Measurable cachedMeasure = (config, now) -> {
                MemoryStatus ms = osMetricsImpl.getMemoryStatus();
                return ms.cached;
            };
            name = metrics.metricName("cached", metricGrpPrefix,
                    "cached memory for mb", metricsTags);
            metrics.addMetric(name, cachedMeasure);

            Measurable freeMeasure = (config, now) -> {
                MemoryStatus ms = osMetricsImpl.getMemoryStatus();
                return ms.free;
            };
            name = metrics.metricName("free", metricGrpPrefix,
                    "free memory for mb", metricsTags);
            metrics.addMetric(name, freeMeasure);

            Measurable usedMeasure = (config, now) -> {
                MemoryStatus ms = osMetricsImpl.getMemoryStatus();
                return ms.used;
            };
            name = metrics.metricName("used", metricGrpPrefix,
                    "used memory for mb", metricsTags);
            metrics.addMetric(name, usedMeasure);
        }
    }
}
