package com.szewec.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CtripScenicJob implements Job {

	private Logger logger = LoggerFactory.getLogger(CtripScenicJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.info("我是job1，调取器ID：{}，描述：{}", context.getScheduler().getSchedulerInstanceId(),context.getJobDetail().getDescription());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
