package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.Role;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class RoleCacheService extends AbstractCacheService<Role> {
	
	@Override
	public Class<Role> type() {
		return Role.class;
	}
}
