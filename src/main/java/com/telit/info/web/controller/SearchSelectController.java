package com.telit.info.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.telit.info.data.admin.Role;
import com.telit.info.util.CommonUtil;
import com.telit.info.web.NetUitl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telit.info.data.business.School;
import com.telit.info.trans.service.DataService;


@Controller
@RequestMapping("/admin/search")
public class SearchSelectController {
	@Resource
	private DataService dataService;
	
	@ResponseBody
	@RequestMapping(value = "/schools")
	public String loadSchools(HttpSession session) {
		Role role = (Role) session.getAttribute("currentRole");
		if (role == null){
			return "";
		}
		List<School> schools  = NetUitl.getRolePermissionSchool(dataService,role.getId());
		StringBuffer buffer = new StringBuffer();
		buffer.append("<select>");
		for (int i = 0 ; i < schools.size() ; i++) {
			School school = schools.get(i);
			buffer.append("<option value='" + school.getId() + "'>" + school.getSchoolName() + "</option>");
		}
		buffer.append("</select>");
		return buffer.toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/clients")
	public String clients(String ops) {
		String[] ss = ops.split(",");
		StringBuffer buffer = new StringBuffer();
		buffer.append("<select>");
		for (String s : ss) {
			buffer.append("<option value='" + s + "'>" + s + "</option>");
		}
		buffer.append("</select>");
		return buffer.toString();
	}

	@ResponseBody
	@RequestMapping(value = "/clients2")
	public String clients2(String ops,String vas,String type) throws Exception{
		Class<?> clazz = Class.forName(type);
		List<String> os = CommonUtil.split(ops,",",String.class);
		List<?> vs = CommonUtil.split(vas,",",clazz);
		StringBuffer buffer = new StringBuffer();
		buffer.append("<select>");
		if (os.size() == vs.size()){
			for (int i = 0 ; i < os.size() ; i++) {
				buffer.append("<option value='" + vs.get(i) + "'>" + os.get(i) + "</option>");
			}
		}
		buffer.append("</select>");
		return buffer.toString();
	}
}
