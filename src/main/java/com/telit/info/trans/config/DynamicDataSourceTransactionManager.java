package com.telit.info.trans.config;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

@SuppressWarnings("serial")
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {
	
	@Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
		if (!DynamicDataSourceHolder.NORMALTYPE) {
			//设置数据源
	        boolean readOnly = definition.isReadOnly();
	        if (readOnly) {
	            DynamicDataSourceHolder.putDataSource(DynamicDataSourceGlobal.READ);
	        } else {
	            DynamicDataSourceHolder.putDataSource(DynamicDataSourceGlobal.WRITE);
	        }
		}
        super.doBegin(transaction,definition);
    }
	
    //清理本地线程的数据源
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        if (!DynamicDataSourceHolder.NORMALTYPE) {
        	DynamicDataSourceHolder.clearDataSource();
        }
    }
}
