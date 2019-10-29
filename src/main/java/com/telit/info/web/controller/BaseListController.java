package com.telit.info.web.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import com.telit.info.data.SqlRule;
import com.telit.info.trans.service.DataService;

public class BaseListController {
	@Autowired
	protected DataService dataService;
	protected Map<String,SqlRule> rules = new HashMap<String,SqlRule>();
}
