package com.telit.info.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PageUtil {
	
	public static <T> List<T> page(Map<String, Object> map,List<T> src,int page,int len){
		List<T> result = new ArrayList<T>();
		if (page < 1) {
			page = 1;
		}
		int pages = 0 , size = 0 ;
		if (src != null && src.size() > 0) {
			size = src.size();
			int end = page * len;
			end = end < src.size() ? end : size;
			for (int i = (page -1) * len ; i < end ; i++) {
				result.add(src.get(i));
			}
			pages = src.size() % len == 0 ? (size / len) : (size / len + 1);
		}
		map.put("page",page);
		map.put("total",pages);
		map.put("records",size);
		map.put("rows",result);
		return result;
	}
	
	public static void page(Map<String, Object> map,List<?> src,int size,int page,int len){
		if (page < 1) {
			page = 1;
		}
		int pages = size % len == 0 ? (size / len) : (size / len + 1);
		map.put("page",page);
		map.put("total",pages);
		map.put("records",size);
		map.put("rows",src);
	}
}
