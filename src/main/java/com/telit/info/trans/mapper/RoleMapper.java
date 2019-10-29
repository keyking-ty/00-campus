package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.admin.Role;
import com.telit.info.trans.CommonMapper;
public interface RoleMapper extends CommonMapper<Role> {
	@DataOnRead
	int queryRoleCount(String Where);
	@DataOnRead
	List<Role> queryRole(@Param("conds")String Where,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
	@DataOnRead
	List<Role> queryRoleByUser(@Param("uid")Integer userId,@Param("op")String type);
}