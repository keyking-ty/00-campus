package com.telit.info.web;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.wxpay.sdk.WXPay;
import com.wxpay.sdk.WXPayConfig;
import com.wxpay.sdk.WXPayUtil;
import com.telit.info.actions.ConstData;
import com.telit.info.actions.ImportType;
import com.telit.info.data.FileData;
import com.telit.info.data.ImportResult;
import com.telit.info.data.IntegerKey;
import com.telit.info.data.RespResult;
import com.telit.info.data.admin.Menu;
import com.telit.info.data.admin.MenuOperate;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.RoleMenu;
import com.telit.info.data.admin.RoleSchool;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.app.PayTran;
import com.telit.info.data.business.CardInfo;
import com.telit.info.data.business.CityHotInfo;
import com.telit.info.data.business.NetMeal;
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.MenuMapper;
import com.telit.info.trans.mapper.RoleMapper;
import com.telit.info.trans.mapper.SchoolMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CityHotUtil;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.TimeUtils;
import com.telit.info.web.config.AliPayConfig;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

public class NetUitl {
	
	public static List<Role> selectRolesByUserId(DataService dataService, String type , Integer id) {
		RoleMapper mapper = dataService.getMapper(Role.class);
		List<Role> roles = mapper.queryRoleByUser(id,type);
		return roles;
	}
	
	public static List<Menu> selectMenusByRoleId(DataService dataService, Integer roleId) {
		MenuMapper mapper = dataService.getMapper(Menu.class);
		return mapper.getAllMenusByRole(roleId);
	}
	
	public static void changeRolePermission(DataService dataService, Integer roleId, Integer type, String str) {
		int _type = type.intValue();
		if (_type == 0) {
			Example example = new Example(RoleMenu.class);
			example.createCriteria().andEqualTo("roleId",roleId);
			dataService.deleteByExample(example,RoleMenu.class);
			List<Integer> ids = CommonUtil.split(str, ",",Integer.class);
			CommonUtil.forEach(ids, (data) -> {
				RoleMenu trolemenu = new RoleMenu();
				trolemenu.setRoleId(roleId);
				trolemenu.setMenuId(data);
				dataService.insert(trolemenu);
			});
		} else if (_type == 1) {
			Example example = new Example(RoleSchool.class);
			example.createCriteria().andEqualTo("roleId",roleId);
			dataService.deleteByExample(example,RoleSchool.class);
			List<Integer> ids = CommonUtil.split(str, ",",Integer.class);
			CommonUtil.forEach(ids, (data) -> {
				RoleSchool ms = new RoleSchool();
				ms.setSchoolId(data);
				ms.setRoleId(roleId);
				dataService.insert(ms);
			});
		} else if (_type == 2) {
			Example example = new Example(MenuOperate.class);
			example.createCriteria().andEqualTo("roleId",roleId);
			dataService.deleteByExample(example,MenuOperate.class);
			//添加新的权限
			if (!StringUtils.isEmpty(str)) {
				String[] ss = str.split(",");
				String flag = "m_";
				for (int i = 0; i < ss.length;) {
					String s = ss[i];
					if (s.startsWith(flag)) {
						s = s.substring(2);
						Integer menuId = Integer.parseInt(s);
						do {
							i++;
							if (i == ss.length) {
								break;
							}
							String oid = ss[i];
							if (oid.startsWith(flag)) {
								break;
							}
							Integer operateId = Integer.parseInt(oid);
							MenuOperate ms = new MenuOperate();
							ms.setMenuId(menuId);
							ms.setOperateId(operateId);
							ms.setRoleId(roleId);
							dataService.insert(ms);
						} while (true);
					}
				}
			}
		}
	}

	
	public static RespResult checkDataProp(DataService dataService,Class<?> type, Object... objs) {
		if (objs == null || objs.length != 2) {
			return null;
		}
		RespResult result = new RespResult();
		for (int i = 0 ; i < objs.length ;) {
			Object value  = objs[i++];
			String method = objs[i++].toString();
			Example example = new Example(type);
			Criteria conds = example.createCriteria();
			conds.andEqualTo(method,value);
			Object target = dataService.search(example,type);
			if (target != null) {
				String tips = ConstData.CHEKPRO_ERROR_TIPS.get(type).get(method);
				result.setMsg(tips);
				int id = ((IntegerKey)target).getId();
				result.setState(id);
				return result;
			}
		}
		return result;
	}
	
	public static boolean checkOperate(DataService dataService, Role role, Integer menuId, Integer operateId) {
		Example example = new Example(MenuOperate.class);
		example.createCriteria().andEqualTo("roleId",role.getId()).andEqualTo("menuId",menuId)
		.andEqualTo("operateId",operateId);
		return dataService.count(example,MenuOperate.class) > 0;
	}
	
