package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_packm")
@Data
public class CityHotInfo implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "user_id")
	private Integer userId;
	private String account;
	@Column(name = "group_id")
	private Integer groupId;
	@Column(name = "city_group_id")
	private String cityGroupId = "0";
	@Column(name = "create_date")
	private String createDate;
	private String sta;
	private String rem;
	private String clstr;
	@Column(name = "serialNumber")
	private String serialNumber;
	@Transient
	private String code;
	
	public boolean checkSucc() {
		if (code == null) {
			return true;
		}
		return code.equals("E00");
	}
}
