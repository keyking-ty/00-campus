<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>手机套餐</title>
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
        var layerid,editor,schools;
        $(document).ready(function () {
            //页面加载完成之后执行
            pageInit();
            layui.use(['layer','form','layedit','upload'], function(){
                var layer = layui.layer,
                layedit = layui.layedit,
                upload  = layui.upload,
                $ = layui.$,
                form = layui.form;
                //创建一个编辑器
                var editIndex = layedit.build('LAY_demo_editor');
                //自定义验证规则
                form.verify({
                    mealName: function(value) {
                        if(value.length == 0) {
                            return '名称不能为空';
                        }
                    },
                    price: function(value) {
                        if(value.length == 0) {
                            return '价格不能为空';
                        }
                    },
                    realyPrice: function(value) {
                        if(value.length == 0) {
                            return '折扣价格不能为空';
                        }
                    },
                    divideNum: function(value) {
                        if(value.length == 0) {
                            return '分成比例不能为空';
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
                    if ($("#businessActivity").is(':checked')){
                    	_data = _data.replace(/businessActivity=on/,"businessActivity=true");
                    }
                    if ($("#visitActivity").is(':checked')){
                    	_data = _data.replace(/visitActivity=on/,"visitActivity=true");
                    }
                    var content = editor.getContent();
                    _data += "&content=" + content;
                    $.ajax({
                        type: "POST",
                        url: "${request.contextPath!}/admin/cardMeal/addOrUpdate",
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
                                return;
                            }
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
                    });
                    return false;//防止表单提交后跳转
                });
                //监听选择变化
	            form.on('select(addType)', function(data) {
	            	if (data.elem.value == 1){
	            		$("#addTextId").hide();
	            		$("#addPicId").show();
	            	}else{
	            		$("#addTextId").show();
	            		$("#addPicId").hide();
	            	}
	            });
	            //上传icon文件
                upload.render({
				    elem: '#mealIconUpload', //绑定元素
				    url: '${request.contextPath!}/file/upload?module=cardMeal', //上传接口
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
			    //上传内容文件
                upload.render({
				    elem: '#picUpload', //绑定元素
				    url: '${request.contextPath!}/file/upload?module=cardMeal', //上传接口
				    done: function(res){
				      //上传完毕回调
				      if (res.succ){
				      	 addContentItem($("#addType").val(),res.fileName);
				      	 layer.close(addDivPopLayerId);
				      	 layui.form.render();
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
			$("#mealIconLook").hide();
			$("#iconUrl").val('');
		}
		
		function setIconValue(fileUrl){
			$("#iconUrl").val(fileUrl);
	      	var html = "<image src='${request.contextPath!}/file/downLoad?fileName=" + fileUrl + "' />";
	      	$("#showUploadImagediv").html(html);
	      	$("#mealIconLook").show();
		}
		
		function fillRadio(divName,value){
			var html = '';
			if (value == '上架'){
				html += '<input type="radio" name="statu" value="上架" title="上架"  checked>';
				html += '<input type="radio" name="statu" value="下架" title="下架">';
			}else{
				html += '<input type="radio" name="statu" value="上架" title="上架">';
				html += '<input type="radio" name="statu" value="下架" title="下架" checked>';
			}
			$(divName).html(html);
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
	        $("#mealIconLook").click(function () {
	        	layer.open({//开启表单弹层
                    skin: 'layui-layer-molv',
                    area: ['400px','400px'],
                    type: 1,
                    title:'查看图片',
                    content: $('#showUploadImagediv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
                return false;
	        });
        	$("#add").hide();
            $("#edit").hide();
            $("#delete").hide();
            checkOperate('${request.contextPath!}',6103,false);
            initKindEditor_content('${request.contextPath!}','content','100%',350,'cardMeal');
            //创建jqGrid组件
            jQuery("#list2").jqGrid({
                mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                url: '${request.contextPath!}/admin/cardMeal/list',
                datatype: "json",//请求数据返回的类型。可选json,xml,txt
                colModel: [
                	{label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 100, sortable: true, align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},  
                    {label: '学校名称', name: 'schoolName', width: 180, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/schools',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '运营商', name: 'operator', width: 180, sortable: true,align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=中国移动,中国电信,中国联通',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '套餐名称', name: 'name', width: 180, sortable: true , align:'center',
                		searchoptions: {
                			sopt: ['eq','cn']
                		}
                	},
                	{label: '原价(单位:元)', name: 'price', width: 180, sortable: true , align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '售价(单位:元)', name: 'realyPrice', width: 180, sortable: true , align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '分成比(%)', name: 'divideNum', width: 180, sortable: true , align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '套餐推荐', name: 'hotOrder', width: 180, sortable: true , align:'center',
                		searchoptions: {
                			sopt: ['eq','ne','lt','le','gt','ge']
                		}
                	},
                	{label: '激活方式', name: 'activityType', width: 180, sortable: true , align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=营业厅激活,上门激活',
                			sopt: ['eq','ne']
                		}
                	},
                	{label: '状态', name: 'statu', width: 180, sortable: true , align:'center',stype: 'select',
                		searchoptions: {
                			dataUrl: '${request.contextPath!}/admin/search/clients?ops=上架,下架',
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
                },
                loadComplete: function (data) {
                    schools = data.schools;
                    var htmlStr = '';
                    schools.forEach(function(school){
                        htmlStr += '<option value="' + school.id +'">' + school.schoolName + '</option>';
                    });
                    $("#schoolId").html(htmlStr);
                    if (data.operators != '全部'){
                        $("#operator").html('<option value="' + data.operators + '">' + data.operators + '</option>');
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
                clearUploadInfo();
                fillRadio('#statuDiv','售卖');
                setTimeout(function(){
                    editor.setContent('');
                },100);
                layui.form.render();
                $("#reset").click();
                layerid = layer.open({//开启表单弹层
                    skin: 'layui-layer-molv',
                    area: ['1000px','800px'],
                    type: 1,
                    title:'新增手机套餐',
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
                        layer.close(index); //如果设定了yes回调，需进行手工关闭
                        //请求后台，执行删除操作
                        $.ajax({
                            type: "POST",
                            url:"${request.contextPath!}/admin/cardMeal/deleteMeal",
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
                                        type: 1,
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
                    url:"${request.contextPath!}/admin/cardMeal/selectById",
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
                    	$("#schoolId").val(data.meal.schoolId);
                    	if (data.meal.iconUrl){
                    		setIconValue(data.meal.iconUrl);
                    	}else{
                    		clearUploadInfo();
                    	}
                    	$("#operator").val(data.meal.operator);
                    	$("#name").val(data.meal.name);
                        $("#price").val(data.meal.price);
                        $("#realyPrice").val(data.meal.realyPrice);
                        $("#divideNum").val(data.meal.divideNum);
                        $("#hotOrder").val(data.meal.hotOrder);
                        $("#auther").val(data.meal.auther);
                        $("#activityTypeId").val(data.meal.activityType);
                        fillRadio("#statuDiv",data.meal.statu);
                        setTimeout(function(){
                            editor.setContent(data.meal.content);
                        },100);
                        layui.form.render();
                        //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                        layerid = layer.open({
                            skin: 'layui-layer-molv',
                            area:['1000px','800px'],
                            type: 1,
                            title:'编辑手机套餐',
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
        <div id="addeditformdivid" hidden="" class="layui-fluid">
            <form class="layui-form" action="" id="addeditformid">
                <label hidden="true" id="editlabelid"></label>
                <input id="editid" name="id" value="" hidden="true" />
                <div class="layui-form-item" style="margin-top:10px">
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
                    <label class="layui-form-label">套餐名称</label>
                    <div class="layui-input-inline">
                        <input id="name" name="name" lay-verify="mealName" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">套餐图标</label>
                    <button type="button" class="layui-btn" id="mealIconUpload" name="mealIconUpload" >
                      <i class="layui-icon">&#xe67c;</i>上传图片
                    </button>
                    <button type="button" class="layui-btn" id="mealIconLook" hidden="true">查看</button>
                    <input id="iconUrl" name="iconUrl" hidden="true" />
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">推荐排序</label>
                    <div class="layui-input-inline">
                        <input id="hotOrder" name="hotOrder" onkeyup="IWS_CheckDecimal(this)" class="layui-input">
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
                    <label class="layui-form-label">激活方式</label>
                    <div class="layui-input-inline">
                        <select id="activityTypeId" name="activityType">
                            <option value="营业厅激活">营业厅激活</option>
                            <option value="上门激活">上门激活</option>
                        </select>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">价格</label>
                    <div class="layui-input-inline">
                        <input id="price" name="price" onkeyup="IWS_CheckDecimal(this)" lay-verify="price" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">折扣价</label>
                    <div class="layui-input-inline">
                        <input id="realyPrice" name="realyPrice" onkeyup="IWS_CheckDecimal(this)" lay-verify="realyPrice" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">分成</label>
                    <div class="layui-input-inline">
                        <input id="divideNum" name="divideNum" onkeyup="IWS_CheckDecimal(this)" lay-verify="divideNum" class="layui-input">
                    </div>
                    <div class="layui-form-mid layui-word-aux">%</div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">套餐内容</label>
                    <div class="layui-input-block">
                        <textarea id="content"  class="layui-textarea"></textarea>
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
    </body>
</html>