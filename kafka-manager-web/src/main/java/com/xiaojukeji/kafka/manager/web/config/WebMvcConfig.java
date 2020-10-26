package com.xiaojukeji.kafka.manager.web.config;

import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.inteceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zengqiao
 * @date 20/1/19
 */
@SpringBootConfiguration
@Component
@DependsOn({"permissionInterceptor"})
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/kafka").setViewName("index");
        registry.addViewController("/kafka/**").setViewName("index");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor).addPathPatterns(ApiPrefix.API_PREFIX + "**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // SWAGGER
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        // FE
        registry.addResourceHandler("index.html", "/**").addResourceLocations("classpath:/templates/","classpath:/static/");
    }
}