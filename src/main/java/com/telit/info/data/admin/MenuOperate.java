package com.telit.info.data.admin;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.telit.info.data.IntegerKey;

@Table(name = "t_menu_operate")
public class MenuOperate implements IntegerKey{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	@Column(name = "menu_id")
	private Integer menuId;
	@Column(name = "operate_id")
	private Integer operateId;
	@Column(name = "role_id")
	private Integer roleId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMenuId() {
		return menuId;
	}
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	public Integer getOperateId() {
		return operateId;
	}
	public void setOperateId(Integer operateId) {
		this.operateId = operateId;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
}
