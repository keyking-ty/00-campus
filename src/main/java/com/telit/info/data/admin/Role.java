package com.telit.info.data.admin;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.telit.info.data.IntegerKey;

import lombok.Data;


@Table(name = "t_role")
@Data
public class Role implements IntegerKey{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String bz;
    private String name;
    private String remarks;
    private String operator;//运营商
    
	public boolean checkOperator(String operator) {
		if (this.operator != null) {
			return this.operator.equals("全部") || this.operator.equals(operator);
		}
		return false;
	}
}