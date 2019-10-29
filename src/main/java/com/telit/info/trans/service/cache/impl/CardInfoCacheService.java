package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.CardInfo;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class CardInfoCacheService extends AbstractCacheService<CardInfo> {

	@Override
	public Class<CardInfo> type() {
		return CardInfo.class;
	}
}
