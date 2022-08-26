package com.xiaojukeji.know.streaming.km.common.bean.po.changerecord;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "kafka_change_record")
public class KafkaChangeRecordPO extends BasePO {
    private Long clusterPhyId;

    private Integer resType;

    private String resName;

    private Integer operateType;

    private Date operateTime;

    /**
     * 唯一字段
     */
    private String uniqueField;

    public KafkaChangeRecordPO(Long clusterPhyId,
                               ModuleEnum moduleEnum,
                               String resName,
                               OperationEnum operationEnum,
                               Date operateTime) {
        this.clusterPhyId = clusterPhyId;
        this.resType = moduleEnum.getCode();
        this.resName = resName;
        this.operateType = operationEnum.getCode();
        this.operateTime = operateTime;
        this.createTime = operateTime;
        this.updateTime = operateTime;

        this.uniqueField = String.format("%d-%d-%s-%d-%d", clusterPhyId, moduleEnum.getCode(), resName, operationEnum.getCode(), operateTime.getTime());
    }

    public KafkaChangeRecordPO(Long clusterPhyId,
                               ModuleEnum moduleEnum,
                               String resName,
                               OperationEnum operationEnum,
                               Date operateTime,
                               Date uniqueFieldOperateTime) {
        this.clusterPhyId = clusterPhyId;
        this.resType = moduleEnum.getCode();
        this.resName = resName;
        this.operateType = operationEnum.getCode();
        this.operateTime = operateTime;
        this.createTime = operateTime;
        this.updateTime = operateTime;

        this.uniqueField = String.format("%d-%d-%s-%d-%d", clusterPhyId, moduleEnum.getCode(), resName, operationEnum.getCode(), uniqueFieldOperateTime.getTime());
    }
}
