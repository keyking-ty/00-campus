<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>用户管理表格</title>
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
    
    <script type="text/javascript">
        var layerid;//当前弹层id;这个id可以定义多个，主要的目的是为了在回调函数关闭弹层时使用的
        $(function () {
            //页面加载完成之后执行
            pageInit();
            layui.use(['layer','form','layedit','laydate','upload'], function(){
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
                    userName: function(value) {
                        if(value.length < 5) {
                            return '用户名至少得5个字符';
                        }
                    },
                    trueName: function(value) {
                        if(value.length == 0) {
                            return '昵称不能未空';
                        }
                    },
                    password: [/(.+){6,12}$/, '密码必须6到12位'],
                    content: function(value) {
                        layedit.sync(editIndex);
                    }
                });
	            //上传文件
                upload.render({
				    elem: '#iconUpload',//绑定元素
				    url: '${request.contextPath!}/file/upload?module=sys_user',//上传接口
				    done: function(res){
				      //上传完毕回调
				      if (res.succ){
				      	 setIconValue(1,null,res.fileName);
				      	 layer.alert('上传成功');
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
			    //监听选择变化
	            form.on('select(iconType)', function(data) {
	            	if (data.elem.value == 0){
	            		$("#iconType0").show();
	            		$("#iconType1").hide();
	            	}else{
	            		$("#iconType1").show();
	            		$("#iconType0").hide();
	            	}
	            });
                //监听提交
                form.on('submit(addeditsubmitfilter)', function(data) {
                    //为了防止form中的id值被重置后置空,将编辑的id存放在label中
                    $("#editid").val($("#editlabelid").html() );
                    $("#editlabelid").html("");
                    var _data = $('#addeditformid').serialize();
                    $.ajax({
                        type: 'POST',
                        url: '${request.contextPath!}/admin/user/addupdateuser',
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
                    return false;
                });
            });
        });

		function setIconValue(type,iconName,iconUrl){
			if (iconName){
				$("#iconName").val(iconName);
			}
			if (iconUrl){
				var html = "<image src='${request.contextPath!}/file/downLoad?fileName=" + iconUrl + "' />";
      			$("#showUploadImagediv").html(html);
      			$("#iconLook").show();
      			$("#iconUrl").val(iconUrl);
			}else{
				$("#iconLook").hide();
			}
			$("#iconType").val(type);
	      	if (type == 0){
	      		$("#iconType0").show();
	      		$("#iconType1").hide();
	      	}else{
	      		$("#iconType1").show();
	      		$("#iconType0").hide();
	      	}
		}
		
        function pageInit() {
        	$("#iconLook").click(function () {
	        	layer.open({//开启表单弹层
                    skin: 'layui-layer-molv',
                    area:'20%',
                    type: 1,
                    title:'查看图片',
                    content: $('#showUploadImagediv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
	        });
        	$("#add").hide();
            $("#edit").hide();
            $("#delete").hide();
            $("#editrole").hide();
            checkOperate('${request.contextPath!}',6020,false);
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: 'post',//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/user/list',
                datatype: 'json',//请求数据返回的类型。可选json,xml,txt
                colModel: [//这里会根据index去解析jsonReader中root对象的属性，填充cell
                    {label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 100, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                    {label: '昵称', name: 'trueName', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                    {label: '账号', name: 'userName', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                    {label: '密码', name: 'password', width: 200, sortable: true,align:'center',search: false},
                    {label: '备注', name: 'bz', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                    {label: '角色', name: 'roles', width: 180, sortable: false , align:'center', search: false}
                ],
                page: 1,
                rowNum: 30,//一页显示多少条
                rowList: [10, 20, 30],//可供用户选择一页显示多少条
                pager: '#pager2',//表格页脚的占位符(一般是div)的id
                height: "100%",
                autowidth: true,
                loadError: function (xhr,status,error) {
                    if (xhr.status == 401){
                        top.location.href = ${request.contextPath!} + '/tologin';
                    }
                }
                //multiselect: true
                //rownumbers: false, // 显示行号
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
                setIconValue(0,'userface1.jpg',null);
                layui.form.render();
                layerid = layer.open({//开启表单弹层
                    skin: 'layui-layer-molv',
                    area: ['500px','460px'],
                    type: 1,
                    title:'新建用户',
                    content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
            });
            $("#delete").click(function () {
            	var selects = getSelectOne('请选择要删除的记录');
            	if (selects == null){
            		return false;
            	}
                layer.open({
	                content: '删除选中数据?',
	                btn: ['yes','no'],//定义两个按钮，是和否
	                yes: function(index, layero){//点击是时候的回调
	                    //do something
	                    layer.close(index); //如果设定了yes回调，需进行手工关闭
	                    //请求后台，执行删除操作
	                    $.ajax({
	                        type: "POST",
	                        url:"${request.contextPath!}/admin/user/deleteuser",
	                        data:{ids: selects.join(',')},
	                        async: false,
                            complete: function(jqXHR, textStatus){
                                if (jqXHR.status == 401){
                                    top.location.href = ${request.contextPath!} + '/tologin';
                                }
                            },
	                        success: function(data) {
	                            if (data.state == 'fail'){
	                                layer.alert(data.mesg);
	                                return false;
	                            }
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
	                    });
	                }
	            });
            });
            $("#edit").click(function () {
            	var selects = getSelectOne('请选择要编辑的记录');
            	if (selects == null){
            		return false;
            	}
                //请求后台，获取该记录的详细记录，并填充进表单
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/user/selectUserById",
                    data:{id: selects[0]},
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
                        $("#editlabelid").html(data.tuser.id);//临时存放id，当提交时再去除赋值给input
                        $("#userName").val(data.tuser.userName);
                        $("#password").val(data.tuser.password);
                        $("#trueName").val(data.tuser.trueName);
                        $("#bz").val(data.tuser.bz);
                        setIconValue(data.tuser.iconType,data.tuser.iconName,data.tuser.iconUrl);
                        layui.form.render();
                        layerid=layer.open({
                            skin: 'layui-layer-molv',
                            area: ['500px','460px'],
                            type: 1,
                            title:'编辑用户',
                            content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });
                    }
                });
            });
            //编辑用户角色
            $("#editrole").click(function () {
            	var selects = getSelectOne('请选择要编辑的记录');
            	if (selects == null){
            		return false;
            	}
                //获得当前用户已经拥有的角色集合和未拥有的角色集合，并组装表单的复选按钮
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/user/selectUserById",
                    data:{id: selects[0]},
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
                        $("#editrolelabelid").html(selects[0]);//临时存放id，当提交时再去除赋值给input
                        var roleList = [];
                        roleList=data.roleList;//该记录已经拥有的记录集合
                        var notinrolelist = [];
                        notinrolelist=data.notinrolelist;//该记录尚未拥有的记录集合
                        var strs="";
                        $.each(roleList, function (n, value) {//n从0开始自增+1；value为每次循环的单个对象
                            strs+='<input type="checkbox" name="role" title="'+value.name+'" value="'+value.id+'"  checked="checked">';
                        });
                        $.each(notinrolelist, function (n, value) {
                            strs+='<input type="checkbox" name="role" title="'+value.name+'"  value="'+value.id+'" >';
                        });
                        $("#checkboxlistid").empty();//每次填充前都要清空所有按钮，重新填充
                        $("#checkboxlistid").append(strs);
                        layui.form.render(); //更新全部
                        layerid = layer.open({
                            skin: 'layui-layer-molv',
                            area:'30%',
                            type: 1,
                            title:'编辑用户角色',
                            content: $('#editroleformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });
                    }
                });
            });
        }
    </script>
</head>
<body>
<div class="layui-btn-group">
    <button class="layui-btn" id="add">增加</button>
    <button class="layui-btn" id="edit">编辑</button>
    <button class="layui-btn" id="editrole">设置角色</button>
    <button class="layui-btn" id="delete">删除</button>
</div>
<table id="list2"></table>
<div id="pager2"></div>
<div id="showUploadImagediv" hidden="" class="layui-fluid" style="margin: 15px;"></div>
<#--↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓add↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-->
<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="addeditformid">
        <label hidden="true" id="editlabelid"></label>
        <input id="editid" name="id" value="" hidden/>
        <div class="layui-form-item">
            <label class="layui-form-label">登录账号</label>
            <div class="layui-input-inline">
                <input type="text" id="userName" name="userName" lay-verify="userName" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">登录密码</label>
            <div class="layui-input-inline">
            	<input type="text" id="password" name="password" lay-verify="password" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">头像类型</label>
            <div class="layui-input-inline">
		      <select id="iconType" name="iconType" lay-filter="iconType">
		        <option value="0">系统</option>
		        <option value="1">自定义</option>
		      </select>
		    </div>
        </div>
        <div id="iconType0" class="layui-form-item">
            <label class="layui-form-label">头像图标</label>
            <div class="layui-input-inline">
		      <select id="iconName" name="iconName">
		        <option value="userface1.jpg">头像1</option>
		        <option value="userface2.jpg">头像2</option>
		        <option value="userface3.jpg">头像3</option>
		        <option value="userface4.jpg">头像4</option>
		        <option value="userface5.jpg">头像5</option>
		      </select>
		    </div>
        </div>
        <div id="iconType1" class="layui-form-item">
            <label class="layui-form-label">头像图标</label>
            <button type="button" class="layui-btn" id="iconUpload" name="iconUpload" >
			  <i class="layui-icon">&#xe67c;</i>上传图片
			</button>
			<button type="button" class="layui-btn" id="iconLook" hidden="true">查看</button>
			<input id="iconUrl" name="iconUrl" hidden="true" />
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">昵称</label>
            <div class="layui-input-inline">
                <input type="text" id="trueName" name="trueName" lay-verify="trueName" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">备注信息</label>
            <div class="layui-input-inline">
                <input type="text" id="bz" name="bz" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item" style="margin-top:30px">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="addeditsubmitfilter">立即提交</button>
                <button id="reset" type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>

    </form>
</div>
<#--↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑add↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑-->

<#--↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓为用户设置角色↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-->
<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="editroleformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="editroleformid">
        <label hidden="true" id="editrolelabelid"></label>
        <input id="editroleid" name="id" value="" hidden />
        <div class="layui-form-item">
            <label class="layui-form-label">角色复选框</label>
            <div class="layui-input-block" id="checkboxlistid">
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="editroleformsubmit">立即提交</button>
                <button id="editroleformreset" type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>

    </form>
</div>
<#--↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑为用户设置角色↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑-->
</body>
</html>