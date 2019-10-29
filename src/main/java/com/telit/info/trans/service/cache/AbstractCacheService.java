package com.telit.info.trans.service.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.telit.info.trans.SqlMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.telit.info.annos.DataOnRead;
import com.telit.info.annos.DataOnWrite;
import com.telit.info.data.IntegerKey;
import com.telit.info.trans.CommonMapper;
import com.telit.info.trans.SqlAction;
import com.telit.info.util.CommonUtil;

import tk.mybatis.mapper.entity.Example;
/**
 * 数据处理实现类
 * @author tanyong
 * @param <T>
 */
public abstract class AbstractCacheService<T extends IntegerKey> implements CacheService<T> {

	@Autowired
    protected CommonMapper<T> mapper;
	
	@Override
	@DataOnRead
	public Object load(Integer key) {
		return mapper.selectByPrimaryKey(key);
	}

	
	@Override
	@DataOnWrite
	@SuppressWarnings("unchecked")
	public int insert(Object value) {
		T t = (T)value;
		mapper.insertSelective(t);
		return t.getId();
	}
	
	@Override
	@DataOnWrite
	@SuppressWarnings("unchecked")
	public void insertMore(List<?> datas) {
		mapper.insertList((List<T>)datas);
	}

	@SuppressWarnings("unchecked")
	@Override
	@DataOnWrite
	public String save(Object obj) {
		T t = (T)obj;
		mapper.updateByPrimaryKey(t);
		return t.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	@DataOnWrite
	public Object del(Integer key) {
		Object obj = load(key);
		if (obj == null) {
			return null;
		}
		int r = mapper.delete((T)obj);
		if (r > 0) {
			return obj;
		}
		return null;
	}

	@Override
	@DataOnRead
    public List<T> selectByExample(Example example) {
		if (example == null) {
			return mapper.selectAll();
		}
        return mapper.selectByExample(example);
    }

    @Override
    @DataOnRead
    public int selectCountByExample(Object example) {
        return mapper.selectCountByExample(example);
    }

	@Override
	@DataOnRead
	public Object selectOneByExample(Example example) {
		List<T> objs = mapper.selectByExample(example);
		if (objs != null && objs.size() > 0){
			return objs.get(0);
		}
		return null;
	}

	@Override
	@DataOnWrite
	public boolean deleteByExample(Example example) {
		return mapper.deleteByExample(example) > 0;
	}

	@Override
	@DataOnRead
	public <E> List<E> selectMoreByDataSql(String excuteSqlStr,SqlAction<E> action) {
		List<SqlMap> datas = mapper.selectMoreByDataSql(excuteSqlStr);
		List<E> result = new ArrayList<E>();
		for (int i = 0 ; i < datas.size() ; i++){
			E e = action.doAction(datas.get(i));
			if (e != null) {
				result.add(e);
			}
		}
		return result;
	}

	@Override
	@DataOnRead
	public <E> E selectOneByDataSql(String excuteSqlStr,SqlAction<E> action) {
		SqlMap data = mapper.selectOneByDataSql(excuteSqlStr);
		if (data != null) {
			return action.doAction(data);
		}
		return null;
	}

	@Override
	@DataOnWrite
	public int excuteSql(String excuteSqlStr) {
		return mapper.excuteSql(excuteSqlStr);
	}
	
	@Override
	public CommonMapper<T> getMapper() {
		return mapper;
	}
}
