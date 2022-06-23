package com.xiaojukeji.kafka.manager.web.config;

import com.xiaojukeji.kafka.manager.common.utils.GitPropUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置页面
 * @author huangyiminghappy@163.com
 * @date 2019-05-09
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xiaojukeji.kafka.manager.web.api"))
                .paths(PathSelectors.any())
                .build()
                .enable(true);
    }

    private ApiInfo apiInfo() {
        String version = GitPropUtil.getProps(GitPropUtil.VERSION_FIELD_NAME);
        String commitId = GitPropUtil.getProps(GitPropUtil.COMMIT_ID_FIELD_NAME);

        return new ApiInfoBuilder()
                .title("LogiKM接口文档")
                .description("欢迎使用滴滴LogiKM")
                .version(String.format("%s-%s", version == null? "": version, commitId == null? "": commitId))
                .build();
    }

}
