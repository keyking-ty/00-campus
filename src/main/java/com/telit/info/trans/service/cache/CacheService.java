package com.telit.info.trans.service.cache;

import java.util.List;

import com.telit.info.trans.CommonMapper;
import com.telit.info.trans.SqlAction;

import tk.mybatis.mapper.entity.Example;

public interface CacheService<T> {
	int PRE_SCAN_TIME = 5;//5秒扫描一次
	/**
	 * 加载数据
	 * @param key
	 * @return
	 */
	Object load(Integer key);
	/**
	 * 插入数据
	 * @param value 数据对象
	 */
	int insert(Object value);
	
	/**
	 * 插入多个数据
	 * @param datas
	 */
	void insertMore(List<?> datas);
	
	/**
	 * 类型
	 * @return
	 */
	Class<T> type();
	/**
	 * 立即保持数据
	 * @param key
	 */
	String save(Object key);
	/**
	 * 删除对象
	 * @param key
	 */
	Object del(Integer key);
	/**
	 * 通过条件查询数据库
	 * @param example
	 * @return
	 */
	List<T> selectByExample(Example example);
	/**
	 * 通过条件查询数据库
	 * @param example
	 * @return
	 */
	int selectCountByExample(Object example);
	/**
	 * 通过条件查询一个
	 * @param example
	 * @return
	 */
	Object selectOneByExample(Example example);
	/**
	 * 根据条件删除
	 * @param example
	 * @return
	 */
	boolean deleteByExample(Example example);
	
	/**
	 * 通过字定义sql查询
	 * @param excuteSqlStr
	 * @return
	 */
	<E> List<E> selectMoreByDataSql(String excuteSqlStr,SqlAction<E> action);
	/**
	 * 通过字定义sql查询
	 * @param excuteSqlStr
	 * @return
	 */
	<E> E selectOneByDataSql(String excuteSqlStr,SqlAction<E> action);
	/**
	 * 通过字定义sql修改
	 * @param excuteSqlStr
	 * @return
	 */
	int excuteSql(String excuteSqlStr);
	
	/**
	 * 获取mapper
	 * @param name
	 * @return
	 */
	CommonMapper<T> getMapper();
}
