package com.telit.info.data.business;

import lombok.Data;

@Data
public class ReportView {
	private String type;
	private String name;
	private float total;
	private int count;
	private float divide;
	
	public void addCount() {
		count ++;
	}
	public void addTotal(float value) {
		total += value;
	}
	public void addDivide(float value) {
		divide += value;
	}
}
