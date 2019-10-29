package com.telit.info.data.app;

import lombok.Data;

@Data
public class OrderView {
	private int id;
	private String name;
	private String orderNum;
	private String payId;
	private String orderType;
	private String orderSta;
	private float oriPrice;
	private float payPrice;
	private String payType;
	private String payDate;
	private String content;
}
