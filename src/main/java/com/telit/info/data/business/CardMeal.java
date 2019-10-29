package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telit.info.data.IntegerKey;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;

import lombok.Data;

@Table(name = "t_card_meal")
@Data
public class CardMeal implements IntegerKey{
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
	private String name;
	private float price;
	@Column(name = "realy_price")
	private float realyPrice;
	@Column(name = "divide_num")
	private int divideNum;
	@Column(name = "hot_order")
	private int hotOrder;
	private String content;
	private String statu;
	private String auther;
	@Column(name = "activity_type")
	private String activityType;
	
	public void copy(CardMeal meal) {
		schoolId         = meal.schoolId;
		operator         = meal.operator;
		iconUrl          = meal.iconUrl;
		name             = meal.name;
		price            = meal.price;
		realyPrice       = meal.realyPrice;
		divideNum        = meal.divideNum;
		hotOrder         = meal.hotOrder;
		statu            = meal.statu;
		content          = meal.content;
		activityType     = meal.activityType;
	}
	
	public void deletePics(String uploadFilePath) {
		CommonUtil.deleteFile(uploadFilePath,iconUrl);
		if (content != null && !content.isEmpty()) {
			JsonArray array = JsonUtil.gson.fromJson(content, JsonArray.class);
			for (int i = 0 ; i < array.size() ; i++) {
				JsonObject json = array.get(i).getAsJsonObject();
				int t = json.get("t").getAsInt();
				if (t == 1) {
					CommonUtil.deleteFile(uploadFilePath,json.get("v").getAsString());
				}
			}
		}
	}
}
