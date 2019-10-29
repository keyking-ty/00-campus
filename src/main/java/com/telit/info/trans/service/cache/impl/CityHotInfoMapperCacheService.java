package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.CityHotInfo;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class CityHotInfoMapperCacheService extends AbstractCacheService<CityHotInfo> {
	@Override
	public Class<CityHotInfo> type() {
		return CityHotInfo.class;
	}
}
