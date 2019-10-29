//引入新的插件
layui.config({
    base: '/static/js/'//拓展模块的根目录
}).extend({
    pca: 'pca'
});
function setAddressValue(p,c,a) {
	layui.use(['form', 'layedit', 'laydate', 'upload', "jquery", "pca"], function () {
	    var $ = layui.$
	    , form = layui.form
	    , pca = layui.pca;
	    //带初始值进行初始化
	    pca.init('select[name=province]', 'select[name=city]', 'select[name=area]','input[name=otherArea]',p,c,a);
	})
};