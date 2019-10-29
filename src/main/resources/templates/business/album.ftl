<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>专辑管理</title>
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
                //自定义验证规则
                form.verify({
                    albumName: function(value) {
                        if(value.length == 0) {
                            return '专辑名称不能为空';
                        }
                    },
                    albumAuther: function(value) {
                        if(value.length == 0) {
                            return '专辑作者不能为空';
                        }
                    },
                    albumContent: function(value) {
                        if(value.length == 0) {
                            return '内容简介不能为空';
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
                    var _data = $('#addeditformid').serialize();
                    //alert(_data);
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/album/addOrUpdate",
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
				    url: '${request.contextPath!}/file/upload?module=album', //上传接口
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
                    },
			    });
            });
        });
        
		function clearUploadInfo(){
			$("#iconLook").hide();
			$("#iconUrl").val('');
		}
		
		function setIconValue(fileUrl){
			$("#iconUrl").val(fileUrl);
	      	var html = "<image src='${request.contextPath!}/file/downLoad?fileName=" + fileUrl + "' />";
	      	$("#showUploadImagediv").html(html);
	      	$("#iconLook").show();
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
            checkOperate('${request.contextPath!}',6300,false);
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/album/list',
                datatype: "json",//请求数据返回的类型。可选json,xml,txt
                colModel: [  
                	{label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                	{label: '专辑名称', name: 'name', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                	{label: '专辑类型', name: 'type', width: 100, sortable: false,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=普通,精品,VIP',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '总集数', name: 'total',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '免费集数', name: 'free',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '累计播放数', name: 'playCount',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '订阅次数', name: 'subscribeCount',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '累计收入(单位:元)', name: 'income',width: 100, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '作者', name: 'auther',width: 100, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                    {label: '收费类型', name: 'payType', width: 100, sortable: false,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=免费,章节,买断',
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
            	$("#editlabelid").html('');
            	$("#reset").click();
            	clearUploadInfo();
	            layerid = layer.open({//开启表单弹层
	                skin: 'layui-layer-molv',
	                area: ['500px','600px'],
	                type: 1,
	                title:'新增专辑',
	                content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
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
                            url:"${request.contextPath!}/admin/album/delete",
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
                //请求后台，获取该记录的详细记录，并填充进表单
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/album/selectById",
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
                    	if (data.album.iconUrl){
                    		setIconValue(data.album.iconUrl);
                    	}else{
                    		clearUploadInfo();
                    	}
                    	$("#name").val(data.album.name);
                        $("#title").val(data.album.title);
                        $("#content").val(data.album.content);
                        $("#changeDes").val(data.album.changeDes);
                        $("#type").val(data.album.type);
                        $("#auther").val(data.album.auther);
                        $("#autherDes").val(data.album.autherDes);
                        $("#payType").val(data.album.payType);
                        layui.form.render();
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid=layer.open({
                            skin: 'layui-layer-molv',
                            area: ['500px','600px'],
                            type: 1,
                            title:'编辑专辑',
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
            <label class="layui-form-label">专辑名称</label>
            <div class="layui-input-inline">
                <input id="name" name="name" required lay-verify="albumName"  autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">专辑图标</label>
            <button type="button" class="layui-btn" id="iconUpload" >
			  <i class="layui-icon">&#xe67c;</i>上传图片
			</button>
			<button type="button" class="layui-btn" id="iconLook" hidden="true">查看</button>
			<input id="iconUrl" name="iconUrl" hidden="true" />
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">专辑标题</label>
            <div class="layui-input-inline">
                <input id="title" name="title" class="layui-input">
            </div>
        </div>
                <div class="layui-form-item">
            <label class="layui-form-label">专辑类型</label>
            <div class="layui-input-inline">
		      <select id="type" name="type">
		        <option value="精品">精品</option>
		        <option value="VIP">VIP</option>
		        <option value="普通">普通</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">收费类型</label>
            <div class="layui-input-inline">
		      <select id="payType" name="payType">
		        <option value="免费">免费</option>
		        <option value="章节">章节</option>
		        <option value="买断">买断</option>
		      </select>
		    </div>
        </div>
		<div class="layui-form-item">
            <label class="layui-form-label">更新简介</label>
            <div class="layui-input-inline">
                <input id="changeDes" name="changeDes" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">内容简介</label>
            <div class="layui-input-inline">
                <input id="content" name="content" required lay-verify="albumContent" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">作者</label>
            <div class="layui-input-inline">
            	<input id="auther" name="auther" required lay-verify="albumAuther" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">作者介绍</label>
            <div class="layui-input-inline">
            	<input id="autherDes" name="autherDes" class="layui-input">
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