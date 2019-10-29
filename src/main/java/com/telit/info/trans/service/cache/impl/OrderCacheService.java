package com.telit.info.trans.service.cache.impl;

import org.springframework.stereotype.Service;

import com.telit.info.data.app.Order;
import com.telit.info.trans.service.cache.AbstractCacheService;

@Service
public class OrderCacheService extends AbstractCacheService<Order>{
	
	@Override
	public Class<Order> type() {
		return Order.class;
	}
}
