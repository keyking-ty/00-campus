package com.telit.info.trans.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DynamicDataSourceConfig {
	public DataSource writeDataSource() {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("writeDS.properties");
			DynamicDataSourceHolder.NORMALTYPE = "0".equals(properties.getProperty("dbType"));
			DruidDataSource dataSource = new DruidDataSource();
			dataSource.configFromPropety(properties);
			return dataSource;
		} catch (IOException e) {
			throw new RuntimeException("read and config DruidDataSource From writeDS.properties error");
		}
	}

	public DataSource readDataSource() {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("readDS.properties");
			DruidDataSource dataSource = new DruidDataSource();
			dataSource.configFromPropety(properties);
			return dataSource;
		} catch (IOException e) {
			throw new RuntimeException("read and config DruidDataSource From readDS.properties error");
		}
	}

	@Bean(name = "dataSource")
	public DynamicDataSource getDynamicDataSource() {
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
		dataSourceMap.put(DynamicDataSourceGlobal.WRITE,writeDataSource());
		if (!DynamicDataSourceHolder.NORMALTYPE) {
			dataSourceMap.put(DynamicDataSourceGlobal.READ,readDataSource());
		}
		dynamicDataSource.setTargetDataSources(dataSourceMap);
		return dynamicDataSource;
	}
}