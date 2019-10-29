package com.telit.info.trans.service.cache;

import com.telit.info.util.TimeUtils;

public class TimerLine {
	protected int time;
	protected Object obj;
	protected long start;
	
	protected TimerLine(int time,Object obj) {
		this.obj   = obj;
		this.time  = time;
		this.reset();
	}
	
	protected boolean check(long now) {
		if (time > 0) {
			return now > start + time;
		}
		return false;
	}
	
	public void reset() {
		this.start = TimeUtils.nowLong();
	}
}
