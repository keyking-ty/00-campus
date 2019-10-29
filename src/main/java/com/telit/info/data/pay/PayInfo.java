package com.telit.info.data.pay;

import lombok.Data;

@Data
public class PayInfo {
    //订单总金额，单位为分
	float totalFee;
    //微信支付订单号
	String transactionId;
    //商户系统的订单号，与请求一致。
	String outTradeNo;
    //商家数据包，原样返回
	String attach;
}
