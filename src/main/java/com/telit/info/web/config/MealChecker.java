package com.telit.info.web.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.telit.info.data.app.AppUser;
import com.telit.info.data.business.CityHotInfo;
import com.telit.info.data.business.School;
import com.telit.info.trans.mapper.AppUserMapper;
import com.telit.info.trans.service.DataService;
import com.telit.info.util.CityHotUtil;

@Component
@EnableScheduling
public class MealChecker {
	// 每隔5秒执行一次："*/5 * * * * ?"
	// 每隔1分钟执行一次："0 */1 * * * ?"
	// 每天23点执行一次："0 0 23 * * ?"
	// 每天凌晨1点执行一次："0 0 1 * * ?"
	// 每月1号凌晨1点执行一次："0 0 1 1 * ?"
	// 每月最后一天23点执行一次："0 0 23 L * ?"
	// 每周星期天凌晨1点实行一次："0 0 1 ? * L"
	// 在26分、29分、33分执行一次："0 26,29,33 * * * ?"
	// 每天的0点、13点、18点、21点都执行一次："0 0 0,13,18,21 * * ?"
	// 表示在每月的1日的凌晨2点调度任务："0 0 2 1 * ? *"
	// 表示周一到周五每天上午10：15执行作业："0 15 10 ? * MON-FRI"
	// 表示2002-2006年的每个月的最后一个星期五上午10:15执行："0 15 10 ? 6L 2002-2006"
	@Autowired
    private DataService dataService;
	private boolean open = false;
	
	@Scheduled(cron = "0 */30 * * * ?")
	public void syncResServerStatus() {
		if (!open) {
			return;
		}
		AppUserMapper mapper = dataService.getMapper(AppUser.class);
		List<AppUser> users = mapper.queryMeals();
		Date now = new Date();
		List<Integer> ids = new ArrayList<Integer>();
		Map<Integer,School> schools = new HashMap<Integer,School>();
		for (AppUser user : users) {
			if (user.getStartDate() != null && user.getEndDate() != null) {
				if (now.after(user.getEndDate())) {//已过期,需要修改城市热点数据
					Integer schoolId = user.getSchoolId();
					if (schoolId != null && schoolId.intValue() > 0) {
						School school = schools.get(schoolId);
						if (school == null) {
							school = dataService.get(schoolId,School.class);
							schools.put(schoolId,school);
						}
						if (school == null) {
							continue;
						}
						List<CityHotInfo> infos = CityHotUtil.stop(school,user.getAccount(),"0");
						dataService.insertMore(infos);
					}
					ids.add(user.getId());
				}
			}
		}
		if (ids.size() > 0) {
			mapper.updateTimeOut(ids);
		}
		
	}
}
