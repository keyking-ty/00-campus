package com.telit.info.web.controller.business;

import com.telit.info.data.business.ReportSearchData;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/admin/report_merge")
public class MergeMealReportController extends ReportController{

	private static final String PERMISSIONS_MENU_STR = "融合套餐";

	public MergeMealReportController(){
		reportType = "全部";
	}
	
	@RequestMapping("/mrg")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public String businessMrg() {
		return "business/report_merge";
	}

	@ResponseBody
	@RequestMapping(value = "/search")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR })
	public Map<String, Object> search(HttpSession session, ReportSearchData data) throws Exception {
		data.setMerge(true);
		return _search(session,data);
	}

	@RequestMapping("export")
	@RequiresPermissions(value = { PERMISSIONS_MENU_STR })
	public void export(HttpSession session, HttpServletResponse response, ReportSearchData data) throws Exception{
		data.setMerge(true);
		_doExport(session, response, data);
	}
}
