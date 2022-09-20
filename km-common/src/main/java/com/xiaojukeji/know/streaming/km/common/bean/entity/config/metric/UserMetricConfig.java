package com.xiaojukeji.know.streaming.km.common.bean.entity.config.metric;

import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserMetricConfig {

    private int type;

    private String metric;

    private boolean set;

    private int rank;

    public UserMetricConfig(int type, String metric, boolean set, Integer rank) {
        this.type = type;
        this.metric = metric;
        this.set = set;
        this.rank = rank == null ? Constant.DEFAULT_METRIC_RANK : rank;
    }

    public UserMetricConfig(int type, String metric, boolean set) {
        this.type = type;
        this.metric = metric;
        this.set = set;
        this.rank = Constant.DEFAULT_METRIC_RANK;
    }

    @Override
    public int hashCode(){
       return metric.hashCode() << 1 + type;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof UserMetricConfig)) {
            // 非 UserMetricConfig 类型，则返回false
            return false;
        }

        UserMetricConfig u = (UserMetricConfig) o;
        return type == u.getType() && metric.equals(u.getMetric());
    }
}
