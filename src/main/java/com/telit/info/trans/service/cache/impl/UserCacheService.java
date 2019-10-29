package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.User;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class UserCacheService extends AbstractCacheService<User> {
	
	@Override
	public Class<User> type() {
		return User.class;
	}
}
