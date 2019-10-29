package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.UsedItem;
import com.telit.info.trans.CommonMapper;

public interface UsedItemMapper extends CommonMapper<UsedItem> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId);
	@DataOnRead
	List<UsedItem> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	UsedItem getLinkOne(@Param("itemId")Integer itemId);
	/**
	 * 前端接口开始
	 */
	@DataOnRead
	List<UsedItem> list(@Param("userId")Integer userId,@Param("schoolId")Integer schoolId,@Param("start")Integer start,@Param("len")Integer len);
}
