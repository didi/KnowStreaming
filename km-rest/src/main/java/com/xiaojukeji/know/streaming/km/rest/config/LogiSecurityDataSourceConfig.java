package com.xiaojukeji.know.streaming.km.rest.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author wyb
 * @date 2022/12/5
 */
@Configuration("myLogiSecurityDataSourceConfig")
@MapperScan(basePackages = "com.didiglobal.logi.security.dao.mapper",sqlSessionFactoryRef = "logiSecuritySqlSessionFactory")
public class LogiSecurityDataSourceConfig {

    @Bean("logiSecuritySqlSessionFactory")
    public SqlSessionFactory logiSecuritySqlSessionFactory(
            @Qualifier("logiSecurityDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return bean.getObject();
    }

    @Bean("logiSecuritySqlSessionTemplate")
    public SqlSessionTemplate logiSecuritySqlSessionTemplate(
            @Qualifier("logiSecuritySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
