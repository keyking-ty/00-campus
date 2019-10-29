package com.telit.info.web.controller.app;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telit.info.actions.ConstData;
import com.telit.info.data.RespResult;
import com.telit.info.data.app.Order;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.NetMeal;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.TimeUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@Api(tags = { "宽带套餐" })
@RestController
@RequestMapping("/app/netMeal")
public class OrderController {
	
	@Resource
	private DataService dataService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${domainUrl}")
	private String domainUrl;
	
	@PostMapping("/order")
	public RespResult order(@ApiParam(name = "userId", value = "传入用户编号", required = true) @RequestParam Integer userId,
			@ApiParam(name = "mealId", value = "传入套餐编号", required = true) @RequestParam Integer mealId) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(userId, AppUser.class);
		if (user == null) {
			result.setMsg("错误的用户编号");
			return result;
		}
		NetMeal meal = dataService.get(mealId, NetMeal.class);
		if (meal == null) {
			result.setMsg("错误的套餐编号");
			return result;
		}
		logger.info(user.getMobile() + " order " + meal.getName());
		Order order = new Order();
		String orderNum = CommonUtil.randomStr(32);
		order.setOrderNum(orderNum);
		order.setOrderType(ConstData.ORDER_TYPE_NET_MEAL);
		order.setKeyId(mealId);
		order.setOrderSta(ConstData.PAY_STA_WAIT);
		String price = meal.getRealyPrice() + "";
		order.setOriPrice(price);
		order.setPayPrice(price);
		order.setUserId(userId);
		order.setCreateDate(TimeUtils.nowString());
		order.setContent(meal.getName());
		Integer id = dataService.insert(order);
		result.setState(1);
		result.put("orderNum",orderNum);
		result.put("orderId",id);
		result.put("wechat",domainUrl + ConstData.REQ_PAY_HEAD + ConstData.REQ_PAY_WECHAT);
		result.put("ali",domainUrl + ConstData.REQ_PAY_HEAD + ConstData.REQ_PAY_ALY);
		return result;
	}
}
