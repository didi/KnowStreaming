package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.group.Group;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.GroupTopicMember;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupOverviewVO;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.group.GroupTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author wyb
 * @date 2022/10/10
 */
public class GroupConverter {

    private GroupConverter() {

    }

    public static GroupOverviewVO convert2GroupOverviewVO(Group group) {
        GroupOverviewVO vo = ConvertUtil.obj2Obj(group, GroupOverviewVO.class);

        vo.setState(group.getState().getState());
        vo.setTopicNameList(group.getTopicMembers().stream().map(elem -> elem.getTopicName()).collect(Collectors.toList()));

        return vo;
    }

    public static Group convert2Group(GroupPO po) {
        if (po == null) {
            return null;
        }

        Group group = ConvertUtil.obj2Obj(po, Group.class);
        if (!ValidateUtils.isBlank(po.getTopicMembers())) {
            group.setTopicMembers(ConvertUtil.str2ObjArrayByJson(po.getTopicMembers(), GroupTopicMember.class));
        } else {
            group.setTopicMembers(new ArrayList<>());
        }

        group.setType(GroupTypeEnum.getTypeByCode(po.getType()));
        group.setState(GroupStateEnum.getByState(po.getState()));
        return group;
    }

    public static GroupPO convert2GroupPO(Group group) {
        if (group == null) {
            return null;
        }

        GroupPO po = ConvertUtil.obj2Obj(group, GroupPO.class);
        po.setTopicMembers(ConvertUtil.obj2Json(group.getTopicMembers()));
        po.setType(group.getType().getCode());
        po.setState(group.getState().getState());
        po.setUpdateTime(new Date());
        return po;
    }
}
