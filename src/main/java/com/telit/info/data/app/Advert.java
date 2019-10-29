package com.telit.info.data.app;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.telit.info.data.IntegerKey;

import lombok.Data;

@Table(name = "t_advert")
@Data
public class Advert implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "adv_type")
    private String advType;
	@Column(name = "adv_title")
    private String advTitle;
	@Column(name = "adv_img")
    private String advImg;
	@Column(name = "adv_lnk")
    private String advLnk;
	@Column(name = "adv_info")
    private String advInfo;
	@Column(name = "create_date")
    private String createDate;
	@Column(name = "adv_sort")
    private Integer advSort;
	@Column(name = "adv_sta")
    private String advSta;
	private String auther;
	@Column(name = "start_date")
	private String startDate;
	@Column(name = "time_last")
	int timeLast;
	@Column(name = "school_id")
	String schoolId;//学校编号
	
	public void copy(Advert advert) {
		this.advTitle = advert.advTitle;
		this.advImg = advert.advImg;
		this.advInfo = advert.advInfo;
		this.advLnk = advert.advLnk;
		this.advSta = advert.advSta;
		this.advSort = advert.advSort;
		this.startDate = advert.startDate;
		this.timeLast = advert.timeLast;
		this.schoolId = advert.schoolId;
	}
}