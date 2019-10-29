<@com.head title="修改密码">
    <base id="base" href="${request.contextPath!}">
    <link href="${request.contextPath!}/static/layui/css/layui.css" type="text/css" media="screen" rel="stylesheet"/>
    <script src="${request.contextPath!}/static/layui/layui.js" type="text/javascript"></script>
<script>
    //一般直接写在一个js文件中
    layui.use(['layer','form','table','layedit'], function(){
        var layer = layui.layer
            ,form = layui.form
            ,$ = layui.$
            ,layedit = layui.layedit
            ,laytpl = layui.laytpl
            ,table = layui.table;
        //创建一个编辑器
        var editIndex = layedit.build('LAY_demo_editor');
        //自定义验证规则
        form.verify({
            oldPassword:function(value) {
                if(value.length == 0) {
                    return '旧密码不能为空';
                }
            },
            newPassword: function(value) {
                if (value.length < 6) {
                    return '密码必须6到12位';
                }
                var pwd = $("#repeatPwd").val();
                if (value != pwd){
                	return '两次输入不相同';
                }
            },
            content: function(value) {
                layedit.sync(editIndex);
            }
        });
        //监听表单提交事件
        form.on('submit(submitButton)', function (data) {
            $("#userId").val(${currentUser.id!});
            var _data = $("#formid").serialize();
            alert(_data);
            $.ajax({
                type: "POST",
                url: "${request.contextPath!}/admin/user/updatePassword",
                data: _data,
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
                    if(data.state=='success'){
                        layer.open({
                            content: data.mesg,
                            yes: function(index, layero){
                                window.location.href="${request.contextPath!}/user/logout";
                            }
                        });
                        return false;
                    }
                }
            });
            return false;//防止表单提交
        });
    });
</script>
</@com.head>
<@com.body>
<div class="layui-fluid">
    <form class="layui-form" action="" id="formid">
        <input type="hidden" name="id" id="userId" value=""/>
        <div class="layui-form-item">
            <label class="layui-form-label">旧密码</label>
            <div class="layui-input-inline">
                <input type="password" name="oldPassword" required lay-verify="oldPassword" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">新密码</label>
            <div class="layui-input-inline">
                <input type="password" name="newPassword" required lay-verify="newPassword" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">重复新密码</label>
            <div class="layui-input-inline">
                <input type="password" id="repeatPwd" required autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit lay-filter="submitButton">立即提交</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
</div>
</@com.body>