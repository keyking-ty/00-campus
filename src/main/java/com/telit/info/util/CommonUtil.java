package com.telit.info.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.telit.info.actions.BooleanAction;
import com.telit.info.actions.BooleanIndexAction;
import com.telit.info.actions.ConstData;
import com.telit.info.actions.SelectAction;
import com.telit.info.actions.VoidAction;
import com.telit.info.actions.VoidIndexAction;
import com.telit.info.data.FileData;
import com.telit.info.data.ImportResult;
import com.telit.info.data.SqlRule;

public class CommonUtil {
	private static Random _random = new SecureRandom();
	private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String DEFAULT_CHARSET = "UTF-8";
	private static final Pattern phonePattern = Pattern.compile("1\\d{10}");
	
	public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
	
	public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }
	
	public static boolean isArrayEmpty(Collection<?> cols) {
		return cols == null || cols.size() == 0;
	}
	
	public static boolean checkMobile(String mobiles) {
		if (mobiles == null || mobiles.length() != 11){
			return false;
		}
        return phonePattern.matcher(mobiles).matches();
    }
	
	@SuppressWarnings("unchecked")
	public  static <T> List<T> split(String str, String flag,Class<T> clazz) {
		if (isEmpty(str) || str.equals("null")) {
			return null;
		}
		List<T> result = new ArrayList<T>();
		String[] ss = str.split(flag);
		for (String s : ss) {
			if (isEmpty(s)) {
				continue;
			}
			if (clazz == String.class) {
				result.add((T)s);
			}else {
				T t = JsonUtil.decodeToObj(s,clazz);
				result.add(t);
			}
		}
		return result;
	}
	
	public static String merge(Object[] datas, String flag) {
		if (datas != null && datas.length > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(datas[0]);
			for (int i = 1 ; i < datas.length ; i++) {
				sb.append(flag).append(datas[i]);
			}
			return sb.toString();
		}
		return "null";
	}
	
	public static ImportResult writeFile(String path, String fileName, List<String> datas) {
		OutputStream out = null;
		BufferedOutputStream buffer = null;
		ImportResult result = new ImportResult();
		result.setFileName(fileName);
		result.setOperateTime(TimeUtils.nowString());
		try {
			File dest = new File(path + "/" + fileName);
			if (!dest.getParentFile().exists()) {
				// 判断文件父目录是否存在
				dest.getParentFile().mkdir();
			}
			dest.createNewFile();
			out = new FileOutputStream(dest);
			buffer = new BufferedOutputStream(out);
			for (String data : datas) {
				if (data.contains(ConstData.OPERATE_SUCC_STR)) {
					result.addSucc();
				}else{
					result.addFail();
				}
				String str = data + "\r\n";
				buffer.write(str.getBytes());
			}
			buffer.flush();
			buffer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.flush();
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static <T> void removeByCondition(List<T> datas, BooleanAction<T> filter) {
		if (filter != null && datas != null) {
			for (int i = 0 ; i < datas.size();) {
				if (filter.doAction(datas.get(i))) {
					i++ ;
				} else {
					datas.remove(i);
				}
			}
		}
	}
	
	public static <T> boolean search(List<T> datas, BooleanAction<T> filter) {
		return searchObj(datas,filter) != null;
	}
	
	public static <T> T searchObj(List<T> datas, BooleanAction<T> filter) {
		if (filter != null && datas != null) {
			for (T data : datas) {
				if (filter.doAction(data)) {
					return data ;
				}
			}
		}
		return null;
	}
	
	public static <T> void forEach(List<T> datas , VoidAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.size() ; i++) {
				action.doAction(datas.get(i));
			}
		}
	}
	
	public static <T> void forEach(List<T> datas , VoidIndexAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.size() ; i++) {
				action.doAction(i,datas.get(i));
			}
		}
	}
	
	public static <T> void forEachBreak(List<T> datas,BooleanAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.size() ; i++) {
				if (action.doAction(datas.get(i))) {
					break;
				}
			}
		}
	}
	
	public static <T> void forEachBreak(List<T> datas,BooleanIndexAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.size() ; i++) {
				if (action.doAction(i,datas.get(i))) {
					break;
				}
			}
		}
	}
	
	public static <T> void forEachContinue(List<T> datas,BooleanAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.size() ; i++) {
				if (action.doAction(datas.get(i))) {
					continue;
				}
			}
		}
	}
	
	public static <T> void forEachContinue(List<T> datas,BooleanIndexAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.size() ; i++) {
				if (action.doAction(i,datas.get(i))) {
					continue;
				}
			}
		}
	}
	
	public static <T> void forEach(T[] datas , VoidAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.length ; i++) {
				action.doAction(datas[i]);
			}
		}
	}
	
	public static <T> void forEach(T[] datas , VoidIndexAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.length ; i++) {
				action.doAction(i,datas[i]);
			}
		}
	}
	
	public static <T> void forEachBreak(T[] datas,BooleanAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.length ; i++) {
				if (action.doAction(datas[i])) {
					break;
				}
			}
		}
	}
	
	public static <T> void forEachBreak(T[] datas,BooleanIndexAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.length ; i++) {
				if (action.doAction(i,datas[i])) {
					break;
				}
			}
		}
	}
	
	public static <T> void forEachContinue(T[] datas,BooleanAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.length ; i++) {
				if (action.doAction(datas[i])) {
					continue;
				}
			}
		}
	}
	
	public static <T> void forEachContinue(T[] datas,BooleanIndexAction<T> action) {
		if (action != null && datas != null) {
			for (int i = 0 ; i < datas.length ; i++) {
				if (action.doAction(i,datas[i])) {
					continue;
				}
			}
		}
	}
	
	public static FileData getDataFromFile(String path,String fileName) throws Exception{
		File file = null;
		if (fileName.startsWith("/")) {
			file = new File(path + fileName);
		}else {
			file = new File(path + "/" + fileName);
		}
		if (file.exists()) { //判断文件父目录是否存在
			FileData fileLog = new FileData();
			fileLog.setName(fileName);
			byte[] buffer = new byte[1024];
			FileInputStream fis = null; //文件输入流
			BufferedInputStream bis = null;
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			int i = bis.read(buffer);
			while (i != -1) {
				fileLog.add(buffer,i);
				i = bis.read(buffer);
			}
			fis.close();
			return fileLog;
		}
		return null;
	}
	
	public static void deleteFile(String path,String fileName){
		try {
			if (fileName == null || fileName.isEmpty()) {
				return;
			}
			File file = new File(path + "/" + fileName);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			
		}
	}
	
	public static int random(int len) {
		return _random.nextInt(len);
	}
	
	public static int random(int min,int max) {
		if (max < min) {
			return 0;
		}
		int n = _random.nextInt(max + 1 - min) + min;
		return n;
	}
	
	public static String randomStr(int len) {
		StringBuffer sb = new StringBuffer();
		int clen = SYMBOLS.length();
		while (len > 0) {
			int index = random(clen);
			char c    = SYMBOLS.charAt(index);
			sb.append(c);
			len--;
		}
		return sb.toString();
	}
	
	public static <S,T> List<T> selectProp(List<S> srcs,SelectAction<S,T> action){
		List<T> result = new ArrayList<T>();
		if (srcs != null) {
			for (int i = 0 ; i < srcs.size() ; i++) {
				T t = action.doAction(srcs.get(i));
				if (t != null) {
					result.add(t);
				}
			}
		}
		return result;
	}
	
	private static void initSelfRule(Map<String,SqlRule> rules,String table,Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0 ; i < fields .length ; i++) {
			Field field = fields[i];
			if (field.getAnnotation(Transient.class) != null) {
				continue;
			}
			SqlRule rule = new SqlRule();
			rule.setKey(field.getName());
			rule.setType(field.getType());
			Column colum = field.getAnnotation(Column.class);
			if (colum != null){
				rule.setColumn(colum.name());
			}else{
				rule.setColumn(field.getName());
			}
			rule.setTable(table);
			rules.put(field.getName(),rule);
		}
	}
	
	public static void initSqlRule(Map<String,SqlRule> rules,String table,Class<?> clazz) {
		if (clazz == null) {
			return ;
		}
		initSelfRule(rules,table,clazz);
		initSqlRule(rules,table,clazz.getSuperclass());
	}
	
	private static Class<?> getSelfFieldType(Class<?> clazz,String fieldName){
		try {
			Field field = clazz.getDeclaredField(fieldName);
			if (field != null) {
				return field.getType();
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getClassFieldType(Class<?> clazz , String fieldName) {
		if (clazz == null) {
			return null;
		}
		Class<?> type = getSelfFieldType(clazz,fieldName);
		if (type != null) {
			return type;
		}else {
			return getClassFieldType(clazz.getSuperclass(),fieldName);
		}
	}
	
	public static String encodeBase64(String src) {
		try {
			byte[] textByte = src.getBytes(DEFAULT_CHARSET);
			String encodedText = Base64.getEncoder().encodeToString(textByte);
			return encodedText;
		} catch (Exception e) {
			
		}
		return null;
	}
	
	public static String getSuffix(String str){
		return str = str.substring(str.lastIndexOf(".")+1);
	}
	
	public static boolean checkFileExt(String fileExt,String imageType) {
		// 定义允许上传的文件扩展名
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", imageType);
        /* 现在只有图片 extMap.put("flash", "swf,flv"); extMap.put("media",
         * "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
         * extMap.put("file",
         * "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
         */
		return Arrays.asList(extMap.get("image").toLowerCase().split(",")).contains(fileExt);
	}
	
}
