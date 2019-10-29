package com.telit.info.web.controller.business;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.ibatis.jdbc.SQL;


import com.telit.info.actions.ConstData;
import com.telit.info.data.admin.Role;
import com.telit.info.data.app.PayTran;
import com.telit.info.data.business.ReportSearchData;
import com.telit.info.data.business.ReportView;
import com.telit.info.data.business.School;
import com.telit.info.data.business.SubPay;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.ExcelUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.NetUitl;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ReportController {
	@Resource
	private DataService dataService;
	
	protected String reportType;
	
	private List<ReportView> report(DataService dataService, Role role,ReportSearchData searchData) {
		List<ReportView> views = new ArrayList<ReportView>();
		String all = "全部";
		List<String> conditions = new ArrayList<String>();
		if (!role.getOperator().equals(all)) {
			conditions.add("meal.operator ='" + role.getOperator() + "'");
		}
		if (CommonUtil.isNotEmpty(searchData.getPayType())) {
			String temp = searchData.getPayType().replace(",","','");
			conditions.add("pay.pay_type IN ('" + temp + "')");
		}
		if (!reportType.equals(all)) {
			conditions.add("pay.pay_rem LIKE '%" + reportType + "%'");
		}
		conditions.add("pay.pay_sta !='已退款'");
		conditions.add("pay.pay_rem !='超市通购买'");
		conditions.add("pay.pay_rem !=''");
		if (CommonUtil.isNotEmpty(searchData.getItemType())) {
			conditions.add("pay.pay_rem like '%" + searchData.getItemType() + "%'");
		}
		if (CommonUtil.isNotEmpty(searchData.getSchoolId())) {
			conditions.add("meal.school_id IN (" + searchData.getSchoolId() + ")");
		}else {
			String s = "SELECT t_role_school.school_id FROM t_role_school WHERE t_role_school.role_id=" + role.getId();
			conditions.add("meal.school_id IN(" + s + ")");
		}
		if (searchData.isMerge()){
			conditions.add("meal._merge='可以'");
		}
		if (CommonUtil.isNotEmpty(searchData.getStartDate())){
			conditions.add("pay.pay_date >'" + searchData.getStartDate() + " 00:00:00'");
		}
		if (CommonUtil.isNotEmpty(searchData.getEndDate())){
			conditions.add("pay.pay_date <'" + searchData.getEndDate() + " 23:59:59'");
		}
		String sql =  new SQL() {
            {
                SELECT("pay.pay_rem name,pay.pay_amount amount,o.order_type type,meal.divide_num divide");
                FROM("t_pay_tran pay");
                INNER_JOIN("t_order o on o.id = pay.order_id");
                INNER_JOIN("t_net_meal meal on meal.id = o.key_id");
				for (int i = 0 ; i < conditions.size(); i++) {
					String str = conditions.get(i);
					WHERE(str);
					if (i < conditions.size() - 1){
						AND();
					}
				}
            }
        }.toString();
        List<SubPay> pays = dataService.selectMoreByDataSql(sql, PayTran.class, (data)->{
        	SubPay pay = new SubPay();
        	pay.setName(data.getString("name"));
        	pay.setAmount(data.getFloat("amount"));
        	pay.setType(data.getString("type"));
        	pay.setDivide(data.getInt("divide"));
        	return pay;
        });
		CommonUtil.forEachContinue(pays,(pay) -> {
			String name = pay.getName();
			ReportView view = CommonUtil.searchObj(views,(v) -> v.getName().equals(name));
			if (view == null) {
				view = new ReportView();
				view.setName(name);
				view.setType(pay.getType());
				views.add(view);
			}
			view.addCount();
			view.addTotal(pay.getAmount());
			float divideNum = pay.getDivide() / 100.0f;
			view.addDivide(divideNum * pay.getAmount());
			return false;
		});
		if (views.size() > 0) {// 排序规则
			int st = searchData.getSidx().equals("count") ? 1 : searchData.getSidx().equals("total") ? 2 : 3;
			views.sort((o1, o2) -> {
				if (searchData.getSord().equals("asc")) {
					if (st == 1) {
						return Integer.compare(o1.getCount(), o2.getCount());
					} else if (st == 2) {
						return Float.compare(o1.getTotal(), o2.getTotal());
					} else {
						return Float.compare(o1.getDivide(), o2.getDivide());
					}
				} else {
					if (st == 1) {
						return Integer.compare(o2.getCount(), o1.getCount());
					} else if (st == 2) {
						return Float.compare(o2.getTotal(), o1.getTotal());
					} else {
						return Float.compare(o2.getDivide(), o1.getDivide());
					}
				}
			});
		}
		return views;
	}
	
	public Map<String, Object> _search(HttpSession session, ReportSearchData data) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Role currentRole = (Role) session.getAttribute("currentRole");
		List<ReportView> views = report(dataService,currentRole,data);
		PageUtil.page(result,views, data.getPage(),data.getRows());
		List<School> schools = NetUitl.getRolePermissionSchool(dataService,currentRole.getId());
		result.put("schools",schools);
		return result;
	}
	
	public void _doExport(HttpSession session, HttpServletResponse response,ReportSearchData search) throws Exception{
		Role currentRole = (Role) session.getAttribute("currentRole");
		if (currentRole == null) {
			return;
		}
		List<ReportView> views = report(dataService,currentRole,search);
		String fileName = UUID.randomUUID().toString() + ".xls";
		OutputStream out = response.getOutputStream();
		response.reset();
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(fileName,"UTF-8"));
		WritableWorkbook book = ExcelUtil.writeExcel(out,ConstData.ReportModulTitles,false);
		if (views != null) {
			WritableSheet sheet = book.getSheet(0);
			for (int i = 0 ; i < views.size() ; i++) {
				ReportView view = views.get(i);
				int row = i + 1;
				Label label = new Label(0,row,view.getName(),ExcelUtil.CenterCellFormat);
				sheet.addCell(label);
				label = new Label(1,row,view.getType(),ExcelUtil.CenterCellFormat);
				sheet.addCell(label);
				label = new Label(2,row,String.valueOf(view.getTotal()),ExcelUtil.CenterCellFormat);
				sheet.addCell(label);
				label = new Label(3,row,String.valueOf(view.getCount()),ExcelUtil.CenterCellFormat);
				sheet.addCell(label);
			}
		}
		book.write();
		book.close();
	}
}
