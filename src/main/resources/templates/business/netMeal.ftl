<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="cn">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>宽带套餐</title>
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
                        mealKey: function(value) {
                            if(value.length == 0) {
                                return '套餐编号不能为空';
                            }
                        },
                        bandWidth: function(value) {
                            if(value.length == 0) {
                                return '带宽不能为空';
                            }
                        },
                        lastTime: function(value) {
                            if(value.length == 0) {
                                return '持续时间不能为空';
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
                        var content = editor.getContent();
                        _data += "&content=" + content;
                        $.ajax({
                            type: "POST",
                            url: "${request.contextPath!}/admin/netMeal/addOrUpdate",
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
                    //监听选择变化
                    form.on('select(schoolId)', function(data) {
                        schools.forEach(function(school){
                            if (school.id == data.elem.value){
                                fillNetType(school);
                            }
                        });
                    });
                    //上传文件
                    upload.render({
                        elem: '#mealIconUpload', //绑定元素
                        url: '${request.contextPath!}/file/upload?module=netMeal', //上传接口
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
                $("#mealIconLook").hide();
                $("#iconUrl").val('');
            }

            function setIconValue(fileUrl){
                $("#iconUrl").val(fileUrl);
                var html = "<image src='${request.contextPath!}/file/downLoad?fileName=" + fileUrl + "' />";
                $("#showUploadImagediv").html(html);
                $("#mealIconLook").show();
            }

            function fillNetType(school){
                var html = '';
                if (school.cityHot){
                    html += '<option value="cityHot">城市热点</option>';
                }
                if (school.portal){
                    html += '<option value="portal">安徽电信</option>';
                }
                if (school.rjrz){
                    html += '<option value="rjrz">锐捷认证</option>';
                }
                $("#netType").html(html);
            }

            function fillRadio(divName,name,trueTitle,falseTitle,flag){
                var html = '';
                if (flag){
                    html += '<input type="radio" name="' + name + '" value="' + trueTitle + '" title="' + trueTitle + '"  checked>';
                    html += '<input type="radio" name="' + name + '" value="' + falseTitle + '" title="' + falseTitle + '">';
                }else{
                    html += '<input type="radio" name="' + name + '" value="' + trueTitle + '" title="' + trueTitle + '" >';
                    html += '<input type="radio" name="' + name + '" value="' + falseTitle + '" title="' + falseTitle + '" checked>';
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
                        area:'20%',
                        type: 1,
                        title:'查看图片',
                        content: $('#showUploadImagediv') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                    });
                });
                $("#add").hide();
                $("#edit").hide();
                $("#delete").hide();
                checkOperate('${request.contextPath!}',6100,false);
                initKindEditor_content('${request.contextPath!}','content','100%',350,'info');
                //创建jqGrid组件
                $("#list2").jqGrid({
                    mtype: "post",//向后台请求数据的ajax的类型。可选post,get
                    url: '${request.contextPath!}/admin/netMeal/list',
                    datatype: "json",//请求数据返回的类型。可选json,xml,txt
                    colModel: [
                        {label: 'ID', name: 'id',sorttype: 'integer',key: true, width: 100, sortable: true, align:'center',
                            searchoptions: {
                                sopt: ['eq','ne','lt','le','gt','ge']
                            }
                        },
                        {label: '学校', name: 'schoolName', width: 180, sortable: true,align:'center',stype: 'select',
                            searchoptions: {
                                dataUrl: '${request.contextPath!}/admin/search/schools',
                                sopt: ['eq','ne']
                            }
                        },
                        {label: '运营商', name: 'operator', width: 180, sortable: false,align:'center',stype: 'select',
                            searchoptions: {
                                dataUrl: '${request.contextPath!}/admin/search/clients?ops=中国移动,中国电信,中国联通',
                                sopt: ['eq','ne']
                            }
                        },
                        {label: '套餐名称', name: 'name', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['cn']
                            }
                        },
                        {label: '时长(天)', name: 'lastTime', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['eq','ne','lt','le','gt','ge']
                            }
                        },
                        {label: '带宽(M)', name: 'bandWidth', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['eq','ne','lt','le','gt','ge']
                            }
                        },
                        {label: 'key值', name: 'keyWord', width: 180, sortable: false,align:'center',
                            searchoptions: {
                                sopt: ['eq','cn']
                            }
                        },
                        {label: '原价', name: 'price', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['eq','ne','lt','le','gt','ge']
                            }
                        },
                        {label: '售价', name: 'realyPrice', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['eq','ne','lt','le','gt','ge']
                            }
                        },
                        {label: '分成(%)', name: 'divideNum', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['eq','ne','lt','le','gt','ge']
                            }
                        },
                        {label: '热门排序', name: 'hotOrder', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['eq','ne','lt','le','gt','ge']
                            }
                        },
                        {label: '创建者', name: 'auther', width: 180, sortable: true,align:'center',
                            searchoptions: {
                                sopt: ['eq','cn']
                            }
                        },
                        {label: '状态', name: 'statu', width: 180, sortable: false,align:'center',stype: 'select',
                            searchoptions: {
                                dataUrl: '${request.contextPath!}/admin/search/clients?ops=上架,下架',
                                sopt: ['eq','ne']
                            }
                        },
                        {label: '融合', name: 'merge', width: 180, sortable: false,align:'center',stype: 'select',
                            searchoptions: {
                                dataUrl: '${request.contextPath!}/admin/search/clients?ops=可以,不可以',
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
                        var htmlStr1 = '';
                        var htmlStr2 = '';
                        schools.forEach(function(school){
                            htmlStr1 += '<input type="checkbox" name="schoolId" title="' + school.schoolName + '" value="' + school.id + '"/>';
                            htmlStr2 += '<option value="' + school.id +'">' + school.schoolName + '</option>';
                        });
                        $("#exportSchoolId").html(htmlStr1);
                        $("#schoolId").html(htmlStr2);
                        if (data.operators != '全部'){
                            $("#operator").html('<option value="' + data.operators + '">' + data.operators + '</option>');
                            $("#exportOperator").html('<input type="checkbox" name="operator" title="' + data.operators + '" value="' + data.operators + '"/>');
                        }
                    }
                });
                /*创建jqGrid的操作按钮容器*/
                $('#list2').navGrid('#pager2',{
                    search: true, add: false, edit: false, del: false, refresh: true
                },{},/*edit options*/{},/*add options*/{},/*delete options*/
                {
                    multipleSearch: true
                });
                //添加按钮点击事件
                $("#add").click(function () {
                    $("#editlabelid").html('');
                    fillNetType(schools[0]);
                    clearUploadInfo();
                    fillRadio("#statuDiv","statu","上架","下架",true);
                    fillRadio("#mergeDiv","merge","可以","不可以",false);
                    setTimeout(function(){
                        editor.setContent('');
                    },100);
                    layui.form.render();
                    $("#reset").click();
                    layerid = layer.open({//开启表单弹层
                        skin: 'layui-layer-molv',
                        area: ['1000px','800px'],
                        type: 1,
                        title:'新增宽带',
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
                                url:"${request.contextPath!}/admin/netMeal/deleteMeal",
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
                    var selectId = getSelectOne('请选择要编辑的记录');
                    if (selectId == null){
                        return false;
                    }
                    $.ajax({
                        type: "POST",
                        url:"${request.contextPath!}/admin/netMeal/selectById",
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
                            fillNetType(schools[0]);
                            $("#schoolId").val(data.meal.schoolId);
                            schools.forEach(function(school){
                                if (school.id == data.meal.schoolId){
                                    if (school.cityHot){
                                        $("#netType").val("cityHot");
                                    }else if (school.portal){
                                        $("#netType").val("portal");
                                    }else if (school.rjrz){
                                        $("#netType").val("rjrz");
                                    }
                                }
                            });
                            if (data.meal.iconUrl){
                                setIconValue(data.meal.iconUrl);
                            }else{
                                clearUploadInfo();
                            }
                            $("#operator").val(data.meal.operator);
                            $("#name").val(data.meal.name);
                            $("#lastTime").val(data.meal.lastTime);
                            $("#bandWidth").val(data.meal.bandWidth);
                            $("#keyWord").val(data.meal.keyWord);
                            $("#price").val(data.meal.price);
                            $("#realyPrice").val(data.meal.realyPrice);
                            $("#divideNum").val(data.meal.divideNum);
                            $("#hotOrder").val(data.meal.hotOrder);
                            $("#innerOrder").val(data.meal.innerOrder);
                            $("#description").val(data.meal.description);
                            $("#auther").val(data.meal.auther);
                            $("#activityRule").val(data.meal.activityRule);
                            fillRadio("#statuDiv","statu","上架","下架",data.meal.statu);
                            fillRadio("#mergeDiv","merge","可以","不可以",data.meal.merge);
                            setTimeout(function(){
                                editor.setContent(data.meal.content);
                            },100);
                            layui.form.render();
                            //开启编辑表单所在的弹层。注意编辑和新建的表单是一套模板
                            layerid=layer.open({
                                skin: 'layui-layer-molv',
                                area: ['1000px','800px'],
                                type: 1,
                                title:'编辑宽带',
                                content: $('#addeditformdivid') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                            });
                        }
                    });
                });
                $("#lookUsed").click(function(){
                    fillRadio("#exportMerge","merge","可以","不可以",false);
                    layui.form.render();
                    layerid=layer.open({
                        skin: 'layui-layer-molv',
                        area: ['400px','300px'],
                        type: 1,
                        title:'用户分析导出',
                        content: $('#userDetailDivId') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                    });
                });
                $("#doExportAction").click(function () {
                    var schools = $('input[name="schoolId"]:checked');
                    if (schools.length == 0){
                        layer.alert('请选择学校');
                        return;
                    }
                    var operators = $('input[name="operator"]:checked');
                    if (operators.length == 0){
                        layer.alert('请选择运营商');
                        return;
                    }
                    var params = $("#userDetailFormId").serialize();
                    console.log('params = ' + params);
                    window.open('${request.contextPath!}/admin/netMeal/export?' + params);
                });
            };
        </script>
    </head>
    <body>
        <div class="layui-btn-group">
            <button class="layui-btn" id="add">增加</button>
            <button class="layui-btn" id="edit">编辑</button>
            <button class="layui-btn" id="delete">删除</button>
            <button class="layui-btn" id="lookUsed">用户分析</button>
        </div>
        <table id="list2"></table>
        <div id="pager2"></div>
        <div id="showUploadImagediv" hidden="" class="layui-fluid" style="margin: 15px;"></div>
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
                    <label class="layui-form-label">套餐名称</label>
                    <div class="layui-input-block">
                        <input id="name" name="name" lay-verify="mealName" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">套餐编号</label>
                    <div class="layui-input-block">
                        <input id="keyWord" name="keyWord" lay-verify="mealKey" class="layui-input">
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
                    <label class="layui-form-label">带宽</label>
                    <div class="layui-input-inline">
                        <input id="bandWidth" name="bandWidth" onkeyup="IWS_CheckDecimal(this)" lay-verify="bandWidth" class="layui-input">
                    </div>
                    <div class="layui-form-mid layui-word-aux">M</div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">时间</label>
                    <div class="layui-input-inline">
                        <input id="lastTime" name="lastTime" onkeyup="IWS_CheckDecimal(this)" lay-verify="lastTime" class="layui-input">
                    </div>
                    <div class="layui-form-mid layui-word-aux">天</div>
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
                    <label class="layui-form-label">上网方式</label>
                    <div class="layui-input-inline">
                        <select id="netType" name="netType">
                        </select>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">宽带描述</label>
                    <div class="layui-input-block">
                        <input id="description" name="description" class="layui-input">
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
                    <label class="layui-form-label">内部排序</label>
                    <div class="layui-input-inline">
                        <input id="innerOrder" name="innerOrder" onkeyup="IWS_CheckDecimal(this)" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">生效规则</label>
                    <div class="layui-input-block">
                        <input id="activityRule" name="activityRule" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">宽带内容</label>
                    <div class="layui-input-block">
                        <textarea id="content" class="layui-textarea"></textarea>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">状态</label>
                    <div id="statuDiv" class="layui-input-block">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">融合</label>
                    <div id="mergeDiv" class="layui-input-block">
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
        <div id="userDetailDivId" hidden="" class="layui-fluid" style="margin: 15px;">
            <form class="layui-form" id="userDetailFormId">
                <div class="layui-form-item" style="margin-top:20px">
                    <label class="layui-form-label">运营商</label>
                    <div id="exportOperator" class="layui-input-inline">
                        <input type="checkbox" name="operator" title="中国移动" value="中国移动" />
                        <input type="checkbox" name="operator" title="中国电信" value="中国电信" />
                        <input type="checkbox" name="operator" title="中国联通" value="中国联通" />
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">学校</label>
                    <div id="exportSchoolId" class="layui-input-inline">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">融合</label>
                    <div id="exportMerge" class="layui-input-block">
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn" type="button" id="doExportAction">导出</button>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>