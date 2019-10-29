package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_card_info")
@Data
public class CardInfo implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "card_number")
	private String cardNumber;
	@Column(name = "card_type")
	private String cardType;
	@Column(name = "predict_value")
	private float predictValue;
	private String operator;//运营商
	private String province;
	private String city;
	private String area;
	@Column(name = "school_id")
	private Integer schoolId;
	@Transient
    private String schoolName;//用来显示学校名称
	private String statu;
	@Column(name = "create_time")
	private String createTime;
	private String auther;
	private String meals;
}
