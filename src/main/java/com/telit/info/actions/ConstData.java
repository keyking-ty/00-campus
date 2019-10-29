package com.telit.info.actions;

import java.util.HashMap;
import java.util.Map;

import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.AlbumHot;
import com.telit.info.data.business.CardMeal;
import com.telit.info.data.business.NetMeal;
import com.telit.info.data.business.School;

public class ConstData {
	public static final String REQ_PAY_HEAD = "/pay";
	public static final String REQ_PAY_WECHAT = "/wechat/result";
	public static final String REQ_PAY_ALY = "/ali/result";
	public static final String REQ_PAY_CANCLE = "/bckMoney";
	
	public static final String WECHAT_RESUL_SUCC = "SUCCESS";
	public static final String WECHAT_RESUL_FAIL = "FAIL";
	public static final String WECHAT_RESUL_OK   = "OK";
	public static final String ORDER_STA_OK      = "交易成功";
	public static final String PAY_STA_OK        = "支付成功";
	public static final String PAY_STA_WAIT      = "等待支付";
	public static final String PAY_STA_CANCLE    = "已退款";
	public static final String PAY_TYPE_WECHAT   = "微信支付";
	public static final String PAY_TYPE_ALI      = "支付宝支付";
	public static final String PAY_TYPE_OUT_LINE = "线下支付";
	public static final String ORDER_TYPE_NET_MEAL = "套餐充值";
	
	/***
	 * 菜单的编号
	 */
	public static final int ROLE_MRG_MENU_ID    = 6010;//角色管理
	public static final int SYS_USER_MENU_ID    = 6020;//系统用户
	public static final int SCHOOL_MRG_MENU_ID  = 6050;//学校管理
	public static final int MEAL_MRG_MENU_ID    = 6100;//宽带管理
	public static final int NUMBER_MRG_MENU_ID  = 6101;//号码管理
	public static final int CM_MRG_MENU_ID      = 6103;//套餐管理
	public static final int ORDER_MRG_MENU_ID   = 6104;//订单管理
	public static final int USER_MRG_MENU_ID    = 6200;//用户管理
	public static final int MORE1_MENU_ID       = 6201;//批量开户
	public static final int MORE2_MENU_ID       = 6202;//批量融合
	public static final int MORE3_MENU_ID       = 6203;//批量报停
	public static final int MORE4_MENU_ID       = 6204;//批量复通
	public static final int MORE5_MENU_ID       = 6205;//批量开通
	public static final int ALBUM_MENU_ID       = 6300;//专辑管理
	public static final int ALBUM_CURRICULUM_ID = 6301;//课程管理
	public static final int ALBUM_HOT_ID        = 6302;//专辑推荐管理
	public static final int INFO_MENU_ID        = 6501;//信息管理
	public static final int GOODS_MENU_ID       = 6501;//物品管理
	public static final int MARKET_MENU_ID      = 6502;//二手市场
	public static final int REPAIR_MENU_ID      = 6503;//宽带保修
	public static final int ADVERT_MENU_ID      = 6504;//广告管理

	public static String[] NumberImportModulTitles = {
		"号码(必须)","运营商(必须)","可选套餐编号(多个套餐用半角逗号分隔)","类型",
		"预存话费","学校(必须)","省份(必须)",
		"城市(必须)","区/县","状态(必须:已售|待售)",
		"操作(insert|update|delete)"
	};
	
