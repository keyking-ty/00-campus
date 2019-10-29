package com.telit.info.data;

import lombok.Data;

@Data
public class SqlRule {
	String key;
	Class<?> type;
	String column;
	String table;
	
	public SqlRule(String table,Class<?> type) {
		this.table = table;
		this.type  = type;
	}
	
	public SqlRule() {
		
	}
	/**
	 * key和column相同的
	 * @param value
	 */
	public void init(String value) {
		key    = value;
		column = value;
	}
}
