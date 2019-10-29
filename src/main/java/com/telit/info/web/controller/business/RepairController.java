package com.telit.info.web.controller.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telit.info.actions.ConstData;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.business.InfoData;
import com.telit.info.data.business.RepairData;
import com.telit.info.trans.mapper.RepairMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/repair")
public class RepairController extends BaseListController{
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	private int curMenuId = ConstData.REPAIR_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "宽带保修";
	
	@RequestMapping("/mrg")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String businessMrg() {
        return "business/repair";
    }
	
	@ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (rules.size() == 0) {
			Table table = InfoData.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),RepairData.class);
			SqlRule rule = new SqlRule("t_app_user",String.class);
			rule.setKey("userName");
			rule.setColumn("real_name");
			rules.put(rule.getKey(),rule);
			rule = new SqlRule("t_school",String.class);
			rule.setKey("schoolName");
			rule.setColumn("school_name");
			rules.put(rule.getKey(),rule);
			rule = new SqlRule("t_user",String.class);
			rule.setKey("operatorName");
			rule.setColumn("school_name");
			rules.put(rule.getKey(),rule);
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
		RepairMapper mapper = dataService.getMapper(RepairData.class);
		int size = mapper.queryListCount(where,currentRole.getId());
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<RepairData> meals = mapper.queryList(where,currentRole.getId(),orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/selectById")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> selectById(HttpSession session,RepairData repair) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
        	RepairMapper mapper = dataService.getMapper(RepairData.class);
        	repair = mapper.getLinkOne(repair.getId());
            if (repair == null) {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录");
                return resultmap;
            }
            resultmap.put("repair",repair);
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
	
	@ResponseBody
    @RequestMapping(value = "/delete")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> delete(HttpSession session,RepairData repair) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (repair.getId() != null && repair.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
        		if (dataService.delete(repair.getId(),RepairData.class) == null) {
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
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
            return resultmap;
        }
	}
	
	@ResponseBody
    @RequestMapping(value = "/prepear")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> prepear(HttpSession session,RepairData repair) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
        	RepairData old = dataService.get(repair.getId(), RepairData.class);
            if (old == null) {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录");
                return resultmap;
            }
            RepairMapper mapper = dataService.getMapper(RepairData.class);
            List<User> users = mapper.alotUsers(old.getSchoolId());
            resultmap.put("users",users);
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
	
	@ResponseBody
    @RequestMapping(value = "/allot")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> allot(HttpSession session,RepairData repair) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (repair.getId() != null && repair.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.allotRepair.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.allotRepair.getError());
        			return resultmap;
            	}
            	RepairData old = dataService.get(repair.getId(),RepairData.class);
            	if (old.getOperator() != null && old.getOperator().intValue() > 0) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg","已派过单了");
        			return resultmap;
            	}
            	old.setOperator(repair.getOperator());
            	old.setStu("已派单");
            	dataService.save(old);
            	resultmap.put("state", "success");
                resultmap.put("mesg", "派单成功");
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "删除失败");
            }
            return resultmap;
        } catch (Exception e) {
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
            return resultmap;
        }
	}
	
	@ResponseBody
    @RequestMapping(value = "/complete")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> complete(HttpSession session,RepairData repair) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		User currentUser = (User) session.getAttribute("currentUser");
		if (currentUser == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "请先重新登录");
			return resultmap;
		}
		try {
            if (repair.getId() != null && repair.getId().intValue() != 0) {
            	Role currentRole = (Role) session.getAttribute("currentRole");
    			if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.completeRepair.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.completeRepair.getError());
        			return resultmap;
            	}
            	RepairData old = dataService.get(repair.getId(), RepairData.class);
            	if ("已处理".equals(old.getStu())) {
            		resultmap.put("state", "fail");
                    resultmap.put("mesg", "已完成过了");
                    return resultmap;
            	}
            	if (old.getOperator().intValue() != currentUser.getId().intValue()) {
            		resultmap.put("state", "fail");
                    resultmap.put("mesg", "你没有权限");
            	}else {
            		old.setStu("已处理");
            		dataService.save(old);
            		resultmap.put("state","success");
                    resultmap.put("mesg","操作成功");
            	}
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "删除失败");
            }
            return resultmap;
        } catch (Exception e) {
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
            return resultmap;
        }
	}
}
