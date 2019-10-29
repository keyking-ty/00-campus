package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.admin.User;
import com.telit.info.trans.CommonMapper;
public interface UserMapper extends CommonMapper<User> {
	@DataOnRead
	int queryUserCount(String where);
	@DataOnRead
	List<User> queryUser(@Param("conds")String Where,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
}