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
import com.telit.info.data.app.Advert;
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.AdvertMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.util.TimeUtils;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/advert")
public class AdvertMrgController extends BaseListController{
	int curMenuId = ConstData.ADVERT_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "广告管理";
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public String businessMrg() {
		return "business/advert";
	}
	
	private void initRule() {
		Table table = Advert.class.getAnnotation(Table.class);
		CommonUtil.initSqlRule(rules,table.name(),Advert.class);
	}
	
	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		if (rules.size() == 0) {
			initRule();
		}
		//Role role = (Role) session.getAttribute("currentRole");
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
		AdvertMapper mapper = dataService.getMapper(Advert.class);
		int size = mapper.queryListCount(where);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<Advert> meals = mapper.queryList(where,orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,Advert advert) {
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
			advert.setSchoolId(NetUitl.defaulMultipleSchool(dataService,currentRole.getId(),advert.getSchoolId()));
            if (advert.getId() == null || advert.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	advert.setAuther(currentUser.getTrueName());
            	advert.setCreateDate(TimeUtils.nowString());
            	dataService.insert(advert);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	Advert oldObject = dataService.get(advert.getId(),Advert.class);
                if (oldObject == null) {
                    resultmap.put("state","fail");
                    resultmap.put("mesg","专辑不存在");
                    return resultmap;
                } else {
                	oldObject.copy(advert);
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
    public Map<String, Object> selectById(HttpSession session,Advert advert) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (advert.getId() != null && advert.getId().intValue() > 0) {
            	advert = dataService.get(advert.getId(),Advert.class);
                if (advert == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state","fail");
                resultmap.put("mesg","无法找到该记录的id");
                return resultmap;
            }
            if (!NetUitl.fillMultipleSchool(dataService,resultmap,currentRole.getId(),advert.getSchoolId())) {
            	return resultmap;
            }
            resultmap.put("advert",advert);
            resultmap.put("state","success");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state","fail");
            resultmap.put("mesg","获取失败，系统异常");
            return resultmap;
        }
	}
	
	@ResponseBody
    @RequestMapping(value = "/delete")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deleteMeal(HttpSession session,Advert advert) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (advert.getId() != null && advert.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
                Advert target = dataService.delete(advert.getId(),Advert.class);
                if (target != null) {
            		CommonUtil.deleteFile(uploadFilePath,advert.getAdvImg());
            	}else {
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
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "删除失败，系统异常");
        }
		return resultmap;
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
}
