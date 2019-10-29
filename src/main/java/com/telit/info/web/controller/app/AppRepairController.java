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
import com.telit.info.data.business.RepairData;
import com.telit.info.trans.mapper.RepairMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.JsonUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = { "故障报修接口" })
@RestController
@RequestMapping("/app/repair")
public class AppRepairController {
	@Resource
	private DataService dataService;
	
	@Value("${uploadFilePath}")
	private String uploadFilePath;
	
	@ApiOperation("发布故障报修")
	@PostMapping("/issue")
	public RespResult issue(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "phone", value = "联系电话", required = true) @RequestParam String phone,
			@ApiParam(name = "content", value = "描述文字", required = true) @RequestParam String content,
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
		if (!CommonUtil.checkMobile(phone)) {
			result.setMsg("联系电话格式不对");
			return result;
		}
		RepairData repair = new RepairData();
		repair.setContent(content);
		if (CommonUtil.isNotEmpty(pics)) {
			List<String> temp = CommonUtil.split(pics,",",String.class);
			repair.setPics(JsonUtil.encodeToStr(temp));
		}
		repair.setPhone(phone);
		repair.setTime(new Timestamp(System.currentTimeMillis()));
		repair.setUserId(id);
		repair.setSchoolId(user.getSchoolId());
		repair.setStu("派单中");
		if (dataService.insert(repair) <= 0) {
			result.setMsg("系统异常");
			return result;
		}
		result.setState(1);
		return result;
	}
	
	@ApiOperation("修改故障报修")
	@PostMapping("/change")
	public RespResult update(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "repairId", value = "故障报修编号", required = true) @RequestParam Integer repairId,
			@ApiParam(name = "phone", value = "联系电话") @RequestParam(required=false) String phone,
			@ApiParam(name = "content", value = "故障描述文字") @RequestParam(required=false) String content,
			@ApiParam(name = "pics", value = "图片上传成功返回url,多张用半角逗号拼接如(1.jpg,2.jpg)") @RequestParam(required=false) String pics) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		RepairData repair = dataService.get(repairId, RepairData.class);
		if (repair.getUserId().intValue() != id.intValue()) {
			result.setMsg("你不是发布者,无法修改");
			return result;
		}
		if (CommonUtil.isNotEmpty(content)) {
			repair.setContent(content);
		}
		if (CommonUtil.isNotEmpty(phone)) {
			if (!CommonUtil.checkMobile(phone)) {
				result.setMsg("联系电话格式不对");
				return result;
			}
			repair.setPhone(phone);
		}
		if (CommonUtil.isNotEmpty(pics)) {
			if (CommonUtil.isNotEmpty(repair.getPics())) {
				List<String> olds = JsonUtil.decodeToList(repair.getPics(), String.class);
				for (String file : olds) {
					CommonUtil.deleteFile(uploadFilePath,file);
				}
			}
			List<String> temp = CommonUtil.split(pics,",",String.class);
			repair.setPics(JsonUtil.encodeToStr(temp));
		}
		dataService.save(repair);
		result.setState(1);
		return result;
	}
	
	@ApiOperation("获取个人发布的故障报修")
	@GetMapping("/mys")
	public RespResult mys(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		RepairMapper mapper = dataService.getMapper(RepairData.class);
		List<RepairData> repairs = mapper.list(id,0,0);
		result.setState(1);
		result.put("repairs",repairs);
		return result;
	}
	
	@ApiOperation("删除个人发布的故障报修")
	@GetMapping("/delete")
	public RespResult delete(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "repairId", value = "故障报修编号", required = true) @RequestParam Integer repairId) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		RepairData repair = dataService.get(repairId, RepairData.class);
		if (repair.getUserId().intValue() != id.intValue()) {
			result.setMsg("你不是发布者,无法删除");
			return result;
		}
		if (dataService.delete(repairId,RepairData.class) == null) {
			result.setMsg("系统异常");
			return result;
		}
		result.setState(1);
		return result;
	}
}
