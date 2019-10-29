package com.telit.info.trans.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.telit.info.trans.config.DynamicDataSourceGlobal;
import com.telit.info.trans.config.DynamicDataSourceHolder;

@Component
@Aspect
public class DynamicDataSourceChange {
	
	@Pointcut("@annotation(com.telit.info.annos.DataOnWrite)")
    public void wirte(){
		
	}
	
	@Pointcut("@annotation(com.telit.info.annos.DataOnRead)")
    public void read(){
		
	}
	
	@Before("wirte()")
    public void setWrite(){
		if (DynamicDataSourceHolder.NORMALTYPE) {
			return;
		}
		DynamicDataSourceHolder.putDataSource(DynamicDataSourceGlobal.WRITE);
	}
	
	@Before("read()")
    public void setRead(){
		if (DynamicDataSourceHolder.NORMALTYPE) {
			return;
		}
		DynamicDataSourceHolder.putDataSource(DynamicDataSourceGlobal.READ);
	}
	
	@After("wirte()")
    public void clearWrite(){
		if (DynamicDataSourceHolder.NORMALTYPE) {
			return;
		}
		DynamicDataSourceHolder.clearDataSource();
	}
	
	@After("read()")
    public void clearRead(){
		if (DynamicDataSourceHolder.NORMALTYPE) {
			return;
		}
		DynamicDataSourceHolder.clearDataSource();
	}
}
