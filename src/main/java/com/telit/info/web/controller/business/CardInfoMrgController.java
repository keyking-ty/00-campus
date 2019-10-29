package com.telit.info.web.controller.business;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.telit.info.actions.ConstData;
import com.telit.info.actions.ImportType;
import com.telit.info.actions.OperateType;
import com.telit.info.data.ImportResult;
import com.telit.info.data.JqGridData;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.data.business.CardInfo;
import com.telit.info.data.business.NetMeal;
import com.telit.info.trans.mapper.CardInfoMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.ExcelUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;
import com.telit.info.web.controller.BaseListController;

import tk.mybatis.mapper.entity.Example;

@Controller
@RequestMapping("/admin/number")
public class CardInfoMrgController extends BaseListController{
	@Value("${importNumberLog}")
	private String logPath;
	private int curMenuId = ConstData.NUMBER_MRG_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "号码管理";
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public String businessMrg() {
		return "business/card";
	}

	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		if (rules.size() == 0) {
			Table table = CardInfo.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),CardInfo.class);
			SqlRule rule = new SqlRule();
			rule.setKey("schoolName");
			rule.setType(String.class);
			rule.setTable("t_card_info");
			rule.setColumn("school_id");
			rules.put(rule.getKey(),rule);//用学校标的名称覆盖CardInfo的schoolName属性
		}
		Role currentRole = (Role) session.getAttribute("currentRole");
		Integer roleId = currentRole.getId();
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
		String operators = null;//运营商权限判断
		if (!currentRole.getOperator().equals("全部")) {
			operators = "'" + currentRole.getOperator() + "'";
		}
		CardInfoMapper mapper = dataService.getMapper(CardInfo.class);
		int size = mapper.queryListCount(where,roleId,operators);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<CardInfo> cards = mapper.queryList(where,roleId,operators,orderStr,page,grid.getRows());
		PageUtil.page(result,cards,size,grid.getPage(),grid.getRows());
		return result;
	}

	@ResponseBody
	@RequestMapping("/import")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public Map<String, Object> importExcel(HttpSession session, MultipartFile file) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "请先重新登录");
			return resultmap;
		}
		User currentUser = (User) session.getAttribute("currentUser");
		if (currentUser == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "请先重新登录");
			return resultmap;
		}
		try {
			if (!NetUitl.checkOperate(dataService, currentRole, curMenuId, OperateType.importData.getId())) {
				resultmap.put("state", "fail");
				resultmap.put("mesg", OperateType.importData.getError());
				return resultmap;
			}
			String auther = currentUser.getTrueName();
			List<List<String>> datas = ExcelUtil.readExcel(file.getInputStream());
			if (datas.size() > 1) {
				List<String> titles = datas.get(0);
				if (titles.size() != ConstData.NumberImportModulTitles.length) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", ConstData.NumberImportErrorMsg);
					return resultmap;
				}
				datas.remove(0);
				String result = NetUitl.importMoreData(dataService,ImportType.card, datas,currentRole.getId(),auther,logPath);
				resultmap.put("state",result);
			} else {
				resultmap.put("state", "fail");
				resultmap.put("mesg", "没有数据可导入");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultmap.put("state", "fail");
			resultmap.put("mesg", "导入失败系统异常");
		}
		return resultmap;
	}

	@RequestMapping("importModule.xls")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public void downModule(HttpServletResponse response) {
		try {
			OutputStream out = response.getOutputStream();
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode("card.xls", "UTF-8"));
			ExcelUtil.writeExcel(out,ConstData.NumberImportModulTitles,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@ResponseBody
	@RequestMapping("logs")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public String allLogs() {
		Example example = new Example(ImportResult.class);
		example.createCriteria().andEqualTo("type",ImportType.card.getValue());
		List<ImportResult> logs = dataService.all(example,ImportResult.class);
		if (logs != null) {
			return JsonUtil.encodeToStr(logs);
		}
		return "[]";
		//String url = ConstData.DATA_APP_NAME + ConstData.REQ_PATH_ADMIN + ConstData.REQ_ADMIN_LIST_LOG;
		//String result = restTemplate.getForObject(url,String.class);
		//if (result == null) {
		//	return "[]";
		//}
		//return result;
	}
	
	@RequestMapping("importLog")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public void downLog(HttpServletResponse response, @RequestParam Integer id) {
		NetUitl.downLog(dataService,response,id,logPath);
	}
	
	@ResponseBody
	@RequestMapping(value = "/lookMealDetail")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public Map<String, Object> lookMealDetail(Integer id) {
		Map<String, Object> resultmap = new HashMap<String, Object>();
		CardInfo card = dataService.get(id, CardInfo.class);
		if (card == null) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "找不到号码信息");
			return resultmap;
		}
		if (card.getMeals() == null || card.getMeals().isEmpty()) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "该号码没有配置套餐信息");
			return resultmap;
		}
		List<String> names = new ArrayList<>();
		List<Integer> ids = CommonUtil.split(card.getMeals(), ",",Integer.class);
		if (ids != null && ids.size() > 0) {
			for (Integer mealId : ids) {
				NetMeal meal = dataService.get(mealId, NetMeal.class);
				if (meal == null) {
					continue;
				}
				names.add(meal.getName());
			}
		}
		if (names.size() == 0) {
			resultmap.put("state", "fail");
			resultmap.put("mesg", "该号码配置套餐信息异常");
		} else {
			resultmap.put("state", "success");
			resultmap.put("names", names);
		}
		return resultmap;
	}
}
