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
@Table(name = "t_repair")
public class RepairData implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	Timestamp time;
	String content;//内容富文本
	String phone;//电话
	@Column(name = "user_id")
	Integer userId;
	@Column(name = "school_id")
	Integer schoolId;
	String pics;
	String stu;//状态：派单中/已派单/已处理
	Integer operator;//修理编号
	@Transient
	String schoolName;//学校名字
	@Transient
	String userName;//发布者名字
	@Transient
	String operatorName;//维修者名字
}
