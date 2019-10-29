package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.InfoData;
import com.telit.info.trans.CommonMapper;

public interface InfoMapper extends CommonMapper<InfoData> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("ss")String ss);
	@DataOnRead
	List<InfoData> queryList(@Param("conds")String where,@Param("ss")String ss,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	List<InfoData> loadByType(@Param("type")String where,@Param("schoolId")String schoolId,@Param("start")Integer start,@Param("len")Integer len);
}
