package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.app.AppUser;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class SupermarketCacheService extends AbstractCacheService<AppUser>{

	@Override
	public Class<AppUser> type() {
		return AppUser.class;
	}
}
