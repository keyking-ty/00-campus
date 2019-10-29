package com.telit.info.data.business;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_album_hot")
@Data
public class AlbumHot implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;
	String type;
	String course;
	@Column(name = "album_id")
	int albumId;
	@Column(name = "order_num")
	int orderNum;
	@Column(name = "start_date")
	String startDate;
	@Column(name = "time_last")
	int timeLast;
	String sta;
	@Column(name = "create_time")
	String createTime;
	String auther;
	@Transient
	String albumName;
	@Transient
	String albumTitle;
	
	public void copy(AlbumHot albumHot) {
		this.type      = albumHot.type;
		this.albumId   = albumHot.albumId;
		this.course    = albumHot.course;
		this.orderNum  = albumHot.orderNum;
		this.startDate = albumHot.startDate;
		this.timeLast  = albumHot.timeLast;
		this.sta       = albumHot.sta;
	}
}
