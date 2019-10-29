package com.telit.info.trans.service;

import java.util.List;

import com.telit.info.trans.CommonMapper;
import com.telit.info.trans.SqlAction;

import tk.mybatis.mapper.entity.Example;


public interface DataService {
	/**
	 * 	获取数据
	 * @param load 如果缓存没用是否从数据库加载
	 * @param key 主键值
	 * @param clazz
	 * @return
	 */
	<T> T get(Integer key,Class<T> clazz);
	
	/**
	 * 保存数据
	 * @param key 等于null保存整个类型缓存数据,不等于null需要保存的对象
	 */
	<T> void save(Object value);
	
	/**
	 *  插入数据
	 * @param value 数据对象
	 * @return
	 */
	int insert(Object value);
	
	/**
	 * 插入多条记录
	 * @param datas
	 */
	void insertMore(List<?> datas);
	
	/**
	 * 删除数据
	 * @param key 数据主键值
	 * @param clazz
	 * @return
	 */
	<T> T delete(Integer key,Class<T> clazz);
	
	/**
	 * 查询列表
	 * @param filter 过滤器
	 * @param clazz
	 * @return
	 */
	<T> List<T> all(Example example,Class<T> clazz);
	
	/**
	 * 查询一个
	 * @param filter
	 * @param clazz
	 * @return
	 */
	<T> T search(Example example,Class<T> clazz);
	
	/**
	 *  计数
	 * @param example
	 * @param clazz
	 * @return
	 */
	int count(Example example,Class<?> clazz);
	
	/**
	 * 根据条件删除
	 * @param example
	 * @param clazz
	 * @return
	 */
	boolean deleteByExample(Example example,Class<?> clazz);
	
	/**
	 * 通过字定义sql查询
	 * @param excuteSqlStr
	 * @return
	 */
	<T> List<T> selectMoreByDataSql(String excuteSqlStr,Class<?> clazz,SqlAction<T> action);
	/**
	 * 通过字定义sql查询
	 * @param excuteSqlStr
	 * @return
	 */
	<T> T selectOneByDataSql(String excuteSqlStr,Class<?> clazz,SqlAction<T> action);
	/**
	 * 通过字定义sql修改
	 * @param excuteSqlStr
	 * @return
	 */
	int excuteSql(String excuteSqlStr,Class<?> clazz);
	
	/**
	 * 获取mapper
	 * @param name
	 * @param clazz
	 * @return
	 */
	<T extends CommonMapper<?>> T getMapper(Class<?> clazz);
}
