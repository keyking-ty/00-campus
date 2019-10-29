package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.AlbumHot;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class AlbumHotCacheService extends AbstractCacheService<AlbumHot> {
	
	@Override
	public Class<AlbumHot> type() {
		return AlbumHot.class;
	}
}
