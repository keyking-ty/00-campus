package com.telit.info.data.business;

import lombok.Data;
import org.joda.time.DateTime;

import com.telit.info.data.JqGridData;
import com.telit.info.util.CommonUtil;
import com.telit.info.util.TimeUtils;

@Data
public class ReportSearchData extends JqGridData{
	private String itemType;
	private String payType;
	private String startDate;
	private String endDate;
	private String schoolId;
	private boolean merge;

	public boolean checkDate(String date) {
		if (CommonUtil.isNotEmpty(startDate) || CommonUtil.isNotEmpty(endDate)) {
			if (CommonUtil.isEmpty(date)) {
				return false;
			}
			if (date.length() > 19) {
				date = date.substring(0,19);
			}
			DateTime stand = TimeUtils.getTime(date);
			if (startDate != null) {
				DateTime start = TimeUtils.getDayTime(startDate);
				if (stand.isBefore(start)) {
					return false;
				}
			}
			if (endDate != null) {
				DateTime end = TimeUtils.getDayTime(endDate);
				if (stand.isAfter(end)) {
					return false;
				}
			}
		}
		return true;
	}
}
