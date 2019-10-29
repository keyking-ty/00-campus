package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.app.Advert;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class AdvertCacheService extends AbstractCacheService<Advert>{
	@Override
	public Class<Advert> type() {
		return Advert.class;
	}
}
