<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>学校管理表格</title>
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
            //页面加载完成之后执行
            pageInit();
            layui.use(['layer','form','layedit','laydate'], function(){
                var layer = layui.layer,
                layedit = layui.layedit,
                laydate = layui.laydate,
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
                    terminalId: function(value) {
                    	var open = $("#cityHot").is(':checked');
                        if (open && value.length == 0) {
                            return '终端编号不能为空';
                        }
                        var num = parseInt(value);
                        if (num < 6000 || num > 6999){
                        	 return '终端编号取值[6000,6999]';
                        }
                    },
                    signkey: function(value) {
                    	var open = $("#cityHot").is(':checked');
                        if (open && value.length == 0) {
                            return '请输入签名key';
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
                    if ($("#cityHot").is(':checked')){
                    	_data = _data.replace(/cityHot=false/, "cityHot=true");
                    }
                    if ($("#portal").is(':checked')){
                    	_data = _data.replace(/portal=false/, "portal=true");
                    }
                    if ($("#rjrz").is(':checked')){
                    	_data = _data.replace(/rjrz=false/, "rjrz=true");
                    }
                    //alert(_data);
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/school/addOrUpdate",
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
                //监听选择变化
	            form.on('checkbox(cityHot)', function(data) {
		        	if (data.elem.checked){
						$("#cityHotSub").show();
					} else {
						$("#cityHotSub").hide();
					}
	            });
            });
        });
        
        function fillNetType(cityHot,portal,rjrz){
        	var html = '';
        	if (cityHot){
        		html += '<input type="checkbox" title="城市热点"  id="cityHot" name="cityHot" lay-filter="cityHot" checked="checked">';
        	}else{
        		html += '<input type="checkbox" title="城市热点"  id="cityHot" name="cityHot" lay-filter="cityHot">';
        	}
        	if (portal){
        		html += '<input type="checkbox" title="安徽电信"  id="portal" name="portal" checked="checked">';
        	}else{
        		html += '<input type="checkbox" title="安徽电信"  id="portal" name="portal">';
        	}
        	if (rjrz){
        		html += '<input type="checkbox" title="锐捷认证"  id="rjrz" name="rjrz" checked="checked">';
        	}else{
        		html += '<input type="checkbox" title="锐捷认证"  id="rjrz" name="rjrz">';
        	}
        	$("#netTypeDiv").html(html);
        }
        
        function pageInit() {
        	$("#add").hide();
            $("#edit").hide();
            $("#delete").hide();
            checkOperate('${request.contextPath!}',6050,false);
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/school/list',
                datatype: "json",//请求数据返回的类型。可选json,xml,txt
                colModel: [  
                    //这里会根据index去解析jsonReader中root对象的属性，填充cell
                    {label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 100, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                    {label: '名称', name: 'schoolName', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                    {label: '省', name: 'province', width: 100, sortable: true,align:'center',search:false},
                    {label: '市', name: 'city', width: 100, sortable: true,align:'center',search:false},
                    {label: '区/县', name: 'area', width: 100, sortable: true,align:'center',search:false},
                    {label: 'WIFI名称', name: 'wifiName', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                    {label: '城市热点环境', name: 'netType', width: 120, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=公网,内网',
                			sopt: ['eq','ne']
                		}
                	},
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
            //添加按钮点击事件
            $("#add").click(function () {
            	$("#editlabelid").html('');
                $("#reset").click();//重置表单(新建时在进入表单前要重置一下表单的内容，不然表单打开后会显示上一次的表单的内容。这里调用表单中重置按钮的点击方法来重置)
                setAddressValue("安徽","合肥","蜀山区");
                fillNetType(false,false,false);
                layerid=layer.open({//开启表单弹层
                    skin: 'layui-layer-molv',
                    area: ['500px','600px'],
                    type: 1,
                    title:'新增学校',
                    content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
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
                        layer.close(index); //如果设定了yes回调，需进行手工关闭
                        //请求后台，执行删除操作
                        $.ajax({
                            type: "POST",
                            url:"${request.contextPath!}/admin/school/deleteSchool",
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
                                        type:1,
                                        area:"10%",
                                        content:data.mesg,
                                        shadeClose:true,
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
            $("#edit").click(function () {
            	var selectId = getSelectOne('请选择要修改的记录');
            	if (selectId == null){
            		return false;
            	}
                //请求后台，获取该记录的详细记录，并填充进表单
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/school/selectById",
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
                        $("#editlabelid").html(selectId);//临时存放id，当提交时再去除赋值给input
                        $("#schoolName").val(data.school.schoolName);
                        setAddressValue(data.school.province,data.school.city,data.school.area);
                        if (data.school.area == '其他'){
                        	$("#otherArea").val(data.school.otherArea);
                        }
                        fillNetType(data.school.cityHot,data.school.portal,data.school.rjrz);
                        if (data.school.cityHot){
                        	$("#cityHotSub").show();
                        	$("#connetIp").val(data.school.connetIp);
                        	$("#loginUrl").val(data.school.loginUrl);
                        	$("#exitUrl").val(data.school.exitUrl);
                        	$("#onlineUrl").val(data.school.onlineUrl);
                        	$("#terminalId").val(data.school.terminalId);
                        	$("#netType").val(data.school.netType);
                        	$("#apiVersion").val(data.school.apiVersion);
                        	$("#signkey").val(data.school.signkey);
                        }else{
                        	$("#cityHotSub").hide();
                        }
                        $("#wifiName").val(data.school.wifiName);
                        layui.form.render(); //更新全部
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid = layer.open({
                            skin: 'layui-layer-molv',
                            area: ['500px','600px'],
                            type: 1,
                            title:'编辑学校',
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
    <button class="layui-btn" id="add">增加</button>
    <button class="layui-btn" id="edit">编辑</button>
    <button class="layui-btn" id="delete">删除</button>
</div>

<table id="list2"></table>
<div id="pager2"></div>
<#--↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓add↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-->
<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="addeditformid">
        <label hidden="true" id="editlabelid"></label>
        <input id="editid" name="id" value="" hidden/>
        <div class="layui-form-item">
            <label class="layui-form-label">学校名称</label>
            <div class="layui-input-block">
                <input type="text" id="schoolName" name="schoolName" lay-verify="schoolName" autocomplete="off" placeholder="请输入学校名称" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">学校地址</label>
            <div class="layui-input-inline">
                <select name="province" lay-filter="province" id="province">
                </select>
                <select name="city" lay-filter="city" id="city">
                </select>
                <select name="area" lay-filter="area" id="area">
                </select>
                <input hidden="true" type="text" id="otherArea" name="otherArea" autocomplete="off" placeholder="请输入其他区名称"  class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
        	<label class="layui-form-label">上网方式：</label>
        	<div id="netTypeDiv" class="layui-input-inline">
        		
        	</div>
        </div>
        <div id="cityHotSub" hidden="true">
        	<div class="layui-form-item">
	            <label class="layui-form-label">网络类型</label>
	            <div class="layui-input-inline">
	            	<select name="netType" id="netType">
	            		<option value="公网">公网</option>
	            		<option value="内网">内网</option>
                	</select>
	            </div>
	        </div>
	        <div class="layui-form-item">
	            <label class="layui-form-label">API版本</label>
	            <div class="layui-input-inline">
	            	<select name="apiVersion" id="apiVersion">
	            		<option value="v1">v1</option>
	            		<option value="v2">v2</option>
                	</select>
	            </div>
	        </div>
	       	<div class="layui-form-item">
	            <label class="layui-form-label">签名key</label>
	            <div class="layui-input-block">
	            	<input type="text" id="signkey" name="signkey" lay-verify="signkey" autocomplete="off" placeholder="请输入签名key" class="layui-input">
	            </div>
	        </div>
        	<div class="layui-form-item">
	            <label class="layui-form-label">连接IP</label>
	            <div class="layui-input-block">
	                <input type="text" id="connetIp" name="connetIp" lay-verify="connetIp" autocomplete="off" placeholder="请输入连参数IP" class="layui-input">
	            </div>
	        </div>
	        <div class="layui-form-item">
	            <label class="layui-form-label">登录URL</label>
	            <div class="layui-input-block">
	                <input type="text" id="loginUrl" name="loginUrl" lay-verify="loginUrl" autocomplete="off" class="layui-input">
	            </div>
	        </div>
	        <div class="layui-form-item">
	            <label class="layui-form-label">退出URL</label>
	            <div class="layui-input-block">
	                <input type="text" id="exitUrl" name="exitUrl" lay-verify="exitUrl" autocomplete="off" class="layui-input">
	            </div>
	        </div>
	        <div class="layui-form-item">
	            <label class="layui-form-label">在线URL</label>
	            <div class="layui-input-block">
	                <input type="text" id="onlineUrl" name="onlineUrl" lay-verify="onlineUrl" autocomplete="off" class="layui-input">
	            </div>
	        </div>
	        <div class="layui-form-item">
	            <label class="layui-form-label">终端编号</label>
	            <div class="layui-input-inline">
	                <input type="text" id="terminalId" name="terminalId" lay-verify="terminalId" autocomplete="off" class="layui-input">
	            </div>
	            <div class="layui-form-mid layui-word-aux">范围6000-6999</div>
	        </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">wifi名称</label>
            <div class="layui-input-inline">
                <input type="text" id="wifiName" name="wifiName" autocomplete="off" class="layui-input">
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