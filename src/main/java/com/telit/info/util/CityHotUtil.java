package com.telit.info.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.telit.info.data.business.CityHotInfo;
import com.telit.info.data.business.School;

public class CityHotUtil {
	private static String TAB_STR_VALUE = "	";
	public static String STUA_STR_OK = "已处理";
	public static String STUA_STR_NONE = "未处理";
	
	private static String createOrderId() {
		String orderId = UUID.randomUUID().toString();
		return orderId.substring(0,30);
	}
	
	private static String getBusiness(String version,String urlHead,String data,String signKey) {
		String requestUrl = null;
		if (version.equals("v1")) {
			String business = CommonUtil.encodeBase64(data);
			requestUrl = urlHead + business;
		}else {
			String business = CommonUtil.encodeBase64(data);
			String sign1 = business + signKey;
			String sign = MD5.getMD5(sign1);
			requestUrl = urlHead + business + "&sign=" + sign;
		}
		return requestUrl;
	}
	
	/**
	 * 开户
	 * @param school
	 * @param account
	 * @param pwd
	 * @param mealId
	 * @return
	 */
	public static CityHotInfo regist(School school,String account,String pwd,String mealId) {
		String version = school.getApiVersion();
		String netType = school.getNetType();
		String url = school.getConnetIp();
		String terminalId = school.getTerminalId();
		CityHotInfo info = new CityHotInfo();
		String data = null;
		String serialNumber = createOrderId();
		info.setSerialNumber(serialNumber);
		if (version.equals("v1")) {
			data = "001" + account + TAB_STR_VALUE + pwd + TAB_STR_VALUE + mealId + TAB_STR_VALUE + terminalId + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}else {
			data = "001" + account + TAB_STR_VALUE + pwd + TAB_STR_VALUE + mealId + TAB_STR_VALUE + serialNumber;
		}
		String requestUrl = getBusiness(version,url,data,school.getSignkey());
		info.setAccount(account);
		info.setCityGroupId(mealId);
		String date = TimeUtils.nowString();
		if (netType.equals("公网")) {
			String code = HttpUtil.doGet(requestUrl,CommonUtil.DEFAULT_CHARSET);
			info.setSta(STUA_STR_OK);
			String rem = "新户注册-" + code + "-" + date;
			info.setRem(rem);
			info.setCode(code);
		}else {
			info.setSta(STUA_STR_NONE);
		}
		info.setCreateDate(date);
		info.setClstr(requestUrl);
		return info;
	}
	
	/**
	 * 修改密码
	 * @param restTemplate
	 * @param url
	 * @param account
	 * @param pwd
	 * @param terminalId
	 * @return
	 */
	public static CityHotInfo update(School school,String account,String pwd) {
		String version = school.getApiVersion();
		String netType = school.getNetType();
		String url = school.getConnetIp();
		String terminalId = school.getTerminalId();
		CityHotInfo info = new CityHotInfo();
		String data = null;
		String serialNumber = createOrderId();
		info.setSerialNumber(serialNumber);
		if (version.equals("v1")) {
			data = "002" + account + TAB_STR_VALUE + pwd + TAB_STR_VALUE + terminalId + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}else {
			data = "002" + account + TAB_STR_VALUE + pwd + TAB_STR_VALUE + serialNumber ;
		}
		String requestUrl = getBusiness(version,url,data,school.getSignkey());
		info.setAccount(account);
		String date = TimeUtils.nowString();
		if (netType.equals("公网")) {
			String code = HttpUtil.doGet(requestUrl,CommonUtil.DEFAULT_CHARSET);
			info.setSta(STUA_STR_OK);
			String rem = "账号修改-" + code + "-" + date;
			info.setRem(rem);
			info.setCode(code);
		}else {
			info.setSta(STUA_STR_NONE);
		}
		info.setCreateDate(date);
		info.setClstr(requestUrl);
		return info;
	}
	
