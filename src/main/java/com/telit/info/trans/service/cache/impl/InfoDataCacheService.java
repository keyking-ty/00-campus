package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.InfoData;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class InfoDataCacheService extends AbstractCacheService<InfoData> {
	@Override
	public Class<InfoData> type() {
		return InfoData.class;
	}

}
