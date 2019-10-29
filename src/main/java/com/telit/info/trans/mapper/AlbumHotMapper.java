package com.telit.info.trans.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.telit.info.annos.DataOnRead;
import com.telit.info.data.business.AlbumHot;
import com.telit.info.trans.CommonMapper;
public interface AlbumHotMapper extends CommonMapper<AlbumHot> {
	@DataOnRead
	int queryListCount(@Param("conds")String where,@Param("roleId")Integer roleId);
	@DataOnRead
	List<AlbumHot> queryList(@Param("conds")String where,@Param("roleId")Integer roleId,@Param("os")String orderStr,@Param("cur")Integer cur,@Param("len")Integer len);
}
