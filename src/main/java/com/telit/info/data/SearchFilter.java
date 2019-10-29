package com.telit.info.data;

import java.util.List;
import java.util.Map;

import lombok.Data;
import tk.mybatis.mapper.entity.Example.Criteria;

@Data
public class SearchFilter {
	String groupOp;
	List<Rule> rules;
	
	public String conds(Map<String, SqlRule> map) {
		StringBuffer where = new StringBuffer();
		if (rules != null) {
			for (int i = 0 ; i < rules.size() ; i++) {
				Rule rule = rules.get(i);
				SqlRule sqlRule = map.get(rule.getField());
				String conds = rule.rule(sqlRule);
				if (conds != null) {
					if (where.length() > 0) {
						where.append(" ").append(groupOp).append(" ");
					}
					where.append(conds);
				}
			}
		}
		if (where.length() == 0) {
			return null;
		}
		return where.toString();
	}
	
	public void conds(Map<String, SqlRule> map,Criteria conds) {
		if (rules != null) {
			boolean flag = "AND".equals(groupOp);
			for (int i = 0 ; i < rules.size() ; i++) {
				Rule rule = rules.get(i);
				SqlRule sqlRule = map.get(rule.getField());
				if (sqlRule != null) {
					rule.rule(conds,flag,sqlRule.getType());
				}
			}
		}
	}
}
