package com.telit.info.trans.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() {
		if (DynamicDataSourceHolder.NORMALTYPE) {
			return DynamicDataSourceGlobal.WRITE;
		}
		DynamicDataSourceGlobal dynamicDataSourceGlobal = DynamicDataSourceHolder.getDataSource();
		if (dynamicDataSourceGlobal == null) {
			return DynamicDataSourceGlobal.WRITE;
		}
		return dynamicDataSourceGlobal;
	}
}
