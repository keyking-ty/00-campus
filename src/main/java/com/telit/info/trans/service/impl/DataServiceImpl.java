package com.telit.info.trans.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.telit.info.trans.CommonMapper;
import com.telit.info.trans.SqlAction;
import com.telit.info.trans.service.DataService;
import com.telit.info.trans.service.cache.CacheService;

import tk.mybatis.mapper.entity.Example;

@Service
public class DataServiceImpl implements DataService {
	
	@Autowired
	List<CacheService<?>> caches;
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Integer key,Class<T> clazz) {
		if (key.intValue() <= 0) {
			return null;
		}
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				Object obj = cache.load(key);
				return obj == null ? null : (T)obj;
			}
		}
		return null;
	}

	@Override
	public void save(Object value) {
		Class<?> clazz = value.getClass();
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				cache.save(value);
				break;
			}
		}
	}

	@Override
	public int insert(Object value) {
		Class<?> clazz = value.getClass();
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				return cache.insert(value);
			}
		}
		return 0;
	}

	@Override
	public void insertMore(List<?> datas) {
		Class<?> clazz = datas.get(0).getClass();
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				cache.insertMore(datas);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T delete(Integer key, Class<T> clazz) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				Object obj = cache.del(key);
				if (obj != null) {
					return (T)obj;
				}else {
					return null;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> all(Example example, Class<T> clazz) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				List<T> resilt = new ArrayList<T>();
				List<?> objs   = cache.selectByExample(example);
				for (int i = 0 ; objs != null && i < objs.size() ; i++) {
					T t = (T)objs.get(i);
					resilt.add(t);
				}
				return resilt;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T search(Example example,Class<T> clazz){
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				Object obj = cache.selectOneByExample(example);
				if (obj != null) {
					return (T) obj;
				}
			}
		}
		return null;
	}
	
	@Override
	public int count(Example example,Class<?> clazz) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				return cache.selectCountByExample(example);
			}
		}
		return 0;
	}

	@Override
	public boolean deleteByExample(Example example, Class<?> clazz) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				return cache.deleteByExample(example);
			}
		}
		return false;
	}

	@Override
	public <T> List<T> selectMoreByDataSql(String excuteSqlStr,Class<?> clazz,SqlAction<T> action) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				return cache.selectMoreByDataSql(excuteSqlStr,action);
			}
		}
		return null;
	}

	@Override
	public <T> T selectOneByDataSql(String excuteSqlStr,Class<?> clazz,SqlAction<T> action) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				return cache.selectOneByDataSql(excuteSqlStr,action);
			}
		}
		return null;
	}

	@Override
	public int excuteSql(String excuteSqlStr,Class<?> clazz) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				return cache.excuteSql(excuteSqlStr);
			}
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends CommonMapper<?>> T getMapper(Class<?> clazz) {
		for (CacheService<?> cache : caches) {
			if (cache.type() == clazz) {
				return (T)cache.getMapper();
			}
		}
		return null;
	}
}
