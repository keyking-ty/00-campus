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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telit.info.actions.ConstData;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.UserIcon;
import com.telit.info.data.admin.MenuOperate;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.Album;
import com.telit.info.data.business.Curriculum;
import com.telit.info.data.business.CurriculumComment;
import com.telit.info.trans.mapper.CurriculumMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

import tk.mybatis.mapper.entity.Example;

@Controller
@RequestMapping("/admin/curriculum")
public class CurriculumMrgController extends BaseListController{
	int curMenuId = ConstData.ALBUM_CURRICULUM_ID;
	private static final String PERMISSIONS_MENU_STR = "课程管理";
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public String businessMrg() {
		return "business/curriculum";
	}
	
	private void initRule() {
		Table table = Curriculum.class.getAnnotation(Table.class);
		CommonUtil.initSqlRule(rules,table.name(),Curriculum.class);
		
		SqlRule rule = new SqlRule(table.name(),String.class);
		rule.setKey("schoolName");
		rule.setType(String.class);
		rule.setColumn("school_id");
		rules.put(rule.getKey(),rule);//用学校标的名称覆盖Curriculum的schoolName属性
		
		String tableName = "sum_data";
		rule = new SqlRule(tableName,Float.class);
		rule.init("commentNum");
		rules.put(rule.getKey(),rule);
	}
	
	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> list(HttpSession session, JqGridData grid) throws Exception {
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (rules.size() == 0) {
			initRule();
		}
		//return NetUitl.list(restTemplate,Curriculum.class,currentRole.getId(),jqgridbean);
		int roleId = currentRole.getId();
		Map<String, Object> result = new HashMap<String, Object>();
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
		CurriculumMapper mapper = dataService.getMapper(Curriculum.class);
		int size = mapper.queryListCount(where,roleId,limitId);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<Curriculum> meals = mapper.queryList(where,roleId,limitId,orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/addOrUpdate")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> addOrUpdate(HttpSession session,Curriculum curriculum) {
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
			Album album = dataService.get(curriculum.getAlbumId(), Album.class);
			if (album == null || album.getRoleId().intValue() != currentRole.getId().intValue()) {
				//只能在自己的角色专辑下面添加课程
				resultmap.put("state","fail");
				resultmap.put("mesg","权限不足");
				return resultmap;
			}
            if (curriculum.getId() == null || curriculum.getId().intValue() == 0) {//新建
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.add.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.add.getError());
        			return resultmap;
            	}
            	curriculum.setRoleId(currentRole.getId());
            	dataService.insert(curriculum);
            } else {//编辑
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.edit.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.edit.getError());
        			return resultmap;
            	}
            	Curriculum oldObject = dataService.get(curriculum.getId(),Curriculum.class);
                if (oldObject == null) {
                    resultmap.put("state","fail");
                    resultmap.put("mesg","订单不存在");
                    return resultmap;
                } else {
                	oldObject.copy(curriculum);
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
    public Map<String, Object> selectById(HttpSession session,Curriculum curriculum) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
            if (curriculum.getId() != null && curriculum.getId().intValue() > 0) {
            	curriculum = dataService.get(curriculum.getId(),Curriculum.class);
                if (curriculum == null) {
                    resultmap.put("state", "fail");
                    resultmap.put("mesg", "无法找到该记录");
                    return resultmap;
                }
            } else {
                resultmap.put("state","fail");
                resultmap.put("mesg","无法找到该记录的id");
                return resultmap;
            }
            resultmap.put("curriculum",curriculum);
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
	public boolean delCurriculum(@PathVariable Integer id) {
		Curriculum curriculum = dataService.delete(id,Curriculum.class);
		if (curriculum == null) {
			return false;
		}
		Example example = new Example(CurriculumComment.class);
		example.createCriteria().andEqualTo("cId",id);
		dataService.deleteByExample(example,CurriculumComment.class);
		//删除视频文件
		CommonUtil.deleteFile(uploadFilePath, curriculum.getFileUrl());
		return true;
	}
	
	@ResponseBody
    @RequestMapping(value = "/delete")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> deleteMeal(HttpSession session,Curriculum curriculum) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (curriculum.getId() != null && curriculum.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
                if (!delCurriculum(curriculum.getId())) {
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
	
	private void _loadComments(Map<String, Object> resultmap, boolean open, Integer id) {
		Example example = new Example(CurriculumComment.class);
		example.createCriteria().andEqualTo("curriculumId",id);
		List<CurriculumComment> ccs = dataService.all(example,CurriculumComment.class);
		if (ccs == null) {
			resultmap.put("ccs", "[]");
		} else {
			CommonUtil.forEach(ccs, (c) -> {
				if (c.getUserId() != null) {
					AppUser user = dataService.get(c.getUserId(), AppUser.class);
					if (user != null) {
						UserIcon icon = new UserIcon();
						icon.setName(user.getRealName());
						icon.setUserId(c.getUserId());
						c.setOwner(icon);
					}
				}
				if (c.getTargetId() != null) {
					AppUser user = dataService.get(c.getTargetId(), AppUser.class);
					if (user != null) {
						UserIcon icon = new UserIcon();
						icon.setName(user.getRealName());
						icon.setUserId(c.getUserId());
						c.setTarget(icon);
					}
				}
			});
			resultmap.put("ccs", ccs);
			resultmap.put("open", open);
		}
	}
	
	@ResponseBody
    @RequestMapping(value = "/comment")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String loadComment(HttpSession session,Curriculum curriculum) {
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			Map<String, Object> resultmap = new HashMap<String, Object>();
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return JsonUtil.encodeToStr(resultmap);
		}
		//String url = ConstData.DATA_APP_NAME + ConstData.REQ_PATH_ADMIN + ConstData.REQ_ADMIN_LOAD_COMMENT;
		//url += "?id={0}&roleId={1}";
		//return restTemplate.getForObject(url,String.class,curriculum.getId(),currentRole.getId());
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Curriculum _curriculum = dataService.get(curriculum.getId(), Curriculum.class);
		if (_curriculum == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "找不到课程,请刷新页面");
		} else {
			resultmap.put("cName", _curriculum.getName());
			boolean flag = NetUitl.checkOperate(dataService,currentRole,ConstData.ALBUM_CURRICULUM_ID,OperateType.deleteComment.getId());
			_loadComments(resultmap,flag,curriculum.getId());
		}
		return JsonUtil.encodeToStr(resultmap);
	}
	
	@ResponseBody
    @RequestMapping(value = "/deleteComment")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String deleteComment(HttpSession session,CurriculumComment cc) {
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			Map<String, Object> resultmap = new HashMap<String, Object>();
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return JsonUtil.encodeToStr(resultmap);
		}
		//String url = ConstData.DATA_APP_NAME + ConstData.REQ_PATH_ADMIN + ConstData.REQ_ADMIN_COMMENT_DEL;
        //return restTemplate.postForObject(url,null,String.class,cc.getId(),cc.getcId());
        Map<String, Object> resultmap = new HashMap<String, Object>();
		if (dataService.delete(cc.getId(),CurriculumComment.class) != null) {
			_loadComments(resultmap,true,cc.getCurriculumId());
		}else {
			resultmap.put("state","fail");
			resultmap.put("mesg","系统异常");
		}
		return JsonUtil.encodeToStr(resultmap);
	}
}
