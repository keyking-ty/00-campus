package com.telit.info.web.controller.business;

import com.telit.info.actions.ConstData;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.NetMeal;
import com.telit.info.data.business.QueryUserMeal;
import com.telit.info.data.business.School;
import com.telit.info.data.business.UserMealExport;
import com.telit.info.trans.mapper.NetMealMapper;
import com.telit.info.util.*;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Table;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/netMeal")
public class NetMealMrgController extends BaseListController{
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	private int curMenuId = ConstData.MEAL_MRG_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "宽带套餐";
	@RequestMapping("/mrg")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String businessMrg() {
        return "business/netMeal";
    }
	
	@ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		Role currentRole = (Role) session.getAttribute("currentRole");
		Map<String, Object> result = new HashMap<String, Object>();
		int roleId = currentRole.getId();
		if (rules.size() == 0) {
			Table table = NetMeal.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),NetMeal.class);
			SqlRule rule = new SqlRule("t_net_meal",String.class);
			rule.setKey("schoolName");
			rule.setColumn("school_id");
			rules.put(rule.getKey(),rule);//用学校标的名称覆盖NetMealde的schoolName属性
		}
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
		if (!currentRole.getOperator().equals("全部")) {
			operators = "'" + currentRole.getOperator() + "'";
		}
		NetMealMapper mapper = dataService.getMapper(NetMeal.class);
		int size = mapper.queryListCount(where,roleId,operators);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<NetMeal> meals = mapper.queryList(where, roleId, operators,orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		List<School> schools = NetUitl.getRolePermissionSchool(dataService,currentRole.getId());
		result.put("schools",schools);
		result.put("operators",currentRole.getOperator());
		return result;
	}

	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,NetMeal meal) {
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
            	NetMeal oldObject = dataService.get(meal.getId(),NetMeal.class);
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
    public Map<String, Object> selectById(HttpSession session,NetMeal meal) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (meal.getId() != null && meal.getId().intValue() > 0) {
            	meal = dataService.get(meal.getId(),NetMeal.class);
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
	public boolean delNetMeal(Integer id) {
		NetMeal meal = dataService.delete(id,NetMeal.class);
		if (meal == null) {
			return false;
		}
		String sql = "UPDATE t_app_user SET groupid=0 WHERE groupid=" + id;
		dataService.excuteSql(sql,AppUser.class);
		//删除icon文件
		CommonUtil.deleteFile(uploadFilePath,meal.getIconUrl());
		return true;
	}
	
	@ResponseBody
    @RequestMapping(value = "/deleteMeal")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deleteMeal(HttpSession session,NetMeal meal) {
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
            	if (!delNetMeal(meal.getId())) {
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

	@RequestMapping("export")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public void export(HttpSession session, HttpServletResponse response, QueryUserMeal queryUserMeal) throws Exception{
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			response.getWriter().println("请重新登录");
			return;
		}
		NetMealMapper mapper = dataService.getMapper(NetMeal.class);
		List<UserMealExport> exportDatas = mapper.queryExport(queryUserMeal);
		Map<Integer,List<UserMealExport>> temp = new HashMap<Integer,List<UserMealExport>>();
		for (UserMealExport exportData : exportDatas){
			List<UserMealExport> list = temp.get(exportData.getMealId());
			if (list == null){
				list = new ArrayList<UserMealExport>();
				temp.put(exportData.getMealId(),list);
			}
			list.add(exportData);
		}
		String fileName = "用户套餐分析-" + TimeUtils.nowDayString();
		OutputStream out = response.getOutputStream();
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(fileName,"UTF-8"));
		WritableWorkbook book = ExcelUtil.writeExcel(out,ConstData.MealDetailModulTitles,false);
		if (temp.size() > 0){
			WritableSheet sheet = book.getSheet(0);
			int row = 1;
			for (List<UserMealExport> list : temp.values()){
				int start = row;
				for (int i = 0 ; i < list.size() ; i++){
					UserMealExport exportData = list.get(i);
					Label label = null;
					if (i == 0){
						label = new Label(0,row,exportData.getMealId().toString(),ExcelUtil.CenterCellFormat);
						sheet.addCell(label);
						label = new Label(1,row,exportData.getMealName(),ExcelUtil.CenterCellFormat);
						sheet.addCell(label);
					}
					label = new Label(2,row,exportData.getUserName(),ExcelUtil.CenterCellFormat);
					sheet.addCell(label);
					label = new Label(3,row,exportData.getPhone(),ExcelUtil.CenterCellFormat);
					sheet.addCell(label);
					label = new Label(4,row,exportData.getSchoolName(),ExcelUtil.CenterCellFormat);
					sheet.addCell(label);
					row ++;
				}
				int end = row - 1;
				sheet.mergeCells(0,start,0,end);
				sheet.mergeCells(1,start,1,end);
			}
		}
		book.write();
		book.close();
	}
}
