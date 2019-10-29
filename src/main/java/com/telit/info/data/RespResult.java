package com.telit.info.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class RespResult{
	int state;
	String msg;
	Map<String,Object> data = new HashMap<String, Object>();
	
	public void put(String key,Object value) {
		data.put(key, value);
	}
	
	public void insertObj(Object obj) {
		try {
			Class<?> type = obj.getClass();
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				data.put(field.getName(),field.get(obj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
