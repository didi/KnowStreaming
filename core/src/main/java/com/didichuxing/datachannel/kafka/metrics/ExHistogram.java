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
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.MetricName;

public class ExHistogram extends ExMetrics {
    private Histogram histogram;
    private boolean biased;

    public ExHistogram(MetricName metricName, boolean biased) {
        super(metricName);
        this.biased = biased;
    }

    public Histogram histogram() {
        if (this.histogram!= null) return histogram;

        synchronized (this) {
            if (this.histogram != null) return histogram;
            this.histogram = Metrics.defaultRegistry().newHistogram(name, biased);
            return histogram;
        }
    }
}
