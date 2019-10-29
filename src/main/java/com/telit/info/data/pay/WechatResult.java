package com.telit.info.data.pay;

import com.telit.info.actions.ConstData;

public class WechatResult {
	private String return_code = ConstData.WECHAT_RESUL_FAIL;
	private String return_msg  = ConstData.WECHAT_RESUL_OK;
	
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
}
