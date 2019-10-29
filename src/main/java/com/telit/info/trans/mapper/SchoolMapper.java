package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.School;
import com.telit.info.trans.CommonMapper;
public interface SchoolMapper extends CommonMapper<School>{
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId);
	@DataOnRead
	List<School> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	List<School> queryByRole(Integer roleId);
}
