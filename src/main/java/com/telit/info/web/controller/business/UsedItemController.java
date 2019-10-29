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
import com.telit.info.data.business.GoodsData;
import com.telit.info.data.business.InfoData;
import com.telit.info.data.business.UsedItem;
import com.telit.info.trans.mapper.UsedItemMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/market")
public class UsedItemController extends BaseListController{
	
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	private int curMenuId = ConstData.MARKET_MENU_ID;
	
	private static final String PERMISSIONS_MENU_STR = "二手市场";
	
	@RequestMapping("/mrg")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String businessMrg() {
        return "business/market";
    }
	
	@ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (rules.size() == 0) {
			Table table = InfoData.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),GoodsData.class);
			SqlRule rule = new SqlRule("t_app_user",String.class);
			rule.setKey("userName");
			rule.setColumn("real_name");
			rules.put(rule.getKey(),rule);
			rule = new SqlRule("t_school",String.class);
			rule.setKey("schoolName");
			rule.setColumn("school_name");
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
		UsedItemMapper mapper = dataService.getMapper(UsedItem.class);
		int size = mapper.queryListCount(where,currentRole.getId());
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<UsedItem> meals = mapper.queryList(where,currentRole.getId(),orderStr,page,grid.getRows());
		PageUtil.page(result,meals,size,grid.getPage(),grid.getRows());
		return result;
	}
	
	@ResponseBody
    @RequestMapping(value = "/selectById")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> selectById(HttpSession session,UsedItem item) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
        try {
        	UsedItemMapper mapper = dataService.getMapper(UsedItem.class);
        	item = mapper.getLinkOne(item.getId());
            if (item == null) {
                resultmap.put("state", "fail");
                resultmap.put("mesg", "无法找到该记录");
                return resultmap;
            }
            resultmap.put("item",item);
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
    @RequestMapping(value = "/delete")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> delete(HttpSession session,UsedItem item) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state","fail");
			resultmap.put("mesg","请先重新登录");
			return resultmap;
		}
		try {
            if (item.getId() != null && item.getId().intValue() != 0) {
            	if (!NetUitl.checkOperate(dataService,currentRole,curMenuId,OperateType.delete.getId())) {
            		resultmap.put("state","fail");
        			resultmap.put("mesg",OperateType.delete.getError());
        			return resultmap;
            	}
            	UsedItem _item  = dataService.delete(item.getId(),UsedItem.class);
        		if (_item == null) {
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
