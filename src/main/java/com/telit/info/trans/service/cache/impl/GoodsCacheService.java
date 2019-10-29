package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.GoodsData;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class GoodsCacheService extends AbstractCacheService<GoodsData> {
	@Override
	public Class<GoodsData> type() {
		return GoodsData.class;
	}
}
