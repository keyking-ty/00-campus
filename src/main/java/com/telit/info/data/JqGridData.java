package com.telit.info.data;

import lombok.Data;

@Data
public class JqGridData {
	int rows;
	long nd;
	int page;
	String sidx;
	String sord;
	String filters;
	String searchField;
	String searchString;
	String searchOper;
}
