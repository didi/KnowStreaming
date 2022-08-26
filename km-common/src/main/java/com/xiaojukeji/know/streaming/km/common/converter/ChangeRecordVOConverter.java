package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.po.changerecord.KafkaChangeRecordPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.changerecord.KafkaChangeRecordVO;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;

import java.util.ArrayList;
import java.util.List;

public class ChangeRecordVOConverter {
    public static List<KafkaChangeRecordVO> convert2KafkaChangeRecordVOList(List<KafkaChangeRecordPO> poList) {
        if (poList == null) {
            return new ArrayList<>();
        }

        List<KafkaChangeRecordVO> voList = new ArrayList<>();
        for (KafkaChangeRecordPO po: poList) {
            KafkaChangeRecordVO vo = new KafkaChangeRecordVO();
            vo.setClusterPhyId(po.getClusterPhyId());
            vo.setResTypeCode(po.getResType());
            vo.setResTypeName(ModuleEnum.valueOf(po.getResType()).getDesc());
            vo.setResName(po.getResName());
            vo.setOperateTime(po.getOperateTime());
            vo.setCreateTime(po.getCreateTime());
            vo.setChangeDesc(OperationEnum.valueOf(po.getOperateType()).getDesc());
            vo.setUpdateTime(po.getUpdateTime());
            voList.add(vo);
        }

        return voList;
    }

    private ChangeRecordVOConverter() {
    }
}
