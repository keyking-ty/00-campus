<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	    <title>订单管理</title>
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
		<script src="${basePath!}/static/js/select.js" type="text/javascript"></script>
		<script type="text/javascript">
	        var layerid;//当前弹层id;这个id可以定义多个，主要的目的是为了在回调函数关闭弹层时使用的
	        $(document).ready(function () {
	            //页面加载完成之后执行
	            pageInit();
	            layui.use(['layer','form','layedit','laydate'], function(){});
	        });
	        function pageInit() {
	            $("#backMoney").hide();
	            checkOperate('${request.contextPath!}',6104,false);
	            //创建jqGrid组件
	            $("#list2").jqGrid({
	                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
	                url: '${request.contextPath!}/admin/order/list',
	                datatype: "json",//请求数据返回的类型。可选json,xml,txt
	                colModel: [  
	                    {label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 50, sortable: true, align:'center',
	                		searchoptions: {
	                			sopt: ['eq','ne','lt','le','gt','ge']
	                		}
	                	},
	                	{label: '用户', name: 'name', width: 100, sortable: true , align:'center',
	                		searchoptions: {
	                			sopt: ['eq','cn']
	                		}
	                	},
	                	{label: '类型', name: 'orderType', width: 100, sortable: true , align:'center',stype: 'select',
	                		searchoptions: {
	                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=套餐充值,套餐升级,线下支付',
	                			sopt: ['eq','ne']
	                		}
	                	},
	                	{label: '商品', name: 'content', width: 100, sortable: true , align:'center',
	                		searchoptions: {
	                			sopt: ['eq','cn']
	                		}
	                	},
	                	{label: '状态', name: 'orderSta', width: 100, sortable: true , align:'center',stype: 'select',
	                		searchoptions: {
	                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=等待支付,交易成功,已退款',
	                			sopt: ['eq','ne']
	                		}
	                	},
	                	{label: '订单号', name: 'orderNum', width: 100, sortable: true , align:'center',
	                		searchoptions: {
	                			sopt: ['eq','cn']
	                		}
	                	},
	                	{label: '价格(单位:元)', name: 'oriPrice', width: 100, sortable: true , align:'center',
	                		searchoptions: {
	                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
	                		}
	                	},
	                	{label: '实付(单位:元)', name: 'payPrice', width: 100, sortable: true , align:'center',
	                		searchoptions: {
	                			sopt: ['eq','ne','lt','le','gt','ge']
	                		}
	                	},
	                	{label: '支付方式', name: 'payType', width: 100, sortable: true , align:'center',stype: 'select',
	                		searchoptions: {
	                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=微信支付,支付宝支付',
	                			sopt: ['eq','ne']
	                		}
	                	},
	                	{label: '支付订单号', name: 'payId', width: 100, sortable: true , align:'center',
	                		searchoptions: {
	                			sopt: ['eq','cn']
	                		}
	                	},
	                	{label: '支付时间', name: 'payDate',sorttype:'date', width: 100, sortable: true , align:'center',
	                		searchoptions: {
	                			sopt: ['lt','le','gt','ge'],
	                			dataInit : function (elem) {
									layui.laydate.render({
									  elem: elem,//指定元素
									  type: 'datetime',
									  format: 'yyyy-MM-dd hh:mm:ss',
									  theme: 'grid'
									});
								}
	                		}
	                	}
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
					}
	            });
	            /*创建jqGrid的操作按钮容器*/
	            $('#list2').navGrid('#pager2',{                
	                search: true,
	                add: false,
	                edit: false,
	                del: false,
	                refresh: true
	            },{},/*edit options*/{},/*add options*/{},/*delete options*/
	            {
	            	multipleSearch: true
	        	});
	            $("#backMoney").click(function () {
	            	layui.layer.alert("暂未开启");
	            });
	        }
		</script>
	</head>
	<body>
		<div class="layui-btn-group">
		    <button class="layui-btn" id="backMoney">退款</button>
		</div>
		<table id="list2"></table>
		<div id="pager2"></div>
	</body>
</html>