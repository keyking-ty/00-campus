package com.telit.info.web.controller.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.telit.info.actions.ConstData;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.RespResult;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.CityHotInfo;
import com.telit.info.data.business.NetMeal;
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.AppUserMapper;
import com.telit.info.util.CityHotUtil;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/aum")
public class AppUserMrgController extends BaseListController{
	private int curMenuId = ConstData.USER_MRG_MENU_ID;
	private final static String PERMISSIONS_MENU_STR = "用户管理";
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public String businessMrg() {
		return "business/appUser";
	}
	
	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		Role role = (Role) session.getAttribute("currentRole");
		if (rules.size() == 0) {
			Table table = AppUser.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),AppUser.class);
			SqlRule rule = new SqlRule("t_app_user",String.class);
			rule.setKey("schoolName");
			rule.setColumn("school_id");
			rules.put(rule.getKey(),rule);//用学校标的名称覆盖Supermarket的schoolName属性
			rule = new SqlRule("t_net_meal",String.class);
			rule.setKey("mealName");
			rule.setColumn("name");
			rules.put(rule.getKey(),rule);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		int roleId = role.getId();
		String where = null;
		SearchFilter filter = JsonUtil.decodeToObj(grid.getFilters(),SearchFilter.class);
		if (filter != null && filter.getRules() != null) {
			where = filter.conds(rules);
		}
		String sid = grid.getSidx();
		String orderStr = null;
		if (CommonUtil.isNotEmpty(sid)) {
			SqlRule rule = rules.get(sid);
			if (rule != null) {
				orderStr = rule.getTable() + "." + rule.getColumn() + " " + grid.getSord();
			}
		}
		AppUserMapper mapper = dataService.getMapper(AppUser.class);
		int size = mapper.queryListCount(where,roleId);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<AppUser> users = mapper.queryList(where,roleId,orderStr,page,grid.getRows());
		PageUtil.page(result,users,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/initSelectData")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String initSelectData(HttpSession session) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			map.put("state","fail");
			map.put("mesg","请先重新登录");
			return JsonUtil.encodeToStr(map);
		}
		return NetUitl.getAppUserInitData(dataService,currentRole.getId());
	}
	
	@ResponseBody
    @RequestMapping(value = "/selectById")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String selectById(HttpSession session,AppUser user) {
		JsonObject json = new JsonObject();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			json.addProperty("state","fail");
			json.addProperty("mesg","请先重新登录");
		}else {
			try {
	        	user = dataService.get(user.getId(),AppUser.class);
	            if (user == null) {
	            	json.addProperty("state","fail");
	            	json.addProperty("mesg","无法找到该记录");
	            }else {
	            	 String result = NetUitl.getAppUserInitData(dataService,currentRole.getId());
	                 JsonObject _json = JsonUtil.gson.fromJson(result,JsonObject.class);
	                 json.addProperty("state","success");
	                 json.add("user",JsonUtil.gson.toJsonTree(user));
	                 json.add("schools",_json.get("schools"));
	                 json.add("meals",_json.get("meals"));
	                 json.add("cards",_json.get("cards"));
	            }
	        } catch (Exception e) {
	        	json.addProperty("state", "fail");
	        	json.addProperty("mesg", "获取失败，系统异常");
	        }
		}
        return json.toString();
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,AppUser user) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		User currentUser = (User)session.getAttribute("currentUser");
		if (currentUser == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
			//先检查名称唯一
			RespResult rr = NetUitl.checkDataProp(dataService,AppUser.class,user.getLoginName(),"loginName",user.getAccount(),"account");
            if (rr.getState() > 0) {
            	int id = rr.getState();
            	if (user.getId() == null || user.getId().intValue() == 0 || user.getId().intValue() != id) {
            		resultmap.put("state","fail");
            		resultmap.put("mesg",rr.getMsg());
                    return resultmap;
            	}
            }
			if (!NetUitl.checkSchool(dataService,currentRole,user.getSchoolId())) {
				resultmap.put("state","fail");
    			resultmap.put("mesg","你没有权限选择这个学校");
    			return resultmap;
			}
            if (user.getId() == null || user.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	dataService.insert(user);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	AppUser oldObject = dataService.get(user.getId(),AppUser.class);
                if (oldObject == null) {
                    resultmap.put("state","fail");
                    resultmap.put("mesg","用户不存在");
                    return resultmap;
                } else {
                	oldObject.copy(user);
                	dataService.save(oldObject);
                }
            }
            resultmap.put("state","success");
            resultmap.put("mesg","操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，系统异常");
        }
        return resultmap;
	}
	
	@ResponseBody
    @RequestMapping(value = "/delete")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deleteUser(HttpSession session,AppUser user) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (user.getId() != null && user.getId().intValue() > 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
            	AppUser target = dataService.delete(user.getId(),AppUser.class);
                if (target == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "删除失败,无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "删除失败");
            }
            resultmap.put("state", "success");
            resultmap.put("mesg", "删除成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
            return resultmap;
        }
	}
	
	@ResponseBody
    @RequestMapping(value = "/authenData")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> authenData(HttpSession session,Integer id) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
		}else {
			AppUser user = dataService.get(id,AppUser.class);
            if (user == null) {
            	resultmap.put("state","fail");
    			resultmap.put("mesg","找不到数据");
            }else{
            	resultmap.put("state","success");
            	if (StringUtils.isNotEmpty(user.getIdcardPosimg())) {
            		resultmap.put("idcardPosimg",user.getIdcardPosimg());
            	}
            	if (StringUtils.isNotEmpty(user.getIdcardSideimg())) {
            		resultmap.put("idcardSideimg",user.getIdcardSideimg());
            	}
            	if (StringUtils.isNotEmpty(user.getStucardPosimg())) {
            		resultmap.put("stucardPosimg",user.getStucardPosimg());
            	}
            	if (StringUtils.isNotEmpty(user.getStucardSideimg())) {
            		resultmap.put("stucardSideimg",user.getStucardSideimg());
            	}
            	resultmap.put("authenSta",user.getAuthenSta());
            }
		}
		return resultmap;
	}
	
	@ResponseBody
    @RequestMapping(value = "/authenUser")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> authenUser(HttpSession session,AppUser user) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
		}else {
			if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.authenUser.getId())) {
        		resultmap.put("state","fail");
    			resultmap.put("mesg",OperateType.authenUser.getError());
        	}else{
        		AppUser _user = dataService.get(user.getId(),AppUser.class);
                if (_user == null) {
                	resultmap.put("state","fail");
        			resultmap.put("mesg","找不到数据");
                }else{
                	if (user.getAuthenSta().equals("已认证")) {
                    	School school = dataService.get(_user.getSchoolId(),School.class);
                    	if (school.isCityHot()) {
                    		//认证通过需要去城市热点开户
                    		String mealId = "2";//默认预开户套餐编号
                    		if (_user.getMealId() != null && _user.getMealId().intValue() > 0) {
                    			NetMeal meal = dataService.get(_user.getMealId(), NetMeal.class);
                    			if (meal != null) {
                    				mealId = meal.getKeyWord();
                    			}
                    		}
                    		CityHotInfo info = CityHotUtil.regist(school,_user.getAccount(),_user.getLoginPwd(),mealId);
                    		info.setUserId(_user.getId());
                    		info.setGroupId(_user.getMealId());
                    		info.setCityGroupId(mealId);
                    		if (info.checkSucc()) {
                        		_user.setAuthenSta(user.getAuthenSta());
                        		dataService.save(_user);
                        		resultmap.put("state","success");
                            	resultmap.put("mesg","操作成功");
                    		}else{
                    			String error = ConstData.CITY_HOT_OPEN_ERROR.get(info.getCode());
                    			error = error == null ? "开户失败" : error;
                    			resultmap.put("state","fail");
                            	resultmap.put("mesg",error);
                    		}
                    		dataService.insert(info);
                    		return resultmap;
                    	}
                	}
                	_user.setAuthenSta(user.getAuthenSta());
                	dataService.save(_user);
            		resultmap.put("state","success");
                	resultmap.put("mesg","操作成功");
                }
        	}
		}
		return resultmap;
	}
}
