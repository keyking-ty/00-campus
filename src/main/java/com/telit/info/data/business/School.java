package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_school")
@Data
public class School implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
	@Column(name = "school_name")
	String schoolName;
	String province;
	String city;
	String area;
	@Column(name = "other_area")
	String otherArea;
	@Column(name = "city_hot")
    boolean cityHot;
    boolean portal;
	boolean rjrz;
	@Column(name = "str_citylink")
	String connetIp;
	@Column(name = "cityhot_login")
	String loginUrl;
	@Column(name = "cityhot_exit")
	String exitUrl;
	@Column(name = "cityhot_online")
	String onlineUrl;
	@Column(name = "city_sbname")
	String terminalId;//终端编号
	@Column(name = "wifi_name")
	String wifiName;
	@Column(name = "net_type")
	String netType;
	@Column(name = "api_version")
	String apiVersion;
	@Column(name = "sign_key")
	String signkey;
	
	public void copy(School school) {
		schoolName = school.schoolName;
		province = school.province;
		city = school.city;
		area = school.area;
		otherArea = school.otherArea;
		cityHot = school.cityHot;
		portal = school.portal;
		rjrz = school.rjrz;
		if (cityHot) {
			connetIp   = school.connetIp;
			loginUrl   = school.loginUrl;
			exitUrl    = school.exitUrl;
			onlineUrl  = school.onlineUrl;
			terminalId = school.terminalId;
			netType    = school.netType;
			apiVersion = school.apiVersion;
			signkey    = school.signkey;
		}
		wifiName = school.wifiName;
	}
}
