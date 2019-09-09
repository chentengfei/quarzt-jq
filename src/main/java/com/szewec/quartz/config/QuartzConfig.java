package com.szewec.quartz.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig{
	
	@Autowired
	DataSource dataSource;
	
	@Bean  
    public SchedulerFactoryBean schedulerFactoryBean(QuartzJobFactory myJobFactory) throws Exception {  
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();  
        schedulerFactoryBean.setDataSource(dataSource);  
        //使job实例支持spring 容器管理
        schedulerFactoryBean.setOverwriteExistingJobs(true);  
        schedulerFactoryBean.setJobFactory(myJobFactory);  
        schedulerFactoryBean.setQuartzProperties(quartzProperties());  
        // 延迟10s启动quartz  
        schedulerFactoryBean.setStartupDelay(10);  
  
        return schedulerFactoryBean;  
    }
	
	
	
	@Bean
	public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws IOException, SchedulerException {
		 Scheduler scheduler = schedulerFactoryBean.getScheduler(); 
		 System.out.println(scheduler.getSchedulerName());
		 //挂在任务和触发器运行监听
//		 scheduler.getListenerManager().addTriggerListener(new TriggerListeners());
//		 scheduler.getListenerManager().addJobListener(new JobListeners());
		 scheduler.start();
		return scheduler;
	}
	
	
	
	
	
	/**
	 * 设置quartz属性
	 */
	public Properties quartzProperties() throws IOException {
		Properties prop = new Properties();
		InputStream inputStream = QuartzConfig.class.getClassLoader().getResourceAsStream("quartz.properties");
		prop.load(inputStream);
		return prop;
	}
	
}
