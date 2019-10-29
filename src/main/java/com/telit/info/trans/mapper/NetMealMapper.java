package com.telit.info.trans.mapper;

import java.util.List;

import com.telit.info.data.business.QueryUserMeal;
import com.telit.info.data.business.UserMealExport;
import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.NetMeal;
import com.telit.info.trans.CommonMapper;
public interface NetMealMapper extends CommonMapper<NetMeal> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("operators")String operators);
	@DataOnRead
	List<NetMeal> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("operators")String operators,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	List<UserMealExport> queryExport(@Param("queryData") QueryUserMeal queryUserMeal);
}
