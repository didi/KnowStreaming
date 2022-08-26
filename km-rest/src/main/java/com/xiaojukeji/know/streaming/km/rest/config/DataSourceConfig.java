package com.xiaojukeji.know.streaming.km.rest.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author zengqiao
 * @date 20/3/17
 */
@Configuration
@MapperScan("com.xiaojukeji.know.streaming.km.persistence.mysql")
public class DataSourceConfig {
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.know-streaming")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "kmGlobalConfig")
    public GlobalConfig globalConfig(){
        GlobalConfig globalConfig=new GlobalConfig();
        globalConfig.setBanner(false);
        GlobalConfig.DbConfig dbConfig=new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.AUTO);
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/*.xml"));
        bean.setGlobalConfig(globalConfig());

        // 输出sql
//        MybatisConfiguration configuration = new MybatisConfiguration();
//        configuration.setJdbcTypeForNull(JdbcType.NULL);
//        configuration.setMapUnderscoreToCamelCase(true);
//        configuration.setCacheEnabled(false);
//        // 配置打印sql语句
//        configuration.setLogImpl(StdOutImpl.class);
//        bean.setConfiguration(configuration);


        //添加分页插件，不加这个，分页不生效
        bean.setPlugins(paginationInterceptor());

        return bean.getObject();
    }

    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "sqlSession")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDbType(DbType.MYSQL);
        return page;
    }
}