	/**
	 * 报停
	 * @param restTemplate
	 * @param url
	 * @param account
	 * @param money
	 * @param terminalId
	 * @return
	 */
	public static List<CityHotInfo> stop(School school,String account,String money) {
		String version = school.getApiVersion();
		String netType = school.getNetType();
		String url = school.getConnetIp();
		String terminalId = school.getTerminalId();
		List<CityHotInfo> infos = new ArrayList<CityHotInfo>();
		//先强制下线
		CityHotInfo info1 = kickOff(version,netType,url,terminalId,school.getSignkey(),account);
		infos.add(info1);
		if (!info1.checkSucc()) {
			return infos;
		}
		CityHotInfo info = new CityHotInfo();
		String data = null;
		String serialNumber = createOrderId();
		info.setSerialNumber(serialNumber);
		if (version.equals("v1")) {
			data = "004" + account + TAB_STR_VALUE + money + TAB_STR_VALUE + terminalId + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}else {
			data = "004" + account + TAB_STR_VALUE + money + TAB_STR_VALUE + serialNumber ;
		}
		String requestUrl = getBusiness(version,url,data,school.getSignkey());
		info.setAccount(account);
		String date = TimeUtils.nowString();
		if (netType.equals("公网")) {
			String code = HttpUtil.doGet(requestUrl,CommonUtil.DEFAULT_CHARSET);
			info.setSta(STUA_STR_OK);
			String rem = "账号报停-" + code + "-" + date;
			info.setRem(rem);
			info.setCode(code);
		}else {
			info.setSta(STUA_STR_NONE);
		}
		info.setCreateDate(date);
		info.setClstr(requestUrl);
		infos.add(info);
		return infos;
	}
	
	public static CityHotInfo kickOff(String version,String netType,String url,String terminalId,String signKey,String account) {
		CityHotInfo info = new CityHotInfo();
		String data = null;
		String serialNumber = createOrderId();
		info.setSerialNumber(serialNumber);
		if (version.equals("v1")) {
			data = "014" + account + TAB_STR_VALUE + terminalId + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}else {
			data = "014" + account + TAB_STR_VALUE + serialNumber;
		}
		String requestUrl = getBusiness(version,url,data,signKey);
		info.setAccount(account);
		String date = TimeUtils.nowString();
		if (netType.equals("公网")) {
			String code = HttpUtil.doGet(requestUrl,CommonUtil.DEFAULT_CHARSET);
			info.setSta(STUA_STR_OK);
			String rem = "强制下线-" + code + "-" + date;
			info.setRem(rem);
			info.setCode(code);
		}else {
			info.setSta(STUA_STR_NONE);
		}
		info.setCreateDate(date);
		info.setClstr(requestUrl);
		return info;
	}
	
	/**
	 * 复通
	 * @param restTemplate
	 * @param url
	 * @param account
	 * @param money
	 * @param terminalId
	 * @return
	 */
	public static CityHotInfo restore(School school,String account,String money) {
		String version = school.getApiVersion();
		String netType = school.getNetType();
		String url = school.getConnetIp();
		String terminalId = school.getTerminalId();
		CityHotInfo info = new CityHotInfo();
		String data = null;
		String serialNumber = createOrderId();
		info.setSerialNumber(serialNumber);
		if (version.equals("v1")) {
			data = "005" + account + TAB_STR_VALUE + money + TAB_STR_VALUE + terminalId + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}else {
			data = "005" + account + TAB_STR_VALUE + money + TAB_STR_VALUE + serialNumber;
		}
		String requestUrl = getBusiness(version,url,data,school.getSignkey());
		info.setAccount(account);
		String date = TimeUtils.nowString();
		if (netType.equals("公网")) {
			String code = HttpUtil.doGet(requestUrl,CommonUtil.DEFAULT_CHARSET);
			info.setSta(STUA_STR_OK);
			String rem = "账号复通-" + code + "-" + date;
			info.setRem(rem);
			info.setCode(code);
		}else {
			info.setSta(STUA_STR_NONE);
		}
		info.setCreateDate(date);
		info.setClstr(requestUrl);
		return info;
	}
	
