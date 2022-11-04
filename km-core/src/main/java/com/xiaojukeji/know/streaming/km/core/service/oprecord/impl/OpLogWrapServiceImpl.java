package com.xiaojukeji.know.streaming.km.core.service.oprecord.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.didiglobal.logi.security.service.OplogService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class OpLogWrapServiceImpl implements OpLogWrapService {
    private static final ILog log = LogFactory.getLog(OpLogWrapServiceImpl.class);

    @Autowired
    private OplogService oplogService;

    @Override
    public Integer saveOplogAndIgnoreException(OplogDTO oplogDTO) {
        try {
            // fix request that cannot find thread binding (issue#743)
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (null == servletRequestAttributes) {
                servletRequestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());
                RequestContextHolder.setRequestAttributes(servletRequestAttributes, true);
            }
            return oplogService.saveOplog(oplogDTO);
        } catch (Exception e) {
            log.error("method=saveOplogAndIgnoreException||oplogDTO={}||errMsg=exception.", oplogDTO, e);
        }

        return 0;
    }
}
