package com.xiaojukeji.know.streaming.km.rest.config;

import com.didiglobal.logi.security.common.constant.Constants;
import com.google.common.base.Predicates;
import com.xiaojukeji.know.streaming.km.common.constant.ApiPrefix;
import com.xiaojukeji.know.streaming.km.common.utils.GitPropUtil;
import com.xiaojukeji.know.streaming.km.rest.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import springfox.documentation.builders.*;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    private static final String FE_INDEX_PAGE_HTML = "layout/index";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // FE-首页
        registry.addViewController("/").setViewName(FE_INDEX_PAGE_HTML);

        // FE-系统管理，因为系统管理模块的uri和前端静态资源名字重复了，因此这里为了辨别，所以进行了比较详细的规则描述
        registry.addViewController("/config").setViewName(FE_INDEX_PAGE_HTML);
        registry.addViewController("/config/user").setViewName(FE_INDEX_PAGE_HTML);
        registry.addViewController("/config/setting").setViewName(FE_INDEX_PAGE_HTML);
        registry.addViewController("/config/operation-log").setViewName(FE_INDEX_PAGE_HTML);

        // FE-多集群管理
        registry.addViewController("/cluster").setViewName(FE_INDEX_PAGE_HTML);
        registry.addViewController("/cluster/**").setViewName(FE_INDEX_PAGE_HTML);

        // FE-登录
        registry.addViewController("/login").setViewName(FE_INDEX_PAGE_HTML);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 会进行拦截的接口
        registry.addInterceptor(permissionInterceptor).addPathPatterns(ApiPrefix.API_PREFIX + "**", Constants.API_PREFIX + "/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // COMMON
        registry.addResourceHandler("/**").addResourceLocations("classpath:/templates/", "classpath:/static/");

        // SWAGGER
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.or(
                        RequestHandlerSelectors.basePackage("com.xiaojukeji.know.streaming.km.rest.api"),
                        RequestHandlerSelectors.basePackage("com.didiglobal.logi.security.controller")))
                .paths(PathSelectors.any())
                .build()
                .enable(true);
    }

    private ApiInfo apiInfo() {
        String version = GitPropUtil.getProps(GitPropUtil.VERSION_FIELD_NAME);
        String commitId = GitPropUtil.getProps(GitPropUtil.COMMIT_ID_FIELD_NAME);

        return new ApiInfoBuilder()
                .title("KS-KM 接口文档")
                .description("欢迎使用滴滴KS-KM")
                .contact(new Contact("zengqiao", "", "zengqiao@didiglobal.com"))
                .version(String.format("%s-%s", version == null? "": version, commitId == null? "": commitId))
                .build();
    }

}

