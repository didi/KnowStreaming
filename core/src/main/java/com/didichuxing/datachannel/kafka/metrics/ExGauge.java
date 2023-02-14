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

package com.didichuxing.datachannel.kafka.metrics;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.MetricName;

public class ExGauge<T> extends ExMetrics {
    private Gauge<T> _gauge;
    private Gauge<T> gauge;

    public ExGauge(MetricName metricName, Gauge<T> gauge) {
        super(metricName);
        this._gauge = gauge;
    }

    public void mark() {
        if (gauge != null) return;

        synchronized (this) {
            if (gauge == null) {
                this.gauge = Metrics.defaultRegistry().newGauge(name, this._gauge);
            }
        }
    }
}
