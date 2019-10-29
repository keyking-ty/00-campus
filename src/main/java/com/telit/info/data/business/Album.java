package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_album")
@Data
public class Album implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	String name;//名称
	String title;//标题
	String content;//内容简介
	@Column(name = "school_id")
	Integer schoolId;
	@Transient
	String schoolName;
	@Column(name = "change_des")
	String changeDes;//更新简述
	String type;
	String auther;
	@Column(name = "auther_des")
	String autherDes;//作者简述
	@Column(name = "pay_type")
	String payType;//免费，章节，买断
	@Column(name = "role_id")
	Integer roleId;//上传的角色编号
	@Column(name = "icon_url")
	String iconUrl;//图标名称
	@Transient
	float income;//收入
	@Transient
	int playCount;//播放次数
	@Transient
    int total;//课程总数
	@Transient
    int free;//免费数量
	@Transient
	int subscribeCount;//订阅次数
	
	public void copy(Album album) {
		this.name = album.name;
		this.type = album.type;
		this.title = album.title;
		this.auther = album.auther;
		this.autherDes = album.autherDes;
		this.content = album.content;
		this.iconUrl = album.iconUrl;
		this.payType = album.payType;
		this.changeDes = album.changeDes;
	}
}
