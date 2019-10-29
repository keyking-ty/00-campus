package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.CardMeal;
import com.telit.info.trans.CommonMapper;
public interface CardMealMapper extends CommonMapper<CardMeal> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("operators")String operators);
	@DataOnRead
	List<CardMeal> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("operators")String operators,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
}
