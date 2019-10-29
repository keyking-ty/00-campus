package com.telit.info.data.app;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;


@Data
@Table(name = "t_app_user")
public class AppUser implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	@Column(name = "user_type")
    String userType;//用户类型
	@Column(name = "school_id")
    Integer schoolId;//学校编号
    @Column(name = "login_name")
    String loginName;//登录名称
	@Transient
    String schoolName;//用来显示学校名称
    @Column(name = "login_pwd")
    String loginPwd;//登录密码
    @Column(name = "authen_sta")
    String authenSta = "未认证";//认证状态
    @Column(name = "id_card")
    String idCard;//身份证编号
    @Column(name = "real_name")
    String realName;//真实姓名
	@Column(name = "nick_name")
	String nickName;//昵称
    @Column(name = "idcard_posimg")
    String idcardPosimg;//身份证正面图片url
    @Column(name = "idcard_sideimg")
    String idcardSideimg;//身份证反面图片url
    @Column(name = "stucard_posimg")
    String stucardPosimg;//学生证正面图片url
    @Column(name = "stucard_sideimg")
    String stucardSideimg;//学生证方面图片url
    String mobile;//手机号码
    String account;//学号
    String sex;//性别
    @Column(name = "meal_id")
    Integer mealId = 2;//套餐编号
    @Transient
    String mealName;//用来显示套餐名称
	@Column(name = "meal_card")
    String mealCard;//套餐号码
    @Column(name = "start_date")
    Date startDate;//套餐开始时间
    @Column(name = "end_date")
    Date endDate;//套餐结束时间
    @Column(name = "head_img")
    String headImg;//头像url
    String province;//省份
	String city;//城市
    String area;//区
    @Column(name = "user_adress")
    String userAdress;//街道地址
    @Column(name = "reg_date")
    Date regDate;//注册日期
    String fade;//学院
    String major;//专业
    float balance;//余额

    
	public void copy(AppUser user) {
		this.loginName = user.loginName;
		this.loginPwd = user.loginPwd;
		this.schoolId = user.schoolId;
		this.userType = user.userType;
		this.mobile   = user.mobile;
		this.account  = user.account;
		this.mealId   = user.mealId;
		this.province = user.province;
		this.city = user.city;
		this.area = user.area;
		this.fade = user.fade;
		this.major = user.major;
		this.balance = user.balance;
		this.mealCard = user.mealCard;
		this.nickName = user.nickName;
		this.sex      = user.sex;
	}
}