<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>信息管理</title>
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
	
	<!--网页编辑器引入-->
	<script type="text/javascript" charset="utf-8" src="${request.contextPath!}/static/ueditor/ueditor.config.js"></script>
	<script type="text/javascript" charset="utf-8" src="${request.contextPath!}/static/ueditor/ueditor.all.min.js"></script>
	<script type="text/javascript" charset="utf-8" src="${request.contextPath!}/static/ueditor/lang/zh-cn/zh-cn.js"></script>
	
	<script type="text/javascript">
        var layerid,editor//当前弹层id;这个id可以定义多个，主要的目的是为了在回调函数关闭弹层时使用的
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
                    infoTitle: function(value) {
                        if(value.length == 0) {
                            return '标题不能为空';
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
                    var content = editor.getContent();
                    _data += "&content=" + content;
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/info/addOrUpdate",
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

        function initKindEditor_content(contentPath,id, width, height,module) {
            editor = UE.getEditor(id,{
                initialFrameHeight: height,
                initialFrameWidth:width,
                toolbars: [[
                    'undo', //撤销
                    'redo', //重做
                    'bold', //加粗
                    'indent', //首行缩进
                    'italic', //斜体
                    'underline', //下划线
                    'strikethrough', //删除线
                    'subscript', //下标
                    'fontborder', //字符边框
                    'superscript', //上标
                    'formatmatch', //格式刷
                    'pasteplain', //纯文本粘贴模式
                    'source', //源代码
                    'horizontal', //分隔线
                    'fontfamily', //字体
                    'fontsize', //字号
                    'paragraph', //段落格式
                    'customstyle', //自定义标题
                    'forecolor', //字体颜色
                    'backcolor', //背景色
                    'insertcode', //代码语言
                    'simpleupload', //单图上传
                    'insertimage', //多图上传
                    'link', //超链接
                    'emotion', //表情
                    'map', //Baidu地图
                    'justifyleft', //居左对齐
                    'justifyright', //居右对齐
                    'justifycenter', //居中对齐
                    'justifyjustify', //两端对齐
                    'fullscreen', //全屏
                    'pagebreak', //分页
                    'imagenone', //默认
                    'imageleft', //左浮动
                    'imageright', //右浮动
                    'imagecenter', //居中
                    'wordimage', //图片转存
                    'lineheight', //行间距
                    'edittip ', //编辑提示
                    'scrawl', //涂鸦
                    'autotypeset', //自动排版
                    'selectall', //全选
                    'preview', //预览
                    'horizontal', //分隔线
                    'removeformat', //清除格式
                    'unlink', //取消链接
                    'cleardoc', //清空文档
                    'link', //超链接
                    'emotion', //表情
                    'spechars', //特殊字符
                    'insertorderedlist', //有序列表
                    'insertunorderedlist', //无序列表
                    'rowspacingtop', //段前距
                    'rowspacingbottom', //段后距
                ]
                ]
                ,zIndex:999999999999//编辑器在页面上的z-index层级的基数，默认是900
                ,autoFloatEnabled: false//是否保持toolbar的位置不动，默认true
                ,wordCount:true
                ,maximumWords:1000
                ,wordCountMsg:'{#count}/1000'
            });
            //复写UEDITOR的getActionUrl 方法,定义自己的Action
            UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
            UE.Editor.prototype.getActionUrl = function (_action) {
                if (_action == 'uploadimage' || _action == 'uploadfile') {
                    return contentPath + '/file/ueditorupload?module=' + module;
                }else if (_action == 'uploadscrawl') {
                    return contentPath + '/file/ueditoruploadscrawl?module=' + module;
                }else {
                    return this._bkGetActionUrl.call(this,_action);
                }
            };
        }
        function pageInit() {
        	$("#add").hide();
            $("#edit").hide();
            $("#delete").hide();
            checkOperate('${request.contextPath!}',6500,false);
            //initKindEditor_content('content','100%',350,'');
            initKindEditor_content('${request.contextPath!}','content','100%',350,'info');
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/info/list',
                datatype: "json",//请求数据返回的类型。可选json,xml,txt
                colModel: [
                	{label: 'ID',sorttype:'int', name: 'id',sorttype: 'integer',key: true, width: 100, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},  
                    {label: '标题', name: 'title', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','nu','nn','bw','bn','ew','en','cn','nc','in','ni']
                		}
                	},
                    {label: '类型', name: 'type', width: 180, sortable: false,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=资讯,公告',
                			sopt: ['eq','ne']
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
                	},
                    {label: '阅读次数', sorttype:'int',name: 'readCount', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge','in','ni']
                		}
                	},
                    {label: '发布者', name: 'authorName', width: 180, sortable: true,align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
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
                    url: "${request.contextPath!}/admin/info/shcools",
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
				            setTimeout(function(){
				            	editor.setContent('');
				            },100);
			                $("#reset").click();
			                layui.form.render();
			                layerid = layer.open({//开启表单弹层
			                    skin: 'layui-layer-molv',
			                    area: ['1000px','800px'],
			                    type: 1,
			                    title:'新增信息',
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
                    content: '请确定是否要删除选中的记录?',
                    btn: ['yes', 'no'],//定义两个按钮，是和否
                    yes: function(index, layero){//点击是时候的回调
                        //do something
                        layer.close(index); //如果设定了yes回调，需进行手工关闭
                        //请求后台，执行删除操作
                        $.ajax({
                            type: "POST",
                            url:"${request.contextPath!}/admin/info/deleteInfo",
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
            $("#edit").click(function () {
                var selectId = getSelectOne('请选择要编辑的记录');
            	if (selectId == null){
            		return false;
            	}
                $.ajax({
                    type: "POST",
                    url:"${request.contextPath!}/admin/info/selectById",
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
                        //向表单填充数据
                        fillSchool(data.hs,data.ns);
                        $("#title").val(data.info.title);
                        $("#type").val(data.info.type);
                        $("#readCount").val(data.info.readCount);
                        $("#sort").val(data.info.readCount);
                        $("#author").val(data.info.author);
			            setTimeout(function(){
			            	editor.setContent(data.info.content);
			            },100);
			            layui.form.render();
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid=layer.open({
                            skin: 'layui-layer-molv',
                            area: ['1000px','800px'],
                            type: 1,
                            title:'编辑信息',
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
            <label class="layui-form-label">类型</label>
            <div class="layui-input-inline">
            	<select id="type" name="type" lay-filter="infoType">
            		<option value="资讯">资讯</option>
            		<option value="公告">公告</option>
			    </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">标题</label>
            <div class="layui-input-block">
                <input id="title" name="title" lay-verify="infoTitle" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">作者</label>
            <div class="layui-input-inline">
                <input id="author" name="author" lay-verify="infoAuthor" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">阅读次数</label>
            <div class="layui-input-inline">
                <input id="readCount" name="readCount" onkeyup="IWS_CheckDecimal(this)"  class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">排序</label>
            <div class="layui-input-inline">
                <input id="sort" name="sort" onkeyup="IWS_CheckDecimal(this)" class="layui-input">
            </div>
            <div class="layui-form-mid layui-word-aux">数字越大越靠前</div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">内容</label>
            <div class="layui-input-block">
            	<textarea id="content"></textarea>
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