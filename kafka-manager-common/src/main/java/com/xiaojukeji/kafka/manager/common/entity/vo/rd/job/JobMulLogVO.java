package com.xiaojukeji.kafka.manager.common.entity.vo.rd.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Job日志")
public class JobMulLogVO {
    @ApiModelProperty(value = "末尾日志ID")
    private Long endLogId;

    @ApiModelProperty(value = "日志信息")
    private List<JobLogVO> logList;

    public JobMulLogVO(List<JobLogVO> logList, Long startLogId) {
        this.logList = logList == null? new ArrayList<>(): logList;
        if (!this.logList.isEmpty()) {
            this.endLogId = this.logList.stream().map(elem -> elem.id).reduce(Long::max).get() + 1;
        } else {
            this.endLogId = startLogId;
        }
    }
}
