package com.telit.info.trans.config;

public class DynamicDataSourceHolder {
	private static final ThreadLocal<DynamicDataSourceGlobal> holder = new ThreadLocal<DynamicDataSourceGlobal>();
	public static boolean NORMALTYPE = false;
	
	public static void putDataSource(DynamicDataSourceGlobal dataSource) {
		holder.set(dataSource);
	}
 
	public static DynamicDataSourceGlobal getDataSource() {
		return holder.get();
	}
 
	public static void clearDataSource() {
		holder.remove();
	}
}
