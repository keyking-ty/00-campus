package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.admin.RoleSchool;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class MenuSchoolCacheService extends AbstractCacheService<RoleSchool> {

	@Override
	public Class<RoleSchool> type() {
		return RoleSchool.class;
	}
}
