<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>课程管理</title>
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
                $ = layui.$,
                upload  = layui.upload,
                form = layui.form;
                //创建一个编辑器
                var editIndex = layedit.build('LAY_demo_editor');
                //自定义验证规则
                form.verify({
                    curriculumName: function(value) {
                        if(value.length == 0) {
                            return '课程名称不能为空';
                        }
                    },
                    curriculumContent: function(value) {
                        if(value.length == 0) {
                            return '课程简介不能为空';
                        }
                    },
                    curriculumAid: function(value) {
                        if(value.length == 0) {
                            return '专辑编号不能为空';
                        }
                    },
                    content: function(value) {
                        layedit.sync(editIndex);
                    }
                });
                //上传文件
                upload.render({
				    elem: '#fileUpload', //绑定元素
				    url: '${request.contextPath!}/file/upload?module=curriculum', //上传接口
				    accept: 'video',//视频文件类型检验
				    exts: 'mp4',
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
                //监听提交
                form.on('submit(addeditsubmitfilter)', function(data) {
                    //为了防止form中的id值被重置后置空,将编辑的id存放在label中
                    $("#editid").val($("#editlabelid").html() );
                    $("#editlabelid").html("");
                    var _data = $('#addeditformid').serialize();
                    //alert(_data);
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/curriculum/addOrUpdate",
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
            });
        });
        
		function setIconValue(fileUrl){
			$("#fileUrl").val(fileUrl);
			if (fileUrl == ''){
				$("#lookBtn").hide();
			}else{
				$("#lookBtn").show();
				var htmlStr = '<source src="${request.contextPath!}/file/downLoad?fileName='+ fileUrl + '" type="video/mp4">';
				$("#videoPlayer").html(htmlStr);
			}
		}
		
		function fillCommentData(data) {
			var htmlStr = '';
            data.ccs.forEach(function(cc){
            	var userInfo = cc.createTime + '&nbsp;&nbsp;|&nbsp;&nbsp;';
            	if (cc.target){
            		userInfo += cc.owner.name + '回复'  +  cc.target.name;
            	}else{
            		userInfo += cc.owner.name;
            	}
            	htmlStr += '<div style="margin-top:10px"><span class="layui-badge">' + userInfo + ' : </span></div>';
            	htmlStr += '<div class="layui-card-body">';
            	htmlStr += cc.content;
            	htmlStr += '</div>';
            	if (data.open){
            		htmlStr += '<button class="layui-btn" onclick="deleteComment(' + cc.id + ')">删除</button>';
            	}
        	});
        	$("#allComments").html(htmlStr);
		}
		
		function deleteComment(id) {
			var jid = jQuery("#list2").jqGrid('getGridParam', 'selrow');//jqgrid逻辑id，不是业务表单的主键字段id,这里要注意
            var ret = jQuery("#list2").jqGrid('getRowData',jid);//通过jqgrid的逻辑id获取该行数据，通过数据对象ret来获取表单主键字段ret.id
			$.ajax({
                type: "POST",
                url:"${request.contextPath!}/admin/curriculum/deleteComment",
                data:{id: id,cId: ret.id},
                dataType: 'json',
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
                    fillCommentData(data);
                }
            });
		}
		
        function pageInit() {
        	$("#lookBtn").click(function () {
	        	layer.open({//开启表单弹层
                    skin: 'layui-layer-molv',
                    area:'24%',
                    type: 1,
                    title:'查看内容',
                    content: $('#showUploadDiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
	        });
        	$("#add").hide();
            $("#edit").hide();
            $("#delete").hide();
            checkOperate('${request.contextPath!}',6301,false);
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/curriculum/list',
                datatype: "json",//请求数据返回的类型。可选json,xml,txt
                colNames: ['ID', '课程名称','课程类型', '课程介绍', '课程时长','播放次数','订阅次数','收费(单位:元)','累计收入','点赞次数','评论数量','状态'],//jqGrid的列显示名字
                colModel: [  
                    {label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                    {label: '课程名称', name: 'name', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                	{label: '课程类型', name: 'payType', width: 100, sortable: false,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=免费,章节,买断',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '课程介绍', name: 'description', width: 100, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                	{label: '播放时长', name: 'playTime',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '播放次数', name: 'playCount',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '订阅次数', name: 'subscribeCount',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '收费(单位:元)', name: 'sellPrice',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '累计收入(单位:元)', name: 'income',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '点赞次数', name: 'praise',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '累计评论数', name: 'commentNum',width: 60, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '状态', name: 'stu', width: 100, sortable: false,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=已发布,未发布',
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
            	setIconValue('');
	            layerid = layer.open({//开启表单弹层
	                skin: 'layui-layer-molv',
	                area: ['500px','600px'],
	                type: 1,
	                title:'新增课程',
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
                        //do something
                        layer.close(index); //如果设定了yes回调，需进行手工关闭
                        //请求后台，执行删除操作
                        $.ajax({
                            type: "POST",
                            url:"${request.contextPath!}/admin/curriculum/delete",
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
                    url:"${request.contextPath!}/admin/curriculum/selectById",
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
                    	$("#name").val(data.curriculum.name);
                        $("#aId").val(data.curriculum.albumId);
                        $("#playTime").val(data.curriculum.playTime);
                        $("#payType").val(data.curriculum.payType);
                        $("#stu").val(data.curriculum.stu);
                        $("#sellPrice").val(data.curriculum.sellPrice);
                        $("#description").val(data.curriculum.description);
                        if (data.curriculum.fileUrl){
                        	setIconValue(data.curriculum.fileUrl);
                        }else{
                        	setIconValue('');
                        }
                        layui.form.render();
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid=layer.open({
                            skin: 'layui-layer-molv',
                            area: ['500px','600px'],
                            type: 1,
                            title:'编辑课程',
                            content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });
                    }
                });
            });
            $("#lookComment").click(function () {
            	var selectId = getSelectOne('请选择要查看的记录');
            	if (selectId == null){
            		return false;
            	}
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/curriculum/comment",
                    data:{id: selectId},
                    dataType: 'json',
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
                        $("#cName").html(data.cName);
                        fillCommentData(data);
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid = layer.open({
                            skin: 'layui-layer-molv',
                            area: ['500px','600px'],
                            type: 1,
                            title:'评论详情',
                            content: $('#showCommentDiv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
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
    <button class="layui-btn" id="lookComment">查看评论</button>
</div>
<table id="list2"></table>
<div id="pager2"></div>
<div id="showCommentDiv" hidden="" class="layui-fluid" style="margin: 15px;">
    <label>课程名称:</label>
    <label id="cName"></label>
    <div id="allComments" class="layui-card">
	</div>
</div>
<div id="showUploadDiv" hidden="" class="layui-fluid" style="margin: 15px;">
	<video id="videoPlayer" width="320" height="240" controls>
		
	</video>
</div>
<#--↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓add↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓-->
<#--带有 class="layui-fluid" 的容器中，那么宽度将不会固定，而是 100% 适应-->
<div id="addeditformdivid" hidden="" class="layui-fluid" style="margin: 15px;">
    <form class="layui-form" action="" id="addeditformid">
        <label hidden="true" id="editlabelid"></label>
        <input id="editid" name="id" value="" hidden="true" />
        <div class="layui-form-item">
            <label class="layui-form-label">课程名称</label>
            <div class="layui-input-inline">
                <input id="name" name="name" required lay-verify="curriculumName"  autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">专辑编号</label>
            <div class="layui-input-inline">
                <input id="aId" name="albumId" required lay-verify="curriculumAid" onkeyup="IWS_CheckDecimal(this)" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">课程时长</label>
            <div class="layui-input-inline">
                <input id="playTime" name="playTime" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">课程类型</label>
            <div class="layui-input-inline">
		      <select id="payType" name="payType">
		        <option value="免费">免费</option>
		        <option value="章节">章节</option>
		        <option value="买断">买断</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">发布状态</label>
            <div class="layui-input-inline">
		      <select id="stu" name="stu">
		        <option value="已发布">已发布</option>
		        <option value="未发布">未发布</option>
		      </select>
		    </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">单集售价</label>
             <div class="layui-input-inline">
                <input id="sellPrice" name="sellPrice" onkeyup="IWS_CheckDecimal(this)" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">课程描述</label>
            <div class="layui-input-inline">
                <input id="description" name="description" required lay-verify="curriculumContent" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">课程文件</label>
            <button type="button" class="layui-btn" id="fileUpload" >
			  <i class="layui-icon">&#xe67c;</i>上传
			</button>
			<button type="button" class="layui-btn" id="lookBtn" hidden="true">查看</button>
			<input id="fileUrl" name="fileUrl" hidden="true" />
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