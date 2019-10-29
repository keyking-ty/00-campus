function checkOperate(contextPath,menu,flag){
	$.ajax({
        type: "POST",
        url: contextPath + '/user/checkMenuOperate',
        data: {menu: menu},
        async: true,
        dataType: 'json',
        timeout: 3000,
        cache: false,
        complete: function(jqXHR, textStatus){
            if (jqXHR.status == 401){
                top.location.href = contextPath + '/tologin';
            }
        },
        success: function(data) {
        	if (data == "fail"){
        		return;
        	}
        	for (var i = 0 ; i < data.length ; i++){
    			$('#' + data[i]).show();
    		}
        }
    });
};
function IWS_CheckDecimal(obj) {
    var temp = /^\d+\.?\d{0,2}$/;
    if (!temp.test(obj.value)) {
        obj.value = obj.value.substr(0, obj.value.length - 1);
        IWS_CheckDecimal(obj);
    }
};
