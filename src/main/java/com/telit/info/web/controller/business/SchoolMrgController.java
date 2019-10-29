package com.telit.info.web.controller.business;

import com.telit.info.actions.ConstData;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.RespResult;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.RoleSchool;
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.SchoolMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Table;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/school")
public class SchoolMrgController extends BaseListController{
	private int curMenuId = ConstData.SCHOOL_MRG_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "学校管理";
	
	@RequestMapping("/toSchoolManager")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public String addMeal() {
		return "business/school";
	}
	
	@ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		Role role = (Role) session.getAttribute("currentRole");
		if (rules.size() == 0) {
			Table table = School.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),School.class);
		}
		Integer roleId = role.getId();
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
		SchoolMapper mapper = dataService.getMapper(School.class);
		int size = mapper.queryListCount(where,roleId);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<School> schools = mapper.queryList(where,roleId,orderStr,page,grid.getRows());
		PageUtil.page(result,schools,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,School school) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
			//判断名称唯一
			RespResult rr = NetUitl.checkDataProp(dataService,School.class,school.getSchoolName(),"schoolName");
			if (rr.getState() > 0) {
            	int id = rr.getState();
            	if (school.getId() == null || school.getId().intValue() == 0 || school.getId().intValue() != id) {
            		resultmap.put("state","fail");
            		resultmap.put("mesg",rr.getMsg());
                    return resultmap;
            	}
            }
            if (school.getId() == null || school.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	dataService.insert(school);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	School oldObject = dataService.get(school.getId(),School.class);
                if (oldObject == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "学校不存在");
                    return resultmap;
                } else {
                	oldObject.copy(school);
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
    public Map<String, Object> selectById(School school) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
        try {
            if (school.getId() != null && school.getId().intValue() != 0) {
            	school = dataService.get(school.getId(),School.class);
                if (school == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录的id");
                return resultmap;
            }
            resultmap.put("school",school);
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
	public boolean delSchool(Integer id) {
		Example example = new Example(RoleSchool.class);
		example.createCriteria().andEqualTo("schoolId",id);
		dataService.deleteByExample(example,RoleSchool.class);
		
		example = new Example(School.class);
		example.createCriteria().andEqualTo("id",id);
		dataService.deleteByExample(example,School.class);
		return true;
	}
	
	@ResponseBody
    @RequestMapping(value = "/deleteSchool")
    @RequiresPermissions(value = {"角色管理"})
    public Map<String, Object> deleteSchool(HttpSession session,School school) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (school.getId() != null && school.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
            	if (!delSchool(school.getId())) {
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
