function getSelectDatas(errorTips){
	var grid = $("#list2");
	var rowKey = grid.getGridParam("selrow");
	if (!rowKey){
		layer.alert(errorTips);
		return null;
	}
	var selects = grid.getGridParam("selarrrow");
	return selects;
}

function getSelectOne(errorTips){
	var id = $("#list2").jqGrid('getGridParam','selrow');
	if (!id){
		layer.alert(errorTips);
		return null;
	}
	return id;
}
