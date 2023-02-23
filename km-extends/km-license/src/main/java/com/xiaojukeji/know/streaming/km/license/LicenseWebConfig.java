package com.xiaojukeji.know.streaming.km.license;

import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author didi
 */
@Configuration
public class LicenseWebConfig implements WebMvcConfigurer {
    @Autowired
    private LicenseInterceptor licenseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 会进行拦截的接口
        registry.addInterceptor(licenseInterceptor).addPathPatterns(ApiPrefix.API_PREFIX + "**");
    }

}

