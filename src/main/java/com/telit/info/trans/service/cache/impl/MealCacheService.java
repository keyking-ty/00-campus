package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.NetMeal;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class MealCacheService extends AbstractCacheService<NetMeal> {

	@Override
	public Class<NetMeal> type() {
		return NetMeal.class;
	}
}
