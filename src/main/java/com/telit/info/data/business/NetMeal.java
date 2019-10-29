package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_net_meal")
@Data
public class NetMeal implements IntegerKey {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	@Column(name = "school_id")
	private Integer schoolId;
	@Transient
    private String schoolName;//用来显示学校名称
	private String operator;//运营商
	@Column(name = "icon_url")
	private String iconUrl;//图标
	@Column(name = "net_type")
	private String netType;
	private String name;
	@Column(name = "last_time")
	private int lastTime;
	@Column(name = "band_width")
	private int bandWidth;
	@Column(name = "key_word")
	private String keyWord;//运营商的套餐编号
	private float price;
	@Column(name = "realy_price")
	private float realyPrice;
	@Column(name = "divide_num")
	private int divideNum;
	@Column(name = "hot_order")
	private int hotOrder;
	@Column(name = "inner_order")
	private int innerOrder;
	private String description;
	private String content;
	private String statu;
	private String auther;
	@Column(name = "activity_rule")
	private String activityRule;
	@Column(name = "_merge")
	private String merge;
	
	public void copy(NetMeal meal) {
		schoolId     = meal.schoolId;
		operator     = meal.operator;
		iconUrl      = meal.iconUrl;
		netType      = meal.netType;
		name         = meal.name;
		lastTime     = meal.lastTime;
		bandWidth    = meal.bandWidth;
		keyWord      = meal.keyWord;
		price        = meal.price;
		realyPrice   = meal.realyPrice;
		divideNum    = meal.divideNum;
		hotOrder     = meal.hotOrder;
		innerOrder   = meal.innerOrder;
		description  = meal.description;
		content      = meal.content;
		statu        = meal.statu;
		activityRule = meal.activityRule;
		merge        = meal.merge;
	}
}
