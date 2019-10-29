package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.admin.Menu;
import com.telit.info.trans.CommonMapper;
public interface MenuMapper extends CommonMapper<Menu> {
	@DataOnRead
	List<Menu> getTopMenusByRole(@Param("roleId")Integer roleId,@Param("parentId")Integer parentId);
	@DataOnRead
	List<Menu> getAllMenusByRole(Integer roleId);
}