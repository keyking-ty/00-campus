package com.telit.info.trans;

import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

/**
 * 这个自己的映射mapper必须继承它
 * @author tanyong
 * @param <T>
 */
public interface CommonMapper<T> extends Mapper<T>, MySqlMapper<T> {
	//FIXME 特别注意，该接口不能被扫描到，否则会出错
    //FIXME 最后在启动类中通过MapperScan注解指定扫描的mapper路径：
	@SelectProvider(type=GeneratorSql.class,method="excuteSql")
	List<SqlMap> selectMoreByDataSql(String excuteSqlStr);
	@SelectProvider(type=GeneratorSql.class,method="excuteSql")
	SqlMap selectOneByDataSql(String excuteSqlStr);
	@UpdateProvider(type=GeneratorSql.class,method="excuteSql")
	int excuteSql(String excuteSqlStr);
}
