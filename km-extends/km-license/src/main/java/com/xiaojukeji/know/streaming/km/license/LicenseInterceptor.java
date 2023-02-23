package com.xiaojukeji.know.streaming.km.license;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.license.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

import static com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix.API_V3_PREFIX;

@Component
public class LicenseInterceptor implements HandlerInterceptor {
    private static final ILog      LOGGER = LogFactory.getLog(LicenseInterceptor.class);

    private static final String PHYSICAL_CLUSTER_URL = API_V3_PREFIX + "physical-clusters";
    private static final String UTF_8 = "utf-8";

    @Autowired
    private LicenseService licenseService;

    /**
     * 拦截预处理
     * @return boolean false:拦截, 不向下执行, true:放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (PHYSICAL_CLUSTER_URL.equals( request.getRequestURI() ) &&
                "POST".equals( request.getMethod() )) {

            Result<Void> result = licenseService.addClusterLimit();
            if (result.failed()) {
                // 如果出错，构造错误信息
                OutputStream out = null;
                try {
                    response.setCharacterEncoding(UTF_8);
                    response.setContentType("text/json");
                    out = response.getOutputStream();
                    out.write(ConvertUtil.obj2Json(result).getBytes(UTF_8));
                    out.flush();
                } catch (IOException e) {
                    LOGGER.error( "method=preHandle||msg=physical-clusters add exception! ", e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        LOGGER.error( "method=preHandle||msg=outputStream close exception! ", e);
                    }
                }

                // 拒绝向下执行
                return false;
            }
        }

        // 未达到限制，继续后续的执行
        return true;
    }
}