	private static CityHotInfo prevention(String version,String netType,String url,String terminalId,String signKey,String account,String money) {
		String mealId = "2";
		CityHotInfo info = new CityHotInfo();
		String data = null;
		String serialNumber = createOrderId();
		info.setSerialNumber(serialNumber);
		if (version.equals("v1")) {
			data = "009" + account + TAB_STR_VALUE + mealId + TAB_STR_VALUE + money + TAB_STR_VALUE + terminalId + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}else {
			data = "009" + account + TAB_STR_VALUE + mealId + TAB_STR_VALUE + money + TAB_STR_VALUE + serialNumber;
		}
		String requestUrl = getBusiness(version,url,data,signKey);
		info.setAccount(account);
		info.setCityGroupId(mealId);
		String date = TimeUtils.nowString();
		if (netType.equals("公网")) {
			String code = HttpUtil.doGet(requestUrl,CommonUtil.DEFAULT_CHARSET);
			info.setSta(STUA_STR_OK);
			String rem = "预先开户-" + code + "-" + date;
			info.setRem(rem);
			info.setCode(code);
		}else {
			info.setSta(STUA_STR_NONE);
		}
		info.setCreateDate(date);
		info.setClstr(requestUrl);
		return info;
	}
	
	/**
	 * 变更套餐
	 * @param restTemplate
	 * @param url
	 * @param account
	 * @param mealId
	 * @param money
	 * @param terminalId
	 * @return
	 */
	public static List<CityHotInfo> change(School school,String head,String account,String mealId,String money) {
		String version = school.getApiVersion();
		String netType = school.getNetType();
		String url = school.getConnetIp();
		String terminalId = school.getTerminalId();
		String signKey = school.getSignkey();
		List<CityHotInfo> infos = new ArrayList<CityHotInfo>();
		//先强制下线
		CityHotInfo info1 = kickOff(version,netType,url,terminalId,signKey,account);
		infos.add(info1);
		if (!info1.checkSucc()) {
			return infos;
		}
		//然后复通
		CityHotInfo info2 = restore(school,account,money);
		infos.add(info2);
		if (!info2.checkSucc() && !info2.getCode().equals("E84")) {
			return infos;
		}
		//套餐编号改成2
		CityHotInfo info3 = prevention(version,netType,url,terminalId,signKey,account,money);
		infos.add(info3);
		if (!info3.checkSucc() && !info3.getCode().equals("E31")) {
			return infos;
		}
		CityHotInfo info = new CityHotInfo();
		String data = null;
		String serialNumber = createOrderId();
		info.setSerialNumber(serialNumber);
		if (version.equals("v1")) {
			data = "006" + account + TAB_STR_VALUE + mealId + TAB_STR_VALUE + money + TAB_STR_VALUE + terminalId + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}else {
			data = "006" + account + TAB_STR_VALUE + mealId + TAB_STR_VALUE + money + TAB_STR_VALUE + serialNumber + TAB_STR_VALUE;
		}
		String requestUrl = getBusiness(version,url,data,school.getSignkey());
		info.setAccount(account);
		info.setCityGroupId(mealId);
		String date = TimeUtils.nowString();
		if (netType.equals("公网")) {
			String code = HttpUtil.doGet(requestUrl,CommonUtil.DEFAULT_CHARSET);
			info.setSta(STUA_STR_OK);
			String rem = head + code + "-" + date;
			info.setRem(rem);
			info.setCode(code);
		}else {
			info.setSta(STUA_STR_NONE);
		}
		info.setCreateDate(date);
		info.setClstr(requestUrl);
		infos.add(info);
		return infos;
	}
}
