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
import com.telit.info.data.JqGridData;
import com.telit.info.data.SearchFilter;
import com.telit.info.data.SqlRule;
import com.telit.info.data.admin.Role;
import com.telit.info.data.app.Order;
import com.telit.info.data.app.OrderView;
import com.telit.info.trans.mapper.OrderMapper;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.PageUtil;
import com.telit.info.web.controller.BaseListController;

@Controller
@RequestMapping("/admin/order")
public class OrderMrgController extends BaseListController{
	int curMenuId = ConstData.ORDER_MRG_MENU_ID;
	private static final String PERMISSIONS_MENU_STR = "订单管理";
	
	@RequestMapping("/mrg")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public String businessMrg() {
        return "business/order";
    }
	
	private void initRule() {
		SqlRule rule = new SqlRule();
		rule.setKey("name");
		rule.setType(String.class);
		rule.setTable("t_app_user");
		rule.setColumn("real_name");
		rules.put(rule.getKey(),rule);
		
		rule = new SqlRule();
		rule.setKey("payId");
		rule.setType(String.class);
		rule.setTable("t_pay_tran");
		rule.setColumn("out_trade_no");
		rules.put(rule.getKey(),rule);
		
		rule = new SqlRule();
		rule.setKey("payType");
		rule.setType(String.class);
		rule.setTable("t_pay_tran");
		rule.setColumn("pay_type");
		rules.put(rule.getKey(),rule);
		
		rule = new SqlRule();
		rule.setKey("payDate");
		rule.setType(String.class);
		rule.setTable("t_pay_tran");
		rule.setColumn("pay_date");
		rules.put(rule.getKey(),rule);
	}
	
	@ResponseBody
    @RequestMapping(value = "/list")
    @RequiresPermissions(value = {PERMISSIONS_MENU_STR})
    public Map<String, Object> list(HttpSession session,JqGridData grid) throws Exception {
		Role role = (Role) session.getAttribute("currentRole");
		if (rules.size() == 0) {
			Table table = Order.class.getAnnotation(Table.class);
			CommonUtil.initSqlRule(rules,table.name(),Order.class);
			initRule();
		}
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
		String operators = null;//运营商权限判断
		if (!role.getOperator().equals("全部")) {
			operators = "'" + role.getOperator() + "'";
		}
		OrderMapper mapper = dataService.getMapper(Order.class);
		int size = mapper.queryListCount(where,roleId,operators);
		int page = grid.getPage() > 1 ? grid.getPage() - 1 : 0 ;
		List<OrderView> orders = mapper.queryList(where,roleId,operators,orderStr,page,grid.getRows());
		PageUtil.page(result,orders,size,grid.getPage(),grid.getRows());
		return result;
	}
}
