package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.admin.User;
import com.telit.info.data.business.RepairData;
import com.telit.info.trans.CommonMapper;

public interface RepairMapper extends CommonMapper<RepairData> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId);
	@DataOnRead
	List<RepairData> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	RepairData getLinkOne(@Param("repairId")Integer id);
	@DataOnRead
	List<User> alotUsers(@Param("schoolId")Integer schoolId);
	/**
	 * 前端接口开始
	 */
	@DataOnRead
	List<RepairData> list(@Param("userId")Integer userId,@Param("start")Integer start,@Param("len")Integer len);
}
