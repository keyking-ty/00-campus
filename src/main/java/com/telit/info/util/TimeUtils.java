package com.telit.info.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 基于joda-time的工具方法
 * 参考资料 
 * http://blog.csdn.net/dhdhdh0920/article/details/7415359
 * http://persevere.iteye.com/blog/1755237
 * timezone 资料http://joda-time.sourceforge.net/timezones.html
 *
 */
public class TimeUtils {
	public static final long SECOND  = 1000L;//单位毫秒
	public static final long _MINUTE = 60L;//单位秒
	public static final long MINUTE  = _MINUTE * SECOND;//单位毫秒
	public static final long _HOUR   = _MINUTE * _MINUTE;//单位秒
	public static final long HOUR    = _MINUTE * MINUTE;//单位毫秒
	public static final long _DAY    = 24L * _HOUR;//单位秒
	public static final long DAY     = 24L * HOUR;//单位毫秒
	
	public static final DateTimeZone tz = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));//设置时区为北京时间
	private static final DateTimeFormatter FORMAT_DAY     = DateTimeFormat.forPattern("yyyy-MM-dd");//自定义日期格式
	private static final DateTimeFormatter FORMAT_CH_YEAR = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");  
	public static final DateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String formatDay(DateTime dt){
		return dt.toString(FORMAT_DAY);
	}
	
	public static String formatDay(long time){
		return formatDay(getTime(time));
	}
	
	public static String formatChYear(DateTime dt){
		return dt.toString(FORMAT_CH_YEAR);
	}
	
	public static String formatChYear(long time){
		return formatChYear(getTime(time));
	}
	
	/**
	 * 检测是否是同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameDay(long time1,long time2){
		return isSameDay(getTime(time1),getTime(time2));
	}
	
	/***
	 * 检测是否是同一天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(DateTime date1 ,DateTime date2){
		return getDays(date1,date2) == 0;
	}
	
	/**
	 * 得到二个时间距离多少天
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getDays(long start,long end){
		return getDays(getTime(start),getTime(end));
	}
	
	/**
	 * 得到二个时间距离多少天
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getDays(DateTime start,DateTime end){
		int days = Days.daysBetween(start,end).getDays();
		return days;
	}
	
	/**
	 * 检测time是今天
	 * @param time
	 * @return
	 */
	public static boolean isSameDay(long time){
		return isSameDay(now(),getTime(time));
	}
	
	/**
	 * time1和time2是否在同一周
	 * @param end
	 * @return
	 */
	public static boolean isSameWeek(long time1,long time2){
		return isSameWeek(getTime(time1),getTime(time2));
	}
	
	/**
	 * 日期是否是同一周
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeek(DateTime date1,DateTime date2){
		return date1.getYear() == date2.getYear() && date1.getWeekyear() == date2.getWeekyear();
	}
	
	/**
	 * 与现在是否同一周
	 * @param end
	 * @return
	 */
	public static boolean isSameWeek(long time){
		return isSameWeek(now(),getTime(time));
	}
	
	/**
	 * 与现在是否同一月
	 * @param end
	 * @return
	 */
	public static boolean isSameMonth(long time){
		return isSameMonth(now(),getTime(time));
	}
	
	/**
	 * time1月time2是否是同一月份
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameMonth(long time1,long time2){
		return isSameMonth(getTime(time1),getTime(time2));
	}
	
	/**
	 * date1月date2是否是同一月份
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameMonth(DateTime date1,DateTime date2){
		return date1.getYear() == date2.getYear() && date1.getMonthOfYear() == date2.getMonthOfYear();
	}
	
	/**
	 * 判断现在是否在指定的时间段
	 * @param start  小时
	 * @param end	  小时
	 * @return
	 */
	public static boolean isInside(int start,int end){
		int now = now().getHourOfDay();
		if(now >= start && now <end){
			return true;
		}
		return false;
	}
	
	/**
	 * 获得每周周几
	 * @return
	 */
	public static int getWeek(){
		return now().getDayOfWeek();
	}
	
	/**
	 * 在指定的周几时间内
	 * @param par
	 * @return
	 */
	public static boolean isWeekDay(int... par){
		int wd = getWeek();
		for (int i : par){
			if (wd == i){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 根据long获得时间
	 * @param time
	 * @return
	 */
	public static DateTime getTime(long time){
		return new DateTime(time);
	}
	
	/**
	 * 根据字符串获得时间 yyyy-MM-dd
	 * @param str
	 * @return
	 */
	public static DateTime getDayTime(String str){
		return  DateTime.parse(str,FORMAT_DAY);  
	}
	
	/**
	 * 根据中文格式字符串获得时间 yyyy-MM-dd HH:mm:ss
	 * @param str
	 * @return
	 */
	public static DateTime getTime(String str){
		if (str.length() > 19) {
			return  DateTime.parse(str.substring(0,19),FORMAT_CH_YEAR); 
		}else {
			return  DateTime.parse(str,FORMAT_CH_YEAR); 
		}
	}
	
	/**
	 * 获取当前时间的字符串
	 * @return
	 */
	public static String nowString(){
		return formatChYear(nowLong());
	}

	/***
	 * 获取当前日期的字符串
	 * @return
	 */
	public static String nowDayString(){
		return formatDay(nowLong());
	}

	/**
	 * 获得当前时间
	 * @return
	 */
	public static DateTime now(){
		return DateTime.now(tz);
	}
	
	/**
	 * 获得当前时间
	 * @return
	 */
	public static long nowLong(){
		return now().getMillis();
	}
	
	/**
	 * 返回两个日期之间的全部日期
	 * @param start
	 * @param end
	 * @return
	 */
	public static String[] getDays(String start,String end){
		DateTime startDate = getTime(start);
		DateTime endDate   = getTime(end);
		Days _days         =  Days.daysBetween(startDate, endDate);
		int num            = _days.getDays();
		String days[]      = new String[num+1];
		days[0]=start;
		for(int i=1 ; i<days.length ; i++){
			DateTime d = startDate.plusDays(i);
			days[i]    = d.toString(FORMAT_DAY);
		}
		return days;
	}
	
	/**
	 * 增加秒数
	 * @param time
	 * @return
	 */
	public static synchronized Timestamp addSecond(long paramLong , int paramInt) {
		return new Timestamp(paramLong + paramInt * SECOND);
	}
	

	/**
	 * 取得今天某一时、分的毫秒数
	 * @return
	 */
	public static long getSecondsToClock(int hour, int minute , int second){
		DateTime time = now();
		long longTime = time.getMillis();
		int _hour     = time.getHourOfDay();
		int _minute   = time.getMinuteOfHour();
		int _second   = time.getSecondOfMinute();
		int _ms       = time.getMillisOfSecond();
		long temp1 = _hour * HOUR + _minute * MINUTE + _second * SECOND + _ms;
		longTime -= temp1;
		long temp2 = hour * HOUR + minute * MINUTE + second * SECOND;
		longTime += temp2;
		return longTime;
	}
	
	/**
	 * 取得现在距离今天某时刻的时间差值
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getDesSecondsToClock(int hour, int minute , int second){
		DateTime time = now();
		int _hour     = time.getHourOfDay();
		int _minute   = time.getMinuteOfHour();
		int _second   = time.getSecondOfMinute();
		long temp1    = _hour * _HOUR + _minute * _MINUTE + _second;
		long temp2    = hour * _HOUR + minute * _MINUTE  + second;
		return Math.max(0,temp2-temp1);
	}
	
	/**
	 * 取得现在距离   某年-某月-某日   某时:某分:某秒的毫秒
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getSecondesToDayAndClock(int year, int month, int day, int hour, int minute, int second) {
		DateTime tar = new DateTime(year,month,day,hour, minute,second);
		return tar.getMillis() - nowLong();
	}
	
	/**
	 * 判断当前时间是否在两个时间段内 
	 * @param time
	 * @return
	 */
	public static boolean timeIsBetween(String start, String end) {
		DateTime time1 = getTime(start);
		DateTime time2 = getTime(end);
		DateTime now = now();
		return now.isAfter(time1) && now.isAfter(time2);
	}
	
	
	/**
	 * 返回昨天的日期 2016-07-03
	 * @param time
	 * @return
	 */
	public static String yesterday() {
		long now   = nowLong();
		now -= DAY;
		return formatDay(now);
	}
	
	public static long getTodayZeroTime(){
		return getZeroTime(now());
	}
	
	public static long getZeroTime(long time){
		return getZeroTime(new DateTime(time));
	}
	
	public static long getZeroTime(DateTime time){
		int year  = time.getYear();
		int month = time.getMonthOfYear();
		int day   = time.getDayOfMonth();
		DateTime temp = new DateTime(year,month,day,0,0,0);
		return temp.getMillis();
	}
	
	//获得指定时间的毫秒值
	public static long getRequireTime(DateTime time,int hour,int minute,int second){
		int year  = time.getYear();
		int month = time.getMonthOfYear();
		int day   = time.getDayOfMonth();
		DateTime temp = new DateTime(year,month,day,hour,minute,second);
		return temp.getMillis();
	}
	public static long getRequireTime(long timeMillis,int hour,int minute,int second){
		DateTime time = new DateTime(timeMillis);
		int year  = time.getYear();
		int month = time.getMonthOfYear();
		int day   = time.getDayOfMonth();
		DateTime temp = new DateTime(year,month,day,hour,minute,second);
		return temp.getMillis();
	}
	
	
	public static boolean checkDateInDay(String start,int day) {
		if (start == null || start.isEmpty()) {
			return false;
		}
		DateTime time1 = null;
		if (start.length() == 10) {
			time1 = DateTime.parse(start,FORMAT_DAY);
		}else{
			time1 = DateTime.parse(start,FORMAT_CH_YEAR);
		}
		int des = getDays(time1,now());
		return des <= day;
	}
	
	public static boolean isFirstDayOfMonth(){
		DateTime now = now();
		int day = now.getDayOfMonth();
		return day == 1;
	}

}
