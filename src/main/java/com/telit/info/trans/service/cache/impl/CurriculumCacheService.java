package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.Curriculum;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class CurriculumCacheService extends AbstractCacheService<Curriculum> {

	@Override
	public Class<Curriculum> type() {
		return Curriculum.class;
	}
}
