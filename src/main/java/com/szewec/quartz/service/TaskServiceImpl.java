package com.szewec.quartz.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szewec.quartz.model.TaskInfo;

@Service
public class TaskServiceImpl implements TaskService {
	private Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Autowired
	private Scheduler scheduler;

	/**
	 * 所有任务列表
	 */
	public List<TaskInfo> list() {
		List<TaskInfo> list = new ArrayList<>();

		try {
			for (String groupJob : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupJob))) {
					List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
					for (Trigger trigger : triggers) {
						Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
						JobDetail jobDetail = scheduler.getJobDetail(jobKey);

						String cronExpression = "", createTime = "";

						if (trigger instanceof CronTrigger) {
							CronTrigger cronTrigger = (CronTrigger) trigger;
							cronExpression = cronTrigger.getCronExpression();
							createTime = cronTrigger.getDescription();
						}
						TaskInfo info = new TaskInfo();
						info.setJobName(jobKey.getName());
						info.setJobGroup(jobKey.getGroup());
						info.setJobDescription(jobDetail.getDescription());
						info.setJobStatus(triggerState.name());
						info.setCronExpression(cronExpression);
						info.setCreateTime(createTime);
						list.add(info);
					}
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 保存定时任务
	 * 
	 * @param info
	 */
	@SuppressWarnings("unchecked")
	public void addJob(TaskInfo info) {
		String jobName = info.getJobName(), jobGroup = info.getJobGroup(), cronExpression = info.getCronExpression(),
				jobDescription = info.getJobDescription(), createTime = getNowDate();
		try {
			if (checkExists(jobName, jobGroup)) {
				logger.info("add job fail, job already exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
			}

			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);

			CronScheduleBuilder schedBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
					.withMisfireHandlingInstructionDoNothing();
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime)
					.withSchedule(schedBuilder).build();

			Class<? extends Job> clazz = (Class<? extends Job>) Class.forName(jobName);
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).withDescription(jobDescription).build();
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException | ClassNotFoundException e) {
			logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
		}
	}

	/**
	 * 修改定时任务
	 * 
	 * @param info
	 */
	public void edit(TaskInfo info) {
		String jobName = info.getJobName(), jobGroup = info.getJobGroup(), cronExpression = info.getCronExpression(),
				jobDescription = info.getJobDescription(), createTime = getNowDate();
		try {
			if (!checkExists(jobName, jobGroup)) {
				logger.info("edit job fail, job is not exist, jobGroup:{}, jobName:{}", jobGroup, jobName);
			}
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
			JobKey jobKey = new JobKey(jobName, jobGroup);
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
					.withMisfireHandlingInstructionDoNothing();
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription(createTime)
					.withSchedule(cronScheduleBuilder).build();

			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			jobDetail.getJobBuilder().withDescription(jobDescription);
			HashSet<Trigger> triggerSet = new HashSet<>();
			triggerSet.add(cronTrigger);

			scheduler.scheduleJob(jobDetail, triggerSet, true);
		} catch (SchedulerException e) {
			logger.error("类名不存在或执行表达式错误,exception:{}", e.getMessage());
		}
	}

	/**
	 * 删除定时任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 */
	public void delete(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		try {
			if (!checkExists(jobName, jobGroup)) {
				scheduler.pauseTrigger(triggerKey);// 停止触发器 
				scheduler.unscheduleJob(triggerKey);// 移除触发器 
				scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));// 删除任务 
				logger.info("delete job, triggerKey:{},jobGroup:{}, jobName:{}", triggerKey, jobGroup, jobName);
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 暂停定时任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 */
	public void pause(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		try {
			scheduler.pauseTrigger(triggerKey);
			logger.info("pause job success, triggerKey:{},jobGroup:{}, jobName:{}", triggerKey, jobGroup, jobName);
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 重新开始任务
	 * 
	 * @param jobName
	 * @param jobGroup
	 */
	public void resume(String jobName, String jobGroup) {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

		try {
			scheduler.resumeTrigger(triggerKey);
			logger.info("resume job success,triggerKey:{},jobGroup:{}, jobName:{}", triggerKey, jobGroup, jobName);
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 验证是否存在
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @throws SchedulerException
	 */
	public boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
		return scheduler.checkExists(triggerKey);
	}

	private String getNowDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
}
