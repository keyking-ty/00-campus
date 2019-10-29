package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;
import com.telit.info.data.UserIcon;

import lombok.Data;

@Table(name = "t_cur_comment")
@Data
public class CurriculumComment implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	@Column(name = "curriculum_id")
	Integer curriculumId;//课程编号
	String content;//评论内容
	@Column(name = "create_time")
	String createTime;//评论时间
	@Column(name = "user_id")
	Integer userId;//评论人编号
	@Column(name = "target_id")
	Integer targetId;//回复目标
	@Transient
	UserIcon owner;
	@Transient
	UserIcon target;
}
