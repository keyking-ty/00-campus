package com.telit.info.web.controller.admin;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telit.info.data.admin.Menu;
import com.telit.info.data.admin.MenuOperate;
import com.telit.info.data.admin.Operate;
import com.telit.info.data.admin.RoleMenu;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;

import tk.mybatis.mapper.entity.Example;



@Controller
@RequestMapping("/admin/menu")
public class MenuController {
	@Resource
	private DataService dataService;
	
	private final static String PERMISSIONS_MENU_STR = "菜单管理";
	
    @RequestMapping("/tomunemanage")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String tousermanage() {
        return "sys/menu";
    }
    
	private JsonArray getAllMenuByParentId(List<Menu> menus, Integer parentId) {
		JsonArray jsonArray = getMenuByParentId(menus, parentId);
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
			if (jsonObject.get("open").getAsString().equals("true")) {
				JsonArray arrays = getAllMenuByParentId(menus, jsonObject.get("id").getAsInt());
				jsonObject.add("children", arrays);
			}
		}
		return jsonArray;
	}

	private JsonArray getMenuByParentId(List<Menu> menus, Integer parentId) {
		JsonArray jsonArray = new JsonArray();
		CommonUtil.forEachContinue(menus, (menu) -> {
			if (menu.getpId().intValue() != parentId.intValue()) {
				return true;
			}
			JsonObject jsonObject = new JsonObject();
			Integer menuId = menu.getId();
			jsonObject.addProperty("id", menuId); // 节点id
			jsonObject.addProperty("name", menu.getName()); // 节点名称
			// 判断该节点下是否还有子节点
			if (CommonUtil.search(menus, (data) -> data.getpId().intValue() == menuId.intValue())) {
				jsonObject.addProperty("open", "true"); // 有子节点
			} else {
				jsonObject.addProperty("open", "false"); // 无子节点
			}
			jsonObject.addProperty("state", String.valueOf(menu.getState()));
			jsonObject.addProperty("iconValue", menu.getIcon());
			jsonObject.addProperty("pId", String.valueOf(menu.getpId()));
			jsonArray.add(jsonObject);
			return false;
		});
		return jsonArray;
	}
	
    @ResponseBody
    @PostMapping("/loadCheckMenuInfo")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String loadCheckMenuInfo(Integer parentId) throws Exception {
    	List<Menu> memus = dataService.all(null, Menu.class);
		memus.sort((o1, o2) -> Integer.compare(o1.getId().intValue(), o2.getId().intValue()));
		JsonObject json = new JsonObject();
		JsonArray array = getAllMenuByParentId(memus, parentId);
		json.add("menus", array);
		List<Operate> operates = dataService.all(null, Operate.class);
		json.add("ops", JsonUtil.gson.toJsonTree(operates));
		return json.toString();
    }

    /**
    * @Description: 编辑节点之前将该节点select出来
    * @Date: Created in 2018/2/24 17:03
    * @param 
    */
    @ResponseBody
    @RequestMapping(value = "/selectMenuById")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> selectMenuById(Integer id) {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        try {
            if (id == null || id == 0){
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法获取节点id");
                return resultmap;
            }else{
        		Menu tmenu = dataService.get(id,Menu.class);
                if (tmenu == null){
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该节点对象");
                    return resultmap;
                }else{
                    resultmap.put("state", "success");
                    resultmap.put("mesg", "获取该节点对象成功");
                    resultmap.put("tmenu", tmenu);
                    return resultmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state", "fail");
            resultmap.put("mesg", "操作失败，系统异常");
            return resultmap;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/addupdatemenu")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String,Object> addupdatemenu(Menu tmenu) {
    	//String url = ConstData.DATA_APP_NAME + ConstData.REQ_PATH_ADMIN + ConstData.REQ_ADMIN_MENU_ADD_OR_UPDATE;
    	//return restTemplate.postForObject(url,tmenu,String.class);
    	Map<String,Object> resultmap = new HashMap<String, Object>();
		try {
			// 首先校验本次新增操作提交的菜单对象中的name属性的值是否存在
			int _id = tmenu.getId() == null ? 0 : tmenu.getId().intValue();
			Example example = new Example(Menu.class);
			example.createCriteria().andEqualTo("name",tmenu.getName()).andNotEqualTo("id",_id);
			if (dataService.search(example,Menu.class) != null) {
				resultmap.put("state", "fail");
				resultmap.put("mesg", "当前菜单名已存在");
				return resultmap;
			}
			if (_id == 0) {// 新建
				// 校验是否提交了pId
				if (tmenu.getpId() == null || tmenu.getpId().intValue() == 0) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", "无法获取父级id");
					return resultmap;
				} else {
					Menu pmenu = dataService.get(tmenu.getpId(),Menu.class);
					if (pmenu != null && pmenu.getState() == 3) {
						resultmap.put("state", "fail");
						resultmap.put("mesg", "3级菜单不可再添加子菜单");
						return resultmap;
					}
					if ("-1".equalsIgnoreCase(String.valueOf(pmenu.getpId()))
							&& "1".equalsIgnoreCase(String.valueOf(pmenu.getState()))) {// 如果父节点是最顶级那一个，则本次新增为一级菜单
						// 一级菜单的名字不可为纯数字
						if (isNumeric(tmenu.getName())) {
							resultmap.put("state", "fail");
							resultmap.put("mesg", "1级菜单的名字不可为纯数字");
							return resultmap;
						}
						tmenu.setState(1);
					} else if ("1".equalsIgnoreCase(String.valueOf(pmenu.getpId()))
							&& "1".equalsIgnoreCase(String.valueOf(pmenu.getState()))) {// 如果父节点是一级菜单，本次新增为2级菜单
						tmenu.setState(2);
					} else if (!"1".equalsIgnoreCase(String.valueOf(pmenu.getpId()))
							&& "2".equalsIgnoreCase(String.valueOf(pmenu.getState()))) {// 如果父节点是二级菜单，本次新增为3级菜单
						tmenu.setState(3);
					}
					example = new Example(Menu.class);
					example.createCriteria().andEqualTo("pId",tmenu.getpId());
					example.setOrderByClause("id desc");//按id降序排列
					List<Menu> list = dataService.all(example,Menu.class);
					if (list != null && list.size() > 0) {
						//如果本次新增的菜单实体的同一级菜单集合不为空
						tmenu.setId(list.get(0).getId() + 1);// 获取已经存在的同级菜单的id的最大值+1
					} else {// 如果本次新增的菜单实体还没有同一级的菜单的话，则根据父节点生成子节点id
						if ("1".equalsIgnoreCase(String.valueOf(tmenu.getpId()))) {
							tmenu.setId(tmenu.getpId() * 10);// 第一个一级菜单id为1*10
						} else {
							tmenu.setId(tmenu.getpId() * 100);// 二级三级菜单id生成策略为根据父菜单id*100
						}
					}
				}
				dataService.insert(tmenu);// 新加菜单
			} else {
				//编辑(对于节点的编辑只允许编辑icon、name、url)
				Menu oldMenu = dataService.get(tmenu.getId(),Menu.class);
				if (oldMenu == null) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", "菜单不存在");
					return resultmap;
				}
				oldMenu.setIcon(tmenu.getIcon());
				oldMenu.setName(tmenu.getName());
				oldMenu.setUrl(tmenu.getUrl());
				oldMenu.setOperates(tmenu.getOperates());
				dataService.save(oldMenu);
			}
			resultmap.put("state", "success");
			resultmap.put("mesg", "操作成功");
			resultmap.put("id", tmenu.getId());
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("state", "fail");
			resultmap.put("mesg", "操作失败，系统异常");
		}
		return resultmap;
    }
    
    @Transactional(rollbackFor = Exception.class)
	private void delMenu(Integer id) {
		//删除自己以及子菜单
		Example example = new Example(Menu.class);
		example.createCriteria().orEqualTo("pId",id).orEqualTo("id",id);
		List<Menu> menus = dataService.all(example,Menu.class);
		dataService.deleteByExample(example,Menu.class);
		//删除自己以及子菜单角色权限
		List<Integer> dels = CommonUtil.selectProp(menus,(data)->data.getId());
		example = new Example(RoleMenu.class);
		example.createCriteria().andIn("menuId",dels);
		dataService.deleteByExample(example,RoleMenu.class);
		//删除自己以及子菜单操作
		example = new Example(MenuOperate.class);
		example.createCriteria().andIn("menuId",dels);
		dataService.deleteByExample(example,MenuOperate.class);
	}
    
    @ResponseBody
    @RequestMapping(value = "/deletemenu")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deletemenu(HttpSession session,Menu tmenu) {
        Map<String, Object> resultmap = new HashMap<String, Object>();
        try {
            if(tmenu.getId()!=null && !tmenu.getId().equals(0)){
                Menu menu = dataService.get(tmenu.getId(),Menu.class);
                if (menu == null){
                    resultmap.put("state","fail");
                    resultmap.put("mesg","删除失败,无法找到该记录");
                    return resultmap;
                }else{
                    delMenu(tmenu.getId());
                }
            }else{
                resultmap.put("state", "fail");
                resultmap.put("mesg", "删除失败");
            }
            resultmap.put("state","success");
            resultmap.put("mesg","删除成功");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state","fail");
            resultmap.put("mesg","删除失败，系统异常");
            return resultmap;
        }
    }
}
