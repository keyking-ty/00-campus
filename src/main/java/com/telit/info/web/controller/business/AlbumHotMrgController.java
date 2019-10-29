package com.telit.info.web.controller.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
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
import com.telit.info.data.business.Album;
import com.telit.info.data.business.AlbumHot;
import com.telit.info.trans.mapper.AlbumHotMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.util.TimeUtils;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/albumHot")
public class AlbumHotMrgController extends BaseListController{
	int curMenuId = ConstData.ALBUM_HOT_ID;
	private static final String PERMISSIONS_MENU_STR = "推荐管理";
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public String businessMrg() {
		return "business/albumHot";
	}
	
	private void initRule() {
		Table table = AlbumHot.class.getAnnotation(Table.class);
		CommonUtil.initSqlRule(rules,table.name(),AlbumHot.class);
		String tableName = "t_album";
		SqlRule rule = new SqlRule(tableName,String.class);
		rule.init("albumName");
		rules.put(rule.getKey(),rule);
		
		rule = new SqlRule(tableName,String.class);
		rule.init("albumTitle");
		rule.setType(String.class);
		rules.put(rule.getKey(),rule);
	}
	
	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		if (rules.size() == 0) {
			initRule();
		}
		Role role = (Role) session.getAttribute("currentRole");
		int roleId = role.getId();
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
		AlbumHotMapper mapper = dataService.getMapper(AlbumHot.class);
		int size = mapper.queryListCount(where,roleId);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<AlbumHot> meals = mapper.queryList(where,roleId,orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,AlbumHot albumHot) {
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
			RespResult result = NetUitl.checkDataProp(dataService,AlbumHot.class,albumHot.getAlbumId(),"albumId");
        	if (result.getState() > 0) {
            	int id = result.getState();
            	if (albumHot.getId() == null || albumHot.getId().intValue() == 0 || albumHot.getId().intValue() != id) {
            		resultmap.put("state","fail");
            		resultmap.put("mesg",result.getMsg());
                    return resultmap;
            	}
            }
            if (albumHot.getId() == null || albumHot.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	Album album = dataService.get(albumHot.getAlbumId(), Album.class);
            	if (album == null) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg","专辑编号非法");
        			return resultmap;
            	}
            	albumHot.setAuther(currentUser.getTrueName());
            	albumHot.setCreateTime(TimeUtils.nowString());
            	dataService.insert(albumHot);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	AlbumHot oldObject = dataService.get(albumHot.getId(),AlbumHot.class);
                if (oldObject == null) {
                    resultmap.put("state","fail");
                    resultmap.put("mesg","推荐不存在");
                    return resultmap;
                } else {
                	oldObject.copy(albumHot);
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
    public Map<String, Object> selectById(HttpSession session,AlbumHot albumHot) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (albumHot.getId() != null && albumHot.getId().intValue() > 0) {
            	albumHot = dataService.get(albumHot.getId(),AlbumHot.class);
                if (albumHot == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state","fail");
                resultmap.put("mesg","无法找到该记录的id");
                return resultmap;
            }
            resultmap.put("albumHot",albumHot);
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
    public Map<String, Object> deleteMeal(HttpSession session,AlbumHot albumHot) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (albumHot.getId() != null && albumHot.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
            	AlbumHot target = dataService.delete(albumHot.getId(),AlbumHot.class);
                if (target == null) {
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
