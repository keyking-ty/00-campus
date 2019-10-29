package com.telit.info.web.controller.app;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telit.info.data.RespResult;
import com.telit.info.data.app.Advert;
import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.NetMeal;
import com.telit.info.data.business.School;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.TimeUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tk.mybatis.mapper.entity.Example;

@Api(tags = { "营业厅接口" })
@RestController
@RequestMapping("/app/business")
public class AppBusinessController {
	@Resource
	private DataService dataService;

	@ApiOperation("大厅")
	@GetMapping("/hall")
	public RespResult hallDatas(@ApiParam(name = "id", value = "传入用户编号",required = true) @RequestParam Integer id) {
		RespResult result = new RespResult();
		AppUser user  = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("用户编号异常");
			return result;
		}
		School school = dataService.get(user.getSchoolId(),School.class);
		if (school == null) {
			result.setMsg("数据异常,请联系客服!");
			return result;
		}
		Example example = new Example(Advert.class);
		example.createCriteria().andEqualTo("advSta","开启")
		.andEqualTo("advType","首页").andLike("schoolId","%_" + school.getId() + "_%");
		List<Advert> adverts = dataService.all(example,Advert.class);
		CommonUtil.removeByCondition(adverts, (data)->{
			if (data.getTimeLast() > 0) {
				return !TimeUtils.checkDateInDay(data.getStartDate(),data.getTimeLast());
			}
			return true;
		});
		result.put("ads",adverts);
		return result;
	}
	
	@ApiOperation("宽带套餐")
	@GetMapping("/meal")
	public RespResult netMeal(@ApiParam(name = "id", value = "传入用户编号",required = true) @RequestParam Integer id) {
		RespResult result = new RespResult();
		AppUser user  = dataService.get(id, AppUser.class);
		if (user == null) {
			result.setMsg("用户编号异常");
			return result;
		}
		School school = dataService.get(user.getSchoolId(),School.class);
		if (school == null) {
			result.setMsg("数据异常,请联系客服!");
			return result;
		}
		Example example = new Example(NetMeal.class);
		example.createCriteria().andEqualTo("statu","上架")
		.andEqualTo("schoolId",school.getId());
		example.setOrderByClause("hot_order DESC,inner_order DESC");
		List<NetMeal> meals = dataService.all(example,NetMeal.class);
		result.put("meals",meals);
		return result;
	}
}
