package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.business.CurriculumComment;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class CurriculumCommentCacheService extends AbstractCacheService<CurriculumComment> {

	@Override
	public Class<CurriculumComment> type() {
		return CurriculumComment.class;
	}
}
