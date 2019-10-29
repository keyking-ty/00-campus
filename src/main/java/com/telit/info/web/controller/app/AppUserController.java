package com.telit.info.web.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telit.info.actions.ConstData;
import com.telit.info.data.RespResult;
import com.telit.info.data.SmsCode;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.CityHotInfo;
import com.telit.info.data.business.InfoData;
import com.telit.info.data.business.School;
import com.telit.info.sms.SmsSender;
import com.telit.info.trans.mapper.InfoMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CityHotUtil;
import com.telit.info.util.CommonUtil;
import com.telit.info.web.NetUitl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tk.mybatis.mapper.entity.Example;

@Api(tags = { "用户接口" })
@RestController
@RequestMapping("/app/user")
public class AppUserController {
	
	@Resource
	private DataService dataService;
	
	@Autowired
	private SmsSender smsSender;
	
	Map<String,SmsCode> codes = new HashMap<String,SmsCode>();
	
	private boolean changePwd(AppUser user,String pwd,RespResult result) {
		School school = dataService.get(user.getSchoolId(),School.class);
		if (school == null) {
			result.setMsg("系统异常");
			return true;
		}
		if ("已认证".equals(user.getAuthenSta())) {
			CityHotInfo info = CityHotUtil.update(school,user.getAccount(),user.getLoginPwd());
			if (info.checkSucc()) {
				info.setUserId(user.getId());
	    		info.setGroupId(user.getMealId());
	    		dataService.insert(info);
	    		user.setLoginPwd(pwd);
	    		dataService.save(user);
	    		result.setState(1);
	    		return false;
			}else{
				String error = ConstData.CITY_HOT_UPDATE_ERROR.get(info.getCode());
				error = error == null ? "修改失败" : error;
	        	result.setMsg(error);
	        	return true;
			}
		}else{
			//如果还没有实名认证没有设置学号不去城市热点修改密码
			result.setState(1);
			user.setLoginPwd(pwd);
			dataService.save(user);
			return false;
		}
	}
	
	private void createCode(String titile,String phone,RespResult result) {
		String code = CommonUtil.randomStr(6);
		if (smsSender.sendMsgToPhone(phone,titile + code + ",有效期5分钟")) {
			SmsCode sms = new SmsCode(code,5);
			codes.put(phone,sms);
			result.setState(1);
		} else{
			result.setMsg("发送失败");
		}
	}
	
	private boolean checkCode(String key,String code,RespResult result) {
		SmsCode sms = codes.get(key);
		if (sms == null) {
			result.setMsg("验证码已过期");
			return true;
		}
		String tips = sms.check(code);
		if (tips != null) {
			result.setMsg(tips);
			return true;
		}
		return false;
	}

	@ApiOperation("用户登录接口")
	@GetMapping("/login")
	public RespResult userLogin(
			@ApiParam(name = "loginName", value = "传入登录帐号", required = true) @RequestParam String loginName,
			@ApiParam(name = "loginPwd", value = "传入登录密码", required = true) @RequestParam String loginPwd) {
		RespResult result = new RespResult();
		Example example = new Example(AppUser.class);
		example.createCriteria().orEqualTo("loginName",loginName)
		.orEqualTo("account",loginName)
		.orEqualTo("idCard",loginName)
		.orEqualTo("mobile",loginName);
		AppUser user = dataService.search(example,AppUser.class);
		if (user == null || !loginPwd.equals(user.getLoginPwd())) {
			result.setMsg("登录账户或者登录密码错误");
		} else {
			result.setState(1);
			result.insertObj(user);
			School school = dataService.get(user.getSchoolId(),School.class);
			result.put("loginUrl", school.getLoginUrl());//城市热点登录url
			result.put("exitUrl", school.getExitUrl());//城市热点退出url
			result.put("onlineUrl", school.getOnlineUrl());//城市热点在线url
			result.put("wifi", school.getWifiName());//学校的wifi名称
		}
		return result;
	}
	
	@ApiOperation("获得注册短信验证码")
	@PostMapping("/registSmsCode")
	public RespResult smsCodeOnRegist(@ApiParam(name = "phone", value = "手机号码", required = true) @RequestParam String phone) {
		RespResult result = new RespResult();
		if (!CommonUtil.checkMobile(phone)) {
			result.setMsg("手机号码格式不对");
			return result;
		}
		RespResult rr = NetUitl.checkDataProp(dataService, AppUser.class,phone,"mobile");
		if (rr.getState() > 0) {
			result.setMsg("该号码已经注册过了");
			return result;
		}
		createCode("【00校园平台】您的注册验证码是:",phone,result);
		return result;
	}
	
	@ApiOperation("用户注册接口")
	@PostMapping("/regist")
	public RespResult userRegist(
			@ApiParam(name = "phone", value = "手机号码", required = true) @RequestParam String phone,
			@ApiParam(name = "pwd", value = "登录密码", required = true) @RequestParam String pwd,
			@ApiParam(name = "code", value = "验证码", required = true) @RequestParam String code,
			@ApiParam(name = "schoolId", value = "学校编号", required = true) @RequestParam Integer schoolId) {
		RespResult result = new RespResult();
		if (!CommonUtil.checkMobile(phone)) {
			result.setMsg("手机号码格式不对");
			return result;
		}
		//if (checkCode(phone,code,result)) {
		//	return result;
		//}
		if (StringUtils.isEmpty(pwd)) {
			result.setMsg("登录密码不能为空");
			return result;
		}
		RespResult rr = NetUitl.checkDataProp(dataService, AppUser.class, phone,"mobile");
        if (rr.getState() > 0) {
        	result.setMsg(rr.getMsg());
			return result;
        }
		School school = dataService.get(schoolId, School.class);
		if (school == null) {
			result.setMsg("错误的学校编号");
			return result;
		}
		AppUser user = new AppUser();
		user.setLoginPwd(pwd);
		user.setMobile(phone);
		user.setLoginName(phone);
		user.setSchoolId(schoolId);
		Integer id = dataService.insert(user);
		if (id > 0) {
			result.setState(1);
		} else {
			result.setMsg("系统异常");
		}
		return result;
	}

