package com.telit.info.web.controller.business;

import java.sql.Timestamp;
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
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.InfoMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/info")
public class InfoMrgController extends BaseListController{
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	private int curMenuId = ConstData.INFO_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "信息管理";
	
	@RequestMapping("/mrg")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String businessMrg() {
        return "business/info";
    }
	
	@ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		Role currentRole = (Role) session.getAttribute("currentRole");
		Map<String, Object> result = new HashMap<String, Object>();
		if (rules.size() == 0) {
			Table table = InfoData.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),InfoData.class);
			SqlRule rule = new SqlRule("t_user",String.class);
			rule.setKey("authorName");
			rule.setColumn("true_name");
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
		List<School> schools = NetUitl.getRolePermissionSchool(dataService,currentRole.getId());
		String temp = null;
		if (schools != null && schools.size() > 0) {
			for (int i = 0 ; i < schools.size() ; i++){
				School school = schools.get(i);
				if (i == 0) {
					temp = "(instr(t_info.school_id,'_" + school.getId() + "_') > 0";
				}else{
					temp += " OR instr(t_info.school_id,'_" + school.getId() + "_') > 0";
				}
			}
			temp += ")";
		}
		InfoMapper mapper = dataService.getMapper(InfoData.class);
		int size = mapper.queryListCount(where,temp);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<InfoData> meals = mapper.queryList(where,temp,orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		return result;
	}
			
	@ResponseBody
    @RequestMapping(value = "/shcools")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> shcools(HttpSession session) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			map.put("state","fail");
			map.put("mesg","请先重新登录");
			return map;
		}
		List<School> schools = NetUitl.getRolePermissionSchool(dataService,currentRole.getId());
		if (schools != null && schools.size() > 0) {
			map.put("state","success");
			map.put("schools",schools);
		}else {
			map.put("state","fail");
			map.put("mesg","未配置学校");
		}
		return map;
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,InfoData info) {
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
			info.setSchoolId(NetUitl.defaulMultipleSchool(dataService, currentRole.getId(),info.getSchoolId()));
            if (info.getId() == null || info.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	if (CommonUtil.isEmpty(info.getAuthor())) {
            		info.setAuthor("佚名");
            	}
            	info.setTime(new Timestamp(System.currentTimeMillis()));
            	info.setAuthorId(currentRole.getId());
            	dataService.insert(info);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	InfoData oldObject = dataService.get(info.getId(),InfoData.class);
                if (oldObject == null) {
                    resultmap.put("state","fail");
                    resultmap.put("mesg","数据未找到");
                    return resultmap;
                } else {
                	oldObject.copy(info);
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
    public Map<String, Object> selectById(HttpSession session,InfoData info) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (info.getId() != null && info.getId().intValue() > 0) {
            	info = dataService.get(info.getId(),InfoData.class);
                if (info == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录的id");
                return resultmap;
            }
            if (!NetUitl.fillMultipleSchool(dataService,resultmap,currentRole.getId(),info.getSchoolId())) {
            	return resultmap;
            }
            resultmap.put("info",info);
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
    @RequestMapping(value = "/deleteInfo")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> delete(HttpSession session,InfoData info) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (info.getId() != null && info.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
        		if (dataService.delete(info.getId(),InfoData.class) == null) {
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
}
