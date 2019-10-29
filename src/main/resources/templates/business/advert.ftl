<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>广告管理</title>
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
            layui.use(['layer','form','layedit','laydate','upload'], function(){
                var layer = layui.layer,
                layedit = layui.layedit,
                laydate = layui.laydate,
                upload  = layui.upload,
                $ = layui.$,
                form = layui.form;
                //创建一个编辑器
                var editIndex = layedit.build('LAY_demo_editor');
                form.verify({
                    timeLast: function(value) {
                        if(value.length == 0) {
                            return '有效期不能未空';
                        }
                    },
                    content: function(value) {
                        layedit.sync(editIndex);
                    }
                });
                //自定义验证规则
                laydate.render({
				  elem: '#startDate'//指定元素
				});
                //监听提交
                form.on('submit(addeditsubmitfilter)', function(data) {
                    //为了防止form中的id值被重置后置空,将编辑的id存放在label中
                    var lastTime = $('#timeLast').val();
                    if (lastTime != 0){
                    	var startDate = $('#startDate').val();
                    	if (startDate == ''){
                    		layer.alert('请设置开始日期');
                    		return false;
                    	}
                    }
                    $("#editid").val($("#editlabelid").html() );
                    $("#editlabelid").html("");
                    var _data = $('#addeditformid').serialize();
                    //alert(_data);
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/advert/addOrUpdate",
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
	            //上传文件
                upload.render({
				    elem: '#iconUpload', //绑定元素
				    url: '${request.contextPath!}/file/upload?module=advert', //上传接口
				    done: function(res){
				      //上传完毕回调
				      if (res.succ){
				      	 setIconValue(res.fileName);
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
            });
        });
        
		function clearUploadInfo(){
			$("#iconLook").hide();
			$("#advImg").val('');
		}
		
		function setIconValue(fileUrl){
			$("#advImg").val(fileUrl);
	      	var html = "<image src='${request.contextPath!}/file/downLoad?fileName=" + fileUrl + "' />";
	      	$("#showUploadImagediv").html(html);
	      	$("#iconLook").show();
		}
		
		function fillSchool(l1,l2){
       		var html = '';
       		if (l1){
       			$.each(l1,function(n, value){
	    			html += '<input type="checkbox" name="schoolId" title="' + value.schoolName + '" value="_' + value.id + '_" checked="checked" />';
	    		});
       		}
	    	if (l2){
       			$.each(l2,function(n, value){
	    			html += '<input type="checkbox" name="schoolId" title="' + value.schoolName + '" value="_' + value.id + '_" />';
	    		});
       		}
       		html += '<div class="layui-form-mid layui-word-aux">不选择默认是全部学校</div>';
	    	$("#schoolDivs").html(html);
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
            checkOperate('${request.contextPath!}',6504,false);
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/advert/list',
                datatype: "json",//请求数据返回的类型。可选json,xml,txt
                colNames: ['ID','类型', '标题', '图片','链接','创建时间','推荐排序','开始时间','持续时间','作者','状态'],//jqGrid的列显示名字
                colModel: [  
                    //这里会根据index去解析jsonReader中root对象的属性，填充cell
                    {label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '类型', name: 'advType', width: 100, sortable: false,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=首页,音频,视频,商城,招聘,社交',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '标题', name: 'advTitle', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                	{label: '图片', name: 'advImg', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                	{label: '链接', name: 'advLnk', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                	{label: '创建时间', name: 'createDate', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '推荐排序', name: 'advSort', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '开始时间', name: 'startDate', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '持续时间(天)', name: 'lastTime', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '操作者', name: 'auther', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                	{label: '状态', name: 'advSta', width: 100, sortable: false,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=开启,关闭',
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
                    url: "${request.contextPath!}/admin/advert/shcools",
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
                        	fillSchool(null,data.schools);
                        	clearUploadInfo();
			                $("#reset").click();
			                layui.form.render();
			                layerid = layer.open({//开启表单弹层
			                    skin: 'layui-layer-molv',
			                    area: ['1000px','800px'],
			                    type: 1,
			                    title:'新增广告',
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
                            url:"${request.contextPath!}/admin/advert/delete",
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
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/advert/selectById",
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
                        $("#editlabelid").html(selectId);//临时存放id，当提交时再去除赋值给input
                    	if (data.advert.advImg){
                    		setIconValue(data.advert.advImg);
                    	}else{
                    		clearUploadInfo();
                    	}
                    	$("#advTitle").val(data.advert.advTitle);
                    	$("#advType").val(data.advert.advType);
                    	$("#advSort").val(data.advert.advSort);
                    	$("#startDate").val(data.advert.startDate);
                    	$("#timeLast").val(data.advert.timeLast);
                    	$("#advSta").val(data.advert.advSta);
                    	$("#advLnk").val(data.advert.advLnk);
                    	$("#advInfo").val(data.advert.advInfo);
                    	fillSchool(data.hs,data.ns);
                        layui.form.render();
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid=layer.open({
                            skin: 'layui-layer-molv',
                            area: ['500px','600px'],
                            type: 1,
                            title:'编辑广告',
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
<div id="showUploadImagediv" hidden="" class="layui-fluid" style="margin: 15px;"></div>
<#--↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓add↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-->
<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="addeditformid">
        <label hidden="true" id="editlabelid"></label>
        <input id="editid" name="id" value="" hidden="true" />
        <div class="layui-form-item">
            <label class="layui-form-label">发布学校</label>
            <div id="schoolDivs" class="layui-input-inline">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">广告标题</label>
            <div class="layui-input-inline">
                <input id="advTitle" name="advTitle" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">广告图片</label>
            <button type="button" class="layui-btn" id="iconUpload" >
			  <i class="layui-icon">&#xe67c;</i>上传图片
			</button>
			<button type="button" class="layui-btn" id="iconLook" hidden="true">查看</button>
			<input id="advImg" name="advImg" hidden="true" />
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">广告类型</label>
            <div class="layui-input-inline">
		      <select id="advType" name="advType">
		        <option value="首页">首页</option>
		        <option value="音频">音频</option>
		        <option value="视频">视频</option>
		        <option value="商城">商城</option>
		        <option value="招聘">招聘</option>
		        <option value="社交">社交</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">广告状态</label>
            <div class="layui-input-inline">
		      <select id="advSta" name="advSta">
		      	<option value="开启">开启</option>
		        <option value="关闭">关闭</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">广告链接</label>
            <div class="layui-input-inline">
                <input id="advLnk" name="advLnk" class="layui-input">
            </div>
        </div>
		<div class="layui-form-item">
            <label class="layui-form-label">广告排序</label>
            <div class="layui-input-inline">
                <input id="advSort" name="advSort" onkeyup="IWS_CheckDecimal(this)" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">开始时间</label>
            <div class="layui-input-inline">
		      <input class="layui-input" id="startDate" name="startDate" >
		    </div>
        </div>
		<div class="layui-form-item">
            <label class="layui-form-label">有效期</label>
            <div class="layui-input-inline">
                <input id="timeLast" name="timeLast" lay-verify="timeLast" onkeyup="IWS_CheckDecimal(this)" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">天(<=0永久)</div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">广告备注</label>
            <div class="layui-input-inline">
                <input id="advInfo" name="advInfo" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px">
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