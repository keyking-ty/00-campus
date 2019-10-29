package com.telit.info.web.controller.app;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telit.info.data.RespResult;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.GoodsData;
import com.telit.info.trans.mapper.GoodsMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = { "失物寻物接口" })
@RestController
@RequestMapping("/app/goods")
public class AppGoodsController {
	@Resource
	private DataService dataService;
	
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	@ApiOperation("发布【失物招领/寻物启事】信息")
	@PostMapping("/issue")
	public RespResult issue(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "type", value = "类型:失物招领/寻物启事", required = true) @RequestParam String type,
			@ApiParam(name = "phone", value = "联系电话", required = true) @RequestParam String phone,
			@ApiParam(name = "content", value = "描述文字", required = false) @RequestParam String content,
			@ApiParam(name = "pics", value = "图片上传成功返回url,多张用半角逗号拼接如(1.jpg,2.jpg)") @RequestParam(required=false) String pics) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		if (CommonUtil.isEmpty(content)) {
			result.setMsg("内容未空");
			return result;
		}
		if (CommonUtil.isEmpty(type)) {
			result.setMsg("类型不匹配");
			return result;
		}
		if (!CommonUtil.checkMobile(phone)) {
			result.setMsg("联系电话格式不对");
			return result;
		}
		GoodsData goods = new GoodsData();
		goods.setContent(content);
		goods.setType(type);
		goods.setPhone(phone);
		if (CommonUtil.isNotEmpty(pics)) {
			List<String> temp = CommonUtil.split(pics, ",", String.class);
			goods.setPics(JsonUtil.encodeToStr(temp));
		}
		goods.setTime(new Timestamp(System.currentTimeMillis()));
		goods.setUserId(id);
		goods.setSchoolId(user.getSchoolId());
		if (dataService.insert(goods) <= 0) {
			result.setMsg("系统异常");
			return result;
		}
		result.setState(1);
		return result;
	}
	
	@ApiOperation("修改【失物招领/寻物启事】信息")
	@PostMapping("/change")
	public RespResult update(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "goodsId", value = "信息编号", required = true) @RequestParam Integer goodsId,
			@ApiParam(name = "content", value = "描述文字") @RequestParam(required=false) String content,
			@ApiParam(name = "phone", value = "联系电话") @RequestParam(required=false) String phone,
			@ApiParam(name = "pics", value = "图片上传成功返回url,多张用半角逗号拼接如(1.jpg,2.jpg)") @RequestParam(required=false) String pics) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		GoodsData goods = dataService.get(goodsId, GoodsData.class);
		if (goods.getUserId().intValue() != id.intValue()) {
			result.setMsg("你不是发布者,无法修改");
			return result;
		}
		if (CommonUtil.isNotEmpty(content)) {
			goods.setContent(content);
		}
		if (CommonUtil.isNotEmpty(phone)) {
			if (!CommonUtil.checkMobile(phone)) {
				result.setMsg("联系电话格式不对");
				return result;
			}
			goods.setPhone(phone);
		}
		if (CommonUtil.isNotEmpty(pics)) {
			if (CommonUtil.isNotEmpty(goods.getPics())) {
				List<String> olds = JsonUtil.decodeToList(goods.getPics(), String.class);
				for (String file : olds) {
					CommonUtil.deleteFile(uploadFilePath,file);
				}
			}
			List<String> temp = CommonUtil.split(pics,",",String.class);
			goods.setPics(JsonUtil.encodeToStr(temp));
		}
		dataService.save(goods);
		result.setState(1);
		return result;
	}
	
	@ApiOperation("获取从start位置开始的len条【失物招领/寻物启事】信息")
	@GetMapping("/list")
	public RespResult load(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "start", value = "开始的位置>=0", required = true) @RequestParam Integer start,
			@ApiParam(name = "len", value = "获取最大数量>0", required = true) @RequestParam Integer len) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		GoodsMapper mapper = dataService.getMapper(GoodsData.class);
		List<GoodsData> goods = mapper.list(0,user.getSchoolId(),start,len);
		result.setState(1);
		result.put("goods",goods);
		result.put("start",start);
		return result;
	}
	
	@ApiOperation("获取个人发布的【失物招领/寻物启事】信息")
	@GetMapping("/mys")
	public RespResult mys(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		GoodsMapper mapper = dataService.getMapper(GoodsData.class);
		List<GoodsData> goods = mapper.list(id,0,0,0);
		result.setState(1);
		result.put("goods",goods);
		return result;
	}
	
	@ApiOperation("删除个人发布的【失物招领/寻物启事】信息")
	@GetMapping("/delete")
	public RespResult delete(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "goodsId", value = "用户编号", required = true) @RequestParam Integer goodsId) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		GoodsData goods = dataService.get(goodsId, GoodsData.class);
		if (goods.getUserId().intValue() != id.intValue()) {
			result.setMsg("你不是发布者,无法删除");
			return result;
		}
		if (dataService.delete(goodsId,GoodsData.class) == null) {
			result.setMsg("系统异常");
			return result;
		}
		result.setState(1);
		return result;
	}
}
