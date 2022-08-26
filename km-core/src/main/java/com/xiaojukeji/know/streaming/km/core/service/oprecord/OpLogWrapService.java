package com.xiaojukeji.know.streaming.km.core.service.oprecord;

import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;

public interface OpLogWrapService {
    Integer saveOplogAndIgnoreException(OplogDTO oplogDTO);
}
