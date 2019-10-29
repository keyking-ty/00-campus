package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.Curriculum;
import com.telit.info.trans.CommonMapper;
public interface CurriculumMapper extends CommonMapper<Curriculum> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("lid")Integer limitId);
	@DataOnRead
	List<Curriculum> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("lid")Integer limitId,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
}
