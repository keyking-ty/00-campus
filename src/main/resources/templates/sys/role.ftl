<!DOCTYPE html>
<html lang="cn">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>角色管理表格</title>
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
		<script src="${request.contextPath!}/static/js/ztree/jquery.ztree.all.js" type="text/javascript"></script>
		<script type="text/javascript">
			var zTreeObj1;
		    var zTreeObj2;
		    var zTreeObj3;
		    var layerid;//当前弹层id;这个id可以定义多个，主要的目的是为了在回调函数关闭弹层时使用的
		    layui.use(['layer','form','layedit','laydate'], function(){
		        var layer = layui.layer,
					layedit = layui.layedit,
	                laydate = layui.laydate,
	                form = layui.form;
		        //创建一个编辑器
		        var editIndex = layedit.build('LAY_demo_editor');
		        //自定义验证规则
		        form.verify({
		            name: function(value) {
		                if(value.length == 0) {
		                    return '角色不能未空';
		                }
		            },
		            password: [/(.+){6,12}$/, '密码必须6到12位'],
		            content: function(value) {
		                layedit.sync(editIndex);
		            }
		        });
		
		        //监听提交
		        form.on('submit(addeditsubmitfilter)', function(data) {
		            //为了防止form中的id值被重置后置空,将编辑的id存放在label中
		            $("#editid").val($("#editlabelid").html() );
		            $("#editlabelid").html("");
		            $.ajax({
		                type: "POST",
		                url:"${request.contextPath!}/admin/role/addupdaterole",
		                data: $('#addeditformid').serialize(),
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
		            return false;
		        });
		
		        //监听提交
		        form.on('submit(editroleformsubmit)', function(data) {
		            //为了防止form中的id值被重置后置空,将编辑的id存放在label中
		            $("#editroleid").val($("#editrolelabelid").html() );
		            $("#editrolelabelid").html("");
		            $.ajax({
		                type: "POST",
		                url:"${request.contextPath!}/admin/user/saveRoleSet",
		                data:$('#editroleformid').serialize(),// 你的formid
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
		            return false;//防止跳转
		        });
		
		        //添加按钮点击事件
		        $("#add").click(function () {
		        	$("#editlabelid").html('');
		            $("#reset").click();//重置表单(新建时在进入表单前要重置一下表单的内容，不然表单打开后会显示上一次的表单的内容。这里调用表单中重置按钮的点击方法来重置)
		            layerid=layer.open({//开启表单弹层
		                skin: 'layui-layer-molv',
		                area: ['500px','350px'],
		                type: 1,
		                title:'新建角色',
		                content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
		            });
		        });
		        $("#delete").click(function () {
		            var selectId = getSelectOne('请选择要删除的记录');
	            	if (selectId == null){
	            		return false;
	            	}
		            layer.open({
	                    content: '删除选中数据?',
	                    btn: ['yes','no'],//定义两个按钮，是和否
	                    yes: function(index, layero){//点击是时候的回调
	                        layer.close(index); //如果设定了yes回调，需进行手工关闭
	                        //请求后台，执行删除操作
	                        $.ajax({
	                            type: "POST",
	                            url:"${request.contextPath!}/admin/role/deleterole",
	                            data:{id: selectId},
	                            async: false,
								complete: function(jqXHR, textStatus){
									if (jqXHR.status == 401){
										top.location.href = ${request.contextPath!} + '/tologin';
									}
								},
	                            success: function(data) {
	                                if(data.state=='fail'){
	                                    layer.alert(data.mesg);
	                                }else{
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
		            var selectId = getSelectOne('请选择要编辑的记录');
	            	if (selectId == null){
	            		return false;
	            	}
		            $.ajax({
	                    type: "POST",
	                    url:"${request.contextPath!}/admin/role/selectRoleById",
	                    data:{id: selectId},
	                    async: false,
						complete: function(jqXHR, textStatus){
	                    	if (jqXHR.status == 401){
								top.location.href = ${request.contextPath!} + '/tologin';
							}
						},
	                    success: function(data) {
	                        if(data.state=='fail'){
	                            layer.alert(data.mesg);
	                            return false;
	                        }
	                        //向表单填充数据
                            $("#editlabelid").html(selectId);//临时存放id，当提交时再去除赋值给input
                            $("#name").val(data.trole.name);
                            $("#bz").val(data.trole.bz);
                            $("#operator").val(data.trole.operator);
                            form.render();
                            //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                            layerid=layer.open({
                                skin: 'layui-layer-molv',
                                area: ['500px','350px'],
                                type: 1,
                                title:'编辑角色',
                                content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                            });
	                    }
	                });
		        });
		        $(function () {
		            $("#add").hide();
		            $("#edit").hide();
		            $("#delete").hide();
		            $('#setMenu').hide();
		            $('#setSchool').hide();
		            $('#setOperate').hide();
		            checkOperate('${request.contextPath!}',6010,true);
		            pageInit();//页面加载完成之后执行
		        });
		        function pageInit() {
		            //创建jqGrid组件
		            jQuery("#list2").jqGrid({
                        mtype: 'POST',//向后台请求数据的ajax的类型。可选post,get
                        url: '${request.contextPath!}/admin/role/list',
                        datatype: "json",//请求数据返回的类型。可选json,xml,txt
                        colModel: [
                        	{label : "ID",name: 'id',sorttype: 'integer',sortable: true, width: 75,align: 'center',
		                		searchoptions: {
		                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
		                		}
		                	},
							{label : "名称",name: 'name',sortable: true, width: 150,align: 'center',
		                		searchoptions: {
		                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
		                		}
		                	},
							{label : "备注",name: 'bz',sortable: true,width: 400,align: 'center',
		                		searchoptions: {
		                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
		                		}
		                	}
                        ],
                        page: 1,
                        rowNum: 30,//一页显示多少条
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
		            $('#savesetpermis').on('click', function () {
			            var selectId = getSelectOne('请选择要设置的数据');
		            	if (selectId == null){
		            		return false;
		            	}
			            var nodes = zTreeObj1.getCheckedNodes(true);
			            var menuArrIds = [];
			            for (var i=0 ; i < nodes.length ; i++){
			                menuArrIds.push(nodes[i].id);
			            }
			            var menuIds = menuArrIds.join(",");
			            $.ajax({
			                type: "POST",
			                url:"${request.contextPath!}/admin/role/saveMenuSet",
			                data:{menuIds:menuIds,roleId: selectId},
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
			                            }
			                        });
			                    }
			                }
			            });
			        });
			        $('#closesetpermis').on('click', function () {
			            layer.close(layerid);
			        });
			        $('#savesetSchools').on('click', function () {
			            var selectId = getSelectOne('请选择要设置的数据');
		            	if (selectId == null){
		            		return false;
		            	}
			            var nodes = zTreeObj2.getCheckedNodes(true);
			            var schoolArrIds = [];
			            for (var i = 0; i < nodes.length ; i++){
			                schoolArrIds.push(nodes[i].id);
			            }
			            var schoolIds = schoolArrIds.join(",");
			            $.ajax({
			                type: "POST",
			                url:"${request.contextPath!}/admin/role/saveSchoolSet",
			                data:{schoolIds:schoolIds,roleId: selectId},
			                async: false,
							complete: function(jqXHR, textStatus){
								if (jqXHR.status == 401){
									top.location.href = ${request.contextPath!} + '/tologin';
								}
							},
			                success: function(data) {
			                    if (data.state=='fail'){
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
			                            }
			                        });
			                    }
			                }
			            });
			        });
			        $('#closesetSchools').on('click', function () {
			            layer.close(layerid);
			        });
			        $('#savesetOperates').on('click', function () {
			            var selectId = getSelectOne('请选择要设置的数据');
		            	if (selectId == null){
		            		return false;
		            	}
			            var nodes = zTreeObj3.getCheckedNodes(true);
			            var OperateArrIds = [];
			            for (var i = 0; i < nodes.length ; i++){
			                OperateArrIds.push(nodes[i].id);
			            }
			            var operateIds = OperateArrIds.join(",");
			            $.ajax({
			                type: "POST",
			                url:"${request.contextPath!}/admin/role/saveOperateSet",
			                data:{operateIds:operateIds,roleId: selectId},
			                async: false,
							complete: function(jqXHR, textStatus){
								if (jqXHR.status == 401){
									top.location.href = ${request.contextPath!} + '/tologin';
								}
							},
			                success: function(data) {
			                    if (data.state=='fail'){
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
			                                //parent.location.reload();
			                            }
			                        });
			                    }
			                }
			            });
			        });
			        $('#closesetOperates').on('click', function () {
			            layer.close(layerid);
			        });
			        $('#setMenu').on('click', function () {
			            var selectId = getSelectOne('请选择要设置的数据');
		            	if (selectId == null){
		            		return false;
		            	}
			            setRoleMenu(selectId);
			        });
			        $('#setSchool').on('click', function () {
			        	var selectId = getSelectOne('请选择要设置的数据');
		            	if (selectId == null){
		            		return false;
		            	}
		            	setRoleSchool(selectId);
			        });
			        $('#setOperate').on('click', function () {
			        	var selectId = getSelectOne('请选择要设置的数据');
		            	if (selectId == null){
		            		return false;
		            	}
			            setRoleOperate(selectId);
			        });
		        }
		    });
		    
		    function setRoleMenu(id) {
		        // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
		        var setting = {
		            check:{
		                enable: true,
		                chkStyle: "checkbox",
		                chkboxType :{ "Y" : "p", "N" : "s" },
		                nocheckInherit: true,
		                chkDisabledInherit: true
		            }
		        };
		        // zTree 的数据属性，深入使用请参考 API 文档（zTreeNode 节点数据详解）
		        var zNodes = [];
		        $.ajax({
		            type: "POST",
		            url:'${request.contextPath!}/admin/role/loadCheckMenuInfo?parentId=1&roleId='+id,
		            async: false,
		            dataType: 'json',
		            timeout: 1000,
		            cache: false,
					complete: function(jqXHR, textStatus){
						if (jqXHR.status == 401){
							top.location.href = ${request.contextPath!} + '/tologin';
						}
					},
		            success: function(data) {
		                zNodes=data;
		                zTreeObj1 = $.fn.zTree.init($("#treeDemo"), setting, zNodes);
		                layerid=layer.open({//开启表单弹层
		                    skin: 'layui-layer-molv',
		                    type: 1,
		                    title:'设置权限',
		                    content: $('#setpermisdiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
		                });
		            }
		        });
		    }
		    
		    function setRoleSchool(id) {
		    	// zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
		        var setting = {
		            check:{
		                enable: true,
		                chkStyle: "checkbox",
		                chkboxType :{ "Y" : "p", "N" : "s" },
		                nocheckInherit: true,
		                chkDisabledInherit: true
		            }
		        };
		        // zTree 的数据属性，深入使用请参考 API 文档（zTreeNode 节点数据详解）
		        var zNodes = [];
		        $.ajax({
		            type: "POST",
		            url:'${request.contextPath!}/admin/role/loadMenuSchools?parentId=1&roleId='+id,
		            async: false,
		            dataType: 'json',
		            timeout: 1000,
		            cache: false,
					complete: function(jqXHR, textStatus){
						if (jqXHR.status == 401){
							top.location.href = ${request.contextPath!} + '/tologin';
						}
					},
		            success: function(data) {
		                zNodes = data;
		                zTreeObj2 = $.fn.zTree.init($("#treeDemo1"), setting, zNodes);
		                layerid = layer.open({//开启表单弹层
		                    skin: 'layui-layer-molv',
		                    type: 1,
		                    title:'学校权限',
		                    content: $('#setSchoolsdiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
		                });
		            }
		        });
		    }
		    
		    function setRoleOperate(id) {
		    	// zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
		        var setting = {
		            check:{
		                enable: true,
		                chkStyle: "checkbox",
		                chkboxType :{ "Y" : "p", "N" : "s" },
		                nocheckInherit: true,
		                chkDisabledInherit: true
		            }
		        };
		        // zTree 的数据属性，深入使用请参考 API 文档（zTreeNode 节点数据详解）
		        var zNodes = [];
		        $.ajax({
		            type: "POST",
		            url:'${request.contextPath!}/admin/role/loadMenuOperates?parentId=1&roleId='+id,
		            async: false,
		            dataType: 'json',
		            timeout: 1000,
		            cache: false,
					complete: function(jqXHR, textStatus){
						if (jqXHR.status == 401){
							top.location.href = ${request.contextPath!} + '/tologin';
						}
					},
		            success: function(data) {
		                zNodes = data;
		                zTreeObj3 = $.fn.zTree.init($("#treeDemo2"), setting, zNodes);
		                layerid = layer.open({//开启表单弹层
		                    skin: 'layui-layer-molv',
		                    type: 1,
		                    title:'操作权限',
		                    content: $('#setOperatesdiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
		                });
		            }
		        });
		    }
		</script>
	</head>
	<body>
		<div class="layui-btn-group">
		    <button class="layui-btn" id="add">增加</button>
		    <button class="layui-btn" id="edit">编辑</button>
		    <button class="layui-btn" id="delete">删除</button>
		    <button class="layui-btn" id="setMenu">菜单权限</button>
		    <button class="layui-btn" id="setSchool">学校权限</button>
		    <button class="layui-btn" id="setOperate">操作权限</button>
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
		            <label class="layui-form-label">运营业商</label>
		            <div class="layui-input-inline">
		            	<select id="operator" name="operator">
		            		<option value="无">无</option>
		            		<option value="中国移动">中国移动</option>
		            		<option value="中国电信">中国电信</option>
		            		<option value="中国联通">中国联通</option>
		            		<option value="全部">全部</option>
			    		</select>
		            </div>
		        </div>
		        <div class="layui-form-item">
		            <label class="layui-form-label">角色名称</label>
		            <div class="layui-input-inline">
		                <input type="text" id="name" name="name" lay-verify="name" autocomplete="off" placeholder="请输入角色名称" class="layui-input">
		            </div>
		        </div>
		        <div class="layui-form-item">
		            <label class="layui-form-label">备注</label>
		            <div class="layui-input-inline">
		                <input type="text" id="bz" name="bz" autocomplete="off" placeholder="请输入备注信息" class="layui-input">
		            </div>
		        </div>
		        <div class="layui-form-item" style="margin-top:40px">
		            <div class="layui-input-block">
		                <button class="layui-btn" lay-submit="" lay-filter="addeditsubmitfilter">立即提交</button>
		                <button id="reset" type="reset" class="layui-btn layui-btn-primary">重置</button>
		            </div>
		        </div>
		    </form>
		</div>
		<#--↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑add↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑-->
		<#--菜单权限设置弹窗-->
		<div  id="setpermisdiv" hidden="" class="layui-fluid" >
		    <ul id="treeDemo" class="ztree"></ul>
		    <button class="layui-btn" id="savesetpermis">保存权限设置</button>
		    <button class="layui-btn" id="closesetpermis">关闭</button>
		</div>
		<#--学校设置弹窗-->
		<div  id="setSchoolsdiv" hidden="" class="layui-fluid" >
		    <ul id="treeDemo1" class="ztree"></ul>
		    <button class="layui-btn" id="savesetSchools">保存</button>
		    <button class="layui-btn" id="closesetSchools">关闭</button>
		</div>
		<#--操作设置弹窗-->
		<div  id="setOperatesdiv" hidden="" class="layui-fluid" >
		    <ul id="treeDemo2" class="ztree"></ul>
		    <button class="layui-btn" id="savesetOperates">保存</button>
		    <button class="layui-btn" id="closesetOperates">关闭</button>
		</div>
	</body>
</html>