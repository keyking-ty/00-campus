package com.telit.info.web.controller.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telit.info.actions.ConstData;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.RespResult;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.business.CardMeal;
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.CardMealMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/cardMeal")
public class CardMealMrgController extends BaseListController{
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	private int curMenuId = ConstData.CM_MRG_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "手机套餐";
	
	@RequestMapping("/mrg")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String businessMrg() {
        return "business/cardMeal";
    }
	
	@ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		if (rules.size() == 0) {
			Table table = CardMeal.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),CardMeal.class);
			SqlRule rule = new SqlRule();
			rule.setKey("schoolName");
			rule.setType(String.class);
			rule.setTable("t_card_meal");
			rule.setColumn("school_id");
			rules.put(rule.getKey(),rule);//用学校标的名称覆盖CardMeal的schoolName属性
		}
		Role role = (Role) session.getAttribute("currentRole");
		int roleId = role.getId();
		Map<String, Object> result = new HashMap<String, Object>();
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
		String operators = null;//运营商权限判断
		if (!role.getOperator().equals("全部")) {
			operators = "'" + role.getOperator() + "'";
		}
		CardMealMapper mapper = dataService.getMapper(CardMeal.class);
		int size = mapper.queryListCount(where,roleId,operators);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<CardMeal> cards = mapper.queryList(where,roleId,operators,orderStr,page,grid.getRows());
		PageUtil.page(result,cards,size,grid.getPage(),grid.getRows());
		List<School> schools = NetUitl.getRolePermissionSchool(dataService,role.getId());
		result.put("schools",schools);
		result.put("operators",role.getOperator());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,CardMeal meal) {
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
			RespResult rr = NetUitl.checkDataProp(dataService,CardMeal.class,meal.getName(),"name");
            if (rr.getState() > 0) {
            	int id = rr.getState();
            	if (meal.getId() == null || meal.getId().intValue() == 0 || meal.getId().intValue() != id) {
            		resultmap.put("state","fail");
            		resultmap.put("mesg",rr.getMsg());
                    return resultmap;
            	}
            }
			if (!NetUitl.checkSchool(dataService,currentRole,meal.getSchoolId())) {
				resultmap.put("state","fail");
    			resultmap.put("mesg","你没有权限选择这个学校");
    			return resultmap;
			}
			//检查运营商权限
			if (!currentRole.checkOperator(meal.getOperator())) {
				resultmap.put("state","fail");
    			resultmap.put("mesg","运营商权限不足");
    			return resultmap;
			}
            if (meal.getId() == null || meal.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	String auther = currentUser.getTrueName();
            	meal.setAuther(auther);
            	dataService.insert(meal);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	CardMeal oldObject = dataService.get(meal.getId(),CardMeal.class);
                if (oldObject == null) {
                    resultmap.put("state","fail");
                    resultmap.put("mesg","订单不存在");
                    return resultmap;
                } else {
                	oldObject.copy(meal);
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
    @RequestMapping(value = "/selectById")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> selectById(HttpSession session,CardMeal meal) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (meal.getId() != null && meal.getId().intValue() > 0) {
            	meal = dataService.get(meal.getId(),CardMeal.class);
                if (meal == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录的id");
                return resultmap;
            }
            resultmap.put("meal",meal);
            resultmap.put("state","success");
            resultmap.put("mesg","获取成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "获取失败，系统异常");
            return resultmap;
        }
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean delCardMeal(Integer id) {
		CardMeal meal = dataService.delete(id,CardMeal.class);
		if (meal == null) {
			return false;
		}
		//删除关联的图片
		meal.deletePics(uploadFilePath);
		return true;
	}
	
	@ResponseBody
    @RequestMapping(value = "/deleteMeal")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deleteMeal(HttpSession session,CardMeal meal) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (meal.getId() != null && meal.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
            	if (!delCardMeal(meal.getId())) {
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
}
