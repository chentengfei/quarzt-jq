package com.szewec.quartz.listener;


import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobListeners implements JobListener {

	private Logger logger = LoggerFactory.getLogger(JobListeners.class);
    public String getName() {
        //这里原本返回null,可以给它个名字，不然运行会报错
        return "JobListeners";
    }
    //这几个方法自己顾名思义
    public void jobToBeExecuted(JobExecutionContext context) {
//    	logger.info("任务：{}，开始了",context.getJobDetail().getKey().getName());
    }

    public void jobExecutionVetoed(JobExecutionContext context) {
    	logger.info("任务：{}，被否决执行了",context.getJobDetail().getKey().getName());
    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
//    	logger.info("任务：{}，结束了",context.getJobDetail().getKey().getName());
    }
}