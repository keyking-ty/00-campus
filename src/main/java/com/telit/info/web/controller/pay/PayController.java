package com.telit.info.web.controller.pay;

import com.alipay.api.internal.util.AlipaySignature;
import com.wxpay.sdk.WXPayConfig;
import com.telit.info.actions.ConstData;
import com.telit.info.data.RespResult;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.app.Order;
import com.telit.info.data.app.PayTran;
import com.telit.info.data.business.CityHotInfo;
import com.telit.info.data.business.NetMeal;
import com.telit.info.data.business.School;
import com.telit.info.data.pay.AlipayNotifyParam;
import com.telit.info.data.pay.PayInfo;
import com.telit.info.data.pay.WechatResult;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CityHotUtil;
import com.telit.info.util.JsonUtil;
import com.telit.info.util.TimeUtils;
import com.telit.info.web.NetUitl;
import com.telit.info.web.config.AliPayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ConstData.REQ_PAY_HEAD)
public class PayController {
	@Resource
	private AliPayConfig aliPayConfig;
	@Resource
	private WXPayConfig wxPayConfig;
	@Resource
	private DataService dataService;
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@PostMapping(ConstData.REQ_PAY_WECHAT)
	public WechatResult wechatNotify(HttpServletRequest request) {
		WechatResult result = new WechatResult();
		String return_code = request.getParameter("return_code");
		String return_msg = request.getParameter("return_msg");
		logger.info("wechatNotify return_code = " + return_code + " >>> return_msg =" + return_msg);
		if (ConstData.WECHAT_RESUL_SUCC.equals(return_code)) {
			String result_code = request.getParameter("result_code");
			if (ConstData.WECHAT_RESUL_SUCC.equals(result_code)) {
				String total_fee = request.getParameter("total_fee");
				String transaction_id = request.getParameter("transaction_id");
				String out_trade_no = request.getParameter("out_trade_no");
				String attach = request.getParameter("attach");
				PayInfo payInfo = new PayInfo();
				payInfo.setTotalFee(Float.parseFloat(total_fee) / 100);
				payInfo.setTransactionId(transaction_id);
				payInfo.setOutTradeNo(out_trade_no);
				payInfo.setAttach(attach);
				RespResult resp = new RespResult();
				try {
					doNetMeal(resp, payInfo, ConstData.PAY_TYPE_WECHAT);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("wechatNotify doNetMeal error >>>> " ,e);
				}
				if (resp.getState() == 1) {
					result.setReturn_code(ConstData.WECHAT_RESUL_SUCC);
				} else {
					logger.error("wechatNotify logic error >>>> " + resp.getMsg());
					result.setReturn_msg(resp.getMsg());
				}
			} else {
				logger.info("wechatNotify result_code = " + result_code);
			}
		}
		return result;
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public void doNetMeal(RespResult result, PayInfo payInfo, String payType) throws Exception{
		int orderId = Integer.parseInt(payInfo.getAttach());
		Order order = dataService.get(orderId, Order.class);
		if (order == null) {
			result.setMsg("系统未找到订单数据 >>> " + orderId);
			return;
		}
		if (ConstData.ORDER_STA_OK.equals(order.getOrderSta())) {//这里微信回调可能多次,系统只处理一次
			result.setMsg("订单已处理");
			return;
		}
		AppUser user = dataService.get(order.getUserId(), AppUser.class);
		if (user == null) {
			result.setMsg("系统未找到用户 >>> " + order.getUserId());
			return;
		}
		if (ConstData.ORDER_TYPE_NET_MEAL.equals(order.getOrderType())) {
			// 套餐充值
			NetMeal meal = dataService.get(order.getKeyId(), NetMeal.class);
			if (meal == null) {
				result.setMsg("系统未找到宽带套餐 >>> " + order.getKeyId());
				return;
			}
			if (payInfo.getTotalFee() != meal.getRealyPrice()) {
				result.setMsg("宽带套餐售价和支付金额不匹配:[" + meal.getRealyPrice() + "," + payInfo.getTotalFee() + "]");
				return;
			}
			//这里处理套餐的逻辑
			if ("已认证".equals(user.getAuthenSta())) {
				School school = dataService.get(user.getSchoolId(), School.class);
				if (user.getMealId().intValue() != order.getKeyId().intValue()) {
					if (user.getMealId().intValue() == 2) {
						// 注册套餐
						CityHotInfo info = CityHotUtil.regist(school,user.getAccount(),user.getLoginPwd(),meal.getKeyWord());
						if (!info.checkSucc()) {
							String error = ConstData.CITY_HOT_OPEN_ERROR.get(info.getCode());
							error = error == null ? info.getCode() : error;
							result.setMsg("注册套餐异常  >>> " + error);
							return;
						}
						dataService.insert(info);
					} else {//修改套餐
						List<CityHotInfo> infos = CityHotUtil.change(school,"套餐充值",user.getAccount(), meal.getKeyWord(), "0");
						CityHotInfo last = infos.get(infos.size() - 1);
						if (!last.checkSucc()) {
							String error = null;
							if (infos.size() >= 3) {
								error = ConstData.CITY_HOT_CHANGE_ERROR.get(last.getCode());
							} else if (infos.size() == 2) {
								error = ConstData.CITY_HOT_RESTORE_ERROR.get(last.getCode());
							} else {
								error = ConstData.CITY_HOT_KICK_ERROR.get(last.getCode());
							}
							error = error == null ? last.getCode() : error;
							result.setMsg("修改套餐异常  >>> " + error);
							return;
						}
						dataService.insertMore(infos);
					}
				}
			}
			user.setMealId(order.getKeyId());
			long today = TimeUtils.getTodayZeroTime();
			Date startDate = TimeUtils.DAY_FORMAT.parse(TimeUtils.formatDay(today));
			user.setStartDate(startDate);
			long end = today + meal.getLastTime() * TimeUtils.DAY;
			Date endDate = TimeUtils.DAY_FORMAT.parse(TimeUtils.formatDay(end));
			user.setEndDate(endDate);
			dataService.save(user);
			order.setOrderSta(ConstData.ORDER_STA_OK);
			dataService.save(order);
			PayTran pay = new PayTran();
			pay.setOrderId(order.getId());
			pay.setUserId(user.getId());
			pay.setPayDate(TimeUtils.nowString());
			pay.setPayType(payType);
			pay.setPayAmount(payInfo.getTotalFee());
			pay.setPaySta(ConstData.PAY_STA_OK);
			pay.setPayRem(meal.getName());
			pay.setOutTradeNo(payInfo.getOutTradeNo());
			pay.setTransactionId(payInfo.getTransactionId());
			dataService.insert(pay);
			result.setState(1);
		}
	}

	private Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
		Map<String, String> retMap = new HashMap<String, String>();
		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			String name = entry.getKey();
			String[] values = entry.getValue();
			int valLen = values.length;
			if (valLen == 1) {
				retMap.put(name, values[0]);
			} else if (valLen > 1) {
				StringBuilder sb = new StringBuilder();
				for (String val : values) {
					sb.append(",").append(val);
				}
				retMap.put(name, sb.toString().substring(1));
			} else {
				retMap.put(name, "");
			}
		}
		return retMap;
	}

	@PostMapping(ConstData.REQ_PAY_ALY)
	public String aliNotify(HttpServletRequest request) {
		Map<String, String> params = convertRequestParamsToMap(request);
		try {
			boolean flag = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAliPayPublicKey(), "GBK", "RSA2");
			if (flag) {
				String json = JsonUtil.encodeToStr(params);
				logger.info("aliNotify = " + json);
				AlipayNotifyParam anp = JsonUtil.decodeToObj(json, AlipayNotifyParam.class);
				if (anp.getTradeStatus() == "TRADE_SUCCESS" || anp.getTradeStatus() == "TRADE_FINISHED") {
					//支付宝付款成功
					PayInfo payInfo = new PayInfo();
					payInfo.setTotalFee(anp.getTotalAmount().floatValue());
					payInfo.setTransactionId(anp.getTradeNo());
					payInfo.setOutTradeNo(anp.getOutTradeNo());
					payInfo.setAttach(anp.getPassbackParams());
					RespResult resp = new RespResult();
					doNetMeal(resp,payInfo,ConstData.PAY_TYPE_WECHAT);
					if (resp.getState() == 1) {
						return ConstData.WECHAT_RESUL_SUCC.toLowerCase();
					} else {
						logger.error("aliNotify logic error >>>> " + resp.getMsg());
					}
				}
			}
		} catch (Exception e) {
			logger.error("aliNotify error", e);
		}
		return ConstData.WECHAT_RESUL_FAIL.toLowerCase();
	}

	@PostMapping(ConstData.REQ_PAY_CANCLE)
	public String reBackMoney(@RequestParam Integer orderId){
		return NetUitl.backMoney(orderId,dataService,aliPayConfig,wxPayConfig,logger);
	}
}
