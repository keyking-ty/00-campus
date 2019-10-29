package com.telit.info.actions;

public enum OperateType {
	add(1,"你没有添加权限"),
	edit(2,"你没有修改权限"),
	delete(3,"你没有删除权限"),
	editRole(4,"你没有编辑角色权限"),
	setMenu(5,"你没有设置菜单权限"),
	setSchool(6,"你没有设置学校权限"),
	setOperate(7,"你没有设置设置权限"),
	importData(8,"你没有导入权限"),
	authenUser(9,"你没有认证权限"),
	backMoney(10,"你没退款权限"),
	curriculumPass(11,"你没有课程审核权限"),
	deleteComment(12,"你没有删除评论权限"),
	allotRepair(13,"你没有维修派单权限"),
	completeRepair(14,"你没有完成维修权限")
	;
	
	private String error;
	private int id;
	private OperateType(int id,String error){
		this.id = id;
		this.error = error;
	}
	public String getError() {
		return error;
	}
	public int getId() {
		return id;
	}
}