	public static boolean checkSchool(DataService dataService, Role role, Integer schoolId) {
		Example example = new Example(RoleSchool.class);
		example.createCriteria().andEqualTo("roleId",role.getId()).andEqualTo("schoolId",schoolId);
		return dataService.count(example,RoleSchool.class) > 0 ;
	}

	public static List<School> getRolePermissionSchool(DataService dataService, Integer roleId) {
		List<School> schools = null;
		if (roleId.intValue() == -99) {
			schools = dataService.all(null,School.class);
		} else {
			SchoolMapper mapper = dataService.getMapper(School.class);
			schools = mapper.queryByRole(roleId);
		}
		return schools;
	}
	
	public static String getAppUserInitData(DataService dataService,Integer roleId) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role role = dataService.get(roleId,Role.class);
		List<School> schools = getRolePermissionSchool(dataService,roleId);
		List<Integer> ids = CommonUtil.selectProp(schools, (school)->school.getId());
		//套餐需要检查运营商权限和学校权限
		Example example = new Example(NetMeal.class);
		Criteria conds = example.createCriteria();
		if (ids.size() > 0) {
			conds.andIn("schoolId",ids);
		}
		if (!role.getOperator().equals("全部")) {
			conds.andEqualTo("operator",role.getOperator());
		}
		List<NetMeal> meals = dataService.all(example,NetMeal.class);
		//卡号需要检查运营商权限和学校权限
		example = new Example(CardInfo.class);
		conds = example.createCriteria();
		if (ids.size() > 0) {
			conds.andIn("schoolId",ids);
		}
		if (!role.getOperator().equals("全部")) {
			conds.andEqualTo("operator",role.getOperator());
		}
		List<CardInfo> cards = dataService.all(example,CardInfo.class);
		resultmap.put("schools", schools);
		resultmap.put("meals", meals);
		resultmap.put("cards",cards);
		resultmap.put("state","success");
		return JsonUtil.encodeToStr(resultmap);
	}
	
	
	
	public static void downLog(DataService dataService,HttpServletResponse response,Integer id,String logPath) {
		try {
			ImportResult logResult = dataService.get(id,ImportResult.class);
			if (logResult == null) {
				return;
			}
			String logName = logResult.getFileName();
			FileData fileData = CommonUtil.getDataFromFile(logPath,logName);
			if (fileData != null) {
				response.setContentType("application/octet-stream; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileData.getName(), "UTF-8"));
				response.getOutputStream().write(fileData.getData());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T getDataByConditions(DataService dataService ,Class<T> type, Object... objs) {
		Example example = new Example(type);
		Criteria conds = example.createCriteria();
		for (int i = 0 ; i < objs.length ;) {
			try {
				Object value  = objs[i++];
				String method = objs[i++].toString();
				conds.andEqualTo(method,value);
			} catch (Exception e) {
				break;
			}
		}
		Object target = dataService.search(example,type);
		if (target != null) {
			return (T) target;
		}
		return null;
	}
	
	public static List<RoleSchool> getRoleSchools(DataService dataService,Integer roleId){
		Example example = new Example(RoleSchool.class);
		example.createCriteria().andEqualTo("roleId",roleId);
		example.setOrderByClause("id asc");
		return dataService.all(example,RoleSchool.class);
	}
	
	private static School getSchoolFromCache(DataService dataService,String schoolName,Map<String,School> schools) {
		School school = schools.get(schoolName);
		if (school == null) {
			Example example = new Example(School.class);
			example.createCriteria().andEqualTo("schoolName",schoolName);
			school = dataService.search(example,School.class);
			if (school != null) {
				schools.put(schoolName,school);
			}
		}
		return school;
	}
	
	private static RoleSchool getRoleSchoolFromCache(DataService dataService,Integer schoolId,Integer roleId,Map<Integer,RoleSchool> rss) {
		RoleSchool school = rss.get(schoolId);
		if (school == null) {
			Example example = new Example(RoleSchool.class);
			example.createCriteria().andEqualTo("roleId",roleId).andEqualTo("schoolId",schoolId);
			school = dataService.search(example,RoleSchool.class);
			if (school != null) {
				rss.put(schoolId,school);
			}
		}
		return school;
	}
	
	private static NetMeal getNetMealFromCache(DataService dataService,Integer id,Map<Integer,NetMeal> meals) {
		NetMeal meal = meals.get(id);
		if (meal == null) {
			meal = dataService.get(id,NetMeal.class);
			if (meal != null) {
				meals.put(id,meal);
			}
		}
		return meal;
	}
	
	public static String importMoreData(DataService dataService,ImportType importType,List<List<String>> datas,Integer roleId,String auther,String logPath) {
		int type = importType.ordinal();
		String fileName = UUID.randomUUID().toString() + ".log";
		Role role = dataService.get(roleId, Role.class);
		List<String> logs = new ArrayList<>();
		ImportType it = null;
		String sn = null;
		String mn = null;
		if (type == ImportType.card.ordinal()) {
			it = ImportType.card;
			for (int i = 0; i < datas.size(); i++) {
				List<String> cols = datas.get(i);
				String _type = cols.get(10);
				String cardNumber = cols.get(0);
				String head = _type + " " + cardNumber;
				String operator = cols.get(1);
				if (cardNumber == null || cardNumber.isEmpty()) {
					logs.add(head + ConstData.OPERATE_FAIL_STR + "号码不能为空");
					continue;
				}
				if (!role.checkOperator(operator)) {
					logs.add(head + ConstData.OPERATE_FAIL_STR + "运营商权限不足");
					continue;
				}
				if ("delete".equals(_type)) {
					String result = delCard(dataService,cardNumber);
					logs.add(result);
				} else if ("insert".equals(_type) || "update".equals(_type)) {
					if (operator == null || operator.isEmpty()) {
						logs.add(head + ConstData.OPERATE_FAIL_STR + "未配置运营商");
						continue;
					}
					String province = cols.get(6);
					if (province == null || province.isEmpty()) {
						logs.add(head + ConstData.OPERATE_FAIL_STR + "未配置省份");
						continue;
					}
					String city = cols.get(7);
					if (city == null || city.isEmpty()) {
						logs.add(head + ConstData.OPERATE_FAIL_STR + "未配置城市");
						continue;
					}
					String statu = cols.get(9);
					if (statu == null || statu.isEmpty()) {
						logs.add(head + ConstData.OPERATE_FAIL_STR + "未配置状态");
						continue;
					}
					String schoolName = cols.get(5);
					if (schoolName == null || schoolName.isEmpty()) {
						logs.add(head + ConstData.OPERATE_FAIL_STR + "未配置学校");
						continue;
					}
					String predict = cols.get(4);
					float predictValue = 0;
					if (predict != null && !predict.isEmpty()) {
						if (!isNumeric(predict)) {
							logs.add(head + ConstData.OPERATE_FAIL_STR + "预存话费必须是数字");
							continue;
						}
						predictValue = Float.parseFloat(predict);
					}
					CardInfo card = new CardInfo();
					card.setCardNumber(cardNumber);
					card.setCardType(cols.get(3));
					card.setPredictValue(predictValue);
					card.setOperator(operator);
					card.setProvince(province);
					card.setCity(city);
					card.setArea(cols.get(8));
					card.setMeals(cols.get(2));
					card.setAuther(auther);
					card.setStatu(statu);
					card.setSchoolName(schoolName);
					String result = changeCard(dataService,_type, head, role.getId(), card);
					logs.add(result);
				} else {
					logs.add("执行  " + cardNumber + ConstData.OPERATE_FAIL_STR + "操作指令错误:[insert|update|delete]");
				}
			}
		} else if (type == ImportType.regist.ordinal()) {
			Map<String,School> schools = new HashMap<String,School>();
			Map<Integer,RoleSchool> rss = new HashMap<Integer,RoleSchool>();
			Map<Integer,NetMeal> meals = new HashMap<Integer,NetMeal>();
			it = ImportType.regist;
			String head = it.getValue() + " >> ";
			for (int i = 0; i < datas.size(); i++) {
				List<String> cols = datas.get(i);
				int row = i + 2;
				String account = cols.get(0).trim();
				if (StringUtils.isEmpty(account)) {
					logs.add(head + ConstData.OPERATE_FAIL_STR + "第" + row + "行的学号未配置");
					continue;
				}
				String identfy = cols.get(3).trim();
				if (StringUtils.isEmpty(identfy)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证为空");
					continue;
				}
				if (identfy.length() < 18) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证长度非法");
					continue;
				}
				String schoolName = cols.get(1).trim();
				if (StringUtils.isEmpty(schoolName)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "学校名称未配置");
					continue;
				}
				if (sn == null) {
					sn = schoolName;
				}
				School school = getSchoolFromCache(dataService,schoolName,schools);
				if (school == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到学校名称：" + schoolName);
					continue;
				}
				RoleSchool rs = getRoleSchoolFromCache(dataService,school.getId(),roleId,rss);
				if (rs == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "角色学校权限不足");
					continue;
				}
				String mealId = cols.get(2).trim();
				int id = 0;
				try {
					id = Integer.parseInt(mealId);
				} catch (NumberFormatException e) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "套餐编号非法");
					continue;
				}
				String cityMealId = "2";
				NetMeal meal = null;
				if (id == 2) {
					if (mn == null) {
						mn = "默认套餐";
					}
				} else {
					meal = getNetMealFromCache(dataService,id,meals);
					if (meal == null) {
						logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到套餐编号=" + mealId);
						continue;
					}
					if (mn == null) {
						mn = meal.getName();
					}
					if (!role.checkOperator(meal.getOperator())) {
						logs.add(head + account + ConstData.OPERATE_FAIL_STR + "运营商权限不足");
						continue;
					}
					cityMealId = meal.getKeyWord();
				}
				Example example = new Example(AppUser.class);
				example.createCriteria().andEqualTo("idcard",identfy);
				AppUser user = dataService.search(example,AppUser.class);
				String pwd = identfy.substring(identfy.length() - 6);
				CityHotInfo info = CityHotUtil.regist(school,account,pwd,cityMealId);
				info.setUserId(user != null ? user.getId() : 0);
				info.setGroupId(id);
				info.setCityGroupId(cityMealId);
				dataService.insert(info);
				if (info.checkSucc()) {
					if (user != null) {
						user.setMealId(meal != null ? meal.getId() : 2);
						dataService.save(user);
					}
					logs.add(head + account + ConstData.OPERATE_SUCC_STR);
				} else {
					String error = ConstData.CITY_HOT_OPEN_ERROR.get(info.getCode());
					error = error == null ? info.getCode() : error;
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + error);
				}
			}
		} else if (type == ImportType.fuse.ordinal()) {
			Map<String,School> schools = new HashMap<String,School>();
			Map<Integer,RoleSchool> rss = new HashMap<Integer,RoleSchool>();
			Map<Integer,NetMeal> meals = new HashMap<Integer,NetMeal>();
			it = ImportType.fuse;
			String head = it.getValue() + " >> ";
			for (int i = 0; i < datas.size(); i++) {
				int row = i + 2;
				List<String> cols = datas.get(i);
				String account = cols.get(0).trim();
				if (StringUtils.isEmpty(account)) {
					logs.add(head + ConstData.OPERATE_FAIL_STR + "第" + row + "行的学号未配置");
					continue;
				}
				String identfy = cols.get(3).trim();
				if (StringUtils.isEmpty(identfy)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证为空");
					continue;
				}
				if (identfy.length() < 18) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证长度非法");
					continue;
				}
				String schoolName = cols.get(1).trim();
				if (StringUtils.isEmpty(schoolName)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "学校名称未配置");
					continue;
				}
				if (sn == null) {
					sn = schoolName;
				}
				School school = getSchoolFromCache(dataService, schoolName, schools);
				if (school == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到学校名称：" + schoolName);
					continue;
				}
				RoleSchool rs = getRoleSchoolFromCache(dataService, school.getId(),roleId,rss);
				if (rs == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "角色学校权限不足");
					continue;
				}
				String mealId = cols.get(2).trim();
				int id = 0;
				try {
					id = Integer.parseInt(mealId);
				} catch (NumberFormatException e) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "套餐编号非法");
					continue;
				}
				NetMeal meal = getNetMealFromCache(dataService, id, meals);
				if (meal == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到套餐编号=" + mealId);
					continue;
				}
				if (mn == null) {
					mn = meal.getName();
				}
				if (!role.checkOperator(meal.getOperator())) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "运营商权限不足");
					continue;
				}
				if (meal.getMerge().equals("不可以")) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "编号=" + mealId + "的套餐无法融合");
					continue;
				}
				String mealCard = cols.get(4).trim();
				if (StringUtils.isEmpty(mealCard)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "套餐号码未配置");
					continue;
				}
				Example example = new Example(AppUser.class);
				example.createCriteria().andEqualTo("idcard",identfy);
				AppUser user = dataService.search(example,AppUser.class);
				if (user == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到用户");
					continue;
				}
				String cityMealId = meal.getKeyWord();
				List<CityHotInfo> infos = CityHotUtil.change(school,"套餐融合-",account,cityMealId,"0");
				CityHotInfo last = infos.get(infos.size() - 1);
				if (last.checkSucc()) {
					user.setMealCard(mealCard);
					user.setMealId(meal.getId());
					dataService.save(user);
					PayTran pay = new PayTran();
					pay.setOrderId(0);
					pay.setUserId(user.getId());
					pay.setPayDate(TimeUtils.nowString());
					pay.setPayType(ConstData.PAY_TYPE_OUT_LINE);
					pay.setPayAmount(meal.getRealyPrice());
					pay.setPaySta(ConstData.PAY_STA_OK);
					pay.setPayRem(meal.getName());
					dataService.save(pay);
					logs.add(head + account + ConstData.OPERATE_SUCC_STR);
				} else {
					String error = null;
					if (infos.size() >= 3) {
						error = ConstData.CITY_HOT_CHANGE_ERROR.get(last.getCode());
					} else if (infos.size() == 2) {
						error = ConstData.CITY_HOT_RESTORE_ERROR.get(last.getCode());
					} else {
						error = ConstData.CITY_HOT_KICK_ERROR.get(last.getCode());
					}
					error = error == null ? last.getCode() : error;
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + error);
				}
				CommonUtil.forEach(infos, (info) -> {
					info.setUserId(user.getId());
					info.setGroupId(meal.getId());
					info.setCityGroupId(meal.getKeyWord());
				});
				dataService.insertMore(infos);
			}
		} else if (type == ImportType.stop.ordinal()) {
			Map<String,School> schools = new HashMap<String,School>();
			Map<Integer,RoleSchool> rss = new HashMap<Integer,RoleSchool>();
			Map<Integer,NetMeal> meals = new HashMap<Integer,NetMeal>();
			it = ImportType.stop;
			String head = it.getValue() + " >> ";
			for (int i = 0; i < datas.size(); i++) {
				int row = i + 2;
				List<String> cols = datas.get(i);
				String account = cols.get(0).trim();
				if (StringUtils.isEmpty(account)) {
					logs.add(head + ConstData.OPERATE_FAIL_STR + "第" + row + "行的学号未配置");
					continue;
				}
				String identfy = cols.get(2).trim();
				if (StringUtils.isEmpty(identfy)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证为空");
					continue;
				}
				if (identfy.length() < 18) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证长度非法");
					continue;
				}
				String schoolName = cols.get(1).trim();
				if (StringUtils.isEmpty(schoolName)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "学校名称未配置");
					continue;
				}
				if (sn == null) {
					sn = schoolName;
				}
				School school = getSchoolFromCache(dataService, schoolName, schools);
				if (school == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到学校名称：" + schoolName);
					continue;
				}
				RoleSchool rs = getRoleSchoolFromCache(dataService,school.getId(),roleId,rss);
				if (rs == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "角色学校权限不足");
					continue;
				}
				Example example = new Example(AppUser.class);
				example.createCriteria().andEqualTo("idcard",identfy);
				AppUser user = dataService.search(example,AppUser.class);
				if (user == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到用户");
					continue;
				}
				NetMeal meal = getNetMealFromCache(dataService,user.getMealId(),meals);
				if (meal != null) {
					if (mn == null) {
						mn = meal.getName();
					}
					if (!role.checkOperator(meal.getOperator())) {
						logs.add(head + account + ConstData.OPERATE_FAIL_STR + "运营商权限不足");
						continue;
					}
				}
				List<CityHotInfo> infos = CityHotUtil.stop(school, account, "0");
				CityHotInfo last = infos.get(infos.size() - 1);
				if (last.checkSucc() || (infos.size() == 2 && "E84".equals(last.getCode()))) {
					// 原先是停用状态也算成功
					logs.add(head + account + ConstData.OPERATE_SUCC_STR);
				} else {
					String error = null;
					if (infos.size() == 2) {
						error = ConstData.CITY_HOT_STOP_ERROR.get(last.getCode());
					} else {
						error = ConstData.CITY_HOT_KICK_ERROR.get(last.getCode());
					}
					error = error == null ? last.getCode() : error;
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + error);
				}
				CommonUtil.forEach(infos, (info) -> {
					info.setUserId(user.getId());
					info.setGroupId(meal == null ? 2 : meal.getId());
					info.setCityGroupId(meal == null ? "2" : meal.getKeyWord());
				});
				dataService.insertMore(infos);
			}
		} else if (type == ImportType.restore.ordinal()) {
			Map<String,School> schools  = new HashMap<String,School>();
			Map<Integer,RoleSchool> rss = new HashMap<Integer,RoleSchool>();
			Map<Integer,NetMeal> meals  = new HashMap<Integer,NetMeal>();
			it = ImportType.stop;
			it = ImportType.restore;
			String head = it.getValue() + " >> ";
			for (int i = 0; i < datas.size(); i++) {
				int row = i + 2;
				List<String> cols = datas.get(i);
				String account = cols.get(0).trim();
				if (StringUtils.isEmpty(account)) {
					logs.add(head + ConstData.OPERATE_FAIL_STR + "第" + row + "行的学号未配置");
					continue;
				}
				String identfy = cols.get(2).trim();
				if (StringUtils.isEmpty(identfy)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证为空");
					continue;
				}
				if (identfy.length() < 18) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证长度非法");
					continue;
				}
				String schoolName = cols.get(1).trim();
				if (StringUtils.isEmpty(schoolName)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "学校名称未配置");
					continue;
				}
				if (sn == null) {
					sn = schoolName;
				}
				School school = getSchoolFromCache(dataService, schoolName, schools);
				if (school == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到学校名称：" + schoolName);
					continue;
				}
				RoleSchool rs = getRoleSchoolFromCache(dataService, school.getId(), roleId, rss);
				if (rs == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "角色学校权限不足");
					continue;
				}
				Example example = new Example(AppUser.class);
				example.createCriteria().andEqualTo("idcard",identfy);
				AppUser user = dataService.search(example,AppUser.class);
				if (user == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到用户");
					continue;
				}
				NetMeal meal = getNetMealFromCache(dataService,user.getMealId(),meals);
				if (meal != null) {
					if (mn == null) {
						mn = meal.getName();
					}
					if (!role.checkOperator(meal.getOperator())) {
						logs.add(head + account + ConstData.OPERATE_FAIL_STR + "运营商权限不足");
						continue;
					}
				}
				CityHotInfo info = CityHotUtil.restore(school, account, "0");
				if (info.checkSucc() || "E84".equals(info.getCode())) {
					// 原先是正常状态也算成功
					logs.add(head + account + ConstData.OPERATE_SUCC_STR);
				} else {
					String error = ConstData.CITY_HOT_RESTORE_ERROR.get(info.getCode());
					error = error == null ? info.getCode() : error;
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + error);
				}
				info.setUserId(user.getId());
				info.setGroupId(meal == null ? 2 : meal.getId());
				info.setCityGroupId(meal == null ? "2" : meal.getKeyWord());
				dataService.insert(info);
			}
		} else if (type == ImportType.open.ordinal()) {
			Map<String,School> schools  = new HashMap<String,School>();
			Map<Integer,RoleSchool> rss = new HashMap<Integer,RoleSchool>();
			Map<Integer,NetMeal> meals  = new HashMap<Integer,NetMeal>();
			it = ImportType.open;
			String head = it.getValue() + " >> ";
			for (int i = 0; i < datas.size(); i++) {
				int row = i + 2;
				List<String> cols = datas.get(i);
				String account = cols.get(0).trim();
				if (StringUtils.isEmpty(account)) {
					logs.add(head + ConstData.OPERATE_FAIL_STR + "第" + row + "行的学号未配置");
					continue;
				}
				String identfy = cols.get(3).trim();
				if (StringUtils.isEmpty(identfy)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证为空");
					continue;
				}
				if (identfy.length() < 18) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "身份证长度非法");
					continue;
				}
				String schoolName = cols.get(1).trim();
				if (StringUtils.isEmpty(schoolName)) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "学校名称未配置");
					continue;
				}
				if (sn == null) {
					sn = schoolName;
				}
				School school = getSchoolFromCache(dataService, schoolName, schools);
				if (school == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到学校名称：" + schoolName);
					continue;
				}
				RoleSchool rs = getRoleSchoolFromCache(dataService, school.getId(), roleId,rss);
				if (rs == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "角色学校权限不足");
					continue;
				}
				String mealId = cols.get(2).trim();
				int id = 0;
				try {
					id = Integer.parseInt(mealId);
				} catch (NumberFormatException e) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "套餐编号非法");
					continue;
				}
				NetMeal meal = getNetMealFromCache(dataService, id, meals);
				if (meal == null) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "找不到套餐编号=" + mealId);
					continue;
				}
				if (mn == null) {
					mn = meal.getName();
				}
				if (!role.checkOperator(meal.getOperator())) {
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + "运营商权限不足");
					continue;
				}
				Example example = new Example(AppUser.class);
				example.createCriteria().andEqualTo("idcard",identfy);
				AppUser user = dataService.search(example,AppUser.class);
				String cityMealId = meal.getKeyWord();
				List<CityHotInfo> infos = CityHotUtil.change(school,"套餐修改-",account, cityMealId, "0");
				CityHotInfo last = infos.get(infos.size() - 1);
				if (last.checkSucc()) {
					if (user != null) {
						user.setMealId(meal.getId());
						dataService.save(user);
					}
					logs.add(head + account + ConstData.OPERATE_SUCC_STR);
				} else {
					String error = null;
					if (infos.size() >= 3) {
						error = ConstData.CITY_HOT_CHANGE_ERROR.get(last.getCode());
					} else if (infos.size() == 2) {
						error = ConstData.CITY_HOT_RESTORE_ERROR.get(last.getCode());
					} else {
						error = ConstData.CITY_HOT_KICK_ERROR.get(last.getCode());
					}
					error = error == null ? last.getCode() : error;
					logs.add(head + account + ConstData.OPERATE_FAIL_STR + error);
				}
				CommonUtil.forEach(infos, (info) -> {
					info.setUserId(user== null ? 0 : user.getId());
					info.setGroupId(meal.getId());
					info.setCityGroupId(meal.getKeyWord());
				});
				dataService.insertMore(infos);
			}
		}
		ImportResult importResult = CommonUtil.writeFile(logPath, fileName, logs);
		importResult.setType(it.getValue());
		importResult.setAuther(auther);
		importResult.setSchool(sn);
		importResult.setMeal(mn);
		dataService.insert(importResult);
		return "success";
	}
	
	private static String changeCard(DataService dataService,String type, String head, int roleId, CardInfo card) {
		String cardNumber = card.getCardNumber();
		if (type.equals("insert")) {
			Example example = new Example(CardInfo.class);
			example.createCriteria().andEqualTo("cardNumber",cardNumber);
			CardInfo old = dataService.search(example,CardInfo.class);
			if (old != null) {
				return head + ConstData.OPERATE_FAIL_STR + "号码已存在";
			}
			String meals = card.getMeals();
			if (meals != null && !meals.isEmpty()) {
				// 需要校验套餐是否存在
				String error = checkMeal(dataService,meals);
				if (error != null) {
					return head + ConstData.OPERATE_FAIL_STR + error;
				}
			}
			String schoolName = card.getSchoolName();
			Example temp = new Example(School.class);
			temp.createCriteria().andEqualTo("schoolName",schoolName);
			School targetSchool = dataService.search(temp,School.class);
			if (targetSchool == null) {
				return head + ConstData.OPERATE_FAIL_STR + "找不到学校: " + schoolName;
			} else {
				//在看看有没有权限
				temp = new Example(RoleSchool.class);
				temp.createCriteria().andEqualTo("roleId",roleId);
				if (dataService.count(temp,RoleSchool.class) <= 0) {
					return head + ConstData.OPERATE_FAIL_STR + "没有学校： " + schoolName + "的权限";
				}
			}
			card.setSchoolId(targetSchool.getId());
			card.setCreateTime(TimeUtils.nowString());
			Integer id = dataService.insert(card);
			if (id > 0) {
				return head + ConstData.OPERATE_SUCC_STR;
			} else {
				return head + ConstData.OPERATE_FAIL_STR + "系统错误";
			}
		} else {
			Example example = new Example(CardInfo.class);
			example.createCriteria().andEqualTo("cardNumber",cardNumber);
			CardInfo old = dataService.search(example,CardInfo.class);
			if (old == null) {
				return head + ConstData.OPERATE_FAIL_STR + "号码不存在";
			}
			String meals = card.getMeals();
			if (meals != null && !meals.isEmpty()) {
				// 需要校验套餐是否存在
				String error = checkMeal(dataService,meals);
				if (error != null) {
					return head + ConstData.OPERATE_FAIL_STR + error;
				}
			}
			String schoolName = card.getSchoolName();
			Example temp = new Example(School.class);
			temp.createCriteria().andEqualTo("schoolName",schoolName);
			School targetSchool = dataService.search(temp,School.class);
			if (targetSchool == null) {
				return head + ConstData.OPERATE_FAIL_STR + "找不到学校: " + schoolName;
			} else {
				// 在看看有没有权限
				temp = new Example(RoleSchool.class);
				temp.createCriteria().andEqualTo("roleId",roleId);
				if (dataService.count(temp,RoleSchool.class) <= 0) {
					return head + ConstData.OPERATE_FAIL_STR + "没有学校： " + schoolName + "的权限";
				}
			}
			card.setSchoolId(targetSchool.getId());
			card.setId(old.getId());
			card.setCreateTime(old.getCreateTime());
			try {
				dataService.save(card);
				return head + ConstData.OPERATE_SUCC_STR;
			} catch (Exception e) {
				return head + ConstData.OPERATE_FAIL_STR + e.getMessage();
			}
		}
	}
	
	private static String checkMeal(DataService dataService , String meals) {
		List<Integer> ids = CommonUtil.split(meals,",",Integer.class);
		Example example = new Example(NetMeal.class);
		example.createCriteria().andIn("id",ids);
		if (dataService.count(example,NetMeal.class) > 0) {
			return null;
		}
		return "套餐编号错误";
	}
	
	private static String delCard(DataService dataService ,String number) {
		String type = "delete";
		try {
			Example example = new Example(CardInfo.class);
			example.createCriteria().andEqualTo("cardNumber",number);
			if (dataService.deleteByExample(example,CardInfo.class)) {
				return type + number + ConstData.OPERATE_SUCC_STR;
			}else{
				return type + number + ConstData.OPERATE_FAIL_STR + "号码不存在";
			}
		} catch (Exception e) {
			return type + number + ConstData.OPERATE_FAIL_STR + e.getMessage();
		}
	}
	
	public static String backMoney(Integer orderId,DataService dataService,AliPayConfig aliPayConfig,WXPayConfig wxPayConfig,Logger logger) {
		PayTran payInfo = getDataByConditions(dataService,PayTran.class,orderId,"orderId");
		if (payInfo != null) {
			if (!ConstData.PAY_STA_OK.equals(payInfo.getPaySta())) {
				return ConstData.WECHAT_RESUL_FAIL;
			}
			if (ConstData.PAY_TYPE_ALI.equals(payInfo.getPayType())) {//支付宝退款
				AlipayClient alipayClient = new DefaultAlipayClient(aliPayConfig.getAliBckMoneyUrl(), aliPayConfig.getAliAppId(),
						aliPayConfig.getAliPayPrivateKey(), "json", "GBK", aliPayConfig.getAliPayPublicKey(), "RSA2");
				AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
				Map<String,String> params = new HashMap<String,String>();
				params.put("out_trade_no",payInfo.getOutTradeNo());
				params.put("trade_no",payInfo.getTransactionId());
				params.put("refund_amount",payInfo.getPayAmount().toString());
				params.put("refund_reason","正常退款");
				String bizContent = JsonUtil.encodeToStr(params);
				request.setBizContent(bizContent);
				try {
					AlipayTradeRefundResponse response = alipayClient.execute(request);
					if (response.isSuccess()) {
						logger.info("aliReBackMoney succ");
						payInfo.setPaySta(ConstData.PAY_STA_CANCLE);
						dataService.save(payInfo);
						return ConstData.WECHAT_RESUL_SUCC;
					}else {
						return response.getMsg();
					}
				} catch (AlipayApiException e) {
					logger.error("aliReBackMoney error", e);
				}
			} else if (ConstData.PAY_TYPE_WECHAT.equals(payInfo.getPayType())) {
				try {//微信退款
					WXPay pay = new WXPay(wxPayConfig);
					Map<String, String> reqData = new HashMap<String, String>();
					String money = String.valueOf(Math.round(payInfo.getPayAmount() * 100));
					reqData.put("total_fee",money);//订单总金额
					reqData.put("refund_fee",money);//退款金额
					reqData.put("out_refund_no",WXPayUtil.generateNonceStr());//随机生成商户退款单号
					reqData.put("op_user_id", wxPayConfig.getMchID());//操作员，默认为商户号
					Map<String, String> result = pay.refund(reqData);
					String return_code = result.get("return_code");
					if (ConstData.WECHAT_RESUL_SUCC.equals(return_code)) {
						payInfo.setPaySta(ConstData.PAY_STA_CANCLE);
						dataService.save(payInfo);
						logger.info("wechatReBackMoney succ");
						return return_code;
					}else {
						return result.get("return_msg");
					}
				} catch (Exception e) {
					logger.error("wechatReBackMoney error", e);
				}
			}
		}
		return ConstData.WECHAT_RESUL_FAIL;
	}
	
	public static boolean fillMultipleSchool(DataService dataService,Map<String,Object> map,Integer roleId,String schoolId) {
		List<School> schools = getRolePermissionSchool(dataService,roleId);
		if (schools == null || schools.size() == 0) {
			map.put("state","fail");
			map.put("mesg", "未配置学校");
			return false;
		}
		if (CommonUtil.isEmpty(schoolId)) {
			map.put("ns",schools);
        }else {
        	List<Integer> ss = CommonUtil.split(schoolId.replace("_", ""), ",",Integer.class);
        	List<School> hs = new ArrayList<School>();
        	List<School> ns = new ArrayList<School>();
        	CommonUtil.forEach(schools,school->{
        		if (ss.contains(school.getId())) {
        			hs.add(school);
        		}else {
        			ns.add(school);
        		}
        	});
        	map.put("hs",hs);
        	map.put("ns",ns);
        }
		return true;
	}
	
	public static String defaulMultipleSchool(DataService dataService,Integer roleId,String schoolId) {
		String temp = schoolId;
		if (CommonUtil.isEmpty(schoolId)) {
			List<School> schools = getRolePermissionSchool(dataService,roleId);
			for (int i = 0 ; i < schools.size() ; i++) {
				School school = schools.get(i);
				if (i == 0) {
					temp = "_" + school.getId() + "_";
				}else{
					temp += ",_" + school.getId() + "_";
				}
			}
		}
		return temp;
	}
}
