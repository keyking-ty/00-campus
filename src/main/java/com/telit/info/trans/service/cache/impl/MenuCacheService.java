package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.Menu;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class MenuCacheService extends AbstractCacheService<Menu> {

	@Override
	public Class<Menu> type() {
		return Menu.class;
	}
}
