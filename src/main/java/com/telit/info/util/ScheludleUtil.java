package com.telit.info.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheludleUtil {
	private static ScheludleUtil instance = null;
	private ScheduledThreadPoolExecutor taskExcutor = new ScheduledThreadPoolExecutor(5);
	private Map<String,ScheduledFuture<?>> tasks = new HashMap<String,ScheduledFuture<?>>();
	
	public static ScheludleUtil getInstance() {
		if (instance == null) {
			instance = new ScheludleUtil();
			instance.init();
		}
		return instance;
	}
	
	protected void init() {
		taskExcutor.scheduleAtFixedRate(new Runnable() {
			public void run(){
				taskExcutor.purge();
			}
		},2,2,TimeUnit.HOURS);
	}
	
	/**
	 *   一次性定时任务
	 * @param taskKey 任务名称
	 * @param command 任务执行逻辑
	 * @param delay 延迟时间
	 * @param unit 时间单位
	 * @return
	 */
	public ScheduledFuture<?> schedule(String taskKey, Runnable command,long delay,TimeUnit unit) {
		ScheduledFuture<?> task = taskExcutor.schedule(command,delay,unit);
		if (taskKey != null) {
			tasks.put(taskKey, task);
		}
		return task;
	}
	
	/**
	 *    重复执行的定时任务
	 * @param taskKey
	 * @param command
	 * @param initialDelay
	 * @param period
	 * @param unit
	 * @return
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(String taskKey,Runnable command, long initialDelay, long period, TimeUnit unit) {
		ScheduledFuture<?> task = taskExcutor.scheduleAtFixedRate(command,initialDelay,period,unit);
		if (taskKey != null) {
			tasks.put(taskKey,task);
		}
		return task;
	}
	
	public void cancleTask(String taskKey) {
		ScheduledFuture<?> task = tasks.get(taskKey);
		if (task != null) {
			task.cancel(true);
		}
	}
}
