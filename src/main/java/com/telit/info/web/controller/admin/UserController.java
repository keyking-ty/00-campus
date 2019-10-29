package com.telit.info.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telit.info.data.admin.Menu;
import com.telit.info.data.admin.Operate;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.trans.mapper.MenuMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.web.NetUitl;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Resource
	private DataService dataService;
	
	@Value("${server.servlet.context-path}")
	private String contextPath;
	
	static final String operatorCheckSql = "SELECT t_operate.type type FROM t_operate INNER JOIN t_menu_operate on t_menu_operate.operate_id = t_operate.id WHERE t_menu_operate.role_id={1} AND t_menu_operate.menu_id={2}";
	
	/**
	 * 用户登录请求
     * @param user
     * @return
     */
    @ResponseBody
    @PostMapping("/login")
    public Map<String,Object> login(String imageCode, @Valid User user, BindingResult bindingResult, HttpSession session){
    	Map<String,Object> map = new HashMap<String,Object>();
    	/*
    	if(StringUtils.isEmpty(imageCode)){
    		map.put("success", false);
    		map.put("errorInfo", "请输入验证码！");
    		return map;
    	}
    	if(!session.getAttribute("checkcode").equals(imageCode)){
    		map.put("success", false);
    		map.put("errorInfo", "验证码输入错误！");
    		return map;
    	}*/
    	if (bindingResult.hasErrors()){
    		map.put("success", false);
    		map.put("errorInfo", bindingResult.getFieldError().getDefaultMessage());
    		return map;
    	}
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(),user.getPassword());
		try{
			subject.login(token);//登录认证
			String userName   = (String) SecurityUtils.getSubject().getPrincipal();
			User currentUser  = NetUitl.getDataByConditions(dataService,User.class,userName,"userName");
			session.setAttribute("currentUser",currentUser);
			subject.getSession().setTimeout(1800000);//session过期时间，30分钟
			List<Role> roleList = NetUitl.selectRolesByUserId(dataService,"IN",currentUser.getId());
			map.put("roleList",roleList);
			map.put("roleSize",roleList.size());
			map.put("success",true);
			return map;
		}catch(Exception e){
			e.printStackTrace();
			map.put("success",false);
			map.put("errorInfo","用户名或者密码错误！");
			return map;
		}
    }

	/**
	 * 保存角色信息
	 * @param roleId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/saveRole")
	public Map<String,Object> saveRole(Integer roleId,HttpSession session)throws Exception{
		Map<String,Object> map = new HashMap<String,Object>();
		Role currentRole = dataService.get(roleId,Role.class);
		session.setAttribute("currentRole",currentRole);//保存当前角色信息
		MenuMapper mapper = dataService.getMapper(Menu.class);
		List<Menu> tops = mapper.getTopMenusByRole(roleId,1);
		session.setAttribute("tmenuOneClassList",tops);
		map.put("success",true);
		return map;
	}


	/**
	 * 安全退出
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/logout")
	public String logout() throws Exception {
		SecurityUtils.getSubject().logout();
		return "redirect:/tologin";
	}

	@ResponseBody
	@RequestMapping(value = "/checkMenuOperate")
	public String checkMenuOperate(HttpSession session,Integer menu)throws Exception{
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			return "fail";
		}
		String sql = operatorCheckSql.replace("{1}",currentRole.getId().toString()).replace("{2}",menu.toString());
		List<String> datas = dataService.selectMoreByDataSql(sql,Operate.class,(data)->{
			return data.getString("type");
		});
		return JsonUtil.encodeToStr(datas);
	}
	
	/**
	 * 加载权限菜单
	 * @param session
	 * @return
	 * @throws Exception
	 * 这里传入的parentId是1
	 */
	@ResponseBody
	@GetMapping("/loadMenuInfo")
	public String loadMenuInfo(HttpSession session, Integer parentId)throws Exception{
		Role currentRole = (Role) session.getAttribute("currentRole");
		MenuMapper mapper = dataService.getMapper(Menu.class);
		List<Menu> menus = mapper.getAllMenusByRole(currentRole.getId());
		List<Menu> tops = new ArrayList<Menu>();
		Map<Integer,List<Menu>> temp = new HashMap<Integer,List<Menu>>();
		for (Menu menu : menus) {
			Integer pId = menu.getpId();
			if (pId == parentId) {
				tops.add(menu);
			}else {
				List<Menu> children = temp.get(pId);
				if (children == null) {
					children = new ArrayList<Menu>();
					temp.put(pId,children);
				}
				children.add(menu);
			}
		}
		JsonObject jMenus = new JsonObject();
		for (Menu top : tops) {
			JsonObject json = menuToJson(top,temp);
			jMenus.add(top.getName(),json.get("children"));
		}
		session.setAttribute("tmenuOneClassList",tops);
		return jMenus.toString();
	}
	
	private JsonObject menuToJson(Menu menu, Map<Integer,List<Menu>> temp) {
		JsonObject json = new JsonObject();
		if (menu.getpId() != 1) {
			json.addProperty("id", menu.getId()); //节点id
			json.addProperty("title", menu.getName());//节点名称
			json.addProperty("spread", false); //不展开
			json.addProperty("icon", menu.getIcon());
			if (!StringUtils.isEmpty(menu.getUrl())) {
				json.addProperty("href", contextPath + menu.getUrl());//菜单请求地址
			}
		}
		List<Menu> children = temp.get(menu.getId());
		if (children != null && children.size() > 0) {
			JsonArray jsonArray = new JsonArray();
			CommonUtil.forEach(children, (child) -> {
				JsonObject obj = menuToJson(child,temp);
				jsonArray.add(obj);
			});
			json.add("children",jsonArray);
		}
		return json;
	}
}
