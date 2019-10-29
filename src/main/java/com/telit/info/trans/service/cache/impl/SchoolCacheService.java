package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.School;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class SchoolCacheService extends AbstractCacheService<School> {

	@Override
	public Class<School> type() {
		return School.class;
	}
}
