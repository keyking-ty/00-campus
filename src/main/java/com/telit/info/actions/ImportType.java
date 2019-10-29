package com.telit.info.actions;

public enum ImportType {
	card("号码导入"),
	regist("批量开户"),
	fuse("批量融合"),
	stop("批量报停"),
	restore("批量复通"),
	open("批量开通")
	;
	
	private ImportType(String value) {
		this.value = value;
	}
	
	private String value;

	public String getValue() {
		return value;
	}
	
	public static ImportType search(int index) {
		ImportType[] datas = values();
		for (int i = 0 ;  i < datas.length ; i++) {
			if (datas[i].ordinal() == index) {
				return datas[i];
			}
		}
		return null;
	}
}
