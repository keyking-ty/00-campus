package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.MenuOperate;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class MenuOperateCacheService extends AbstractCacheService<MenuOperate> {

	@Override
	public Class<MenuOperate> type() {
		return MenuOperate.class;
	}
}