	@ApiOperation("用户修改密码")
	@PostMapping("/pwd")
	public RespResult userPwd(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "oldPwd", value = "旧密码", required = true) @RequestParam String oldPwd,
			@ApiParam(name = "newPwd", value = "新密码", required = true) @RequestParam String newPwd) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		if (StringUtils.isEmpty(newPwd)) {
			result.setMsg("请设置新密码");
			return result;
		}
		if (!user.getLoginPwd().equals(oldPwd)) {
			result.setMsg("旧的密码错误");
			return result;
		}
		changePwd(user,newPwd,result);
		return result;
	}

	@ApiOperation("用户认证数据上传")
	@PostMapping("/authen")
	public RespResult userAuthen(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "realName", value = "真实姓名", required = true) @RequestParam String realName,
			@ApiParam(name = "nickName", value = "昵称", required = true) @RequestParam String nickName,
			@ApiParam(name = "sex", value = "性别", required = true) @RequestParam String sex,
			@ApiParam(name = "idCard", value = "身份证号码", required = true) @RequestParam String idCard,
			@ApiParam(name = "idcardPosimg", value = "身份证正面上传后返回的图片名称", required = true) @RequestParam String idcardPosimg,
			@ApiParam(name = "idcardSideimg", value = "身份证反面上传后返回的图片名称", required = true) @RequestParam String idcardSideimg,
			@ApiParam(name = "stuCard", value = "学号", required = false) @RequestParam String stuCard,
			@ApiParam(name = "stucardPosimg", value = "学生证反面上传后返回的图片名称", required = false) @RequestParam String stucardPosimg,
			@ApiParam(name = "stucardSideimg", value = "学生证反面上传后返回的图片名称", required = false) @RequestParam String stucardSideimg) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		if ("审核中".equals(user.getAuthenSta())) {
			result.setMsg("认证正在审核中");
			return result;
		}
		if ("已认证".equals(user.getAuthenSta())) {
			result.setMsg("已完成认证");
			return result;
		}
		user.setIdCard(idCard);
		user.setAccount(stuCard);
		user.setIdcardPosimg(idcardPosimg);
		user.setIdcardSideimg(idcardSideimg);
		user.setStucardPosimg(stucardPosimg);
		user.setStucardSideimg(stucardSideimg);
		user.setRealName(realName);
		user.setNickName(nickName);
		user.setSex(sex);
		dataService.save(user);
		result.setState(1);
		return result;
	}

	@ApiOperation("获得修改密码短信验证码")
	@PostMapping("/pwdSmsCode")
	public RespResult smsCode(@ApiParam(name = "phone", value = "手机号码", required = true) @RequestParam String phone) {
		RespResult result = new RespResult();
		AppUser user = NetUitl.getDataByConditions(dataService,AppUser.class,phone,"mobile");
		if (user == null) {
			result.setMsg("手机未注册");
			return result;
		}
		createCode("【00校园平台】您修改密码的验证码是:",user.getMobile(),result);
		return result;
	}
	
	@ApiOperation("短信验证码修改密码")
	@PostMapping("/smsPwd")
	public RespResult checkSmsCode(@ApiParam(name = "phone", value = "手机号码", required = true) @RequestParam String phone,
			@ApiParam(name = "code", value = "短信验证码", required = true) @RequestParam String code,
			@ApiParam(name = "pwd", value = "新密码", required = true) @RequestParam String pwd) {
		RespResult result = new RespResult();
		AppUser user = NetUitl.getDataByConditions(dataService,AppUser.class,phone,"mobile");
		if (user == null) {
			result.setMsg("手机未注册");
			return result;
		}
		if (checkCode(phone,code,result)) {
			return result;
		}
		if (!changePwd(user,pwd,result)) {
			codes.remove(phone);
		}
		return result;
	}

	@ApiOperation("所有的学校列表")
	@GetMapping("/shcools")
	public RespResult loadShcools() {
		RespResult result = new RespResult();
		List<School> schools = NetUitl.getRolePermissionSchool(dataService,-99);
		result.setState(1);
		result.put("array", schools);
		return result;
	}
	
	@ApiOperation("获取从start位置开始的len条信息")
	@GetMapping("/loadInfo")
	public RespResult loadInfo(@ApiParam(name = "id", value = "用户编号", required = true) @RequestParam Integer id,
			@ApiParam(name = "type", value = "类型:资讯/公告", required = true) @RequestParam String type,
			@ApiParam(name = "start", value = "开始的位置>=0", required = true) @RequestParam Integer start,
			@ApiParam(name = "len", value = "获取最大数量>0", required = true) @RequestParam Integer len) {
		RespResult result = new RespResult();
		AppUser user = dataService.get(id,AppUser.class);
		if (user == null) {
			result.setMsg("找不到用户");
			return result;
		}
		InfoMapper mapper = dataService.getMapper(InfoData.class);
		List<InfoData> infos = mapper.loadByType(type,"_" + user.getSchoolId() + "_",start,len);
		result.setState(1);
		result.put("infos",infos);
		result.put("start",start);
		return result;
	}
}
