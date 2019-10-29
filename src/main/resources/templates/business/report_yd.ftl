<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	    <title>中国移动报表</title>
		<!-- jqGrid组件基础样式包-必要 -->
		<link href="${request.contextPath!}/static/css/global.css" type="text/css" media="screen" rel="stylesheet"/>
		<link href="${request.contextPath!}/static/layui/css/layui.css" type="text/css" media="screen" rel="stylesheet"/>
		<link href="${request.contextPath!}/static/css/ztree/metroStyle/metroStyle.css" type="text/css" media="screen" rel="stylesheet"/>
		<link href="${request.contextPath!}/static/css/ztree/demo.css" type="text/css" media="screen" rel="stylesheet"/>
		<script type="text/ecmascript" src="${request.contextPath!}/static/jqgrid/js/jquery.min.js"></script> 
	    <script type="text/ecmascript" src="${request.contextPath!}/static/jqgrid/js/trirand/jquery.jqGrid.min.js"></script>
	    <script type="text/ecmascript" src="${request.contextPath!}/static/jqgrid/js/trirand/i18n/grid.locale-cn.js"></script>
	    <link rel="stylesheet" type="text/css" media="screen" href="${request.contextPath!}/static/jqgrid/css/jquery-ui.css" />
	    <link rel="stylesheet" type="text/css" media="screen" href="${request.contextPath!}/static/jqgrid/css/trirand/ui.jqgrid.css" />
	    <script type="text/ecmascript" src="${request.contextPath!}/static/jqgrid/js/jquery-ui.min.js"></script>
	    <script type="text/ecmascript" src="${request.contextPath!}/static/jqgrid/js/prettify/prettify.js"></script>
	    <link rel="stylesheet" href="${request.contextPath!}/static/jqgrid/css/prettify.css" />
		<script type="text/ecmascript" src="${request.contextPath!}/static/jqgrid/js/codetabs.js"></script>
		<script type="text/ecmascript" src="${request.contextPath!}/static/jqgrid/js/themeswitchertool.js"></script>
		<script src="${request.contextPath!}/static/layui/layui.js" type="text/javascript"></script>
		<script src="${request.contextPath!}/static/js/operate.js" type="text/javascript"></script>
		<script src="${request.contextPath!}/static/js/jqutil.js" type="text/javascript"></script>
		<!--地区插件引入-->
		<script src="${request.contextPath!}/static/js/select.js" type="text/javascript"></script>
		<script type="text/javascript">
	        var layerid;//当前弹层id;这个id可以定义多个，主要的目的是为了在回调函数关闭弹层时使用的
			function checkBoxStr(flag){
				var ids = [];
				var code = "input[name='" + flag + "']:checked";
				$(code).each(function(){
					ids.push($(this).val());
				});
				return ids.join(',');
			}

			function pageInit() {
				//创建jqGrid组件
				$("#list2").jqGrid({
					mtype: "post",//向后台请求数据的ajax的类型。可选post,get
					url: '${request.contextPath!}/admin/report_yd/search',
					datatype: "json",//请求数据返回的类型。可选json,xml,txt
					colNames: ['类型','商品','交易金额(单位:元)','运营商分成','交易数量',],//jqGrid的列显示名字
					colModel: [
						{label: '类型', name: 'type', width: 100, sortable: true,align:'center',search: false},
						{label: '商品', name: 'name', width: 100, sortable: true,align:'center',search: false},
						{label: '交易金额(元)', name: 'total', width: 100, sortable: true,align:'center',search: false},
						{label: '运营商分成', name: 'divide', width: 100, sortable: true,align:'center',search: false},
						{label: '交易数量', name: 'count', width: 100, sortable: true,align:'center',search: false}
					],
					page: 1,
					rowNum: 20,//一页显示多少条
					rowList: [10, 20, 30],//可供用户选择一页显示多少条
					pager: '#pager2',//表格页脚的占位符(一般是div)的id
					height: '100%',
					autowidth: true,
					loadError: function (xhr,status,error) {
						if (xhr.status == 401){
							top.location.href = ${request.contextPath!} + '/tologin';
						}
					},
					loadComplete: function (data) {
						var htmlStr = '';
						data.schools.forEach(function(school){
							htmlStr += '<input type="checkbox" name="schoolId" title="' + school.schoolName + '" value="' + school.id + '"/>';
						});
						$("#searchSchoolId").html(htmlStr);
						layui.form.render();
					}
				});
				/*创建jqGrid的操作按钮容器*/
				/*可以控制界面上增删改查的按钮是否显示*/
				$("#list2").jqGrid('navGrid', '#pager2', {
					//设置为false需要自己重新重新该方法
					edit: false, add: false, del: false, search: false,
				});
			}
	        $(function () {
	            //页面加载完成之后执行
	            pageInit();
	            layui.use(['layer','form','layedit','laydate'], function(){
	            	layui.laydate.render({
					  elem: '#startDate'//指定元素
					});
					layui.laydate.render({
					  elem: '#endDate'//指定元素
					});
	            });
				$("#searchBtn").click(function () {
					var start = $("#startDate").val();
					var end = $("#endDate").val();
					if (start !='' && end != ''){
						if(Date.parse(start) > Date.parse(end)){
							layer.alert('结束日期不能早于开始日期');
							return false;
						}
					}
					$("#list2").jqGrid('setGridParam', {
						url : "${request.contextPath!}/admin/report_yd/search",
						datatype:'json',
						postData : {
							itemType: $("#itemType").val(),
							startDate: start,
							endDate: end,
							schoolId: checkBoxStr('schoolId'),
							payType: checkBoxStr('payType')
						},
						page : 1
					}).trigger("reloadGrid");
				});
				$("#exportBtn").click(function () {
					var start = $("#startDate").val();
					var end = $("#endDate").val();
					if (start !='' && end != ''){
						if(Date.parse(start) > Date.parse(end)){
							layer.alert('结束日期不能早于开始日期');
							return false;
						}
					}
					layer.open({//开启表单弹层
						skin: 'layui-layer-molv',
						area: ['500px,400px'],
						type: 1,
						title:'导出配置',
						content: $('#exportDivId') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
					});
					return false;
				});
				$("#sureDown").click(function () {
					var _data = 'itemType=' + $("#itemType").val();
					_data += '&startDate=' + $("#startDate").val();
					_data += '&endDate=' + $("#endDate").val();
					_data += '&schoolId=' + checkBoxStr('schoolId');
					_data += '&payType=' + checkBoxStr('payType');
					_data += '&' + $("#exportRuleFormId").serialize();
					window.open('${request.contextPath!}/admin/report_yd/export?' + _data);
				});
	        });
		</script>
	</head>
	<body>
		<div class="layui-field-box">
			<form id="searchFormId" class="layui-form">
				<div class="block-new-item">
					<div class="layui-inline">
						<label class="layui-form-label">商品名称:</label>
						<div class="layui-input-inline">
							<input id="itemType" name="itemType" type="text"  class="layui-input"/>
						</div>
					</div>
					<div class="layui-inline">
						<label class="layui-form-label">支付方式:</label>
						<div class="layui-input-inline">
							<input type="checkbox" name="payType" title="微信支付" value="微信支付" />
							<input type="checkbox" name="payType" title="支付宝支付" value="支付宝支付" />
							<input type="checkbox" name="payType" title="线下支付" value="线下支付" />
						</div>
					</div>
					<div class="layui-inline">
						<label class="layui-form-label">学校:</label>
						<div id="searchSchoolId" class="layui-input-inline">

						</div>
					</div>
					<div class="layui-inline">
						<label class="layui-form-label">开始日期:</label>
						<div class="layui-input-inline">
							<input id="startDate" name="startDate" type="text"  class="layui-input"/>
						</div>
					</div>
					<div class="layui-inline">
						<label class="layui-form-label">结束日期:</label>
						<div class="layui-input-inline">
							<input id="endDate" name="endDate" type="text"  class="layui-input"/>
						</div>
					</div>
					<button type="button" class="layui-btn"  id="searchBtn">查询</button>
					<button type="button" class="layui-btn"  id="exportBtn">导出</button>
				</div>
			</form>
		</div>
		<table id="list2"></table>
		<div id="pager2"></div>
		<div id="exportDivId" hidden="" class="layui-fluid" style="margin: 15px;">
			<form class="layui-form" id="exportRuleFormId" >
				<div class="layui-form-item">
					<label class="layui-form-label">排序字段</label>
		            <div class="layui-input-block">
				      <input type="radio" name="sidx" value="total" title="交易金额"  checked>
				      <input type="radio" name="sidx" value="count" title="交易数量"  >
				    </div>
				</div>
				<div class="layui-form-item">
					<label class="layui-form-label">排序规则</label>
		            <div class="layui-input-block">
				      <input type="radio" name="sord" value="asc" title="升序" >
				      <input type="radio" name="sord" value="desc" title="降序"  checked>
				    </div>
				</div>
				<button id="sureDown" class="layui-btn" style="margin-left:120px">继续</button>
			</form>
		</div>
	</body>
</html>