package com.telit.info.web.controller.business;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.telit.info.actions.ImportType;
import com.telit.info.data.ImportResult;
import com.telit.info.data.JqGridData;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.ExcelUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

public class MoreActionController {
	@Autowired 
	DataService dataService;
	
	@Value("${importNumberLog}")
	String logPath;
	
	Map<String,SqlRule> rules = new HashMap<String,SqlRule>();
	
	Map<String, Object> _list(HttpSession session,JqGridData grid,ImportType type) throws Exception {
		if (rules.size() == 0) {
			Table table = ImportResult.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),ImportResult.class);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		Example example = new Example(ImportResult.class);
		Criteria conds = example.createCriteria();
		if (type != null) {
			conds.andEqualTo("type", type.getValue());
		}
		SearchFilter filter = JsonUtil.decodeToObj(grid.getFilters(),SearchFilter.class);
		if (filter != null && filter.getRules() != null) {
			filter.conds(rules,conds);
		}
		String sid = grid.getSidx();
		if (CommonUtil.isNotEmpty(sid)) {
			example.setOrderByClause(sid + " " + grid.getSord());
		}
		List<ImportResult> logs = dataService.all(example,ImportResult.class);
		PageUtil.page(result,logs,grid.getPage(),grid.getRows());
		return result;
	}
	
	String postData(Integer roleId,List<List<String>> datas,String auther,ImportType type) {
		String result = NetUitl.importMoreData(dataService,type,datas,roleId,auther,logPath);
		return result;
	}
	
	void downModuleFile(HttpServletResponse response,String fileName,String[] titles) {
		try {
			OutputStream out = response.getOutputStream();
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
			ExcelUtil.writeExcel(out,titles,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
