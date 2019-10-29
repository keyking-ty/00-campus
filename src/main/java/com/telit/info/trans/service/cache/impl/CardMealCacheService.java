package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.CardMeal;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class CardMealCacheService extends AbstractCacheService<CardMeal> {

	@Override
	public Class<CardMeal> type() {
		return CardMeal.class;
	}
}
