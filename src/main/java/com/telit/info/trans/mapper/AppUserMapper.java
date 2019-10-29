package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.annos.DataOnWrite;
import com.telit.info.data.app.AppUser;
import com.telit.info.trans.CommonMapper;

public interface AppUserMapper extends CommonMapper<AppUser> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId);
	@DataOnRead
	List<AppUser> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	List<AppUser> queryMeals();
	@DataOnWrite
	void updateTimeOut(@Param("ids")List<Integer> ids);
}