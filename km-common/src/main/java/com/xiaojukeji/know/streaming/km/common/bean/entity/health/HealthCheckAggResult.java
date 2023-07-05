package com.xiaojukeji.know.streaming.km.common.bean.entity.health;

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
public class HealthCheckAggResult {
    protected HealthCheckNameEnum checkNameEnum;

    protected List<HealthCheckResultPO> poList;

    protected Boolean passed;

    public HealthCheckAggResult(HealthCheckNameEnum checkNameEnum, List<HealthCheckResultPO> poList) {
        this.checkNameEnum = checkNameEnum;
        this.poList = poList;
        if (ValidateUtils.isEmptyList(poList) || poList.stream().filter(elem -> elem.getPassed() <= 0).count() <= 0) {
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

    public List<String> getNotPassedResNameList() {
        if (poList == null) {
            return new ArrayList<>();
        }

        return poList.stream().filter(elem -> elem.getPassed() <= 0 && !ValidateUtils.isBlank(elem.getResName())).map(elem -> elem.getResName()).collect(Collectors.toList());
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
