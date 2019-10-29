package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_curriculum")
@Data
public class Curriculum implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	String name;//名称
	@Column(name = "album_id")
	Integer albumId;//专辑编号
	@Column(name = "school_id")
	Integer schoolId;
	@Transient
	String schoolName;
	String description;//描述
	@Column(name = "play_time")
	String playTime;//时长
	@Column(name = "play_count")
	int playCount;//播放次数
	@Column(name = "subscribe_count")
	int subscribeCount;//订阅次数
	@Column(name = "pay_type")
	String payType;//免费，章节，买断
	float income;//收入
	int praise;//点赞次数
	String stu;//状态[未发布,已发布]
	@Column(name = "sell_price")
	float sellPrice;//售卖金额
	@Column(name = "role_id")
	Integer roleId;//上传的角色编号
	@Column(name = "file_url")
	String fileUrl;//上传文件名称
	@Transient
	int commentNum;//评论数量

	public boolean checkFree() {
		if (payType != null && payType.equals("免费")) {
			return true;
		}
		return false;
	}
	
	public void copy(Curriculum curriculum) {
		this.name = curriculum.name;
		this.payType = curriculum.payType;
		this.playTime = curriculum.playTime;
		this.description = curriculum.description;
		this.sellPrice = curriculum.sellPrice;
		this.albumId = curriculum.albumId;
		this.stu = curriculum.stu;
		this.fileUrl = curriculum.fileUrl;
	}
}
