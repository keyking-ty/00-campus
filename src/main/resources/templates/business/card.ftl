<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>号码管理</title>
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
        //var schools;
        $(document).ready(function () {
            //页面加载完成之后执行
            pageInit();
            layui.use(['layer','form','layedit','laydate','upload','table'], function(){
                var layer = layui.layer,
                layedit = layui.layedit,
                laydate = layui.laydate,
                upload  = layui.upload,
                $ = layui.$,
                form = layui.form;
                //创建一个编辑器
                var editIndex = layedit.build('LAY_demo_editor');
                //自定义验证规则
                form.verify({
                    schoolName: function(value) {
                        if(value.length == 0) {
                            return '学校名称不能为空';
                        }
                    },
                    connetIp: function(value) {
                    	var open = $("#cityHot").is(':checked');
                        if(open && value.length == 0) {
                            return '连接IP不能为空';
                        }
                    },
                    loginUrl: function(value) {
                    	var open = $("#cityHot").is(':checked');
                        if(open && value.length == 0) {
                            return '登录地址不能为空';
                        }
                    },
                    exitUrl: function(value) {
                    	var open = $("#cityHot").is(':checked');
                        if(open && value.length == 0) {
                            return '退出地址不能为空';
                        }
                    },
                    onlineUrl: function(value) {
                    	var open = $("#cityHot").is(':checked');
                        if(open && value.length == 0) {
                            return '在线地址不能为空';
                        }
                    },
                    //password: [/(.+){6,12}$/, '密码必须6到12位'],
                    content: function(value) {
                        layedit.sync(editIndex);
                    }
                });
                //监听提交
                form.on('submit(addeditsubmitfilter)', function(data) {
                    //为了防止form中的id值被重置后置空,将编辑的id存放在label中
                    $("#editid").val($("#editlabelid").html() );
                    $("#editlabelid").html("");
                    var _data = $('#addeditformid').serialize();
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/number/addOrUpdate",
                        data: _data,
                        async: false,
                        complete: function(jqXHR, textStatus){
                            if (jqXHR.status == 401){
                                top.location.href = ${request.contextPath!} + '/tologin';
                            }
                        },
                        success: function(data) {
                            if(data.state=='fail'){
                                layer.alert(data.mesg);
                            }
                            if(data.state=='success'){
                                layer.open({
                                    skin: 'layui-layer-molv',
                                    type:1,
                                    area:"10%",
                                    content:data.mesg,
                                    shadeClose:true,
                                    end: function(){
                                        layer.close(layerid);
                                        jQuery("#list2").jqGrid().trigger("reloadGrid");//重新加载数据
                                        $("#reset").click();//重置表单
                                    }
                                });
                            }
                        }
                    });
                    return false;//防止表单提交后跳转
                });
                
                //监听radio变化
	            form.on("radio",function(data) {
	            	//importType = data.value;
	            });
	            
	            //上传文件
                upload.render({
				    elem: '#importFile', //绑定元素
				    url: '${request.contextPath!}/admin/number/import',
				    //data: {type: importType},
				    exts: 'xls|xlsx',
				    accept: 'file',
				    before: function(res){
				    	layer.close(layerid);
				    },
				    done: function(res){
				      //上传完毕回调
				      if (res.state == 'success'){
				      	 layer.alert('操作成功,下载日志可以查看导入详情');
                         jQuery("#list2").jqGrid().trigger("reloadGrid");//重新加载数据
				      }else{
				      	 layer.alert(res.msg);
				      }
				    },
                    complete: function(jqXHR, textStatus){
                        if (jqXHR.status == 401){
                            top.location.href = ${request.contextPath!} + '/tologin';
                        }
                    }
			    });
            });
        });
		
		function lookMealDetail(id){
			$.ajax({
                type: "POST",
                url: "${request.contextPath!}/admin/number/lookMealDetail",
                data: {id:id},
                async: false,
                complete: function(jqXHR, textStatus){
                    if (jqXHR.status == 401){
                        top.location.href = ${request.contextPath!} + '/tologin';
                    }
                },
                success: function(data) {
                    if(data.state=='fail'){
                        layer.alert(data.mesg);
                    }
                    if(data.state=='success'){
                    	var html = '';
                    	data.names.forEach(function(name){
		            		html += '<label>' + name + '</label>';
	                	});
	                	$('#mealFormPopId').html(html);
                        layerid = layer.open({
			                skin: 'layui-layer-molv',
			                area:'20%',
			                type: 1,
			                title:'可选套餐',
			                content: $('#mealFormPopDiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
			            });
                    }
                }
            });
		}
		
        function pageInit() {
        	$("#add").hide();
            $("#edit").hide();
            $("#delete").hide();
            $("#importAction").hide();
            checkOperate('${request.contextPath!}',6101,false);
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/number/list',
                datatype: "json",//请求数据返回的类型。可选json,xml,txt  
                colModel: [
                	{label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 100, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '号码', name: 'cardNumber', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '类型', name: 'cardType', width: 180, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=流量王,情侣,动感地带',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '预存话费', name: 'predictValue', width: 60, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '运营商', name: 'operator', width: 180, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=中国移动,中国电信,中国联通',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '学校', name: 'schoolName', width: 180, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/schools',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '状态', name: 'statu', width: 100, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=已售,待售',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '创建时间', name: 'createTime', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['lt','le','gt','ge']
                		}
                	},
                	{label: '创建者', name: 'auther', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '套餐', name: 'lookMeal', width: 100, sortable: false,align:'center',search: false}
                ],
                gridComplete: function () {
                	var ids = jQuery("#list2").jqGrid('getDataIDs');
                    for (var i = 0 ; i < ids.length ; i++) {
                        var id = ids[i];
                        var lookBtn = '<button onclick="lookMealDetail(' + id +')" class="layui-btn layui-btn-radius layui-btn-xs" style="width:inherit;height: 85%"><i class="layui-icon">&#xe615;</i>查看</button>';
                        jQuery("#list2").jqGrid('setRowData',id,{lookMeal: lookBtn});
                    }
                },
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
            $("#importNumber").click(function () {
            	layerid = layer.open({
                    skin: 'layui-layer-molv',
                    area: '300px',
                    type: 1,
                    title:'批量导入',
                    content: $('#importMoreFormDiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
            });
            $("#downModule").click(function () {
                window.open('${request.contextPath!}/admin/number/importModule.xls');
            });
            $("#downOperateLog").click(function () {
				$.ajax({
                    type: 'POST',
                    url: '${request.contextPath!}/admin/number/logs',
                    dataType: 'json',
                    async: false,
                    complete: function(jqXHR, textStatus){
                        if (jqXHR.status == 401){
                            top.location.href = ${request.contextPath!} + '/tologin';
                        }
                    },
                    success: function(data) {
                    	$("#tableBodyId").empty();
                    	data.forEach(function(item){
                    		var html = '<tr>';
                    		html += '<td>' + item.auther  +  '</td>';
                    		html += '<td>' + item.operateTime  +  '</td>';
                    		html += '<td>' + item.totalNum  +  '</td>';
                    		html += '<td>' + item.succNum  +  '</td>';
                    		html += '<td>' + item.failNum  +  '</td>';
                    		html += '<td><button onclick="downLogFile(' + item.id + ')" class="layui-btn layui-btn-radius layui-btn-xs" style="width:inherit;height: 85%"><i class="layui-icon">&#xe61e;</i>下载</button></td>';
                    		html += '</tr>';
                    		$("#tableBodyId").append(html);
	                	});
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid=layer.open({
                            skin: 'layui-layer-molv',
                            area:'50%',
                            type: 1,
                            title:'日志列表',
                            content: $('#showAllLogsDiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });
                    }
                });
            });
        };
        function downLogFile(id){
        	window.open('${request.contextPath!}/admin/number/importLog?id=' + id);
        }
    </script>
</head>
<body>
<div class="layui-btn-group">
    <button class="layui-btn" id="add">增加</button>
    <button class="layui-btn" id="edit">编辑</button>
    <button class="layui-btn" id="delete">删除</button>
    <div id="importAction">
    	<button class="layui-btn" id="importNumber">批量操作</button>
    	<button class="layui-btn" id="downModule">下载模版</button>
    	<button class="layui-btn" id="downOperateLog">下载日志</button>
    </div>
</div>
<table id="list2"></table>
<div id="pager2"></div>
<div id="showAllLogsDiv" hidden="" class="layui-fluid" style="margin: 15px;">
	<table class="layui-table">
	  <thead>
	    <tr>
	      <th>导入人员</th>
	      <th>导入时间</th>
	      <th>导入总数</th>
	      <th>成功数量</th>
	      <th>失败数量</th>
	      <th>操作</th>
	    </tr> 
	  </thead>
	  <tbody id="tableBodyId">
	  </tbody>
	</table>
</div>
<div id="importMoreFormDiv" hidden="" class="layui-fluid" style="margin: 15px;">
	<form class="layui-form" action="" id="importMoreFormId">
		<!--div class="layui-form-item" style="margin-top:20px">
	        <label class="layui-form-label">操作类型</label>
	        <div class="layui-input-block">
	          <input type="radio" name="operateType" value="insert" title="增加"  checked>
	          <input type="radio" name="operateType" value="update" title="修改" >
	          <input type="radio" name="operateType" value="delete" title="删除" >
		    </div>
    	</div-->
    	<div class="layui-form-item">
            <label class="layui-form-label">导入文件</label>
            <button type="button" class="layui-btn" id="importFile" >
			  <i class="layui-icon">&#xe67c;</i>上传
			</button>
        </div>
	</form>
</div>
<div id="mealFormPopDiv" hidden="" class="layui-fluid" style="margin: 15px;">
	<div id="mealFormPopId" class="layui-input-block">
		
	</div>
</div>
<#--↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓add↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-->
<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="addeditformid">
        <label hidden="true" id="editlabelid"></label>
        <input id="editid" name="id" value="" hidden="true" />
        <div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">运营商</label>
            <div class="layui-input-inline">
		      <select id="operator" name="operator">
		        <option value="中国移动">中国移动</option>
		        <option value="中国电信">中国电信</option>
		        <option value="中国联通">中国联通</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">归属地</label>
            <div class="layui-input-inline">
                <select name="province" lay-filter="province" id="province">
                    <option></option>
                </select>
                <select name="city" lay-filter="city" id="city">
                    <option></option>
                </select>
                <select name="area" lay-filter="area" id="area">
                    <option></option>
                </select>
                <input hidden="true" type="text" id="otherArea" name="otherArea" autocomplete="off" placeholder="请输入其他区名称"  class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">卡号</label>
            <div class="layui-input-inline">
                <input id="cardNumber" name="cardNumber" required lay-verify="required"  autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">类型</label>
            <div class="layui-input-inline">
		      <select id="cardType" name="cardType">
		        <option value="靓号">靓号</option>
		        <option value="情侣号">情侣号</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">发布学校</label>
            <div class="layui-input-inline">
            	<select id="schoolId" name="schoolId" lay-filter="schoolId">
			    </select>
            </div>
        </div>
        <div class="layui-form-item">
		    <label class="layui-form-label">状态</label>
		    <div id="statuDiv" class="layui-input-block">
		    </div>
		 </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="addeditsubmitfilter">立即提交</button>
                <button id="reset" type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
</div>
<#--↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑add↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑-->

</body>
</html>