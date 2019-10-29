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
import com.telit.info.data.business.UsedItem;
import com.telit.info.trans.mapper.UsedItemMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = { "二手市场接口" })
@RestController
@RequestMapping("/app/market")
public class AppMarketController {
	@Resource
	private DataService dataService;
	
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	@ApiOperation("发布二手市场信息")
	@PostMapping("/issue")
	public RespResult issue(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "title", value = "标题", required = true) @RequestParam String title,
			@ApiParam(name = "phone", value = "联系电话", required = true) @RequestParam String phone,
			@ApiParam(name = "type", value = "类型:求购/出售", required = true) @RequestParam String type,
			@ApiParam(name = "content", value = "描述文字", required = true) @RequestParam String content,
			@ApiParam(name = "price", value = "售价", required = true) @RequestParam Float price,
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
		UsedItem item = new UsedItem();
		item.setContent(content);
		item.setType(type);
		item.setTitle(title);
		item.setPhone(phone);
		if (CommonUtil.isNotEmpty(pics)) {
			List<String> temp = CommonUtil.split(pics, ",", String.class);
			item.setPics(JsonUtil.encodeToStr(temp));
		}
		item.setTime(new Timestamp(System.currentTimeMillis()));
		item.setUserId(id);
		item.setSchoolId(user.getSchoolId());
		item.setPrice(price);
		if (dataService.insert(item) <= 0) {
			result.setMsg("系统异常");
			return result;
		}
		result.setState(1);
		return result;
	}
	
	@ApiOperation("修改二手市场信息")
	@PostMapping("/change")
	public RespResult update(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "itemId", value = "信息编号", required = true) @RequestParam Integer itemId,
			@ApiParam(name = "price", value = "售价") @RequestParam(required=false) Float price,
			@ApiParam(name = "phone", value = "联系电话") @RequestParam(required=false) String phone,
			@ApiParam(name = "content", value = "描述文字") @RequestParam(required=false) String content,
			@ApiParam(name = "pics", value = "图片上传成功返回url,多张用半角逗号拼接如(1.jpg,2.jpg)") @RequestParam(required=false) String pics) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		UsedItem item = dataService.get(itemId, UsedItem.class);
		if (item.getUserId().intValue() != id.intValue()) {
			result.setMsg("你不是发布者,无法修改");
			return result;
		}
		if (CommonUtil.isNotEmpty(content)) {
			item.setContent(content);
		}
		if (CommonUtil.isNotEmpty(phone)) {
			if (!CommonUtil.checkMobile(phone)) {
				result.setMsg("联系电话格式不对");
				return result;
			}
			item.setPhone(phone);
		}
		if (price != null) {
			item.setPrice(price);
		}
		if (CommonUtil.isNotEmpty(pics)) {
			if (CommonUtil.isNotEmpty(item.getPics())) {
				List<String> olds = JsonUtil.decodeToList(item.getPics(), String.class);
				for (String file : olds) {
					CommonUtil.deleteFile(uploadFilePath,file);
				}
			}
			List<String> temp = CommonUtil.split(pics,",",String.class);
			item.setPics(JsonUtil.encodeToStr(temp));
		}
		dataService.save(item);
		result.setState(1);
		return result;
	}
	
	@ApiOperation("获取从start位置开始的len条二手市场信息")
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
		UsedItemMapper mapper = dataService.getMapper(UsedItem.class);
		List<UsedItem> items = mapper.list(0,user.getSchoolId(),start,len);
		result.setState(1);
		result.put("items",items);
		result.put("start",start);
		return result;
	}
	
	@ApiOperation("获取个人发布的二手市场信息")
	@GetMapping("/mys")
	public RespResult mys(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		UsedItemMapper mapper = dataService.getMapper(UsedItem.class);
		List<UsedItem> items = mapper.list(id,0,0,0);
		result.setState(1);
		result.put("items",items);
		return result;
	}
	
	@ApiOperation("删除个人发布的二手市场信息")
	@GetMapping("/delete")
	public RespResult delete(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "itemId", value = "信息编号", required = true) @RequestParam Integer itemId) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		UsedItem item = dataService.get(itemId, UsedItem.class);
		if (item.getUserId().intValue() != id.intValue()) {
			result.setMsg("你不是发布者,无法删除");
			return result;
		}
		if (dataService.delete(itemId,UsedItem.class) == null) {
			result.setMsg("系统异常");
			return result;
		}
		result.setState(1);
		return result;
	}
}
