package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.dto.rd.AccountDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.common.AccountVO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.AccountDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/3
 */
public class AccountConverter {
    public static AccountDO convert2AccountDO(AccountDTO dto) {
        AccountDO accountDO = new AccountDO();
        accountDO.setUsername(dto.getUsername());
        accountDO.setPassword(dto.getPassword());
        accountDO.setRole(dto.getRole());
        return accountDO;
    }

    public static List<AccountVO> convert2AccountVOList(List<AccountDO> accountDOList) {
        if (ValidateUtils.isNull(accountDOList)) {
            return new ArrayList<>();
        }
        List<AccountVO> voList = new ArrayList<>();
        for (AccountDO accountDO: accountDOList) {
            AccountVO vo = new AccountVO();
            vo.setUsername(accountDO.getUsername());
            vo.setRole(accountDO.getRole());
            voList.add(vo);
        }
        return voList;
    }
}