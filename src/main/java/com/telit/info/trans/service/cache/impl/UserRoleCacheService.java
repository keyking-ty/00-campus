package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.UserRole;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class UserRoleCacheService extends AbstractCacheService<UserRole> {

	@Override
	public Class<UserRole> type() {
		return UserRole.class;
	}
}
