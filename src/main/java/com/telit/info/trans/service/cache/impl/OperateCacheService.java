package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.Operate;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class OperateCacheService extends AbstractCacheService<Operate> {

	@Override
	public Class<Operate> type() {
		return Operate.class;
	}
}
