package com.telit.info.trans.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.telit.info.trans.CommonMapper;

import tk.mybatis.spring.annotation.MapperScan;

@Configuration
//精确到 mapper 目录，以便跟其他数据源隔离
@MapperScan(basePackages = "com.telit.info.trans.mapper", markerInterface = CommonMapper.class, sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisDatasourceConfig {
	
	@Bean(name="sqlSessionFactory")
	public SqlSessionFactory getSqlSessionFactory(@Qualifier("dataSource")DynamicDataSource dataSource) {
		try {
			SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
			bean.setDataSource(dataSource);
			return bean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Bean
	public SqlSessionTemplate getSqlSessionTemplate(@Qualifier("sqlSessionFactory")SqlSessionFactory sqlSessionFactory) {
		SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
		return template;
	}

	/**
	 * 关于事务管理器，不管是JPA还是JDBC等都实现自接口 PlatformTransactionManager
	 * 如果你添加的是 spring-boot-starter-jdbc 依赖，框架会默认注入 DataSourceTransactionManager 实例。
	 * 在Spring容器中，我们手工注解@Bean 将被优先加载，框架不会重新实例化其他的 PlatformTransactionManager 实现类。
	 */
	@Bean
	public DynamicDataSourceTransactionManager getDynamicDataSourceTransactionManager(@Qualifier("dataSource")DynamicDataSource dataSource) {
		DynamicDataSourceTransactionManager transactionManager = new DynamicDataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}
}
