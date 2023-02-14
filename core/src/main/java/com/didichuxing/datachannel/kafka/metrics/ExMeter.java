/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.didichuxing.datachannel.kafka.metrics;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;

import java.util.concurrent.TimeUnit;

public class ExMeter extends ExMetrics {
    private Meter meter;
    private final String describe;
    private final TimeUnit timeUnit;

    public ExMeter(MetricName metricName, String describe, TimeUnit timeUnit) {
        super(metricName);
        this.describe = describe;
        this.timeUnit = timeUnit;
    }

    public Meter meter() {
        if (this.meter != null) return meter;

        synchronized (this) {
            if (this.meter != null) return meter;
            this.meter = Metrics.defaultRegistry().newMeter(name, describe ,timeUnit);
            return meter;
        }
    }

}
