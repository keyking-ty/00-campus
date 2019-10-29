package com.telit.info.data;

import java.util.List;

import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;

import lombok.Data;
import tk.mybatis.mapper.entity.Example.Criteria;

@Data
public class Rule {
	String field;
	String op;
	String data;
	private static final String rule_eq = "eq";
	private static final String rule_ne = "ne";
	private static final String rule_lt = "lt";
	private static final String rule_le = "le";
	private static final String rule_gt = "gt";
	private static final String rule_ge = "ge";
	private static final String rule_nu = "nu";
	private static final String rule_nn = "nn";
	private static final String rule_in = "in";
	private static final String rule_ni = "ni";
	private static final String rule_bw = "bw";
	private static final String rule_bn = "bn";
	private static final String rule_ew = "ew";
	private static final String rule_en = "en";
	private static final String rule_cn = "cn";
	private static final String rule_nc = "nc";
	private static final String notNeddTrans[] = {rule_in,rule_ni,rule_bw,rule_bn,rule_ew,rule_en,rule_cn,rule_nc};
	
	private boolean checkTrans() {
		for (int i = 0 ; i < notNeddTrans.length ; i++) {
			if (notNeddTrans[i].equals(op)) {
				return false;
			}
		}
		return true;
	}
	
	public void rule(Criteria conds,boolean flag , Class<?> clazz) {
		if (CommonUtil.isEmpty(op) || CommonUtil.isEmpty(data) || CommonUtil.isEmpty(field)) {
			return;
		}
		try {
			Object value = null;
			Class<?> type = clazz;
			if (checkTrans()) {
				value = JsonUtil.decodeToObj(data,type);
			}
			switch (op) {
				case rule_eq://等于
					if (flag) {
						conds.andEqualTo(field,value);
					}else {
						conds.orEqualTo(field,value);
					}
					break;
				case rule_ne://不等于
					if (flag) {
						conds.andNotEqualTo(field,value);
					}else {
						conds.orNotEqualTo(field,value);
					}
					break;
				case rule_lt:// <
					if (flag) {
						conds.andLessThan(field,value);
					}else {
						conds.orLessThan(field,value);
					}
					break;
				case rule_le:// <=
					if (flag) {
						conds.andLessThanOrEqualTo(field,value);
					}else {
						conds.orLessThanOrEqualTo(field,value);
					}
					break;
				case rule_gt:// >
					if (flag) {
						conds.andGreaterThan(field,value);
					}else {
						conds.orGreaterThan(field,value);
					}
					break;
				case rule_ge: //>=
					if (flag) {
						conds.andGreaterThanOrEqualTo(field,value);
					}else {
						conds.orGreaterThanOrEqualTo(field,value);
					}
					break;
				case rule_nu://is null
					if (flag) {
						conds.andIsNull(field);
					}else {
						conds.orIsNull(field);
					}
					break;
				case rule_nn://is not null
					if (flag) {
						conds.andIsNotNull(field);
					}else {
						conds.orIsNotNull(field);
					}
					break;
				case rule_in:{//is in
					String temp = "[" + data + "]";
					type = CommonUtil.getClassFieldType(clazz,field);
					List<?> objs = JsonUtil.decodeToList(temp,type);
					if (CommonUtil.isArrayEmpty(objs)) {
						return;
					}
					if (flag) {
						conds.andIn(field, objs);
					}else {
						conds.orIn(field, objs);
					}
					break;
				}
				case rule_ni:{//is not in
					String temp = "[" + data + "]";
					type = CommonUtil.getClassFieldType(clazz,field);
					List<?> objs = JsonUtil.decodeToList(temp,type);
					if (CommonUtil.isArrayEmpty(objs)) {
						return;
					}
					if (flag) {
						conds.andNotIn(field, objs);
					}else {
						conds.orNotIn(field, objs);
					}
					break;
				}
				case rule_bw:// begain with
					if (flag) {
						conds.andLike(field,data + "%");
					}else {
						conds.orLike(field, data + "%");
					}
					break;
				case rule_bn:// not begain with
					if (flag) {
						conds.andNotLike(field, data + "%");
					}else {
						conds.orNotLike(field, data + "%");
					}
					break;
				case rule_ew:// end with
					if (flag) {
						conds.andLike(field,"%" + data);
					}else {
						conds.orLike(field,"%" + data);
					}
					break;
				case rule_en:// not end with
					if (flag) {
						conds.andNotLike(field,"%" + data);
					}else {
						conds.orNotLike(field,"%" + data);
					}
					break;
				case rule_cn:// contains 
					if (flag) {
						conds.andLike(field,data);
					}else {
						conds.orLike(field,data);
					}
					break;
				case rule_nc:// not contain 
					if (flag) {
						conds.andNotLike(field,data);
					}else {
						conds.orNotLike(field,data);
					}
					break;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String rule(SqlRule sqlRule) {
		if (CommonUtil.isEmpty(op) || CommonUtil.isEmpty(data) || CommonUtil.isEmpty(field)) {
			return null;
		}
		StringBuffer conds = new StringBuffer();
		Class<?> type = sqlRule.getType();
		try {
			Object value = data;
			if (type != String.class && checkTrans()) {
				value = JsonUtil.decodeToObj(data,type);
			}
			switch (op) {
				case rule_eq://等于
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					if (type == String.class) {
						conds.append("='").append(value).append("'");
					}else {
						conds.append("=").append(value);
					}
					break;
				case rule_ne://不等于
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					if (type == String.class) {
						conds.append("!='").append(value).append("'");
					}else {
						conds.append("!=").append(value);
					}
					break;
				case rule_lt:// <
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append("<").append(value);
					break;
				case rule_le:// <=
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append("<=").append(value);
					break;
				case rule_gt:// >
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(">").append(value);
					break;
				case rule_ge: //>=
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(">=").append(value);
					break;
				case rule_nu://is null
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append("=NULL");
					break;
				case rule_nn://is not null
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append("!=NULL");
					break;
				case rule_in:{//is in
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					if (type == String.class) {
						String temp = data.replace(",","','");
						conds.append(" IN('").append(temp).append("')");
					}else {
						conds.append(" IN(").append(data).append(")");
					}
					break;
				}
				case rule_ni:{//is not in
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					if (type == String.class) {
						String temp = data.replace(",","','");
						conds.append(" NOT IN('").append(temp).append("')");
					}else {
						conds.append(" NOT IN(").append(data).append(")");
					}
					break;
				}
				case rule_bw:// begain with
					conds.append(" instr(");
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(",'").append(data).append("')=1");
					break;
				case rule_bn:// not begain with
					conds.append(" instr(");
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(",'").append(data).append("')!=1");
					break;
				case rule_ew:// end with
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(" LIKE ").append("'%").append(data).append("'");
					break;
				case rule_en:// not end with
					conds.append(sqlRule.getTable()).append(".id NOT IN (");
					conds.append("SELECT ").append(sqlRule.getTable()).append(".id FROM ");
					conds.append(sqlRule.getTable()).append(" WHERE ");
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(" LIKE ").append("'%").append(data).append("')");
					break;
				case rule_cn:// contains 
					conds.append(" instr(");
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(",'").append(data).append("') > 0");
					break;
				case rule_nc:// not contain 
					conds.append(" instr(");
					conds.append(sqlRule.getTable()).append(".").append(sqlRule.getColumn());
					conds.append(",'").append(data).append("') = 0");
					break;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conds.toString();
	}
}
