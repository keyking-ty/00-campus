package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.ImportResult;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class ImportResultCacheService extends AbstractCacheService<ImportResult> {

	@Override
	public Class<ImportResult> type() {
		return ImportResult.class;
	}
}
