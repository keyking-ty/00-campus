package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.UsedItem;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class UsedItemCacheService extends AbstractCacheService<UsedItem> {
	@Override
	public Class<UsedItem> type() {
		return UsedItem.class;
	}
}
