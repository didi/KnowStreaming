package com.xiaojukeji.know.streaming.km.common.bean.entity.health;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.healthcheck.BaseClusterHealthConfig;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckNameEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class HealthScoreResult {
    private HealthCheckNameEnum checkNameEnum;

    private Float presentDimensionTotalWeight;

    private Float allDimensionTotalWeight;

    private BaseClusterHealthConfig baseConfig;

    private List<HealthCheckResultPO> poList;

    private Boolean passed;

    public HealthScoreResult(HealthCheckNameEnum checkNameEnum,
                             Float presentDimensionTotalWeight,
                             Float allDimensionTotalWeight,
                             BaseClusterHealthConfig baseConfig,
                             List<HealthCheckResultPO> poList) {
        this.checkNameEnum = checkNameEnum;
        this.baseConfig = baseConfig;
        this.poList = poList;
        this.presentDimensionTotalWeight = presentDimensionTotalWeight;
        this.allDimensionTotalWeight = allDimensionTotalWeight;
        if (!ValidateUtils.isEmptyList(poList) && poList.stream().filter(elem -> elem.getPassed() <= 0).count() <= 0) {
            passed = true;
        } else {
            passed = false;
        }
    }

    public Integer getTotalCount() {
        if (poList == null) {
            return 0;
        }

        return poList.size();
    }

    public Integer getPassedCount() {
        if (poList == null) {
            return 0;
        }
        return (int) (poList.stream().filter(elem -> elem.getPassed() > 0).count());
    }

    /**
     * 计算所有检查结果的健康分
     * 比如：计算集群健康分
     */
    public Float calAllWeightHealthScore() {
        Float healthScore = 100 * baseConfig.getWeight() / allDimensionTotalWeight;
        if (poList == null || poList.isEmpty()) {
            return 0.0f;
        }

        return healthScore * this.getPassedCount() / this.getTotalCount();
    }

    /**
     * 计算当前维度的健康分
     * 比如：计算集群Broker健康分
     */
    public Float calDimensionWeightHealthScore() {
        Float healthScore = 100 * baseConfig.getWeight() / presentDimensionTotalWeight;
        if (poList == null || poList.isEmpty()) {
            return 0.0f;
        }

        return healthScore * this.getPassedCount() / this.getTotalCount();
    }

    /**
     * 计算当前检查的健康分
     * 比如：计算集群Broker健康检查中的某一项的健康分
     */
    public Integer calRawHealthScore() {
        if (poList == null || poList.isEmpty()) {
            return 100;
        }

        return 100 * this.getPassedCount() / this.getTotalCount();
    }

    public List<String> getNotPassedResNameList() {
        if (poList == null) {
            return new ArrayList<>();
        }

        return poList.stream().filter(elem -> elem.getPassed() <= 0).map(elem -> elem.getResName()).collect(Collectors.toList());
    }

    public Date getCreateTime() {
        if (ValidateUtils.isEmptyList(poList)) {
            return null;
        }

        return poList.get(0).getCreateTime();
    }

    public Date getUpdateTime() {
        if (ValidateUtils.isEmptyList(poList)) {
            return null;
        }

        return poList.get(0).getUpdateTime();
    }
}
