package com.telit.info.data;

import lombok.Data;

@Data
public class FileUploadResult {
	boolean succ;
	String msg;
	String fileName;
	
	public void fill(String str) {
		if (fileName == null) {
			fileName = str;
		}else {
			fileName = fileName + "," + str;
		}
	}
}
