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
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.MenuOperate;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.business.Album;
import com.telit.info.data.business.Curriculum;
import com.telit.info.trans.mapper.AlbumMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

import tk.mybatis.mapper.entity.Example;

@Controller
@RequestMapping("/admin/album")
public class AlbumMrgController extends BaseListController{
	int curMenuId = ConstData.ALBUM_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "专辑管理";
	
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public String businessMrg() {
		return "business/album";
	}
	
	private void initRule() {
		Table table = Album.class.getAnnotation(Table.class);
		CommonUtil.initSqlRule(rules,table.name(),Album.class);
		SqlRule rule = new SqlRule(table.name(),String.class);
		rule.setKey("schoolName");
		rule.setType(String.class);
		rule.setColumn("school_id");
		rules.put(rule.getKey(),rule);//用学校标的名称覆盖Album的schoolName属性
		
		String tableName = "sum_data";
		rule = new SqlRule(tableName,Float.class);
		rule.init("income");
		rules.put(rule.getKey(),rule);
		
		rule = new SqlRule(tableName,Float.class);
		rule.init("playCount");
		rules.put(rule.getKey(),rule);
		
		rule = new SqlRule(tableName,Float.class);
		rule.init("total");
		rules.put(rule.getKey(),rule);

		rule = new SqlRule(tableName,Float.class);
		rule.init("free");
		rules.put(rule.getKey(),rule);
		
		rule = new SqlRule(tableName,Float.class);
		rule.init("subscribeCount");
		rules.put(rule.getKey(),rule);
	}
	
	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> list(HttpSession session , JqGridData grid) throws Exception {
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (rules.size() == 0) {
			initRule();
		}
		Map<String, Object> result = new HashMap<String, Object>();
		int roleId = currentRole.getId();
		Example temp = new Example(MenuOperate.class);
		temp.createCriteria().andEqualTo("menuId",ConstData.ALBUM_CURRICULUM_ID).andEqualTo("roleId",currentRole.getId())
		.andEqualTo("operateId",OperateType.curriculumPass.ordinal());
		int limitId = 0 ;
		if (dataService.search(temp,MenuOperate.class) == null) {
			limitId = roleId;//不是管理员就只能看到自己的专辑
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
		AlbumMapper mapper = dataService.getMapper(Album.class);
		int size = mapper.queryListCount(where,roleId,limitId);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<Album> meals = mapper.queryList(where, roleId, limitId,orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,Album album) {
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
            if (album.getId() == null || album.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	album.setRoleId(currentRole.getId());
            	dataService.insert(album);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	Album oldObject = dataService.get(album.getId(),Album.class);
                if (oldObject == null) {
                    resultmap.put("state","fail");
                    resultmap.put("mesg","专辑不存在");
                    return resultmap;
                } else {
                	oldObject.copy(album);
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
    public Map<String, Object> selectById(HttpSession session,Album album) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (album.getId() != null && album.getId().intValue() > 0) {
            	album = dataService.get(album.getId(),Album.class);
                if (album == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state","fail");
                resultmap.put("mesg","无法找到该记录的id");
                return resultmap;
            }
            resultmap.put("album",album);
            resultmap.put("state","success");
            return resultmap;
        } catch (Exception e) {
            e.printStackTrace();
            resultmap.put("state","fail");
            resultmap.put("mesg","获取失败，系统异常");
            return resultmap;
        }
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean delAlbum(Integer id) {
		Album album = dataService.delete(id, Album.class);
		if (album == null) {
			return false;
		}
		Example example = new Example(Curriculum.class);
		example.createCriteria().andEqualTo("aId",id);
		List<Curriculum> cs = dataService.all(example,Curriculum.class);
		dataService.deleteByExample(example, Curriculum.class);
		// 删除icon文件
		CommonUtil.forEach(cs,(data) -> {
			CommonUtil.deleteFile(uploadFilePath, data.getFileUrl());
		});
		CommonUtil.deleteFile(uploadFilePath, album.getIconUrl());
		return true;
	}
	
	@ResponseBody
    @RequestMapping(value = "/delete")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deleteMeal(HttpSession session,Album album) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (album.getId() != null && album.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
                if (!delAlbum(album.getId())) {
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
}
