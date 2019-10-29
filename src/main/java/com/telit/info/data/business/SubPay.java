package com.telit.info.data.business;

import lombok.Data;

@Data
public class SubPay {
	private String name;
	private float amount;
	private String type;
	private int divide;
}
