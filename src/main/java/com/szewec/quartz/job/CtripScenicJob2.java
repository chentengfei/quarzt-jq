package com.szewec.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CtripScenicJob2 implements Job {

	private Logger logger = LoggerFactory.getLogger(CtripScenicJob2.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.info("我是job2，调取器ID：{},调度器名称：{}", context.getScheduler().getSchedulerInstanceId(),
					context.getScheduler().getSchedulerName());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
