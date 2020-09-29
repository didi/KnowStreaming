package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.OperateRecordVO;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;

public class OperateRecordModelConverter {

    public static List<OperateRecordVO> convert2OperateRecordVOList(List<OperateRecordDO> operateRecordDOList) {

        if (ValidateUtils.isEmptyList(operateRecordDOList)) {
            return new ArrayList<>();
        }
        List<OperateRecordVO> voList = new ArrayList<>(operateRecordDOList.size());
        for (OperateRecordDO operateRecordDO : operateRecordDOList) {
            OperateRecordVO vo = new OperateRecordVO();
            CopyUtils.copyProperties(vo, operateRecordDO);
            vo.setCreateTime(operateRecordDO.getCreateTime().getTime());
            vo.setModifyTime(operateRecordDO.getModifyTime().getTime());
            vo.setModule(ModuleEnum.valueOf(operateRecordDO.getModuleId()).getMessage());
            vo.setOperate(OperateEnum.valueOf(operateRecordDO.getOperateId()).getMessage());
            voList.add(vo);
        }
        return voList;
    }
}
