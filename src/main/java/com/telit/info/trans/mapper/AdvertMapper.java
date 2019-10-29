package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.app.Advert;
import com.telit.info.trans.CommonMapper;

public interface AdvertMapper extends CommonMapper<Advert> {
	@DataOnRead
	int queryListCount(@Param("conds")String where);
	@DataOnRead
	List<Advert> queryList(@Param("conds")String where,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
}