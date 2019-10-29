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
@Table(name = "t_info")
public class InfoData implements IntegerKey {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	String title;//标题
	String type;//:咨询,公告
	String content;//内容富文本
	Timestamp time;
	String author;//作者
	@Column(name = "read_count")
	Integer readCount;//阅读次数
	Integer sort;//排序规则(降序)
	@Column(name = "school_id")
	String schoolId;//学校编号
	@Column(name = "author_id")
	Integer authorId;//发布者编号
	@Transient
	private String authorName;//发布者名字
	
	public void copy(InfoData info) {
		title = info.title;
		type  = info.type;
		content = info.content;
		readCount = info.readCount;
		sort = info.sort;
		schoolId = info.schoolId;
	}
}
