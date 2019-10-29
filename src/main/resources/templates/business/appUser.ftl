<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>用户管理</title>
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
        var layerid,schools,cards,meals;
        $(document).ready(function () {
            //页面加载完成之后执行
            pageInit();
            layui.use(['layer','form','layedit'], function(){
                var layer = layui.layer,
                layedit = layui.layedit,
                $ = layui.$,
                form = layui.form;
                //创建一个编辑器
                var editIndex = layedit.build('LAY_demo_editor');
                //自定义验证规则
                form.verify({
                    loginName: function(value) {
                        if(value.length == 0) {
                            return '登录账号不能为空';
                        }
                    },
                    loginPwd: function(value) {
                    	if(value.length == 0) {
                            return '登录密码不能为空';
                        }
                    },
                    account: function(value) {
                    	if(value.length == 0) {
                            return '学号不能为空';
                        }
                    },
                    content: function(value) {
                        layedit.sync(editIndex);
                    }
                });
                //监听提交
                form.on('submit(addeditsubmitfilter)', function(data) {
                    //为了防止form中的id值被重置后置空,将编辑的id存放在label中
                    $("#editid").val($("#editlabelid").html() );
                    $("#editlabelid").html("");
                    var balance = $("#balance").val();
                    if (balance == ''){
                    	$("#balance").val('0');
                    }
                    var _data = $('#addeditformid').serialize();
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/aum/addOrUpdate",
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
                                        $("#list2").jqGrid().trigger("reloadGrid");//重新加载数据
                                        $("#reset").click();//重置表单
                                    }
                                });
                            }
                        }
                    });
                    return false;//防止表单提交后跳转
                });
                form.on('submit(authenUserSubmitfilter)', function(data) {
                    //为了防止form中的id值被重置后置空,将编辑的id存放在label中
                    var id = $("#list2").jqGrid('getGridParam', 'selrow');//jqgrid逻辑id，不是业务表单的主键字段id,这里要注意
                    var ret = $("#list2").jqGrid('getRowData', id);//通过jqgrid的逻辑id获取该行数据，通过数据对象ret来获取表单主键字段ret.id
                    var _authenSta = $("#authenSta").val();
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/aum/authenUser",
                        data: {id:ret.id,authenSta:_authenSta},
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
                                        $("#list2").jqGrid().trigger("reloadGrid");//重新加载数据
                                        $("#reset").click();//重置表单
                                    }
                                });
                            }
                        }
                    });
                    return false;//防止表单提交后跳转
                });
                
                //监听学校选择变化
                form.on('select(schoolId)', function(data) {
	            	fillMeal(data.elem.value);
	            });
	            //监听套餐选择变化
                form.on('select(groupId)', function(data) {
	            	fillCardInfo(data.elem.value);
	            });
            });
        });
        
        function fillSchool(schoolId){
        	var html = '';
        	if (schools){
        		schools.forEach(function(school){
	        		html += '<option value="' + school.id +'">' + school.schoolName + '</option>';
	        	});
        	}
        	$("#schoolId").html(html);
        	fillMeal(schoolId);
        }
        
        function fillMeal(schoolId){
        	var html = '<option value="0">无</option>';
        	var mealId = 0;
        	if (meals){
        		meals.forEach(function(meal){
					if (meal.schoolId == schoolId){
						html += '<option value="' + meal.id +'">' + meal.name + '</option>';
						if (mealId == 0){
							mealId = meal.id;
						}
					}
        		});
        	}
        	$("#groupId").html(html);
        	fillCardInfo(mealId);
        }
        
        function checkMeal(card,mealId){
        	if (card.meals){
				var strs = card.meals.split(",");
				for (var i = 0 ; i < strs.length ; i++){
					var mid = parseInt(strs[i]);
					if (mid == mealId){
						return true;
					}
				}
        	}
        	return false;
        }	
        
        function fillCardInfo(mealId){
        	var html = '<option value=" ">无</option>';
        	if (cards){
        		cards.forEach(function(card){
					if (checkMeal(card,mealId)){
						html += '<option value="' + card.cardNumber +'">' + card.cardNumber + '</option>';
					}
	        	});
        	}
        	$("#mealCard").html(html);
        }
        
		function fillData(user){
			$("#realName").val(user.realName);
			setAddressValue(user.province,user.city,user.area);
			$("#loginName").val(user.loginName);
			$("#loginPwd").val(user.loginPwd);
			$("#userType").val(user.userType);
			$("#nickName").val(user.nickName);
			$("#mobile").val(user.mobile);
			$("#idCard").val(user.idCard);
			$("#balance").val(user.balance);
			$("#fade").val(user.fade);
			$("#major").val(user.major);
			$("#account").val(user.account);
			fillSchool(user.schoolId);
			$("#schoolId").val(user.schoolId);
			$("#groupId").val(user.groupId);
			$("#mealCard").val(user.mealCard);
			$("#sex").val(user.sex);
		}
		
        function pageInit() {
        	$("#add").hide();
            $("#edit").hide();
            $("#delete").hide();
            $("#authenUser").hide();
            checkOperate('${request.contextPath!}',6200,false);
            //创建jqGrid组件
            $("#list2").jqGrid({
                mtype: 'post',//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/aum/list',
                datatype: 'json',//请求数据返回的类型。可选json,xml,txt  
                colModel: [  
                   	{label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 100, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '名称', name: 'realName', width: 80, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '性别', name: 'sex', width: 80, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq']
                		}
                	},
                	{label: '类型', name: 'userType', width: 80, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=本科生,研究生,教师,其他',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '学校', name: 'schoolName', width: 180, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/schools',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '学号', name: 'account', width: 80, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '手机', name: 'mobile', width: 80, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '账号', name: 'loginName', width: 80, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '身份证', name: 'loginName', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '套餐', name: 'mealName', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '套餐号码', name: 'mealCard', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '余额', name: 'balance', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '认证', name: 'authenSta', width: 100, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=已认证,未认证,审核中,认证失败',
                			sopt: ['eq','ne']
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
            //添加按钮点击事件
            $("#add").click(function () {
            	$.ajax({
                    type: "POST",
                    url: "${request.contextPath!}/admin/aum/initSelectData",
                    dataType: "json",
                    async: false,
                    complete: function(jqXHR, textStatus){
                        if (jqXHR.status == 401){
                            top.location.href = ${request.contextPath!} + '/tologin';
                        }
                    },
                    success: function(data) {
                        if (data.state == 'fail'){
                            layer.alert(data.mesg);
                        }else{
                        	$("#editlabelid").html('');
                        	schools = data.schools;
                        	cards   = data.cards;
                        	meals   = data.meals;
                        	fillSchool(schools[0].id);
			                setAddressValue('安徽','合肥','蜀山区');
			                layui.form.render('select');
			                $("#reset").click();
			                layerid = layer.open({//开启表单弹层
			                    skin: 'layui-layer-molv',
			                    area: ['500px','600px'],
			                    type: 1,
			                    title:'新增用户',
			                    content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
			                });
                        }
                    }
                });
            });
            $("#delete").click(function () {
            	var selectId = getSelectOne('请选择要删除的记录');
            	if (selectId == null){
            		return false;
            	}
                layer.open({
                    content: '请确定是否要删除选择的记录?',
                    btn: ['yes', 'no'],//定义两个按钮，是和否
                    yes: function(index, layero){//点击是时候的回调
                        //do something
                        layer.close(index); //如果设定了yes回调，需进行手工关闭
                        //请求后台，执行删除操作
                        $.ajax({
                            type: "POST",
                            url:"${request.contextPath!}/admin/aum/delete",
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
                                } else {
                                    //打开成功消息提示
                                    layer.open({
                                        skin: 'layui-layer-molv',
                                        type:1,
                                        area:"10%",
                                        content:data.mesg,
                                        shadeClose:true,
                                        end: function(){
                                            layer.close(layerid);//消息提示结束后回调，关闭上一级新建表单所在弹层
                                            $("#list2").jqGrid().trigger("reloadGrid");//jqgrid数据表重新主动加载数据
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
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/aum/selectById",
                    data:{id: selectId},
                    async: false,
                    dataType: "json",
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
                        $("#editlabelid").html(selectId);//临时存放id，当提交时再去除赋值给input
                        schools = data.schools;
                        meals   = data.meals;
                        cards   = data.cards;
                        fillData(data.user);
                        layui.form.render();
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid=layer.open({
                            skin: 'layui-layer-molv',
                            area: ['500px','600px'],
                            type: 1,
                            title:'编辑用户',
                            content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });
                    }
                });
            });
            $("#authenUser").click(function () {
            	var selectId = getSelectOne('请选择要认证的记录');
            	if (selectId == null){
            		return false;
            	}
            	 $.ajax({
                     type: "GET",
                     url:"${request.contextPath!}/admin/aum/authenData?id=" + selectId,
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
                        var temp = null;
                        var notUpload = '<label class="layui-form-label">未上传</label>';
                        if (data.idcardPosimg){
                        	temp = '<image src="/file/downLoad/' + data.idcardPosimg + '" />';
                        }else{
                          	temp = notUpload;
                        }
                        $("#idcardPosimg").html(temp);
                        if (data.idcardSideimg){
                        	temp = '<image src="/file/downLoad/' + data.idcardSideimg + '" />';
                        }else{
                          	temp = notUpload;
                        }
		            	$("#idcardSideimg").html(temp);
		            	if (data.stucardPosimg){
                        	temp = '<image src="/file/downLoad/' + data.stucardPosimg + '" />';
                        }else{
                          	temp = notUpload;
                        }
		            	$("#stucardPosimg").html(temp);
		            	if (data.stucardSideimg){
                        	temp = '<image src="/file/downLoad/' + data.stucardSideimg + '" />';
                        }else{
                          	temp = notUpload;
                        }
		            	$("#stucardSideimg").html(temp);
		            	$("#authenSta").val(data.authenSta);
		            	layui.form.render();
		            	layerid = layer.open({
		                    skin: 'layui-layer-molv',
		                    area:'40%',
		                    type: 1,
		                    title:'用户认证',
		                    content: $('#authenUserFormDiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
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
    <button class="layui-btn" id="authenUser">认证</button>
</div>
<table id="list2"></table>
<div id="pager2"></div>
<div id="authenUserFormDiv" hidden="" class="layui-fluid" style="margin: 15px;">
	<form class="layui-form" action="" id="authenUserFormId">
        <div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">认证结果</label>
            <div class="layui-input-inline">
		      <select id="authenSta">
		        <option value="未认证">未认证</option>
		        <option value="审核中">审核中</option>
		        <option value="已认证">已认证</option>
		        <option value="认证失败">认证失败</option>
		      </select>
		    </div>
        </div>
		<div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">身份证正面</label>
            <div id="idcardPosimg" class="layui-input-inline">
		    </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">身份证反面</label>
            <div id="idcardSideimg" class="layui-input-inline">
		    </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">学生证正面</label>
            <div id="stucardPosimg" class="layui-input-inline">
		    </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">学生证反面</label>
            <div id="stucardSideimg" class="layui-input-inline">
		    </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="authenUserSubmitfilter">立即提交</button>
            </div>
        </div>
	</form>
</div>
<#--↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓add↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-->
<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="addeditformid">
        <label hidden="true" id="editlabelid"></label>
        <input id="editid" name="id" value="" hidden="true" />
        <div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">姓名</label>
            <div class="layui-input-inline">
		      <input id="realName" name="realName" autocomplete="off" class="layui-input">
		    </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px">
            <label class="layui-form-label">性别</label>
            <div class="layui-input-inline">
            	<select name="sex" id="sex">
            		<option value="男">男</option>
            		<option value="女">女</option>
                </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">地址</label>
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
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">登录账号</label>
            <div class="layui-input-inline">
                <input id="loginName" name="loginName" required lay-verify="loginName"  autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">登录密码</label>
            <div class="layui-input-inline">
                <input type="password" id="loginPwd" name="loginPwd" required lay-verify="loginPwd"  autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">类型</label>
            <div class="layui-input-inline">
		      <select id="userType" name="userType">
		        <option value="学生用户">学生用户</option>
		        <option value="教师">教师</option>
		        <option value="研究生">研究生</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">昵称</label>
            <div class="layui-input-inline">
                <input id="nickName" name="nickName" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">手机号码</label>
            <div class="layui-input-inline">
                <input id="mobile" name="mobile" onkeyup="IWS_CheckDecimal(this)" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">身份证</label>
            <div class="layui-input-inline">
                <input id="idCard" name="idCard" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">余额</label>
            <div class="layui-input-inline">
                <input id="balance" name="balance" onkeyup="IWS_CheckDecimal(this)" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">学校</label>
            <div class="layui-input-inline">
            	<select id="schoolId" name="schoolId" lay-filter="schoolId">
			    </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">开通套餐</label>
            <div class="layui-input-inline">
            	<select id="groupId" name="groupId" lay-filter="groupId">
			    </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">套餐号码</label>
            <div class="layui-input-inline">
                <select id="mealCard" name="mealCard">
			    </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">院系</label>
            <div class="layui-input-inline">
                <input id="fade" name="fade" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">专业</label>
            <div class="layui-input-inline">
                <input id="major" name="major" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">学号</label>
            <div class="layui-input-inline">
                <input id="account" name="account" required lay-verify="account" autocomplete="off" class="layui-input">
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