	public static final String NumberImportErrorMsg = "内容不匹配请按下载模本修改";
	public static String[] OpenUserImportModulTitles = {
		"学号(必须)","学校(必须)","套餐(必须)","身份证(必须)"
	};
	public static String[] FuseUserImportModulTitles = {
		"学号(必须)","学校(必须)","套餐(必须)","身份证(必须)","套餐号码(必须)"
	};
	public static String[] StopUserImportModulTitles = {
		"学号(必须)","学校(必须)","身份证(必须)"
	};
	public static String[] RestoreUserImportModulTitles = {
		"学号(必须)","学校(必须)","身份证(必须)"
	};
	public static String[] ReportModulTitles = {
		"商品名称","类型","交易金额(单位:元)","交易数量"
	};
	public static String[] MealDetailModulTitles = {
		"套餐编号","套餐名称","用户名字","手机号码","学校"
	};
	public static final String OPERATE_SUCC_STR = " succ";
	public static final String OPERATE_FAIL_STR = " fail >>> ";
	public static Map<String,String> CITY_HOT_OPEN_ERROR   = new HashMap<String,String>();
	public static Map<String,String> CITY_HOT_UPDATE_ERROR = new HashMap<String,String>();
	public static Map<String,String> CITY_HOT_KICK_ERROR   = new HashMap<String,String>();
	public static Map<String,String> CITY_HOT_RESTORE_ERROR = new HashMap<String,String>();
	public static Map<String,String> CITY_HOT_CHANGE_ERROR = new HashMap<String,String>();
	public static Map<String,String> CITY_HOT_STOP_ERROR   = new HashMap<String,String>();
	public static Map<Class<?>,Map<String,String>> CHEKPRO_ERROR_TIPS  = new HashMap<Class<?>,Map<String,String>>();
	static {
		CITY_HOT_OPEN_ERROR.put("E10", "注册账号为空");
    	CITY_HOT_OPEN_ERROR.put("E11", "注册账号超长");
    	CITY_HOT_OPEN_ERROR.put("E12", "注册账号字符非法");
    	CITY_HOT_OPEN_ERROR.put("E13", "注册账号已存在");
    	CITY_HOT_OPEN_ERROR.put("E20", "密码为空");
    	CITY_HOT_OPEN_ERROR.put("E21", "密码长度非法");
    	CITY_HOT_OPEN_ERROR.put("E22", "密码字符非法");
    	CITY_HOT_OPEN_ERROR.put("E30", "套餐ID非法");
    	CITY_HOT_OPEN_ERROR.put("E44", "终端代码非法");
    	CITY_HOT_OPEN_ERROR.put("E45", "流水号非法");
    	CITY_HOT_OPEN_ERROR.put("E83", "注册户数超限");
    	CITY_HOT_OPEN_ERROR.put("E99", "未预期错误");
    	
    	CITY_HOT_UPDATE_ERROR.put("E14", "账号不存在");
    	CITY_HOT_UPDATE_ERROR.put("E20", "密码为空");
    	CITY_HOT_UPDATE_ERROR.put("E21", "密码长度非法");
    	CITY_HOT_UPDATE_ERROR.put("E22", "密码字符非法");
    	CITY_HOT_UPDATE_ERROR.put("E44", "终端代码非法");
    	CITY_HOT_UPDATE_ERROR.put("E45", "流水号非法");
    	CITY_HOT_UPDATE_ERROR.put("E99", "未预期错误");
    	
    	CITY_HOT_KICK_ERROR.put("E14", "账号不存在");
    	CITY_HOT_KICK_ERROR.put("E44", "终端代码非法");
    	CITY_HOT_KICK_ERROR.put("E45", "流水号非法");
    	CITY_HOT_KICK_ERROR.put("E99", "未预期错误");
    	
    	CITY_HOT_RESTORE_ERROR.put("E14", "账号不存在");
    	CITY_HOT_RESTORE_ERROR.put("E34", "存在预约套餐记录");
    	CITY_HOT_RESTORE_ERROR.put("E44", "终端代码非法");
    	CITY_HOT_RESTORE_ERROR.put("E45", "流水号非法");
    	CITY_HOT_RESTORE_ERROR.put("E84", "账号状态不符");
    	CITY_HOT_RESTORE_ERROR.put("E99", "未预期错误");
    	
    	CITY_HOT_CHANGE_ERROR.put("E14", "账号不存在");
    	CITY_HOT_CHANGE_ERROR.put("E30", "套餐ID非法");
    	CITY_HOT_CHANGE_ERROR.put("E31", "套餐变更无效");
    	CITY_HOT_CHANGE_ERROR.put("E44", "终端代码非法");
    	CITY_HOT_CHANGE_ERROR.put("E45", "流水号非法");
    	CITY_HOT_CHANGE_ERROR.put("E82", "账号在线");
    	CITY_HOT_CHANGE_ERROR.put("E99", "未预期错误");
    	
    	CITY_HOT_STOP_ERROR.put("E14", "账号不存在");
    	CITY_HOT_STOP_ERROR.put("E44", "终端代码非法");
    	CITY_HOT_STOP_ERROR.put("E45", "流水号非法");
    	CITY_HOT_STOP_ERROR.put("E82", "账号在线");
    	CITY_HOT_STOP_ERROR.put("E99", "未预期错误");
    	
    	Map<String,String> temp = new HashMap<String,String>();
    	temp.put("name","角色名已存在");
    	CHEKPRO_ERROR_TIPS.put(Role.class,temp);
    	
    	temp = new HashMap<String,String>();
    	temp.put("schoolName","学校名已存在");
    	CHEKPRO_ERROR_TIPS.put(School.class,temp);
    	
    	temp = new HashMap<String,String>();
    	temp.put("name","套餐名已存在");
    	CHEKPRO_ERROR_TIPS.put(NetMeal.class,temp);
    	
    	temp = new HashMap<String,String>();
    	temp.put("userName","用户名已存在");
    	CHEKPRO_ERROR_TIPS.put(User.class,temp);
    	
    	temp = new HashMap<String,String>();
    	temp.put("loginName","账号已注册");
    	temp.put("account","学号已存在");
    	temp.put("mobile","手机号已注册");
    	CHEKPRO_ERROR_TIPS.put(AppUser.class,temp);
    	
    	temp = new HashMap<String,String>();
    	temp.put("aId","专辑推荐已存在");
    	CHEKPRO_ERROR_TIPS.put(AlbumHot.class,temp);
    	
    	temp = new HashMap<String,String>();
    	temp.put("name","套餐名已存在");
    	CHEKPRO_ERROR_TIPS.put(CardMeal.class,temp);
	}	
}
