package com.telit.info.web.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.telit.info.data.admin.UserRole;
import com.telit.info.trans.mapper.UserMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

import tk.mybatis.mapper.entity.Example;

/**
 * 后台管理用户Controller
 * 
 * @author zjt
 */
@Controller
@RequestMapping("/admin/user")
public class UserAdminController extends BaseListController{
	
	private int curMenuId = ConstData.SYS_USER_MENU_ID;
	private final static String PERMISSIONS_MENU_STR = "后台用户";
	
	@RequestMapping("/tousermanage")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public String tousermanage() {
		return "sys/user";
	}

	/**
	 * 分页查询用户信息
	 */
	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> list(JqGridData grid) throws Exception {
		if (rules.size() == 0) {
			Table table = User.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),User.class);
		}
		Map<String, Object> resultmap = new HashMap<String, Object>();
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
		UserMapper mapper = dataService.getMapper(User.class);
		int size = mapper.queryUserCount(where);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<User> users = mapper.queryUser(where,orderStr,page,grid.getRows());
		PageUtil.page(resultmap,users,size,grid.getPage(),grid.getRows());
		return resultmap;
	}

	@ResponseBody
	@RequestMapping(value = "/addupdateuser")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> addupdateuser(HttpSession session, User tuser) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "请先重新登录");
			return resultmap;
		}
		try {
			// 首先判断用户名是否可用
			RespResult rr = NetUitl.checkDataProp(dataService,User.class,tuser.getUserName(),"userName");
			if (rr.getState() > 0) {
				int id = rr.getState();
				if (tuser.getId() == null || tuser.getId().intValue() == 0 || tuser.getId().intValue() != id) {
					resultmap.put("state","fail");
					resultmap.put("mesg",rr.getMsg());
					return resultmap;
				}
			}
			if (tuser.getId() == null || tuser.getId().intValue() == 0) {// 新建
				if (!NetUitl.checkOperate(dataService,currentRole, curMenuId, OperateType.add.getId())) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", OperateType.add.getError());
					return resultmap;
				}
				dataService.insert(tuser);
			} else {
				if (!NetUitl.checkOperate(dataService,currentRole, curMenuId, OperateType.edit.getId())) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", OperateType.edit.getError());
					return resultmap;
				}
				User oldUser = dataService.get(tuser.getId(),User.class);
				if (oldUser == null) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", "当前用户名不存在");
					return resultmap;
				} else {
					oldUser.copy(tuser);
					dataService.save(oldUser);
				}
			}
			resultmap.put("state", "success");
			resultmap.put("mesg", "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("state", "fail");
			resultmap.put("mesg", "操作失败，系统异常");
		}
		return resultmap;
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void delUser(Integer id) {
		Example example = new Example(UserRole.class);
		example.createCriteria().andEqualTo("userId",id);
		dataService.deleteByExample(example,UserRole.class);
		example = new Example(User.class);
		example.createCriteria().andEqualTo("id",id);
		dataService.deleteByExample(example,User.class);
	}
	
	@ResponseBody
	@RequestMapping(value = "/deleteuser")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> deleteuser(HttpSession session,Integer[] ids) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "请先重新登录");
			return resultmap;
		}
		try {
			if (!NetUitl.checkOperate(dataService, currentRole, curMenuId, OperateType.delete.getId())) {
				resultmap.put("state", "fail");
				resultmap.put("mesg", OperateType.delete.getError());
				return resultmap;
			}
			for (Integer id : ids) {
				if (id == 1) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", "无法删除管理员");
					return resultmap;
				}
			}
			for (Integer id : ids) {
				delUser(id);
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
	@RequestMapping(value = "/selectUserById")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> selectUserById(User tuser) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		try {
			if (tuser.getId() != null && !tuser.getId().equals(0)) {
				tuser = dataService.get(tuser.getId(),User.class);
				if (tuser == null) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", "无法找到该记录");
					return resultmap;
				}
			} else {
				resultmap.put("state", "fail");
				resultmap.put("mesg", "无法找到该记录的id");
				return resultmap;
			}
			List<Role> roleList = NetUitl.selectRolesByUserId(dataService,"IN",tuser.getId());
			StringBuffer sb = new StringBuffer();
			for (Role r : roleList) {
				sb.append("," + r.getName());
			}
			tuser.setRoles(sb.toString().replaceFirst(",", ""));
			List<Role> allrolelist = NetUitl.selectRolesByUserId(dataService,"NOT IN",tuser.getId());
			resultmap.put("roleList", roleList);// 用户拥有的所有角色
			resultmap.put("notinrolelist", allrolelist);// 用户不拥有的角色
			resultmap.put("tuser",tuser);
			resultmap.put("state","success");
			resultmap.put("mesg", "获取成功");
			return resultmap;
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("state", "fail");
			resultmap.put("mesg", "获取失败，系统异常");
			return resultmap;
		}
	}

	//设置用户角色
	@ResponseBody
	@RequestMapping(value = "/saveRoleSet")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> saveRoleSet(HttpSession session, Integer[] role, Integer id) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "请先重新登录");
			return resultmap;
		}
		if (!NetUitl.checkOperate(dataService,currentRole, curMenuId,OperateType.editRole.getId())) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", OperateType.editRole.getError());
			return resultmap;
		}
		Example example = new Example(UserRole.class);
		example.createCriteria().andEqualTo("userId",id);
		dataService.deleteByExample(example,UserRole.class);
		CommonUtil.forEach(role, (data) -> {
			UserRole tuserrole = new UserRole();
			tuserrole.setRoleId(data);
			tuserrole.setUserId(id);
			dataService.insert(tuserrole);
		});
		resultmap.put("state","success");
		resultmap.put("mesg", "设置成功");
		return resultmap;
	}

	/**
	 * 安全退出
	 *
	 * @return
	 * @throws Exception
	 */

	@GetMapping("/logout")
	@RequiresPermissions(value = { "安全退出" })
	public String logout() throws Exception {
		SecurityUtils.getSubject().logout();
		return "redirect:/tologin";
	}

	// 跳转到修改密码页面
	@RequestMapping("/toUpdatePassword")
	@RequiresPermissions(value = { "修改密码" })
	public String toUpdatePassword() {
		return "sys/updatePassword";
	}

	// 修改密码
	@ResponseBody
	@PostMapping("/updatePassword")
	@RequiresPermissions(value = { "修改密码" })
	public Map<String, Object> updatePassword(Integer id,String oldPassword,String newPassword) throws Exception {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		try {
			User oldUser = dataService.get(id,User.class);
			if (oldUser == null || !oldUser.getPassword().equals(oldPassword)) {
				resultmap.put("state", "fail");
				resultmap.put("mesg", "用户名或密码错误");
				return resultmap;
			} else {
				oldUser.setPassword(newPassword);
				dataService.save(oldUser);
			}
			resultmap.put("state", "success");
			resultmap.put("mesg", "密码修改成功");
			return resultmap;
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("state", "fail");
			resultmap.put("mesg", "密码修改失败，系统异常");
			return resultmap;
		}
	}
}
