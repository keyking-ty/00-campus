package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.RoleMenu;
import com.telit.info.trans.service.cache.AbstractCacheService;
@Service
public class RoleMenuCacheService extends AbstractCacheService<RoleMenu> {

	@Override
	public Class<RoleMenu> type() {
		return RoleMenu.class;
	}
}
