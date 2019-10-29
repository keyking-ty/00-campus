<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	    <title>二手市场</title>
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
	        $(document).ready(function () {
	            pageInit();
	            layui.use(['layer','form','layedit','laydate'], function(){
	                var layer = layui.layer,
	                layedit = layui.layedit,
	                laydate = layui.laydate,
	                $ = layui.$,
	                form = layui.form;
	            });
	        });
	        
			function fillPics(pics){
				if (pics && pics != ''){
					var names = JSON.parse(pics);
					var htmlStr = '';
					names.forEach(function(name){
						htmlStr += "<image src='${request.contextPath!}/file/downLoad?fileName=" + name + "' />";
                	});
		      		$("#showImageDiv").html(htmlStr);
				}else{
					$("#showImageDiv").html('用户未上传图片');
				}
			}
			
	        function pageInit() {
	            $("#delete").hide();
	            checkOperate('${request.contextPath!}',6502,false);
	            //创建jqGrid组件
	            jQuery("#list2").jqGrid({
	                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
	                url: '${request.contextPath!}/admin/market/list',
	                datatype: "json",//请求数据返回的类型。可选json,xml,txt
	                colModel: [
	                	{label: 'ID',sorttype:'int', name: 'id',sorttype: 'integer',key: true, width: 100, align:'center',
	                		searchoptions: {
	                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
	                		}
	                	},
	                    {label: '类型', name: 'type', width: 180, sortable: false,align:'center',stype: 'select',
	                		searchoptions: {
	                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=求购,出售',
	                			sopt: ['eq','ne']
	                		}
	                	},
	                	{label: '学校', name: 'schoolName', width: 180, sortable: true,align:'center',stype: 'select',
	                		searchoptions: {
	                			dataUrl: '${request.contextPath!}/admin/search/schools',
	                			sopt: ['eq','ne']
	                		}
	                	},
	                	{label: '标题', name: 'title', width: 180, sortable: true,align:'center',
	                		searchoptions: {
	                			sopt: ['eq','cn']
	                		}
	                	},
	                	{label: '售价', name: 'price', width: 180, sortable: true,align:'center',
	                		searchoptions: {
	                			sopt: ['eq','ne','lt','le','gt','ge']
	                		}
	                	},
	                    {label: '发布者', name: 'userName', width: 180, sortable: true,align:'center',
	                		searchoptions: {
	                			sopt: ['eq','cn']
	                		}
	                	},
	                	{label: '联系电话', name: 'phone', width: 180, sortable: true,align:'center',
	                		searchoptions: {
	                			sopt: ['eq','cn']
	                		}
	                	},
	                	{label: '时间', sorttype:'date',name: 'time', width: 180, sortable: true,align:'center',
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
	                search: true,//show search button on the toolbar
	                add: false,
	                edit: false,
	                del: false,
	                refresh: true
	            },{},/*edit options*/{},/*add options*/{},/*delete options*/
	            {
	            	multipleSearch: true
	        	});
	            $("#delete").click(function () {
	                var selectId = getSelectOne('请选择要删除的记录');
	            	if (selectId == null){
	            		return false;
	            	}
	                layer.open({
	                    content: '请确定是否要删除选中的记录?',
	                    btn: ['yes', 'no'],//定义两个按钮，是和否
	                    yes: function(index, layero){//点击是时候的回调
	                        //do something
	                        layer.close(index); //如果设定了yes回调，需进行手工关闭
	                        //请求后台，执行删除操作
	                        $.ajax({
	                            type: "POST",
	                            url:"${request.contextPath!}/admin/market/delete",
	                            data:{id: selectId},
	                            async: false,
								complete: function(jqXHR, textStatus){
									if (jqXHR.status == 401){
										top.location.href = ${request.contextPath!} + '/tologin';
									}
								},
	                            success: function(data) {
	                                if (data.state=='fail'){
	                                    layer.alert(data.mesg);
	                                } else{
	                                    //打开成功消息提示
	                                    layer.open({
	                                        skin: 'layui-layer-molv',
	                                        type: 1,
	                                        area: "10%",
	                                        content: data.mesg,
	                                        shadeClose: true,
	                                        end: function(){
	                                            layer.close(layerid);//消息提示结束后回调，关闭上一级新建表单所在弹层
	                                            jQuery("#list2").jqGrid().trigger("reloadGrid");//jqgrid数据表重新主动加载数据
	                                        }
	                                    });
	                                }
	                            }
	                        });
	                    }
	                });
	            });
	            $("#lookPicsDetail").click(function () {
	            	layer.open({//开启表单弹层
	                    skin: 'layui-layer-molv',
	                    area:'40%',
	                    type: 1,
	                    title:'查看图片',
	                    content: $('#showImageDiv')
	                });
	            });
	            $("#look").click(function () {
	                var selectId = getSelectOne('请选择要查看的记录');
	            	if (selectId == null){
	            		return false;
	            	}
	                $.ajax({
	                    type: "POST",
	                    url:"${request.contextPath!}/admin/market/selectById",
	                    data:{id: selectId},
	                    async: false,
						complete: function(jqXHR, textStatus){
							if (jqXHR.status == 401){
								top.location.href = ${request.contextPath!} + '/tologin';
							}
						},
	                    success: function(data) {
	                        if (data.state=='fail'){
	                            layer.alert(data.mesg);
	                            return false;
	                        }
	                        //向表单填充数据
	                        $("#type").html('类型: '  + data.item.type);
	                        $("#title").html('标题: '  + data.item.title);
	                        $("#userName").html('用户: '  + data.item.userName);
	                        $("#price").html('价格: '  + data.item.price);
	                        $("#content").html('内容: ' + data.item.content);
	                        $("#schoolName").html('学校: '  +  data.item.schoolName);
	                        $("#time").html('发布时间: '   +  data.item.time);
	                        fillPics(data.item.pics);
				            layui.form.render();
	                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
	                        layerid = layer.open({
	                            skin: 'layui-layer-molv',
	                            area: ['500px','500px'],
	                            type: 1,
	                            title:'详细信息',
	                            content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
	                        });
	                    }
	                });
	            });
	        };
	    </script>
	</head>
	<body>
		<div class="layui-btn-group">
			<button class="layui-btn" id="look">查看</button>
		    <button class="layui-btn" id="delete">删除</button>
		</div>
		<table id="list2"></table>
		<div id="pager2"></div>
		<div id="showImageDiv" hidden="" class="layui-fluid" style="margin: 15px;"></div>
		<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
		    <form class="layui-form" action="" id="addeditformid">
		    	<div class="layui-form-item">
		            <label id="type"></label>
		        </div>
		        <div class="layui-form-item">
		            <label id="title"></label>
		        </div>
		    	<div class="layui-form-item">
		            <label id="userName"></label>
		        </div>
		        <div class="layui-form-item">
		            <label id="schoolName"></label>
		        </div>
		        <div class="layui-form-item">
		            <label id="time"></label>
		        </div>
		        <div class="layui-form-item">
		            <label id="price"></label>
		        </div>
		        <div class="layui-form-item">
		            <label id="content"></label>
		        </div>
		    </form>
		    <div id="allPics">
		    	<button id="lookPicsDetail" class="layui-btn">查看图片</button>
		    </div>
		</div>
	</body>
</html>