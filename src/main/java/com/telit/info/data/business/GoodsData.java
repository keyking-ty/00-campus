package com.telit.info.data.business;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Data
@Table(name = "t_goods")
public class GoodsData implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	String type;
	String content;
	String phone;//联系电话
	Timestamp time;
	@Column(name = "user_id")
	Integer userId;
	@Column(name = "school_id")
	Integer schoolId;
	String pics;
	@Transient
	String schoolName;//学校名字
	@Transient
	String userName;//发布者名字
}
