package com.telit.info.web.controller.business;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telit.info.data.business.ReportSearchData;

@Controller
@RequestMapping("/admin/report_dx")
public class DXReportController extends ReportController{

	private static final String PERMISSIONS_MENU_STR = "中国电信";
	
	public DXReportController(){
		reportType = PERMISSIONS_MENU_STR;
	}
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public String businessMrg() {
		return "business/report_dx";
	}

	@ResponseBody
	@RequestMapping(value = "/search")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public Map<String, Object> search(HttpSession session, ReportSearchData data) throws Exception {
		return _search(session, data);
	}

	@RequestMapping("export")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public void export(HttpSession session, HttpServletResponse response,ReportSearchData search) throws Exception{
		_doExport(session, response, search);
	}
}
