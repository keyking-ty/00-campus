package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.app.PayTran;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class PaytranCacheService extends AbstractCacheService<PayTran>{
	
	@Override
	public Class<PayTran> type() {
		return PayTran.class;
	}
}
