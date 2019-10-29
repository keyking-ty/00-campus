package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.RepairData;
import com.telit.info.trans.service.cache.AbstractCacheService;
@Service
public class RepairCacheService extends AbstractCacheService<RepairData> {
	@Override
	public Class<RepairData> type() {
		return RepairData.class;
	}
}
