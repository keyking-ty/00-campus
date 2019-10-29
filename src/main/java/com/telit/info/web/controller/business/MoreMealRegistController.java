package com.telit.info.web.controller.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.telit.info.actions.ConstData;
import com.telit.info.actions.ImportType;
import com.telit.info.actions.OperateType;
import com.telit.info.data.JqGridData;
import com.telit.info.data.admin.Role;
import com.telit.info.data.admin.User;
import com.telit.info.util.ExcelUtil;
import com.telit.info.web.NetUitl;

@Controller
@RequestMapping("/admin/more1")
public class MoreMealRegistController extends MoreActionController{
	
	private int curMenuId = ConstData.MORE1_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "批量开通";
	@RequestMapping("/mrg")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public String businessMrg() {
		return "business/more1";
	}
	
	@ResponseBody
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		return _list(session,grid,ImportType.regist);
	}

	@ResponseBody
	@RequestMapping("/import")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
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
				if (titles.size() != ConstData.OpenUserImportModulTitles.length) {
					resultmap.put("state", "fail");
					resultmap.put("mesg", ConstData.NumberImportErrorMsg);
					return resultmap;
				}
				datas.remove(0);
				String result = postData(currentRole.getId(),datas,auther,ImportType.regist);
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
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public void downModule(HttpServletResponse response) {
		downModuleFile(response,"regist.xls",ConstData.OpenUserImportModulTitles);
	}

	@RequestMapping("downLoadLog")
	@RequiresPermissions(value = {PERMISSIONS_MENU_STR})
	public void downLog(HttpServletResponse response, @RequestParam Integer id) {
		NetUitl.downLog(dataService,response,id,logPath);
	}
}
