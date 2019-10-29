package com.telit.info.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {
	
	public static Gson gson = new Gson();
	
	public static <T> List<T> decodeToList(String str , Class<T> clazz){
		if (!CommonUtil.isEmpty(str)){
			List<T> result = new ArrayList<T>();
			JsonArray array = gson.fromJson(str,JsonArray.class);
			for (int i = 0 ; i < array.size() ; i++){
				JsonElement jo = array.get(i);
				T t = gson.fromJson(jo,clazz);
				result.add(t);
			}
			return result;
		}
		return null;
	}
	
	public static <T> List<T> decodeToList(JsonElement element , Class<T> clazz){
		if (element != null){
			List<T> result = new ArrayList<T>();
			JsonArray array = gson.fromJson(element,JsonArray.class);
			for (int i = 0 ; i < array.size() ; i++){
				JsonElement jo = array.get(i);
				T t = gson.fromJson(jo,clazz);
				result.add(t);
			}
			return result;
		}
		return null;
	}
	
	public static <T> void decodeToList(List<T> list,String str , Class<T> clazz){
		if (!CommonUtil.isEmpty(str)){
			JsonArray array = gson.fromJson(str,JsonArray.class);
			for (int i = 0 ; i < array.size() ; i++){
				JsonElement jo = array.get(i);
				T t = gson.fromJson(jo,clazz);
				list.add(t);
			}
		}
	}
	
	public static <K,V> void decodeToMap(Map<K,V> result,String str , Class<K> clazz1 , Class<V> clazz2){
		if (!CommonUtil.isEmpty(str)){
			Type type = new TypeToken<Map<JsonElement,JsonElement>>(){}.getType();
			Map<JsonElement,JsonElement> temp = gson.fromJson(str,type);
			for (JsonElement key : temp.keySet()){
				JsonElement value = temp.get(key);
				K k = gson.fromJson(key,clazz1);
				V v = gson.fromJson(value,clazz2);
				result.put(k,v);
			}
		}
	}
	
	public static <K,V> Map<K,V> decodeToMap(String str , Class<K> clazz1 , Class<V> clazz2){
		if (!CommonUtil.isEmpty(str)){
			Map<K,V> result = new HashMap<K,V>();
			decodeToMap(result,str,clazz1,clazz2);
			return result;
		}
		return null;
	}
	
	public static <K,V> Map<K,List<V>> decodeToMapList(String str , Class<K> clazz1 , Class<V> clazz2){
		if (!CommonUtil.isEmpty(str)){
			Map<K,List<V>> result = new HashMap<K,List<V>>();
			Type type = new TypeToken<Map<JsonElement,List<JsonElement>>>(){}.getType();
			Map<JsonElement,List<JsonElement>> temp = gson.fromJson(str,type);
			for (JsonElement key : temp.keySet()){
				List<JsonElement> lis = temp.get(key);
				List<V> vs = new ArrayList<V>(); 
				for (int i = 0 ; i < lis.size() ; i++){
					JsonElement value = lis.get(i);
					V v = gson.fromJson(value,clazz2);
					vs.add(v);
				}
				K k = gson.fromJson(key,clazz1);
				result.put(k,vs);
			}
			return result;
		}
		return null;
	}
	
	public static <T> T decodeToObj(String str , Class<T> clazz){
		if (!CommonUtil.isEmpty(str)){
			return gson.fromJson(str,clazz);
		}
		return null;
	}
	
	public static String encodeToStr(Object obj) {
		//做修改
		if (obj == null){
			return "";
		}
		String str = gson.toJson(obj);
		return str;
	}
}
