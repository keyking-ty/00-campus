package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.GoodsData;
import com.telit.info.trans.CommonMapper;

public interface GoodsMapper extends CommonMapper<GoodsData> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId);
	@DataOnRead
	List<GoodsData> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	GoodsData getLinkOne(@Param("goodsId")Integer id);
	/**
	 * 前端接口开始
	 */
	@DataOnRead
	List<GoodsData> list(@Param("userId")Integer userId,@Param("schoolId")Integer schoolId,@Param("start")Integer start,@Param("len")Integer len);
}
