package com.telit.info.web.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telit.info.actions.ConstData;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.RespResult;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Menu;
import com.telit.info.data.admin.MenuOperate;
import com.telit.info.data.admin.Operate;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.RoleMenu;
import com.telit.info.data.admin.RoleSchool;
import com.telit.info.data.admin.UserRole;
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.RoleMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;

import tk.mybatis.mapper.entity.Example;


@Controller
@RequestMapping("/admin/role")
public class RoleAdminController {
	@Resource
	private DataService dataService;
	
	private int curMenuId = ConstData.ROLE_MRG_MENU_ID;
	private final static String PERMISSIONS_MENU_STR = "角色管理";
	private Map<String,SqlRule> rules = new HashMap<String,SqlRule>();
	
    @RequestMapping("/torolemanage")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String tousermanage() {
        return "sys/role";
    }

    /**
      * 分页查询角色信息
     */
    @ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(JqGridData grid) throws Exception {
		Map<String, Object> resultmap = new HashMap<String, Object>();
    	if (rules.size() == 0) {
			Table table = Role.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),Role.class);
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
		RoleMapper mapper = dataService.getMapper(Role.class);
		int size = mapper.queryRoleCount(where);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<Role> roles = mapper.queryRole(where,orderStr,page,grid.getRows());
		PageUtil.page(resultmap,roles,size,grid.getPage(),grid.getRows());
		return resultmap;
    }


    @ResponseBody
    @RequestMapping(value = "/addupdaterole")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addupdaterole(HttpSession session,Role trole) {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
        	//首先判断名称是否可用
        	RespResult rr = NetUitl.checkDataProp(dataService,Role.class,trole.getName(),"name");
            if (rr.getState() > 0) {
            	int id = rr.getState();
            	if (trole.getId() == null || trole.getId().intValue() == 0 || trole.getId().intValue() != id) {
            		resultmap.put("state","fail");
            		resultmap.put("mesg",rr.getMsg());
                    return resultmap;
            	}
            }
            if (trole.getId() == null || trole.getId().intValue() == 0) {//新建
            	//判断有没有权限
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	dataService.insert(trole);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
                Role oldObject = dataService.get(trole.getId(),Role.class);
                if (oldObject == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "角色不存在");
                    return resultmap;
                } else {
                	oldObject.setOperator(trole.getOperator());
                	oldObject.setName(trole.getName());
                	oldObject.setBz(trole.getBz());
                	dataService.save(oldObject);
                }
            }
            resultmap.put("state","success");
            resultmap.put("mesg","操作成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，系统异常");
            return resultmap;
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
	public void delRole(Integer id) {
		//删除关联表数据
		Example example = new Example(RoleMenu.class);
		example.createCriteria().andEqualTo("roleId",id);
		dataService.deleteByExample(example,RoleMenu.class);
		
		example = new Example(UserRole.class);
		example.createCriteria().andEqualTo("roleId",id);
		dataService.deleteByExample(example,UserRole.class);
		
		example = new Example(MenuOperate.class);
		example.createCriteria().andEqualTo("roleId",id);
		dataService.deleteByExample(example,MenuOperate.class);
		
		example = new Example(RoleSchool.class);
		example.createCriteria().andEqualTo("roleId",id);
		dataService.deleteByExample(example,RoleSchool.class);
		
		//删除角色
		example = new Example(Role.class);
		example.createCriteria().andEqualTo("id",id);
		dataService.deleteByExample(example,Role.class);
	}
    

    @ResponseBody
    @RequestMapping(value = "/deleterole")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deleteuser(HttpSession session,Role trole) {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (trole.getId() != null && trole.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
            	Role role = dataService.get(trole.getId(),Role.class);
                if (role == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "删除失败,无法找到该记录");
                    return resultmap;
                } else {
                    delRole(trole.getId());
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
    @RequestMapping(value = "/selectRoleById")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> selectRoleById(Role trole) {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        try {
            if (trole.getId() != null && trole.getId().intValue() != 0) {
                trole = dataService.get(trole.getId(),Role.class);
                if (trole == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录的id");
                return resultmap;
            }
            resultmap.put("trole",trole);
            resultmap.put("state","success");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "获取失败，系统异常");
            return resultmap;
        }
    }
    
    private JsonArray getCheckedMenuByParentId(List<Menu> menus, Integer parentId, List<Integer> permissionMenus) {
		JsonArray jsonArray = new JsonArray();
		CommonUtil.forEachContinue(menus, (menu) -> {
			if (menu.getpId().intValue() != parentId.intValue()) {
				return true;
			}
			JsonObject jsonObject = new JsonObject();
			Integer menuId = menu.getId();
			jsonObject.addProperty("id", menuId); // 节点id
			jsonObject.addProperty("name", menu.getName()); // 节点名称
			if (CommonUtil.search(menus, (data) -> data.getpId().intValue() == menuId.intValue())) {
				jsonObject.addProperty("open", "true");// 有子节点
			} else {
				jsonObject.addProperty("open", "false");// 无子节点
			}
			if (permissionMenus.contains(menuId)) {
				jsonObject.addProperty("checked", true);
			}
			jsonArray.add(jsonObject);
			return false;
		});
		return jsonArray;
	}
    
    private JsonArray getAllCheckedMenuByParentId(List<Menu> menus, Integer parentId, List<Integer> permissionMenus) {
		JsonArray jsonArray = getCheckedMenuByParentId(menus, parentId, permissionMenus);
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
			// 判断该节点下时候还有子节点
			String open = jsonObject.get("open").getAsString();
			if (open.equals("true")) {
				Integer pid = jsonObject.get("id").getAsInt();
				jsonObject.add("children", getAllCheckedMenuByParentId(menus, pid, permissionMenus));
			}
		}
		return jsonArray;
	}
    
    /**
     * 根据父节点获取所有复选框权限菜单树
     *
     * @param parentId
     * @param roleId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/loadCheckMenuInfo")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String loadCheckMenuInfo(Integer parentId, Integer roleId) throws Exception {
    	//String url = ConstData.DATA_APP_NAME + ConstData.REQ_PATH_ADMIN + ConstData.REQ_ADMIN_LOAD_MENU_INFO;
    	//return restTemplate.getForObject(url,String.class,parentId,roleId);
    	// 根据角色查询所有权限菜单信息
		List<Menu> permissionMenus = NetUitl.selectMenusByRoleId(dataService,roleId);
		List<Menu> menus = dataService.all(null, Menu.class);
		menus.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
		List<Integer> ids = CommonUtil.selectProp(permissionMenus,(data)->data.getId());
		String json = getAllCheckedMenuByParentId(menus,parentId,ids).toString();
		return json;
    }
    
    /**
     * 保存角色权限设置
     *
     * @param menuIds
     * @param roleId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/saveMenuSet")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> saveMenuSet(HttpSession session,String menuIds, Integer roleId) throws Exception {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.setMenu.getId())) {
    		resultmap.put("state","fail");
			resultmap.put("mesg",OperateType.setMenu.getError());
			return resultmap;
    	}
        menuIds = menuIds.trim();
        menuIds = menuIds.isEmpty() ? "null" : menuIds;
        try {
			NetUitl.changeRolePermission(dataService,roleId,0,menuIds);
			resultmap.put("state","success");
	        resultmap.put("mesg","操作成功");
		} catch (Exception e) {
			resultmap.put("state","success");
	        resultmap.put("mesg","系统错误");
	        e.printStackTrace();
		}
        return resultmap;
    }
    
    @ResponseBody
    @PostMapping("/loadMenuSchools")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String loadMenuSchools(Integer roleId) throws Exception {
    	//String url = ConstData.DATA_APP_NAME + ConstData.REQ_PATH_ADMIN + ConstData.REQ_ADMIN_LOAD_MENU_SCHOOLS;
    	//return restTemplate.getForObject(url,String.class,roleId);
    	List<School> schools = dataService.all(null,School.class);
		Example example = new Example(RoleSchool.class);
		example.createCriteria().andEqualTo("roleId",roleId);
		List<RoleSchool> mss = dataService.all(example,RoleSchool.class);
		JsonArray array = new JsonArray();
		CommonUtil.forEach(schools, (school) -> {
			Integer sid = school.getId();
			JsonObject child = new JsonObject();
			child.addProperty("checked", false);
			CommonUtil.forEachBreak(mss, (ms) -> {
				if (ms.getSchoolId() != null && ms.getSchoolId().intValue() == sid.intValue()) {
					child.addProperty("checked", true);
					return true;
				}
				return false;
			});
			child.addProperty("id", sid);// 节点id
			child.addProperty("name", school.getSchoolName());// 节点名称
			child.addProperty("open", "false");
			array.add(child);
		});
		return array.toString();
    }
    
    @ResponseBody
    @RequestMapping("/saveSchoolSet")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> saveSchoolSet(HttpSession session,String schoolIds, Integer roleId) throws Exception {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.setSchool.getId())) {
    		resultmap.put("state","fail");
			resultmap.put("mesg",OperateType.setSchool.getError());
			return resultmap;
    	}
        schoolIds = schoolIds.trim();
        schoolIds = schoolIds.isEmpty() ? "null" : schoolIds;
        if (!StringUtils.isEmpty(schoolIds)) {
            NetUitl.changeRolePermission(dataService,roleId,1,schoolIds);
        }else{
            resultmap.put("state","fail");
            resultmap.put("mesg","操作失败，未获取选中记录，请重新选择");
            return resultmap;
        }
        resultmap.put("state","success");
        resultmap.put("mesg","操作成功");
        return resultmap;
    }
    
    @ResponseBody
    @PostMapping("/loadMenuOperates")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String loadMenuOperates(Integer roleId) throws Exception {
    	//String url = ConstData.DATA_APP_NAME + ConstData.REQ_PATH_ADMIN + ConstData.REQ_ADMIN_LOAD_MENU_OPERATES;
    	//return restTemplate.getForObject(url,String.class,roleId);
    	List<Operate> operates = dataService.all(null,Operate.class);
		Example example = new Example(MenuOperate.class);
		example.createCriteria().andEqualTo("roleId",roleId);
		List<MenuOperate> mps = dataService.all(example,MenuOperate.class);
		List<Menu> menuList = NetUitl.selectMenusByRoleId(dataService,roleId);
		menuList.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
		JsonArray array = new JsonArray();
		CommonUtil.forEachContinue(menuList, (menu) -> {
			if (menu.getState() != 2 || menu.notHaveOperate()) {
				return true;
			}
			JsonObject jsonObject = new JsonObject();
			Integer menuId = menu.getId();
			jsonObject.addProperty("id", "m_" + menuId);// 节点id
			jsonObject.addProperty("name", menu.getName());// 节点名称
			jsonObject.addProperty("open", "true");// 有子节点
			jsonObject.addProperty("checked", true);
			JsonArray chidren = new JsonArray();
			List<Integer> hids = CommonUtil.split(menu.getOperates(), ",",Integer.class);
			CommonUtil.forEachContinue(operates, (operate) -> {
				Integer oid = operate.getId();
				if (!hids.contains(oid)) {
					return true;
				}
				JsonObject child = new JsonObject();
				child.addProperty("checked", false);
				CommonUtil.forEachBreak(mps, (mp) -> {
					if (mp.getMenuId() != null && mp.getMenuId().intValue() == menuId.intValue()
							&& mp.getOperateId() != null && mp.getOperateId().intValue() == oid.intValue()) {
						child.addProperty("checked", true);
						return true;
					}
					return false;
				});
				child.addProperty("id", oid);// 节点id
				child.addProperty("name", operate.getName());// 节点名称
				child.addProperty("open", "false");
				chidren.add(child);
				return false;
			});
			jsonObject.add("children", chidren);
			array.add(jsonObject);
			return false;
		});
		return array.toString();
    }
    
    @ResponseBody
    @RequestMapping("/saveOperateSet")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> saveOperateSet(HttpSession session,String operateIds, Integer roleId) throws Exception {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.setOperate.getId())) {
    		resultmap.put("state","fail");
			resultmap.put("mesg",OperateType.setOperate.getError());
			return resultmap;
    	}
        operateIds = operateIds.trim();
        operateIds = operateIds.isEmpty() ? "null" : operateIds;
        if (!StringUtils.isEmpty(operateIds)) {
            NetUitl.changeRolePermission(dataService,roleId,2,operateIds);
        }else{
            resultmap.put("state","fail");
            resultmap.put("mesg","操作失败，未获取选中记录，请重新选择");
            return resultmap;
        }
        resultmap.put("state","success");
        resultmap.put("mesg","操作成功");
        return resultmap;
    }
}
