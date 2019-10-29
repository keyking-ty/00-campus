package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.Album;
import com.telit.info.trans.service.cache.AbstractCacheService;
@Service
public class AlbumCacheService extends AbstractCacheService<Album> {

	@Override
	public Class<Album> type() {
		return Album.class;
	}
}
