package com.telit.info.data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "t_import_log")
public class ImportResult implements IntegerKey{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String school = " ";
	private String meal = " ";
	private String auther;
	private String type;
	@Column(name = "operate_time")
	private String operateTime;
	@Column(name = "file_name")
	private String fileName;
	@Column(name = "total_num")
	private int totalNum;
	@Column(name = "succ_num")
	private int succNum;
	@Column(name = "fail_num")
	private int failNum;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMeal() {
		return meal;
	}

	public void setMeal(String meal) {
		this.meal = meal;
	}

	public String getAuther() {
		return auther;
	}

	public void setAuther(String auther) {
		this.auther = auther;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getSuccNum() {
		return succNum;
	}

	public void setSuccNum(int succNum) {
		this.succNum = succNum;
	}

	public int getFailNum() {
		return failNum;
	}

	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}

	public void addSucc() {
		this.succNum  ++;
		this.totalNum ++;
	}
	
	public void addFail() {
		this.failNum ++;
		this.totalNum ++;
	}
}
